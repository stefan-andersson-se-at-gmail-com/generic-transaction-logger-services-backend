/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erbjuder.logger.server.soap.services;

import com.erbjuder.logger.server.common.helper.DataBase;
import com.erbjuder.logger.server.common.helper.MimeTypes;
import com.erbjuder.logger.server.common.helper.TimeStampUtils;
import com.erbjuder.logger.server.common.helper.TransactionComparator;
import com.erbjuder.logger.server.common.helper.XMLUtils;
import com.erbjuder.logger.server.entity.impl.LogMessage;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_01;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_02;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_03;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_04;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_05;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_06;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_07;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_08;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_09;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_10;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_11;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_12;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_13;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_14;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_15;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_16;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_17;
import com.erbjuder.logger.server.exception.InvalidXML;
import com.erbjuder.logger.server.facade.interfaces.LogMessageFacade;
import com.generic.global.transactionlogger.Response;
import com.generic.global.transactionlogger.Transactions;
import com.generic.global.transactionlogger.Transactions.Transaction;
import com.sun.xml.messaging.saaj.util.Base64;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.developer.JAXWSProperties;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.WebServiceContext;
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
public abstract class LogMessageServiceBase {

    private static final Logger logger = Logger.getLogger(LogMessageServiceBase.class.getName());
    private static int addNumberOfMonth = 3;
    private static int subtractNumberOfMonth = -1;
    private static String MESSAGE_ID = "MessageId";
    private static String MESSAGE_ID_URN = "http://www.w3.org/2005/08/addressing";

    public abstract LogMessageFacade getLogMessageFacade();

    public abstract WebServiceContext getWebServiceContext();

