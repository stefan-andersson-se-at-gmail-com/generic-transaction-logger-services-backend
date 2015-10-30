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
package com.erbjuder.logger.server.soap.services;

import com.erbjuder.logger.server.common.helper.DataBase;
import com.erbjuder.logger.server.common.helper.TimeStampUtils;
import com.erbjuder.logger.server.common.helper.TransactionComparator;
import com.erbjuder.logger.server.rest.services.dao.MysqlConnection;
import com.generic.global.transactionlogger.Response;
import com.generic.global.transactionlogger.Transactions;
import com.generic.global.transactionlogger.Transactions.Transaction;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
public class LogMessageServiceBase extends MysqlConnection {

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

        try (Connection connection = MysqlConnection()) {

            // prepareStetment & Connection
            String logMessagePrepareStatementString = getLogMessagePrepaterStatementMysql_Insert();
            PreparedStatement logMessagePreparedStatement = connection.prepareStatement(logMessagePrepareStatementString);

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

            System.err.println("endPK =[ " + endPK + " ]");
            System.err.println("Init startPK =[ " + startPK + " ]");
            // initialize prepareStatement LogMessage
            for (Transactions.Transaction transaction : transactionArray) {
                long logMessageId = startPK;
                presistLogMessage(logMessageId, logMessagePreparedStatement, transaction);
                startPK = presistLogMessageData(logMessageId, connection, transaction, startPK + 1);
            }

            System.err.println("End startPK =[ " + startPK + " ]");
            connection.commit();
            connection.close();

        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            response.setReturn(false);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.setReturn(false);
            return response;

        }

