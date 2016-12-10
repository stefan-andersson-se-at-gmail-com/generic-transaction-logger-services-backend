/* 
 * Copyright (C) 2014 erbjuder.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.erbjuder.logger.server.common.services;

import com.erbjuder.logger.server.common.helper.MysqlConnection;
import com.erbjuder.logger.server.common.helper.TransactionComparator;
import com.erbjuder.logger.server.queue.MessageSender;
import com.generic.global.transactionlogger.Response;
import com.generic.global.transactionlogger.Transactions;
import com.generic.global.transactionlogger.Transactions.Transaction;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.WebServiceException;

/**
 *
 * @author Stefan Andersson
 */
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    com.generic.global.fault.ObjectFactory.class,
    com.generic.global.transactionlogger.ObjectFactory.class
})
public class LogMessageServiceBase {

    @EJB
    MessageSender messageSender;

    private static final Logger logger = Logger.getLogger(LogMessageServiceBase.class.getName());
    public static final int addNumberOfMonth = 12;
    public static final long mysql_max_allowed_packet = 16000000L;//16MB ( MAX LONG = 4294967296L );

    public Response create(Transactions transactions) throws WebServiceException {

        // Return 
        Response response = new Response();
        response.setReturn(true);

        List<Transactions.Transaction> tmpTransactionList = transactions.getTransaction();
        Transactions.Transaction[] transactionArray = tmpTransactionList.toArray(new Transaction[tmpTransactionList.size()]);
        Arrays.sort(transactionArray, new TransactionComparator());

        try (Connection connection = MysqlConnection.getConnectionWrite()) {

            // Build all obnject that we need one time    
            InternalObjects internalObjects = buildInternalObjects(connection, transactionArray);

            // Clear as mutch we can
            transactions = null;
            tmpTransactionList = null;
            transactionArray = null;

            // Persist input data 
            persistLogMessage(connection, internalObjects);
            persistLogMessageData(connection, internalObjects);

            // Close
            connection.commit();
            connection.close();

            // Put message on buss / topic queue.
            messageSender.produceMessages(internalObjects.getInternalTransactionHeaders());

            
            internalObjects.getDbContentSizeMap().clear();
            internalObjects.setDbContentSizeMap( null );
            
        } catch (SQLException sqlError) {
            logger.log(Level.SEVERE, sqlError.getMessage());
            return response;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            response.setReturn(false);
            return response;

        }

        return response;
    }

    private void persistLogMessage(
            Connection connection,
            InternalObjects internalObjects
    )
            throws SQLException {

        // prepareStetment & Connection
        connection.setAutoCommit(false);
        String prepareStatementString = getLogMessagePrepaterStatementMysql_Insert();
        try (PreparedStatement preparedStatement = connection.prepareStatement(prepareStatementString)) {

            int batch_counter = 0;
            int MAX_HEADER_BATCH_SIZE = 1000;

            InternalTransactionHeaders internalTransactionHeaders = internalObjects.getInternalTransactionHeaders();
            for (InternalTransactionHeader internalTransactionHeader : internalTransactionHeaders.getInternalTransactionHeaders()) {

                // execute batch
                if (++batch_counter % MAX_HEADER_BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                    batch_counter = 0;
                }

                // Prepare statement
                preparedStatement.setLong(1, internalTransactionHeader.getPrimaryKey());
                preparedStatement.setInt(2, internalTransactionHeader.getPartitionId());
                preparedStatement.setTimestamp(3, internalTransactionHeader.getUtcClientTimestamp());
                preparedStatement.setTimestamp(4, internalTransactionHeader.getUtcServerTimestamp());
                preparedStatement.setDate(5, internalTransactionHeader.getExpiredDate());
                preparedStatement.setString(6, internalTransactionHeader.getTransactionReferenceID());
                preparedStatement.setString(7, internalTransactionHeader.getApplicationName());
                preparedStatement.setBoolean(8, internalTransactionHeader.getIsError());
                preparedStatement.setString(9, internalTransactionHeader.getFlowName());
                preparedStatement.setString(10, internalTransactionHeader.getFlowPointName());

                preparedStatement.addBatch();

            }

            // persist the last one to
            preparedStatement.executeBatch();
            preparedStatement.clearBatch();
        }

    }