    public Response create(Transactions transactions) throws WebServiceException {

        //
        // Return 
        Response response = new Response();
        response.setReturn(true);

        try {

            //
            // Parse SOAP Header and looking for MessageId
            String message_id = "";
            HeaderList headerList = (com.sun.xml.ws.api.message.HeaderList) getWebServiceContext().getMessageContext().get(JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);
            for (Header header : headerList) {

                String urn = header.getNamespaceURI();
                String key = header.getLocalPart();
                String value = header.getStringContent().trim();

                if (MESSAGE_ID_URN.equalsIgnoreCase(urn) && MESSAGE_ID.equalsIgnoreCase(key) && !value.isEmpty()) {
                    //logger.log(Level.SEVERE, "[ Found messageId " + value + " ] ");
                    message_id = value;
                    response.setReturn(false);
                } else {
                    //logger.log(Level.SEVERE, "Got <Urn : Key> =[ " + urn + " ] " + " [ " + key + " ]");
                }
            }

            //
            // Copy all element into new structure due the original list seams to be NOT modifiable
            // ( That's a requirement of Collection.sort method )
            List<Transactions.Transaction> tmpTransactionList = transactions.getTransaction();
            Transactions.Transaction[] transactionArray = tmpTransactionList.toArray(new Transaction[tmpTransactionList.size()]);
            Arrays.sort(transactionArray, new TransactionComparator());
            for (Transactions.Transaction transaction : transactionArray) {

                LogMessage logMessage = new LogMessage();

                //
                // Mandator
                String referenceId = transaction.getTransactionReferenceID();
                if (message_id.isEmpty() || referenceId.equals(message_id)) {
                    logMessage.setTransactionReferenceID(referenceId);
                } else {
                    logger.log(Level.SEVERE, "[ TransactionReferanceId don't match SOAP Header content-id ] ");
                    response.setReturn(false);
                }

                // 
                // Server UTC time
                logMessage.setApplicationName(transaction.getApplicationName().toLowerCase());
                logMessage.setIsError(transaction.isIsError());
                logMessage.setUtcServerTimeStamp(TimeStampUtils.createSystemNanoTimeStamp());

                try {

                    long UTCLocalTimeStamp = transaction.getUTCLocalTimeStamp().toGregorianCalendar().getTimeInMillis();
                    long UTCLocalTimeStampNanoSeconds = 0;
                    try {
                        UTCLocalTimeStampNanoSeconds = transaction.getUTCLocalTimeStampNanoSeconds().longValue();

                    } catch (Exception UTCLocalTimeStampNanoSecondsNotPressent) {
                        //Skip client nano
                        Timestamp timestamp = new Timestamp(UTCLocalTimeStamp);
                        UTCLocalTimeStampNanoSeconds = timestamp.getNanos();
                    }

                    if (Long.MAX_VALUE > UTCLocalTimeStamp && Long.MIN_VALUE < UTCLocalTimeStamp) {
                        //logger.log(Level.INFO, "UTCLocalTimeStamp=[ " + UTCLocalTimeStamp + " ]");
                        //logger.log(Level.INFO, "UTCLocalTimeStampNanoSeconds=[ " + UTCLocalTimeStampNanoSeconds + " ]");
                        //logger.log(Level.INFO, "createNanoTimeStamp=[ " + TimeStamp.createNanoTimeStamp(UTCLocalTimeStamp, UTCLocalTimeStampNanoSeconds).getNanos() + " ]");
                        logMessage.setUtcLocalTimeStamp(TimeStampUtils.createNanoTimeStamp(UTCLocalTimeStamp, UTCLocalTimeStampNanoSeconds));

                    } else {
                        //logger.log(Level.INFO, "[ Invalid UTCLocalTimeStamp range, Use current system time instead! ] ");
                        //logger.log(Level.INFO, "[ " + UTCLocalTimeStamp + " ] ");
                        logMessage.setUtcLocalTimeStamp(TimeStampUtils.createSystemNanoTimeStamp());
                    }

                } catch (Exception invalidDateException) {
                    logger.log(Level.INFO, "[ Invalid log date! Use current system time instead! ] ");
                    logger.log(Level.INFO, invalidDateException.getMessage());
                    logMessage.setUtcLocalTimeStamp(TimeStampUtils.createSystemNanoTimeStamp());
                }

                //
                // Optional
                try {

                    Date expiredTime = transaction.getExpiryDate().toGregorianCalendar().getTime();
                    if (expiredTime != null && Long.MAX_VALUE > expiredTime.getTime() && Long.MIN_VALUE < expiredTime.getTime()) {
                        logMessage.setExpiredDate(expiredTime);
                    } else {
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        calendar.add(Calendar.MONTH, addNumberOfMonth);
                        logMessage.setExpiredDate(calendar.getTime());
                    }
                } catch (Exception invalidExiredDateException) {
                    //logger.log(Level.INFO, "[ Invalid ExpiryDate! Use default expired time instead! ] ");
                    //logger.log(Level.INFO, invalidExiredDateException.getMessage());
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    calendar.add(Calendar.MONTH, addNumberOfMonth);
                    logMessage.setExpiredDate(calendar.getTime());
                }

                //
                // Transaction point info 
                if (transaction.getTransactionLogPointInfo() != null) {
                    String flowName = transaction.getTransactionLogPointInfo().getFlowName().trim().toLowerCase();
                    String flowPointName = transaction.getTransactionLogPointInfo().getFlowPointName().trim().toLowerCase();

                    if (flowName.isEmpty()) {
                        flowName = transaction.getApplicationName().trim().toLowerCase();
                    }

                    if (flowPointName.isEmpty()) {
                        flowPointName = "";
                    }

                    logMessage.setFlowName(flowName);
                    logMessage.setFlowPointName(flowPointName);

                } else {
                    logMessage.setFlowName("");
                    logMessage.setFlowPointName("");
                }

                //
                // Transaction log data
                logMessage = this.buildTransactioLogDataEntity(transaction, logMessage);

                //
                // Transaction meta info
                logMessage = this.buildTransactioMetaInfoEntity(transaction, logMessage);

                //
                // Persist
                getLogMessageFacade().create(logMessage);

            }

            // Flush and close 
            getLogMessageFacade().getEntityManager().flush();
            getLogMessageFacade().getEntityManager().close();

        } catch (Throwable ex) {
            StringBuilder builder = new StringBuilder();
            builder.append("============= [ Java Server exception ] =============== \n");
            logger.log(Level.SEVERE, builder.toString());
            response.setReturn(false);
        }

        //
        // Return
        return response;
    }

    private LogMessage buildTransactioLogDataEntity(Transactions.Transaction transaction, LogMessage logMessage) {

        for (Transactions.Transaction.TransactionLogData transactionLogData : transaction.getTransactionLogData()) {
            logMessage = addTransactioLogData(transactionLogData, logMessage);
        }

        return logMessage;
    }

    private LogMessage addTransactioLogData(Transactions.Transaction.TransactionLogData transactionLogData, LogMessage logMessage) {

        String base64 = MimeTypes.BASE64;
        String label = StringEscapeUtils.unescapeXml(transactionLogData.getContentLabel().trim().toLowerCase());
        String mimeType = transactionLogData.getContentMimeType().trim().toLowerCase();
        String content = StringEscapeUtils.unescapeXml(transactionLogData.getContent().trim());
        long size = 0;
        if (base64.equalsIgnoreCase(mimeType)) {
            content = this.XMLFormatter(content);
            size = content.getBytes().length;

        } else {
            size = content.getBytes().length;
        }

        // 
        // Bind data to logmessage
        return this.updateLogMessage(label, mimeType, content, size, logMessage);

    }

