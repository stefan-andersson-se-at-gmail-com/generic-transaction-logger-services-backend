/*
 * Copyright (C) 2016 Stefan Andersson
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

import com.erbjuder.logger.server.bean.EmailNotification;
import com.erbjuder.logger.server.bean.EmailService;
import com.erbjuder.logger.server.common.helper.MysqlConnection;
import com.erbjuder.logger.server.common.services.InternalTransactionHeader;
import com.erbjuder.logger.server.common.services.InternalTransactionHeaders;
import com.erbjuder.logger.server.common.services.PrepareStatementHelper;
import com.erbjuder.logger.server.common.services.PrimaryKeySequence;
import com.erbjuder.logger.server.common.services.ResultSetConverter;
import com.erbjuder.logger.server.common.services.ResultSetConverterEmailNotificationList;
import com.generic.global.transactionlogger.Response;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
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
public class EmailNotificationServiceBase {

    private static final String KEY_APPLICATION_NAME = "KEY_APPLICATION_NAME";
    private static final String KEY_FLOW_NAME = "KEY_FLOW_NAME";
    private static final String KEY_FLOW_POINT_NAME = "KEY_FLOW_POINT_NAME";
    private static final String KEY_TRANSACTION_REFERENCE_ID = "KEY_TRANSACTION_REFERENCE_ID";

    private static final Logger logger = Logger.getLogger(EmailNotificationServiceBase.class.getName());

    
    // DELETE THIS METHOD, WHEN LOGIC MOVED
    public void eMailNotification() {

        // Just if some is error    
        /*
                
                 ResultSetConverterEmailNotificationList converter = new ResultSetConverterEmailNotificationList();
            try (Connection connection = MysqlConnection.getConnectionWrite()) {

           
                
                 // Fetch everything in one call ( optimization )
                Map<String, Object> map = fetchAllConfigurationData(
                        KEY_APPLICATION_NAME,
                        KEY_FLOW_NAME,
                        KEY_FLOW_POINT_NAME,
                        KEY_TRANSACTION_REFERENCE_ID,
                        internalHeaders
                );
                
                // Fetch all application name configurations
                String applicationName = (String) map.get(KEY_APPLICATION_NAME);
                if (existApplicationNameConfigurations(connection, applicationName)) {
                    getApplicationNameConfigurations(connection, applicationName, converter);
                }

                // Fetch all flow name configurations
                Set<String> flowNames = (Set<String>) map.get(KEY_FLOW_NAME);
                if (existFlowNameConfigurations(connection, flowNames)) {
                    getFlowNameConfigurations(connection, flowNames, converter);
                }

                // Fetch all flow point name configurations
                Set<String> flowPointNames = (Set<String>) map.get(KEY_FLOW_POINT_NAME);
                if (existFlowPointNameConfigurations(connection, flowPointNames)) {
                    getFlowPointNameConfigurations(connection, flowPointNames, converter);
                }

                // Send iff got all needed data!
                if (applicationName != null && flowNames != null && flowPointNames != null) {

                    // Save just one instance to email adress
                    TreeMap<String, EmailNotification> emailNotifications = new TreeMap();
                    for (EmailNotification emailNotification : converter.getResult()) {

                        if (!emailNotifications.containsKey(emailNotification.getNotificationEmail())) {
                            emailNotifications.put(emailNotification.getNotificationEmail(), emailNotification);
                        }

                    }
                    
                    
                    // Update before sending"
                    updateEmailNotificationsAfterEmailSent(connection, emailNotifications.values());

                    // Close as fast as possible
                    connection.close();

                    // Sending email
                    String subject = "Log notification of [ " + applicationName + " ]";
                    String transactionReferenceId = (String) map.get(KEY_TRANSACTION_REFERENCE_ID);
                    String page = emailPageTemplateError();

                    for (Entry<String, EmailNotification> entry : emailNotifications.entrySet()) {

                        try {
                            
                             
                            emailService.sendEmail(
                                    entry.getKey(),
                                    subject,
                                    emailPageSubstituteError(
                                            page,
                                            applicationName,
                                            transactionReferenceId,
                                            entry.getValue()
                                    ));

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
                            InternetAddress[] address = {new InternetAddress(entry.getKey())};
                            msg.setFrom(new InternetAddress(from));// mailSession.getProperty("mail.from")));
                            msg.setRecipients(Message.RecipientType.TO, address);
                            msg.setSubject(subject);
                            msg.setSentDate(new Date());

                            // Set message content
                            msg.setContent(emailPageSubstituteError(
                                    page,
                                    applicationName,
                                    transactionReferenceId,
                                    entry.getValue()
                            ), "text/plain"); // text/html");

                            //Send the message
                            Transport.send(msg);
                            
                            
                            
                        } catch (Exception ex) {
                            // Do nothing! It's just an eMail
                        }
                    }

                }
                  
            } catch (SQLException sqlError) {
                logger.log(Level.SEVERE, sqlError.getMessage());
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage());
            }
         */
    }

    public Response persist(Set<EmailNotification> emailNotifications) throws SQLException {

        // Default
        Response response = new Response();
        response.setReturn(true);

        // Check iff email configuratione exist(s) 
        emailNotifications = filterOutExistingEmailNotifications(emailNotifications);
        if (!emailNotifications.isEmpty()) {

            try (Connection conn = MysqlConnection.getConnectionWrite()) {

                // Fetch primary keys
                PrimaryKeySequence primaryKeySequence = fetchPrimaryKeySequence(conn, emailNotifications);
                long PK = primaryKeySequence.getStartPK();
                try (PreparedStatement stmt = conn.prepareStatement(getEmailNotificationPrepareStatementMysql_Insert())) {

                    for (EmailNotification emailNotification : emailNotifications) {

                        // Prepare statement
                        stmt.setLong(1, PK);
                        stmt.setString(2, emailNotification.getNotificationEmail());
                        stmt.setString(3, emailNotification.getApplicationName());
                        stmt.setString(4, emailNotification.getFlowName());
                        stmt.setString(5, emailNotification.getFlowPointName());
                        stmt.setDate(6, new java.sql.Date(System.currentTimeMillis()));
                        stmt.setDate(7, new java.sql.Date(System.currentTimeMillis()));
                        stmt.setInt(8, 0);

                        stmt.execute();
                        PK = primaryKeySequence.next();
                    }

                    if (stmt != null) {
                        stmt.close();
                    }

                    conn.close();

                }

            } catch (SQLException sqlError) {
                logger.log(Level.SEVERE, sqlError.getMessage());
                response.setReturn(false);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage());
                response.setReturn(false);
            }
        }
        return response;
    }

    public ResultSetConverter view(
            Long id,
            List<String> notificationEmails,
            List<String> applicationNames,
            List<String> flowNames,
            List<String> flowPointNames,
            Integer page,
            Integer pageSize,
            ResultSetConverter converter
    ) {

        try (Connection conn = MysqlConnection.getConnectionWrite()) {

            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("SELECT ");
            prepareStatement.append("ID, ");
            prepareStatement.append("NOTIFICATIONEMAIL, ");
            prepareStatement.append("APPLICATIONNAME, ");
            prepareStatement.append("FLOWNAME, ");
            prepareStatement.append("FLOWPOINTNAME, ");
            prepareStatement.append("REGISTRATIONDATE, ");
            prepareStatement.append("NOTIFICATIONSENTDATE, ");
            prepareStatement.append("NOTIFICATIONCOUNTER ");
            prepareStatement.append("FROM EmailNotificationConfiguration ");

            if (id != null
                    || notificationEmails != null
                    || applicationNames != null
                    || flowNames != null
                    || flowPointNames != null
                    || !notificationEmails.isEmpty()
                    || !applicationNames.isEmpty()
                    || !flowNames.isEmpty()
                    || !flowPointNames.isEmpty()) {

                prepareStatement.append("WHERE ");

                if (notificationEmails != null && !notificationEmails.isEmpty()) {
                    prepareStatement.append("NOTIFICATIONEMAIL IN ");
                    prepareStatement.append(PrepareStatementHelper.toSQLList(notificationEmails)).append(" ");
                }

                if (applicationNames != null && !applicationNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("APPLICATIONNAME IN ");
                    prepareStatement.append(PrepareStatementHelper.toSQLList(applicationNames)).append(" ");
                }

                if (flowNames != null && !flowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME IN ");
                    prepareStatement.append(PrepareStatementHelper.toSQLList(flowNames)).append(" ");
                }

                if (flowPointNames != null && !flowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME IN ");
                    prepareStatement.append(PrepareStatementHelper.toSQLList(flowPointNames)).append(" ");
                }

            }

            // Pagination: Assume that first page <==> 1
            int pageOffset = page - 1;
            if (pageOffset < 0) {
                pageOffset = 0;
            } else {
                pageOffset = pageOffset * pageSize;
            }
            prepareStatement.append("LIMIT ").append(pageSize).append(" OFFSET ").append(pageOffset).append(" ");

            // System.err.println(prepareStatement.toString());
            CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
            ResultSet rs = stmt.executeQuery();
            converter.convert(rs);
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            return converter;
        } catch (Exception e) {
            e.printStackTrace();
            return converter;

        }

        return converter;

    }

    public Response remove(EmailNotification emailNotification) {

        // Default
        Response response = new Response();
        response.setReturn(true);

        try (Connection conn = MysqlConnection.getConnectionWrite()) {

            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("DELETE FROM EmailNotificationConfiguration WHERE ");

            if (emailNotification.getId() != null) {
                prepareStatement.append("ID = ").append(emailNotification.getId()).append(" ");

            }

            if (emailNotification.getNotificationEmail() != null && !emailNotification.getNotificationEmail().isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append("NOTIFICATIONEMAIL = ");
                prepareStatement.append(PrepareStatementHelper.toSQLValue(emailNotification.getNotificationEmail())).append(" ");
            }

            CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
            stmt.executeUpdate();

            stmt.close();
            conn.close();

        } catch (SQLException sqlError) {
            logger.log(Level.SEVERE, sqlError.getMessage());
            response.setReturn(false);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            response.setReturn(false);
        }

        return response;
    }

    public ResultSetConverter removeAndReplyEmailConfigurations(
            EmailNotification emailNotification,
            Integer page,
            Integer pageSize,
            ResultSetConverter converter) {

        try {

            if (this.remove(emailNotification).isReturn()) {

                List<String> notificationEmails = new ArrayList();
                notificationEmails.add(emailNotification.getNotificationEmail());
                this.view(null,
                        notificationEmails,
                        null,
                        null,
                        null,
                        page,
                        pageSize,
                        converter
                );
            };

        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }

        return converter;
    }

    public void update(Set<EmailNotification> emailNotifications) {

        try (Connection conn = MysqlConnection.getConnectionWrite()) {

            CallableStatement stmt = null;

            for (EmailNotification emailNotification : emailNotifications) {

                if (emailNotification.getApplicationName().isEmpty()
                        && (emailNotification.getFlowName().isEmpty()
                        || emailNotification.getFlowPointName().isEmpty())) {

                    // continue with next in list ( Invalid combination )   
                    continue;
                }

                StringBuilder prepareStatement = new StringBuilder();
                prepareStatement.append("UPDATE EmailNotificationConfiguration SET ");
                prepareStatement.append("NOTIFICATIONEMAIL = ").append(emailNotification.getNotificationEmail());
                prepareStatement.append("APPLICATIONNAME = ").append(emailNotification.getApplicationName());
                prepareStatement.append("FLOWNAME = ").append(emailNotification.getFlowName());
                prepareStatement.append("FLOWPOINTNAME = ").append(emailNotification.getFlowPointName());
                prepareStatement.append("NOTIFICATIONSENTDATE = ").append(new java.sql.Date(System.currentTimeMillis()));
                prepareStatement.append("NOTIFICATIONCOUNTER = ").append(0);
                prepareStatement.append("WHERE ID = ").append(emailNotification.getId());

                stmt = conn.prepareCall(prepareStatement.toString());
                stmt.executeUpdate();

            }

            if (stmt != null) {
                stmt.close();
            }

            conn.close();

        } catch (SQLException sqlError) {
            logger.log(Level.SEVERE, sqlError.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }

    private String getPrimaryKeySequencePrepareStatement_Fetch() {
        return "{? = call notification_seq_generator_fetch(?,?)}";
    }

    private String getEmailNotificationPrepareStatementMysql_Insert() {
        String emailNotificationPrepareStatement
                = "INSERT INTO EmailNotificationConfiguration ("
                + "ID, "
                + "NOTIFICATIONEMAIL, "
                + "APPLICATIONNAME, "
                + "FLOWNAME, "
                + "FLOWPOINTNAME, "
                + "REGISTRATIONDATE, "
                + "NOTIFICATIONSENTDATE, "
                + "NOTIFICATIONCOUNTER) "
                + "values (?, ?, ?, ?, ?, ?, ?, ? )";
        return emailNotificationPrepareStatement;
    }

    private Set<EmailNotification> filterOutExistingEmailNotifications(Set<EmailNotification> emailNotifications) {

        // Check iff email configuratione exist(s) 
        Set<EmailNotification> createTheseEmailNotifications = new HashSet();
        for (EmailNotification emailNotification : emailNotifications) {

            if (emailNotification.getApplicationName().isEmpty()
                    && emailNotification.getFlowName().isEmpty()
                    && emailNotification.getFlowPointName().isEmpty()) {

                // continue with next in list ( Invalid combination )   
                continue;
            }

            if (!existEmailNotificationConfiguration(emailNotification)) {
                createTheseEmailNotifications.add(emailNotification);
            }
        }

        return createTheseEmailNotifications;
    }

    private PrimaryKeySequence fetchPrimaryKeySequence(
            Connection conn,
            Set<EmailNotification> emailNotifications
    ) throws SQLException {

        // Fetch primary keys
        int numOfPrimaryKeys = emailNotifications.size();
        String primaryKeySequencePrepareStatementString = getPrimaryKeySequencePrepareStatement_Fetch();
        CallableStatement primaryKeySequencePrepareStatement = conn.prepareCall(primaryKeySequencePrepareStatementString);
        primaryKeySequencePrepareStatement.registerOutParameter(1, java.sql.Types.BIGINT);
        primaryKeySequencePrepareStatement.setString(2, "seq_gen_1");
        primaryKeySequencePrepareStatement.setInt(3, numOfPrimaryKeys);
        primaryKeySequencePrepareStatement.execute();
        Long endPK = primaryKeySequencePrepareStatement.getLong(1);
        Long startPK = endPK - numOfPrimaryKeys;
        return new PrimaryKeySequence(startPK, endPK);

    }

    private String emailPageSubstituteError(
            String templatePage,
            String applicationName,
            String transactionReferenceId,
            EmailNotification emailNotification) {

        templatePage = templatePage.replaceFirst("\\[APPLICATION_NAME\\]", applicationName);
        templatePage = templatePage.replaceFirst("\\[VIEW_LINK\\]", emailPageViewLink(transactionReferenceId));
        templatePage = templatePage.replaceFirst("\\[REMOVE_LINK\\]", emailPageRemoveLink(emailNotification));
        return templatePage;
    }

    private String emailPageViewLink(String transactionReferenceId) {

        // Create link
        String loggerHost = "http://erbjuder.com";
        String loggerHostPort = "";
        String loggerHostContextRoot = "/log_message_services_client_js_dev";
        String loggMessageViewPage = "/index.html?";
//         fromDate=2017-01-10 14:11:16.828000&toDate=2017-01-11 04:11:16.832999&page=1&pageSize=200
        String loggMessageServiceTransactionReferenceId = "transactionReferenceId=";
        StringBuilder builder = new StringBuilder();
        builder.append(loggerHost);
        builder.append(loggerHostPort);
        builder.append(loggerHostContextRoot);
        builder.append(loggMessageViewPage);
        builder.append("fromDate").append(transactionReferenceId).append("&");
        builder.append("toDate").append(transactionReferenceId).append("&");
        builder.append("page").append("1").append("&");
        builder.append("pageSize").append("200").append("&");
        builder.append(loggMessageServiceTransactionReferenceId).append(transactionReferenceId).append("&");
        builder.append("page=").append("1").append('&');
        builder.append("pageSize=").append("200").append('&');
        return builder.toString();
    }

    private String emailPageRemoveLink(EmailNotification emailNotification) {

        StringBuilder builder = new StringBuilder();
        builder.append("http://erbjuder.com/log_message_services_client_notification_js_dev/delete.html?");
        builder.append("id=").append(emailNotification.getId()).append('&');
        builder.append("notificationEmail=").append(emailNotification.getNotificationEmail()).append('&');
        builder.append("page=").append("1").append('&');
        builder.append("pageSize=").append("200").append('&');
        return builder.toString();
    }

    private String emailPageTemplateError() {

        StringBuilder builder = new StringBuilder();
        /*builder.append("<html><head><meta http-equiv='content-type' content='text/html; charset=utf-8'/>");
        builder.append("<title></title></head><body lang='en-US' dir='ltr'><br/>");
        builder.append("You are registered as notifier when error(s) occurs at : ");
        builder.append("[APPLICATION_NAME].<br/><br/>");
        builder.append("Follow provided link to view error messages(s) : ");
        builder.append("[VIEW_LINK].<br/><br/>");
        builder.append("To unregister yourself! [REMOVE_LINK].</p>");
        builder.append("<p style='margin-bottom: 0in'></p></body></html>");*/

        builder.append("You are registered as notifier when error(s) occurs at : ");
        builder.append("[ [APPLICATION_NAME] ].\n\n");
        builder.append("Follow provided link to view error messages(s) : ");
        builder.append("[VIEW_LINK].\n\n");
        builder.append("To unregister yourself! [REMOVE_LINK].\n\n");

        return builder.toString();

    }

    private String emailPageTemplatePersist() {

        StringBuilder builder = new StringBuilder();
        builder.append("<html><head><meta http-equiv='content-type' content='text/html; charset=utf-8'/>");
        builder.append("<title></title></head><body lang='en-US' dir='ltr'><br/>");
        builder.append("You is registered as a notifier when error(s) occurs in : ");
        builder.append("[APPLICATION_NAME].<br/><br/>");
        builder.append("Follow provided link to view error messages(s) : ");
        builder.append("[VIEW_LINK].<br/><br/>");
        builder.append("To unregister yourself! [REMOVE_LINK].</p>");
        builder.append("<p style='margin-bottom: 0in'></p></body></html>");

        return builder.toString();

    }

    private String emailPageTemplateDelete() {

        StringBuilder builder = new StringBuilder();
        builder.append("<html><head><meta http-equiv='content-type' content='text/html; charset=utf-8'/>");
        builder.append("<title></title></head><body lang='en-US' dir='ltr'><br/>");
        builder.append("You is registered as a notifier when error(s) occurs in : ");
        builder.append("[APPLICATION_NAME].<br/><br/>");
        builder.append("Follow provided link to view error messages(s) : ");
        builder.append("[VIEW_LINK].<br/><br/>");
        builder.append("To unregister yourself! [REMOVE_LINK].</p>");
        builder.append("<p style='margin-bottom: 0in'></p></body></html>");

        return builder.toString();

    }

    private Map<String, Object> fetchAllConfigurationData(
            String key_application_name,
            String key_flow_name,
            String key_flow_point_name,
            String key_transaction_reference_id,
            InternalTransactionHeaders internalHeaders
    ) {

        String applicationName = "";
        String transactionReferenceId = "";
        Set<String> flowNames = new HashSet();
        Set<String> flowPointNames = new HashSet();
        Map<String, Object> output = new TreeMap();
        for (InternalTransactionHeader header : internalHeaders.getInternalTransactionHeaders()) {

            // Same for all
            applicationName = header.getApplicationName();

            // Same for all
            transactionReferenceId = header.getTransactionReferenceID();

            // A bunch 
            flowNames.add(header.getFlowName());

            // A bunch 
            flowPointNames.add(header.getFlowPointName());

        }

        // Bind
        output.put(key_transaction_reference_id, transactionReferenceId);
        output.put(key_application_name, applicationName);
        output.put(key_flow_name, flowNames);
        output.put(key_flow_point_name, flowPointNames);

        // return 
        return output;
    }

    private boolean existIdConfiguration(Connection conn, Long id) throws SQLException {

        boolean existConfiguration = false;

        // A real prepare statement later
        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT EXISTS( SELECT 1 FROM EmailNotificationConfiguration WHERE ID = ");
        prepareStatement.append(PrepareStatementHelper.toSQLValue(id.toString())).append(" LIMIT 1 ) ");

        CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
        ResultSet rs = stmt.executeQuery();

        // exist any result?
        rs.absolute(1);
        if (rs.getInt(1) == 1) {
            existConfiguration = true;
        }

        return existConfiguration;
    }

    private boolean existApplicationNameConfigurations(Connection conn, String applicationName) throws SQLException {

        boolean existConfiguration = false;

        // A real prepare statement later
        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT EXISTS( SELECT 1 FROM EmailNotificationConfiguration WHERE APPLICATIONNAME = ");
        prepareStatement.append(PrepareStatementHelper.toSQLValue(applicationName)).append(" LIMIT 1 ) ");

        CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
        ResultSet rs = stmt.executeQuery();

        // exist any result?
        rs.absolute(1);
        if (rs.getInt(1) == 1) {
            existConfiguration = true;
        }

        return existConfiguration;
    }

    private boolean existFlowNameConfigurations(Connection conn, Set<String> flowNames) throws SQLException {

        boolean existConfiguration = false;

        // A real prepare statement later
        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT EXISTS( SELECT 1 FROM EmailNotificationConfiguration WHERE FLOWNAME IN ");
        prepareStatement.append(PrepareStatementHelper.toSQLList(flowNames)).append(" LIMIT 1 ) ");

        CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
        ResultSet rs = stmt.executeQuery();

        // exist any result?
        rs.absolute(1);
        if (rs.getInt(1) == 1) {
            existConfiguration = true;
        }

        return existConfiguration;
    }

    private boolean existFlowPointNameConfigurations(Connection conn, Set<String> flowPointNames) throws SQLException {
        boolean existConfiguration = false;

        // A real prepare statement later
        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT EXISTS( SELECT 1 FROM EmailNotificationConfiguration WHERE FLOWPOINTNAME IN ");
        prepareStatement.append(PrepareStatementHelper.toSQLList(flowPointNames)).append(" LIMIT 1 ) ");

        CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
        ResultSet rs = stmt.executeQuery();

        // exist any result?
        rs.absolute(1);
        if (rs.getInt(1) == 1) {
            existConfiguration = true;
        }

        return existConfiguration;
    }

    private boolean existEmailNotificationConfiguration(
            EmailNotification emailNotification
    ) {

        boolean existConfiguration = false;
        try (Connection connection = MysqlConnection.getConnectionWrite()) {

            // A real prepare statement later
            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("SELECT EXISTS( SELECT 1 FROM EmailNotificationConfiguration ");
            prepareStatement.append("WHERE NOTIFICATIONEMAIL = ");
            prepareStatement.append(PrepareStatementHelper.toSQLValue(emailNotification.getNotificationEmail())).append(" AND ");
            prepareStatement.append("APPLICATIONNAME = ");
            prepareStatement.append(PrepareStatementHelper.toSQLValue(emailNotification.getApplicationName())).append(" AND ");
            prepareStatement.append("FLOWNAME = ");
            prepareStatement.append(PrepareStatementHelper.toSQLValue(emailNotification.getFlowName())).append(" AND ");
            prepareStatement.append("FLOWPOINTNAME = ");
            prepareStatement.append(PrepareStatementHelper.toSQLValue(emailNotification.getFlowPointName())).append(" LIMIT 1 ) ");

            CallableStatement stmt = connection.prepareCall(prepareStatement.toString());
            ResultSet rs = stmt.executeQuery();

            // exist any result?
            rs.absolute(1);
            if (rs.getInt(1) == 1) {
                existConfiguration = true;
            }

        } catch (SQLException sqlError) {
            logger.log(Level.SEVERE, sqlError.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }

        return existConfiguration;
    }

    private ResultSetConverter getApplicationNameConfigurations(
            Connection conn,
            String applicationName,
            ResultSetConverter converter
    ) throws SQLException, Exception {

        String prepareStatement
                = "SELECT "
                + "ID, "
                + "NOTIFICATIONEMAIL, "
                + "APPLICATIONNAME, "
                + "FLOWNAME, "
                + "FLOWPOINTNAME, "
                + "REGISTRATIONDATE, "
                + "NOTIFICATIONSENTDATE, "
                + "NOTIFICATIONCOUNTER "
                + "FROM EmailNotificationConfiguration "
                + "WHERE APPLICATIONNAME = ?";

        PreparedStatement stmt = conn.prepareStatement(prepareStatement);

        // Prepare statement
        stmt.setString(1, applicationName);
        ResultSet rs = stmt.executeQuery();
        converter.convert(rs);

        return converter;
    }

    private ResultSetConverter getFlowNameConfigurations(
            Connection conn,
            Set<String> flowNames,
            ResultSetConverter converter
    ) throws SQLException, Exception {

        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT ");
        prepareStatement.append("ID, ");
        prepareStatement.append("NOTIFICATIONEMAIL, ");
        prepareStatement.append("APPLICATIONNAME, ");
        prepareStatement.append("FLOWNAME, ");
        prepareStatement.append("FLOWPOINTNAME, ");
        prepareStatement.append("REGISTRATIONDATE, ");
        prepareStatement.append("NOTIFICATIONSENTDATE, ");
        prepareStatement.append("NOTIFICATIONCOUNTER ");
        prepareStatement.append("FROM EmailNotificationConfiguration ");
        prepareStatement.append("WHERE FLOWNAME IN ").append(PrepareStatementHelper.toSQLList(flowNames));

        CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
        ResultSet rs = stmt.executeQuery();
        converter.convert(rs);

        return converter;
    }

    private ResultSetConverter getFlowPointNameConfigurations(
            Connection conn,
            Set<String> flowPointNames,
            ResultSetConverter converter
    ) throws SQLException, Exception {

        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT ");
        prepareStatement.append("ID, ");
        prepareStatement.append("NOTIFICATIONEMAIL, ");
        prepareStatement.append("APPLICATIONNAME, ");
        prepareStatement.append("FLOWNAME, ");
        prepareStatement.append("FLOWPOINTNAME, ");
        prepareStatement.append("REGISTRATIONDATE, ");
        prepareStatement.append("NOTIFICATIONSENTDATE, ");
        prepareStatement.append("NOTIFICATIONCOUNTER ");
        prepareStatement.append("FROM EmailNotificationConfiguration ");
        prepareStatement.append("WHERE FLOWPOINTNAME IN ").append(PrepareStatementHelper.toSQLList(flowPointNames));

        // Prepare statement
        CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
        ResultSet rs = stmt.executeQuery();
        converter.convert(rs);

        return converter;
    }

    private void updateEmailNotificationsAfterEmailSent(
            Connection conn,
            Collection<EmailNotification> emailNotifications
    ) throws SQLException {

        for (EmailNotification emailNotification : emailNotifications) {

            /*
             prepareStatement.append("UPDATE EmailNotificationConfiguration SET ");
                prepareStatement.append("NOTIFICATIONSENTDATE = ").append(new java.sql.Date(System.currentTimeMillis()));
                prepareStatement.append("NOTIFICATIONCOUNTER = ").append(emailNotification.getNotificationCounter() + 1);
                prepareStatement.append("WHERE ID = ").append(emailNotification.getId());
             */
            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("UPDATE EmailNotificationConfiguration SET ");
            prepareStatement.append("NOTIFICATIONSENTDATE = ? ,");
            prepareStatement.append("NOTIFICATIONCOUNTER = ? ");
            prepareStatement.append("WHERE ID = ? ");

            PreparedStatement stmt = conn.prepareStatement(prepareStatement.toString());
            stmt.setDate(1, new java.sql.Date(System.currentTimeMillis()));
            stmt.setInt(2, emailNotification.getNotificationCounter() + 1);
            stmt.setLong(3, emailNotification.getId());

            stmt.executeUpdate();

        }

    }

}