    private void persistLogMessageData(
            Connection connection,
            InternalObjects internalObjects
    )
            throws SQLException {

        // prepareStetment & Connection
        connection.setAutoCommit(false);

        // For all items in [dbContentSizeMap]
        Map<String, ArrayList<InternalTransactionLogData>> dbContentSizeMap = internalObjects.getDbContentSizeMap();
        for (Map.Entry<String, ArrayList<InternalTransactionLogData>> entry : dbContentSizeMap.entrySet()) {

            // 
            // Build batch and execute when ready!
            long accumulated_batch_size = 0L;
            String prepareStatementString = getLogMessageDataPrepaterStatementMysql_Insert(entry.getKey());
            try (PreparedStatement preparedStatement = connection.prepareStatement(prepareStatementString)) {

                //
                // Build batch and execute when ready!
                for (InternalTransactionLogData internalTransactionLogData : entry.getValue()) {

                    // Iff accumulated WILL be larger than MAX ==> execute
                    accumulated_batch_size = accumulated_batch_size + internalTransactionLogData.getContentSize();
                    if (accumulated_batch_size >= LogMessageServiceBase.mysql_max_allowed_packet) {
                        preparedStatement.executeBatch();
                        preparedStatement.clearBatch();
                        accumulated_batch_size = 0L;
                    }

                    preparedStatement.setLong(1, internalTransactionLogData.getPrimaryKey());
                    preparedStatement.setInt(2, internalTransactionLogData.getPartitionId());
                    preparedStatement.setString(3, internalTransactionLogData.getContentLabel());
                    preparedStatement.setString(4, internalTransactionLogData.getContentMimeType());
                    preparedStatement.setString(5, internalTransactionLogData.getContent());
                    preparedStatement.setLong(6, internalTransactionLogData.getContentSize());
                    preparedStatement.setBoolean(7, internalTransactionLogData.getModified());
                    preparedStatement.setBoolean(8, internalTransactionLogData.getSearchable());
                    preparedStatement.setTimestamp(9, internalTransactionLogData.getUtcClientTimestamp());
                    preparedStatement.setTimestamp(10, internalTransactionLogData.getUtcServerTimestamp());
                    preparedStatement.setDate(11, internalTransactionLogData.getExpiredDate());
                    preparedStatement.setLong(12, internalTransactionLogData.getForeignKey());

                    preparedStatement.addBatch();

                }

                // Some prepare statements in buffer ==> execute before next run
                preparedStatement.executeBatch();
                preparedStatement.clearBatch();
            }
        }

    }

    private String getPrimaryKeySequencePrepareStatement_Fetch() {
        return "{? = call seq_generator_fetch(?,?)}";
    }

    private String getLogMessagePrepaterStatementMysql_Insert() {

        String mysqlLogMessagePrepareStatement
                = "INSERT INTO LogMessage ("
                + "ID, "
                + "PARTITION_ID, "
                + "UTCLOCALTIMESTAMP, "
                + "UTCSERVERTIMESTAMP, "
                + "EXPIREDDATE, "
                + "TRANSACTIONREFERENCEID, "
                + "APPLICATIONNAME, "
                + "ISERROR, "
                + "FLOWNAME, "
                + "FLOWPOINTNAME) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

        return mysqlLogMessagePrepareStatement;
    }

    private String getLogMessageDataPrepaterStatementMysql_Insert(String logMessageDataSizePartitionName) {

        String mysqlLogMessageDataSizePrepareStatement = "INSERT INTO " + logMessageDataSizePartitionName + " ("
                + "ID, "
                + "PARTITION_ID, "
                + "LABEL, "
                + "MIMETYPE, "
                + "CONTENT, "
                + "CONTENTSIZE, "
                + "MODIFIED, "
                + "SEARCHABLE, "
                + "UTCLOCALTIMESTAMP, "
                + "UTCSERVERTIMESTAMP, "
                + "EXPIREDDATE, "
                + "LOGMESSAGE_ID ) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

        return mysqlLogMessageDataSizePrepareStatement;
    }

