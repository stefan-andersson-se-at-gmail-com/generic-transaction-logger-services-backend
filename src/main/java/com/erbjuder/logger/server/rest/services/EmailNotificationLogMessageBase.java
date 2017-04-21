/*
 * Copyright (C) 2017 Stefan Andersson
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
package com.erbjuder.logger.server.rest.services;

import com.erbjuder.logger.server.bean.EmailNotificationLogMessage;
import com.erbjuder.logger.server.common.helper.MysqlConnection;
import com.erbjuder.logger.server.common.services.InternalTransactionHeader;
import com.erbjuder.logger.server.common.services.InternalTransactionHeaders;
import com.erbjuder.logger.server.common.services.PrimaryKeySequence;
import com.erbjuder.logger.server.common.services.ResultSetConverter;
import com.erbjuder.logger.server.common.services.ResultSetConverterEmailNotificationList;
import com.erbjuder.logger.server.common.services.ResultSetConverterEmailNotificationPrepareToSendList;
import com.generic.global.transactionlogger.Response;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Stefan Andersson
 */
@Stateless
public class EmailNotificationLogMessageBase {

    @EJB
    EmailNotificationServiceBase emailNotificationServiceBase;

    @Resource(lookup = "mail/Session")
    private Session mailSession;

    private static final Logger logger = Logger.getLogger(EmailNotificationLogMessageBase.class.getName());

