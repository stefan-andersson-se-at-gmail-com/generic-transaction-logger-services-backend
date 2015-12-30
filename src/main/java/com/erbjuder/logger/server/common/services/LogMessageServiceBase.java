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

import com.erbjuder.logger.server.common.helper.DataBase;
import com.erbjuder.logger.server.common.helper.DatabasePartitionHelper;
import com.erbjuder.logger.server.common.helper.MysqlConnection;
import com.erbjuder.logger.server.common.helper.TimeStampUtils;
import com.erbjuder.logger.server.common.helper.TransactionComparator;
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
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.WebServiceException;
import org.apache.commons.lang3.StringEscapeUtils;

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

    private static final Logger logger = Logger.getLogger(LogMessageServiceBase.class.getName());
    private static final int addNumberOfMonth = 3;
    private static final long mysql_max_allowed_packet = 4294967296L;

    public Response create(Transactions transactions) throws WebServiceException {

        // Return 
        Response response = new Response();
        response.setReturn(true);

        List<Transactions.Transaction> tmpTransactionList = transactions.getTransaction();
        Transactions.Transaction[] transactionArray = tmpTransactionList.toArray(new Transaction[tmpTransactionList.size()]);
        Arrays.sort(transactionArray, new TransactionComparator());

        try (Connection connection = MysqlConnection.getConnection()) {

            // Prepare
            PrimaryKeySequence primaryKeySequence = fetchPrimaryKeySequence(connection, transactionArray);
            Map<Transactions.Transaction, Long> hederPrimaryKeyMappning = new HashMap<>(tmpTransactionList.size());

            // Header
            presistLogMessage(connection, transactionArray, primaryKeySequence, hederPrimaryKeyMappning);

            // header data
            presistLogMessageData(connection, transactionArray, hederPrimaryKeyMappning);

            // Close
            connection.commit();
            connection.close();

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

    private void presistLogMessage(
            Connection connection,
            Transactions.Transaction[] transactionArray,
            PrimaryKeySequence primaryKeySequence,
            Map<Transactions.Transaction, Long> hederPrimaryKeyMappning)
            throws SQLException {

        // prepareStetment & Connection
        connection.setAutoCommit(false);
        String prepareStatementString = getLogMessagePrepaterStatementMysql_Insert();
        try (PreparedStatement preparedStatement = connection.prepareStatement(prepareStatementString)) {

            int batch_counter = 0;
            int MAX_HEADER_BATCH_SIZE = 1000;
            PrimaryKeySequence tmpKeySequence
                    = new PrimaryKeySequence(
                            primaryKeySequence.getStartPK(),
                            primaryKeySequence.getEndPK());

            for (Transactions.Transaction header : transactionArray) {

                // execute batch
                if (++batch_counter % MAX_HEADER_BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                    batch_counter = 0;
                }

                // header PK and store the key for child usage!
                long startPK = tmpKeySequence.getStartPK();
                hederPrimaryKeyMappning.put(header, startPK);

                // Mandatory fields
                Boolean isError = header.isIsError();
                String transactionReferenceID = header.getTransactionReferenceID();
                String applicationName = header.getApplicationName();
                Timestamp utcServerTimestamp = TimeStampUtils.createSystemNanoTimeStamp();
                Timestamp utcClientTimestamp = this.getUTCClientTimestamp(header);

                // Optional fields
                String flowName = null;
                String flowPointName = null;
                java.sql.Date expiredDate = new java.sql.Date(this.getExpiredDate(header).getTime());
                if (header.getTransactionLogPointInfo() != null
                        && header.getTransactionLogPointInfo().getFlowName() != null
                        && header.getTransactionLogPointInfo().getFlowPointName() != null
                        && (!header.getTransactionLogPointInfo().getFlowName().trim().isEmpty()
                        || !header.getTransactionLogPointInfo().getFlowName().trim().isEmpty())) {
                    flowName = header.getTransactionLogPointInfo().getFlowName().trim();
                    flowPointName = header.getTransactionLogPointInfo().getFlowPointName().trim();
                }
                
                
                // Prepare statement
                preparedStatement.setLong(1, startPK);
                preparedStatement.setInt(2, DatabasePartitionHelper.calculatePartitionId(utcServerTimestamp));
                preparedStatement.setTimestamp(3, utcClientTimestamp);
                preparedStatement.setTimestamp(4, utcServerTimestamp);
                preparedStatement.setDate(5, expiredDate);
                preparedStatement.setString(6, transactionReferenceID);
                preparedStatement.setString(7, applicationName);
                preparedStatement.setBoolean(8, isError);
                preparedStatement.setString(9, flowName);
                preparedStatement.setString(10, flowPointName);
                
                preparedStatement.addBatch();
                tmpKeySequence.setStartPK(startPK + header.getTransactionLogData().size() + 1);
            }

            // persist the last one to
            preparedStatement.executeBatch();
        }

    }

    private void presistLogMessageData(
            Connection connection,
            Transactions.Transaction[] transactionArray,
            Map<Transactions.Transaction, Long> hederPrimaryKeyMappning
    ) throws SQLException {

        // Prepare log message data, sort on content size
        Map<String, ArrayList<InternalTransactionLogData>> dbContentSizeMap = new HashMap();
        for (Transactions.Transaction logMessage : transactionArray) {
            long logMessageId = hederPrimaryKeyMappning.get(logMessage);
            prepareLogMessageData(logMessageId, logMessageId + 1, logMessage, dbContentSizeMap);
        }

        // For all items in [dbContentSizeMap]
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
                    preparedStatement.setBoolean(7, false);
                    preparedStatement.setBoolean(8, true);
                    preparedStatement.setTimestamp(9, internalTransactionLogData.getUtcClientTimestamp());
                    preparedStatement.setTimestamp(10, internalTransactionLogData.getUtcServerTimestamp());
                    preparedStatement.setDate(11, internalTransactionLogData.getExpiredDate());
                    preparedStatement.setLong(12, internalTransactionLogData.getForeignKey());

                    preparedStatement.addBatch();

                }

                // Some prepare statements in buffer ==> execute before next run
                preparedStatement.executeBatch();
            }
        }

    }

    private void prepareLogMessageData(
            long logMessageId,
            long startPK,
            Transactions.Transaction transaction,
            Map<String, ArrayList<InternalTransactionLogData>> map) {


        //
        // Prepare data for batch load, organized by which database they belongs to
        java.sql.Date expiredDate = new java.sql.Date(this.getExpiredDate(transaction).getTime());
        Timestamp utcClientTimestamp = this.getUTCClientTimestamp(transaction);
        for (Transactions.Transaction.TransactionLogData transactionLogData : transaction.getTransactionLogData()) {

            InternalTransactionLogData internalTransactionLogData
                    = new InternalTransactionLogData(
                            startPK,
                            logMessageId,
                            utcClientTimestamp,
                            transactionLogData,
                            expiredDate);

            String databaseName = internalTransactionLogData.getDatabaseName();
            if (map.containsKey(databaseName)) {
                ArrayList list = map.get(databaseName);
                list.add(internalTransactionLogData);
            } else {
                ArrayList list = new ArrayList<>();
                list.add(internalTransactionLogData);
                map.put(databaseName, list);
            }
            startPK = startPK + 1;
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
                + "LOGMESSAGE_ID) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

        return mysqlLogMessageDataSizePrepareStatement;
    }

    private Timestamp getUTCClientTimestamp(Transactions.Transaction transaction) {

        try {

            long UTCLocalTimeStamp = transaction.getUTCLocalTimeStamp().toGregorianCalendar().getTimeInMillis();
            long UTCLocalTimeStampNanoSeconds = 0;
            try {
                UTCLocalTimeStampNanoSeconds = transaction.getUTCLocalTimeStampNanoSeconds();

            } catch (Exception UTCLocalTimeStampNanoSecondsNotPressent) {
                //Skip client nano
                Timestamp timestamp = new Timestamp(UTCLocalTimeStamp);
                UTCLocalTimeStampNanoSeconds = timestamp.getNanos();
            }

            if (Long.MAX_VALUE > UTCLocalTimeStamp && Long.MIN_VALUE < UTCLocalTimeStamp) {
                // logger.log(Level.INFO, "UTCLocalTimeStamp=[ " + UTCLocalTimeStamp + " ]");
                // logger.log(Level.INFO, "UTCLocalTimeStampNanoSeconds=[ " + UTCLocalTimeStampNanoSeconds + " ]");
                // logger.log(Level.INFO, "createNanoTimeStamp=[ " + TimeStampUtils.createNanoTimeStamp(UTCLocalTimeStamp, UTCLocalTimeStampNanoSeconds).getNanos() + " ]");
                return TimeStampUtils.createNanoTimeStamp(UTCLocalTimeStamp, UTCLocalTimeStampNanoSeconds);

            } else {
                // logger.log(Level.INFO, "[ Invalid UTCLocalTimeStamp range, Use current system time instead! ] ");
                // logger.log(Level.INFO, "[ " + UTCLocalTimeStamp + " ] ");
                return TimeStampUtils.createSystemNanoTimeStamp();
            }

        } catch (Exception invalidDateException) {
            // logger.log(Level.INFO, "[ Invalid log date! Use current system time instead! ] ");
            // logger.log(Level.INFO, invalidDateException.getMessage());
            return TimeStampUtils.createSystemNanoTimeStamp();
        }
    }

    private Date getExpiredDate(Transactions.Transaction transaction) {

        // Optional
        try {

            Date expiredTime = transaction.getExpiryDate().toGregorianCalendar().getTime();
            if (expiredTime != null && Long.MAX_VALUE > expiredTime.getTime() && Long.MIN_VALUE < expiredTime.getTime()) {
                return expiredTime;
            } else {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.add(Calendar.MONTH, LogMessageServiceBase.addNumberOfMonth);
                return calendar.getTime();
            }
        } catch (Exception invalidExiredDateException) {
            // logger.log(Level.INFO, "[ Invalid ExpiryDate! Use default expired time instead! ] ");
            // logger.log(Level.INFO, invalidExiredDateException.getMessage());
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.add(Calendar.MONTH, LogMessageServiceBase.addNumberOfMonth);
            return calendar.getTime();
        }
    }

    private String logMessageDataPartitionNameFromContentSize(long contentSize) {

        // Determ content partition based on size
        //       
        //  TINYTEXT    |           255 (2 8−1) bytes
        //  TEXT        |        65,535 (216−1) bytes = 64 KiB
        //  MEDIUMTEXT  |    16,777,215 (224−1) bytes = 16 MiB
        //  LONGTEXT    | 4,294,967,295 (232−1) bytes =  4 GiB
        String partitionName = "";
        if (contentSize == 0) {
            // continue;

        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_20B) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_01_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_40B) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_02_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_60B) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_03_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_80B) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_04_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_100B) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_05_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_150B) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_06_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_200B) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_07_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_255B) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_08_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_64KB) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_09_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_1MB) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_10_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_2MB) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_11_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_3MB) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_12_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_4MB) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_13_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_5MB) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_14_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_10MB) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_15_NAME;
        } else if (contentSize <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_16MB) {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_16_NAME;
        } else {
            partitionName = DataBase.LOGMESSAGEDATA_PARTITION_17_NAME;
        }

        return partitionName;

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

    // 
    // Temporary internal class
    private class InternalTransactionLogData {

        private final long primaryKey;
        private final long foreignKey;
        private String label;
        private String mimeType;
        private String content;
        private long contentSize;
        private Timestamp utcServerTimestamp;
        private Timestamp utcClientTimestamp;
        private int partitionId;
        private java.sql.Date expiredDate;
        private String logMessageDataPartitionNameFromContentSize;

        public InternalTransactionLogData(
                long primaryKey,
                long foreignKey,
                Timestamp utcClientTimestamp,
                Transactions.Transaction.TransactionLogData transactionLogData,
                java.sql.Date expiredDate) {
            this.primaryKey = primaryKey;
            this.foreignKey = foreignKey;
            this.expiredDate = expiredDate;
            this.utcClientTimestamp = utcClientTimestamp;
            this.processTransactionLogData(transactionLogData);
        }

        public long getPrimaryKey() {
            return primaryKey;
        }

        public long getForeignKey() {
            return foreignKey;
        }

        public String getContentLabel() {
            return label;
        }

        public String getContentMimeType() {
            return mimeType;
        }

        public String getContent() {
            return content;
        }

        public long getContentSize() {
            return contentSize;
        }

        public java.sql.Date getExpiredDate() {
            return expiredDate;
        }

        public Timestamp getUtcServerTimestamp() {
            return utcServerTimestamp;
        }

        public Timestamp getUtcClientTimestamp() {
            return utcClientTimestamp;
        }

        public int getPartitionId() {
            return partitionId;
        }

        public String getDatabaseName() {
            return this.logMessageDataPartitionNameFromContentSize;
        }

        private void processTransactionLogData(Transactions.Transaction.TransactionLogData transactionLogData) {

            this.utcServerTimestamp = TimeStampUtils.createSystemNanoTimeStamp();
            this.partitionId = DatabasePartitionHelper.calculatePartitionId(this.utcServerTimestamp);

            this.label = StringEscapeUtils.unescapeXml(transactionLogData.getContentLabel().trim());
            this.mimeType = transactionLogData.getContentMimeType().trim();
            this.content = transactionLogData.getContent();
            this.contentSize = 0;

            // 
            // Decode all messages
            try {
                content = new String(Base64.getDecoder().decode(content.getBytes()));
                contentSize = content.getBytes().length;
            } catch (Exception e) {

                content = StringEscapeUtils.unescapeXml(content);
                contentSize = content.getBytes().length;
            }

            this.logMessageDataPartitionNameFromContentSize = logMessageDataPartitionNameFromContentSize(contentSize);
        }

    }
}