    private LogMessage updateLogMessage(String label, String mimeType, String content, long size, LogMessage logMessage) {

        //                 
        //  TINYTEXT    |           255 (2 8−1) bytes
        //  TEXT        |        65,535 (216−1) bytes = 64 KiB
        //  MEDIUMTEXT  |    16,777,215 (224−1) bytes = 16 MiB
        //  LONGTEXT    | 4,294,967,295 (232−1) bytes =  4 GiB
        if (size == 0) {
            // continue;

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_20B) {

            LogMessageData_Partition_01 logMessageData_Max20B = new LogMessageData_Partition_01();
            logMessageData_Max20B.setLabel(label);
            logMessageData_Max20B.setMimeType(mimeType);
            logMessageData_Max20B.setContent(content);
            logMessageData_Max20B.setContentSize(size);

            logMessageData_Max20B.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max20B.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max20B.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_01().add(logMessageData_Max20B);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_40B) {

            LogMessageData_Partition_02 logMessageData_Max40B = new LogMessageData_Partition_02();
            logMessageData_Max40B.setLabel(label);
            logMessageData_Max40B.setMimeType(mimeType);
            logMessageData_Max40B.setContent(content);
            logMessageData_Max40B.setContentSize(size);

            logMessageData_Max40B.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max40B.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max40B.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_02().add(logMessageData_Max40B);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_60B) {

            LogMessageData_Partition_03 logMessageData_Max60B = new LogMessageData_Partition_03();
            logMessageData_Max60B.setLabel(label);
            logMessageData_Max60B.setMimeType(mimeType);
            logMessageData_Max60B.setContent(content);
            logMessageData_Max60B.setContentSize(size);

            logMessageData_Max60B.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max60B.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max60B.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_03().add(logMessageData_Max60B);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_80B) {

            LogMessageData_Partition_04 logMessageData_Max80B = new LogMessageData_Partition_04();
            logMessageData_Max80B.setLabel(label);
            logMessageData_Max80B.setMimeType(mimeType);
            logMessageData_Max80B.setContent(content);
            logMessageData_Max80B.setContentSize(size);

            logMessageData_Max80B.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max80B.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max80B.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_04().add(logMessageData_Max80B);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_100B) {

            LogMessageData_Partition_05 logMessageData_Max100B = new LogMessageData_Partition_05();
            logMessageData_Max100B.setLabel(label);
            logMessageData_Max100B.setMimeType(mimeType);
            logMessageData_Max100B.setContent(content);
            logMessageData_Max100B.setContentSize(size);

            logMessageData_Max100B.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max100B.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max100B.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_05().add(logMessageData_Max100B);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_150B) {

            LogMessageData_Partition_06 logMessageData_Max150B = new LogMessageData_Partition_06();
            logMessageData_Max150B.setLabel(label);
            logMessageData_Max150B.setMimeType(mimeType);
            logMessageData_Max150B.setContent(content);
            logMessageData_Max150B.setContentSize(size);

            logMessageData_Max150B.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max150B.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max150B.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_06().add(logMessageData_Max150B);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_200B) {

            LogMessageData_Partition_07 logMessageData_Max200B = new LogMessageData_Partition_07();
            logMessageData_Max200B.setLabel(label);
            logMessageData_Max200B.setMimeType(mimeType);
            logMessageData_Max200B.setContent(content);
            logMessageData_Max200B.setContentSize(size);

            logMessageData_Max200B.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max200B.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max200B.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_07().add(logMessageData_Max200B);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_255B) {

            LogMessageData_Partition_08 logMessageData_Max255B = new LogMessageData_Partition_08();
            logMessageData_Max255B.setLabel(label);
            logMessageData_Max255B.setMimeType(mimeType);
            logMessageData_Max255B.setContent(content);
            logMessageData_Max255B.setContentSize(size);

            logMessageData_Max255B.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max255B.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max255B.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_08().add(logMessageData_Max255B);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_64KB) {
            LogMessageData_Partition_09 logMessageData_Max64KB = new LogMessageData_Partition_09();
            logMessageData_Max64KB.setLabel(label);
            logMessageData_Max64KB.setMimeType(mimeType);
            logMessageData_Max64KB.setContent(content);
            logMessageData_Max64KB.setContentSize(size);

            logMessageData_Max64KB.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max64KB.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max64KB.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_09().add(logMessageData_Max64KB);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_1MB) {
            LogMessageData_Partition_10 logMessageData_Max1MB = new LogMessageData_Partition_10();
            logMessageData_Max1MB.setLabel(label);
            logMessageData_Max1MB.setMimeType(mimeType);
            logMessageData_Max1MB.setContent(content);
            logMessageData_Max1MB.setContentSize(size);
            logMessageData_Max1MB.setSearchable(false);

            logMessageData_Max1MB.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max1MB.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max1MB.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_10().add(logMessageData_Max1MB);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_2MB) {
            LogMessageData_Partition_11 logMessageData_Max2MB = new LogMessageData_Partition_11();
            logMessageData_Max2MB.setLabel(label);
            logMessageData_Max2MB.setMimeType(mimeType);
            logMessageData_Max2MB.setContent(content);
            logMessageData_Max2MB.setContentSize(size);
            logMessageData_Max2MB.setSearchable(false);

            logMessageData_Max2MB.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max2MB.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max2MB.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_11().add(logMessageData_Max2MB);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_3MB) {
            LogMessageData_Partition_12 logMessageData_Max3MB = new LogMessageData_Partition_12();
            logMessageData_Max3MB.setLabel(label);
            logMessageData_Max3MB.setMimeType(mimeType);
            logMessageData_Max3MB.setContent(content);
            logMessageData_Max3MB.setContentSize(size);
            logMessageData_Max3MB.setSearchable(false);

            logMessageData_Max3MB.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max3MB.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max3MB.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_12().add(logMessageData_Max3MB);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_4MB) {
            LogMessageData_Partition_13 logMessageData_Max4MB = new LogMessageData_Partition_13();
            logMessageData_Max4MB.setLabel(label);
            logMessageData_Max4MB.setMimeType(mimeType);
            logMessageData_Max4MB.setContent(content);
            logMessageData_Max4MB.setContentSize(size);
            logMessageData_Max4MB.setSearchable(false);

            logMessageData_Max4MB.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max4MB.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max4MB.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_13().add(logMessageData_Max4MB);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_5MB) {
            LogMessageData_Partition_14 logMessageData_Max5MB = new LogMessageData_Partition_14();
            logMessageData_Max5MB.setLabel(label);
            logMessageData_Max5MB.setMimeType(mimeType);
            logMessageData_Max5MB.setContent(content);
            logMessageData_Max5MB.setContentSize(size);
            logMessageData_Max5MB.setSearchable(false);

            logMessageData_Max5MB.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max5MB.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max5MB.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_14().add(logMessageData_Max5MB);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_10MB) {
            LogMessageData_Partition_15 logMessageData_Max10MB = new LogMessageData_Partition_15();
            logMessageData_Max10MB.setLabel(label);
            logMessageData_Max10MB.setMimeType(mimeType);
            logMessageData_Max10MB.setContent(content);
            logMessageData_Max10MB.setContentSize(size);
            logMessageData_Max10MB.setSearchable(false);

            logMessageData_Max10MB.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max10MB.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max10MB.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_15().add(logMessageData_Max10MB);

        } else if (size <= DataBase.LOGMESSAGEDATA_CONTENT_MAX_SIZE_16MB) {
            LogMessageData_Partition_16 logMessageData_Max16MB = new LogMessageData_Partition_16();
            logMessageData_Max16MB.setLabel(label);
            logMessageData_Max16MB.setMimeType(mimeType);
            logMessageData_Max16MB.setContent(content);
            logMessageData_Max16MB.setContentSize(size);
            logMessageData_Max16MB.setSearchable(false);

            logMessageData_Max16MB.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max16MB.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max16MB.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_16().add(logMessageData_Max16MB);

        } else {
            LogMessageData_Partition_17 logMessageData_Max4GB = new LogMessageData_Partition_17();
            logMessageData_Max4GB.setLabel(label);
            logMessageData_Max4GB.setMimeType(mimeType);
            logMessageData_Max4GB.setContent(content);
            logMessageData_Max4GB.setContentSize(size);
            logMessageData_Max4GB.setSearchable(false);

            logMessageData_Max4GB.setUtcLocalTimeStamp(logMessage.getUtcLocalTimeStamp());
            logMessageData_Max4GB.setUtcServerTimeStamp(logMessage.getUtcServerTimeStamp());
            logMessageData_Max4GB.setLogMessage(logMessage);

            logMessage.getLogMessageData_Partition_17().add(logMessageData_Max4GB);

        }

        return logMessage;

    }

    private String XMLFormatter(String content) {
        String xml = content;
        try {
            xml = new String(Base64.base64Decode(content));
            xml = XMLUtils.XmlFormatter(xml);
        } catch (InvalidXML e1) {
            try {

                // Try to fix invalid characters
                xml = XMLUtils.cleanInvalidXmlChars(xml);
                xml = XMLUtils.XmlFormatter(xml);

            } catch (InvalidXML e2) {
                // Can't do mutch return decoded xml as is
            }
        }
        return xml;
    }

    private LogMessage buildTransactioMetaInfoEntity(Transaction transaction, LogMessage logMessage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