        return response;
    }

    private void presistLogMessage(
            long logMessageId,
            PreparedStatement preparedStatement,
            Transactions.Transaction transaction) throws SQLException {

        // Mandatory fields
        Boolean isError = transaction.isIsError();
        String transactionReferenceID = transaction.getTransactionReferenceID();
        String applicationName = transaction.getApplicationName();
        Timestamp utcServerTimestamp = TimeStampUtils.createSystemNanoTimeStamp();
        Timestamp utcClientTimestamp = this.getUTCClientTimestamp(transaction);

        // Optional fields
        String flowName = "";
        String flowPointName = "";
        java.sql.Date expiredDate = new java.sql.Date(this.getExpiredDate(transaction).getTime());
        if (transaction.getTransactionLogPointInfo() != null
                && transaction.getTransactionLogPointInfo().getFlowName() != null
                && transaction.getTransactionLogPointInfo().getFlowPointName() != null
                && (!transaction.getTransactionLogPointInfo().getFlowName().trim().isEmpty()
                || !transaction.getTransactionLogPointInfo().getFlowName().trim().isEmpty())) {
            flowName = transaction.getTransactionLogPointInfo().getFlowName().trim();
            flowPointName = transaction.getTransactionLogPointInfo().getFlowPointName().trim();
        }

        // Prepare statement
        preparedStatement.setLong(1, logMessageId);
        preparedStatement.setInt(2, this.calculationPartitionId(utcServerTimestamp));
        preparedStatement.setTimestamp(3, utcClientTimestamp);
        preparedStatement.setTimestamp(4, utcServerTimestamp);
        preparedStatement.setDate(5, expiredDate);
        preparedStatement.setString(6, transactionReferenceID);
        preparedStatement.setString(7, applicationName);
        preparedStatement.setBoolean(8, isError);
        preparedStatement.setString(9, flowName);
        preparedStatement.setString(10, flowPointName);

        // Execute and assign logMessage PK
        preparedStatement.executeUpdate();

    }

    private long presistLogMessageData(
            long logMessageId,
            Connection connection,
            Transactions.Transaction transaction,
            long startPK
    ) throws SQLException {

         
        long accumulated_batch_size = 0L;
        PreparedStatement preparedStatement = null;
        java.sql.Date expiredDate = new java.sql.Date(this.getExpiredDate(transaction).getTime());
        for (Transactions.Transaction.TransactionLogData transactionLogData : transaction.getTransactionLogData()) {

            long primaryKey = startPK;
            Timestamp utcServerTimestamp = TimeStampUtils.createSystemNanoTimeStamp();
            Timestamp utcClientTimestamp = this.getUTCClientTimestamp(transaction);

            String label = StringEscapeUtils.unescapeXml(transactionLogData.getContentLabel().trim());
            String mimeType = transactionLogData.getContentMimeType().trim();
            String content = transactionLogData.getContent();
            long contentSize = 0;

            try {

                content = new String(Base64.getDecoder().decode(content.getBytes()));
                contentSize = content.getBytes().length;
                accumulated_batch_size = accumulated_batch_size + contentSize;
            } catch (Exception e) {
                content = StringEscapeUtils.unescapeXml(content);
                contentSize = content.getBytes().length;
                accumulated_batch_size = accumulated_batch_size + contentSize;
            }

            
            // Iff accumulated WILL be larger than MAX ==> execute
            if (accumulated_batch_size >= LogMessageServiceBase.mysql_max_allowed_packet && preparedStatement != null) { 
                preparedStatement.executeBatch();
                accumulated_batch_size = 0L;
            } 
            
            
            String logMessageDataPrepareStatementString = getLogMessageDataPrepaterStatementMysql_Insert(contentSize);
            preparedStatement = connection.prepareStatement(logMessageDataPrepareStatementString);
            connection.setAutoCommit(false);
            preparedStatement.setLong(1, primaryKey);
            preparedStatement.setInt(2, this.calculationPartitionId(utcServerTimestamp));
            preparedStatement.setString(3, label);
            preparedStatement.setString(4, mimeType);
            preparedStatement.setString(5, content);
            preparedStatement.setLong(6, contentSize);
            preparedStatement.setBoolean(7, false);
            preparedStatement.setBoolean(8, true);
            preparedStatement.setTimestamp(9, utcClientTimestamp);
            preparedStatement.setTimestamp(10, utcServerTimestamp);
            preparedStatement.setDate(11, expiredDate);
            preparedStatement.setLong(12, logMessageId);
            
     
            preparedStatement.addBatch();
            startPK = startPK + 1;

        }

        // Some prepare statements in buffer ==> execute
        if (accumulated_batch_size > 0 && preparedStatement != null) {
            preparedStatement.executeBatch();
        }
        return startPK;
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

    private String getLogMessageDataPrepaterStatementMysql_Insert(long contentSize) {

        String LogMessageDataPartitionName = this.logMessageDataPartitionNameFromContentSize(contentSize);
        String mysqlLogMessageDataPrepareStatement = "INSERT INTO " + LogMessageDataPartitionName + " ("
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

        return mysqlLogMessageDataPrepareStatement;
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
                logger.log(Level.INFO, "UTCLocalTimeStamp=[ " + UTCLocalTimeStamp + " ]");
                logger.log(Level.INFO, "UTCLocalTimeStampNanoSeconds=[ " + UTCLocalTimeStampNanoSeconds + " ]");
                logger.log(Level.INFO, "createNanoTimeStamp=[ " + TimeStampUtils.createNanoTimeStamp(UTCLocalTimeStamp, UTCLocalTimeStampNanoSeconds).getNanos() + " ]");
                return TimeStampUtils.createNanoTimeStamp(UTCLocalTimeStamp, UTCLocalTimeStampNanoSeconds);

            } else {
                logger.log(Level.INFO, "[ Invalid UTCLocalTimeStamp range, Use current system time instead! ] ");
                logger.log(Level.INFO, "[ " + UTCLocalTimeStamp + " ] ");
                return TimeStampUtils.createSystemNanoTimeStamp();
            }

        } catch (Exception invalidDateException) {
            logger.log(Level.INFO, "[ Invalid log date! Use current system time instead! ] ");
            logger.log(Level.INFO, invalidDateException.getMessage());
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
            logger.log(Level.INFO, "[ Invalid ExpiryDate! Use default expired time instead! ] ");
            logger.log(Level.INFO, invalidExiredDateException.getMessage());
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.add(Calendar.MONTH, LogMessageServiceBase.addNumberOfMonth);
            return calendar.getTime();
        }
    }

    private int calculationPartitionId(Timestamp utcServerTimestamp) {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR);
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

}