    private PrimaryKeySequence fetchPrimaryKeySequence(
            Connection connection,
            Transactions.Transaction[] transactionArray)
            throws SQLException {

        // Count number of PK we need
        int numOfPrimaryKeys = transactionArray.length;
        for (Transactions.Transaction transaction : transactionArray) {
            numOfPrimaryKeys = numOfPrimaryKeys + transaction.getTransactionLogData().size();
        }

        // Fetch primary keys
        String primaryKeySequencePrepareStatementString = getPrimaryKeySequencePrepareStatement_Fetch();
        CallableStatement primaryKeySequencePrepareStatement = connection.prepareCall(primaryKeySequencePrepareStatementString);
        primaryKeySequencePrepareStatement.registerOutParameter(1, java.sql.Types.BIGINT);
        primaryKeySequencePrepareStatement.setString(2, "seq_gen_1");
        primaryKeySequencePrepareStatement.setInt(3, numOfPrimaryKeys);
        primaryKeySequencePrepareStatement.execute();
        Long endPK = primaryKeySequencePrepareStatement.getLong(1);
        Long startPK = endPK - numOfPrimaryKeys;
        return new PrimaryKeySequence(startPK, endPK);

    }

    private InternalObjects buildInternalObjects(Connection connection, Transactions.Transaction[] transactionArray) throws SQLException {

        // Optimization, loop just one time over input data
        // Result
        InternalObjects internalObjects = new InternalObjects();

        // Header key sequence 
        Map<String, ArrayList<InternalTransactionLogData>> dbContentSizeMap = internalObjects.getDbContentSizeMap();
        InternalTransactionHeaders internalTransactionHeaders = new InternalTransactionHeaders();
        PrimaryKeySequence primaryKeySequence = fetchPrimaryKeySequence(connection, transactionArray);
        PrimaryKeySequence headerKeySequence
                = new PrimaryKeySequence(
                        primaryKeySequence.getStartPK(),
                        primaryKeySequence.getEndPK());

        Map<Transactions.Transaction, InternalTransactionHeader> hederPrimaryKeyMappning = new HashMap<>(transactionArray.length);
        for (Transactions.Transaction transactionHeader : transactionArray) {

            // header PK and store the key for child usage!
            long startPK = headerKeySequence.getStartPK();
            InternalTransactionHeader internalHeader = new InternalTransactionHeader(startPK, transactionHeader);
            hederPrimaryKeyMappning.put(transactionHeader, internalHeader);
            headerKeySequence.setStartPK(startPK + transactionHeader.getTransactionLogData().size() + 1);

            // Child data
            PrimaryKeySequence childDataKeySequence
                    = new PrimaryKeySequence(
                            startPK,
                            transactionHeader.getTransactionLogData().size()
                    );

            //
            // Prepare data for batch load, organized by which database they belongs to
            for (Transactions.Transaction.TransactionLogData transactionLogData : transactionHeader.getTransactionLogData()) {

                InternalTransactionLogData internalTransactionLogData
                        = new InternalTransactionLogData(
                                childDataKeySequence.next(), // Inc heder PK up to child length
                                internalHeader.getPrimaryKey(),
                                internalHeader.getUtcClientTimestamp(),
                                false,
                                true,
                                transactionLogData,
                                internalHeader.getExpiredDate());

                // Add meta data to header 
                internalHeader.getInternalTransactionLogData().add(internalTransactionLogData);

                // Optimization Calculate dbContentSizeMap in this step to
                String databaseName = internalTransactionLogData.getDatabaseName();
                ArrayList list = dbContentSizeMap.get(databaseName);
                if (list != null) {
                    list.add(internalTransactionLogData);
                } else {
                    list = new ArrayList<>();
                    list.add(internalTransactionLogData);
                    dbContentSizeMap.put(databaseName, list);
                }

            }

            // Save to result
            internalTransactionHeaders.addInternalTransactionHeader(internalHeader);

        }

        internalObjects.setInternalTransactionHeaders(internalTransactionHeaders);
        return internalObjects;

    }

    private class InternalObjects {

        private InternalTransactionHeaders internalTransactionHeaders = new InternalTransactionHeaders();
        private Map<String, ArrayList<InternalTransactionLogData>> dbContentSizeMap = new HashMap();

        ;

        public InternalTransactionHeaders getInternalTransactionHeaders() {
            return internalTransactionHeaders;
        }

        public void setInternalTransactionHeaders(InternalTransactionHeaders internalTransactionHeaders) {
            this.internalTransactionHeaders = internalTransactionHeaders;
        }

        public Map<String, ArrayList<InternalTransactionLogData>> getDbContentSizeMap() {
            return dbContentSizeMap;
        }

        public void setDbContentSizeMap(Map<String, ArrayList<InternalTransactionLogData>> dbContentSizeMap) {
            this.dbContentSizeMap = dbContentSizeMap;
        }

    }

}