    public void sendPreparedNotifications() {

        int page = 1;
        int pageSize = 200;
        boolean done = false;
        try (Connection conn = MysqlConnection.getConnectionWrite()) {

            // 1. Fetch all logMessages that shouild be sent    
            // 2. Send Email based on logMessages
            // 3. Mark logMessages as sent
            ResultSetConverterEmailNotificationPrepareToSendList converter = new ResultSetConverterEmailNotificationPrepareToSendList();
            fetchPreparedNotifications(conn, page, pageSize, converter);

            if (converter.getResult().size() > 0) {
                sendPreparedNotifications(conn, converter.getResult());

                // Still more to do?
                while (!done) {

                    if (converter.getResult().isEmpty()) {
                        done = true;

                    } else {

                        // page += 1;
                        converter.getResult().clear();
                        fetchPreparedNotifications(conn, page, pageSize, converter);
                        if (converter.getResult().size() > 0) {
                            sendPreparedNotifications(conn, converter.getResult());

                        } 
                    }
                }
            }

            // Close
            conn.commit();
            conn.close();

        } catch (SQLException sqlError) {
            logger.log(Level.SEVERE, sqlError.getMessage());

        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());

        }

    }

    private ResultSetConverter fetchPreparedNotifications(
            Connection conn,
            int page,
            int pageSize,
            ResultSetConverter converter) throws SQLException, Exception {

        int pageOffset = page - 1;
        if (pageOffset < 0) {
            pageOffset = 0;
        } else {
            pageOffset = pageOffset * pageSize;
        }

        PreparedStatement stmt = conn.prepareStatement(getPrepareToSendStatementMysql_Select());
        stmt.setInt(1, pageSize);
        stmt.setInt(2, pageOffset);
        ResultSet rs = stmt.executeQuery();
        converter.convert(rs);

        return converter;
    }

    private void sendPreparedNotifications(
            Connection conn,
            List<EmailNotificationLogMessage> emailNotificationLogMessages) throws SQLException, Exception {

        // Make sure we have some thing to send and match
        if (!emailNotificationLogMessages.isEmpty()) {

            // Fetch everything in one call ( optimization )
            EmailNotificationLogMessagePreProcessor sendPreProcessor = new EmailNotificationLogMessagePreProcessor();
            sendPreProcessor.process(emailNotificationLogMessages);

            // Fetch all application name configurations
            ResultSetConverterEmailNotificationList emailConverter = new ResultSetConverterEmailNotificationList();
            Set<String> applicationNames = sendPreProcessor.getApplicationNames();
            if (!applicationNames.isEmpty()) {
                emailNotificationServiceBase.getApplicationNameConfigurations(
                        conn,
                        applicationNames,
                        emailConverter
                );

            }

            // Fetch all flow name configurations
            Set<String> flowNames = sendPreProcessor.getFlowNames();
            if (!flowNames.isEmpty()) {
                emailNotificationServiceBase.getFlowNameConfigurations(
                        conn,
                        flowNames,
                        new EmailNotificationPreProcessor().getIdStringList(emailConverter.getResult()),
                        emailConverter
                );
            }

            // Fetch all flow point name configurations
            Set<String> flowPointNames = sendPreProcessor.getFlowPointNames();
            if (!flowPointNames.isEmpty()) {
                emailNotificationServiceBase.getFlowPointNameConfigurations(
                        conn,
                        flowPointNames,
                        new EmailNotificationPreProcessor().getIdStringList(emailConverter.getResult()),
                        emailConverter
                );
            }

            // Matching, Remove logMessages and Update EmailNotificationConfig
            EmailNotificationPreProcessor notificationPreProcessor = new EmailNotificationPreProcessor();
            notificationPreProcessor.process(emailConverter.getResult(), emailNotificationLogMessages);
            removePreparedNotifications(conn, emailNotificationLogMessages);
            emailNotificationServiceBase.updateEmailNotificationsAfterEmailSent(
                    conn,
                    notificationPreProcessor.getEmailNotifications2Update()
            );

            // Create complext eMail
            EmailNotificationUtil emailNotificationUtil = new EmailNotificationUtil();
            for (Entry<String, List<EmailNotificationBinding>> treeEntry : notificationPreProcessor.getBindingStorage().entrySet()) {

                // Select template 
                String eMail = treeEntry.getKey();
                StringBuilder builder = new StringBuilder();
                builder.append(emailNotificationUtil.emailPageTemplateError_Header());
                List<EmailNotificationBinding> emailNotificationBindings = treeEntry.getValue();
                for (EmailNotificationBinding emailNotificationBinding : emailNotificationBindings) {

                    String templateBody = emailNotificationUtil.emailPageTemplateError_Body();
                    templateBody = emailNotificationUtil.emailPageSubstitute_Body(
                            templateBody,
                            emailNotificationBinding.getEmailNotification(),
                            emailNotificationBinding.getMinTime());

                    builder.append(templateBody);
                }

                 // Send email 
                String subject = "Log notification(s)";

                try {

                    String from = "transaction.logger@gmail.com";
                    String host = "mailout.comhem.se";

                    // Create properties, get Session
                    Properties props = mailSession.getProperties();
                    props.clear();
                    props.put("mail.debug", "true");
                    props.put("mail.smtp.host", host);
                    props.put("mail.smtp.port", "25");
                    Session session = mailSession; // Session.getInstance(props);

                    // Instantiate a message
                    Message msg = new MimeMessage(session);

                    //Set message attributes
                    InternetAddress[] address = {new InternetAddress(eMail)};
                    msg.setFrom(new InternetAddress(from));// mailSession.getProperty("mail.from")));
                    msg.setRecipients(Message.RecipientType.TO, address);
                    msg.setSubject(subject);
                    msg.setSentDate(new Date());

                    // Set message content
                    msg.setContent(builder.toString(), "text/plain"); // text/html");

                    //Send the message
                    Transport.send(msg);

                } catch (Exception ex) {
                    // Do nothing! It's just an eMail
                    System.err.println(ex.toString());
                }
            }
            // Clear rule data
            sendPreProcessor.clear();
            notificationPreProcessor.clear();

        }

    }

    private void removePreparedNotifications(
            Connection conn,
            List<EmailNotificationLogMessage> prepareToSendList) throws SQLException, Exception {

        // PrepareToSendList will not be more that pageSize
        conn.setAutoCommit(false);
        PreparedStatement stmt = conn.prepareStatement(getPrepareToSendStatementMysql_Delete());
        for (EmailNotificationLogMessage header : prepareToSendList) {
            stmt.setLong(1, header.getId());
            /*stmt.setString(1, header.getApplicationName());
            stmt.setString(2, header.getFlowName());
            stmt.setString(3, header.getFlowPointName());*/
            stmt.addBatch();

        }

        stmt.executeBatch();
        stmt.clearBatch();
    }

    public Response persist(InternalTransactionHeaders internalHeaders) {

        // Default
        Response response = new Response();
        response.setReturn(true);

        // Insert everything, when sending eMial, remove duplicate! 
        try (Connection connection = MysqlConnection.getConnectionWrite()) {

            // Fetch primary keys
            PrimaryKeySequence primaryKeySequence = fetchPrimaryKeySequence(connection, internalHeaders.getInternalTransactionHeaders().size());
            long PK = primaryKeySequence.getStartPK();
            try (PreparedStatement preparedStatement = connection.prepareStatement(getPrepareToSendStatementMysql_Insert())) {

                // Prepare statement
                for (InternalTransactionHeader header : internalHeaders.getInternalTransactionHeaders()) {

                    preparedStatement.setLong(1, PK);
                    preparedStatement.setLong(2, header.getPrimaryKey());
                    preparedStatement.setInt(3, header.getPartitionId());
                    preparedStatement.setString(4, header.getApplicationName());
                    preparedStatement.setDate(5, header.getExpiredDate());
                    preparedStatement.setString(6, header.getFlowName());
                    preparedStatement.setString(7, header.getFlowPointName());
                    preparedStatement.setBoolean(8, header.getIsError());
                    preparedStatement.setString(9, header.getTransactionReferenceID());
                    preparedStatement.setTimestamp(10, header.getUtcClientTimestamp());
                    preparedStatement.setTimestamp(11, header.getUtcServerTimestamp());
                    preparedStatement.setBoolean(12, false);

                    preparedStatement.execute();

                    PK = primaryKeySequence.next();
                }
            }

        } catch (SQLException sqlError) {
            logger.log(Level.SEVERE, sqlError.getMessage());
            response.setReturn(false);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            response.setReturn(false);
        }

        return response;
    }

    private PrimaryKeySequence fetchPrimaryKeySequence(
            Connection connection,
            int numberOfKeys
    ) throws SQLException {

        // Fetch primary keys
        int numOfPrimaryKeys = numberOfKeys;
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

    private String getPrimaryKeySequencePrepareStatement_Fetch() {
        return "{? = call notification_seq_generator_fetch(?,?)}";
    }

    private String getPrepareToSendStatementMysql_Delete() {
        String emailNotificationPrepareStatement
                = "DELETE FROM EmailNotificationPrepareToSend WHERE ID = ?";

        /* String emailNotificationPrepareStatement
                = "DELETE FROM EmailNotificationPrepareToSend WHERE APPLICATIONNAME = ? FLOWNAME = ? FLOWPOINTNAME = ?"; */
        return emailNotificationPrepareStatement;
    }

    private String getPrepareToSendStatementMysql_Insert() {
        String emailNotificationPrepareStatement
                = "INSERT INTO EmailNotificationPrepareToSend ("
                + "ID, "
                + "LOGMESSAGE_ID, "
                + "PARTITION_ID, "
                + "APPLICATIONNAME, "
                + "EXPIREDDATE, "
                + "FLOWNAME, "
                + "FLOWPOINTNAME, "
                + "ISERROR, "
                + "TRANSACTIONREFERENCEID, "
                + "UTCLOCALTIMESTAMP, "
                + "UTCSERVERTIMESTAMP,"
                + "SENT ) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        return emailNotificationPrepareStatement;

    }

    private String getPrepareToSendStatementMysql_Select() {
        String emailNotificationPrepareStatement
                = "SELECT "
                + "ID, "
                + "LOGMESSAGE_ID, "
                + "PARTITION_ID, "
                + "APPLICATIONNAME, "
                + "EXPIREDDATE, "
                + "FLOWNAME, "
                + "FLOWPOINTNAME, "
                + "ISERROR, "
                + "TRANSACTIONREFERENCEID, "
                + "UTCLOCALTIMESTAMP, "
                + "UTCSERVERTIMESTAMP,"
                + "SENT "
                + "FROM EmailNotificationPrepareToSend WHERE SENT = 0 LIMIT ? OFFSET ?";

        /* String emailNotificationPrepareStatement
                = "SELECT "
                + "APPLICATIONNAME, "
                + "FLOWNAME, "
                + "FLOWPOINTNAME "
                + "FROM EmailNotificationPrepareToSend group by APPLICATIONNAME, FLOWNAME, FLOWPOINTNAME LIMIT ? OFFSET ?"; */
        return emailNotificationPrepareStatement;
    }

}
