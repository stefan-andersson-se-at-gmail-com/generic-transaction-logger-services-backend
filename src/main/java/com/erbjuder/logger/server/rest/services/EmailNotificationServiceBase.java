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
import com.erbjuder.logger.server.common.helper.MysqlConnection;
import com.erbjuder.logger.server.common.helper.TimeStampUtils;
import com.erbjuder.logger.server.common.services.InternalTransactionHeader;
import com.erbjuder.logger.server.common.services.InternalTransactionHeaders;
import com.erbjuder.logger.server.common.services.PrepareStatementHelper;
import com.erbjuder.logger.server.common.services.PrimaryKeySequence;
import com.erbjuder.logger.server.common.services.ResultSetConverter;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.ejb.Stateless;

/**
 *
 * @author Stefan Andersson
 */
@Stateless
public class EmailNotificationServiceBase {

    private static final Logger logger = Logger.getLogger(EmailNotificationServiceBase.class.getName());

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
                        stmt.setTimestamp(7, TimeStampUtils.createSystemNanoTimeStamp());
                        stmt.setInt(8, 0);
                        stmt.setInt(9, emailNotification.getMaxNotifications());
                        stmt.setString(10, emailNotification.getMaxNotificationsUnit());

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

                             
    public ResultSetConverter persistAndReplyEmailConfigurations(
            EmailNotification emailNotification,
            Integer page,
            Integer pageSize,
            ResultSetConverter converter) {

        try {
            
            Set<EmailNotification> emailNotificationSet = new HashSet();
            emailNotificationSet.add(emailNotification);
            if (this.persist(emailNotificationSet).isReturn()) {

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
            prepareStatement.append("NOTIFICATIONCOUNTER, ");
            prepareStatement.append("MAXNOTIFICATIONS, ");
            prepareStatement.append("MAXNOTIFICATIONSUNIT ");
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
                prepareStatement.append("MAXNOTIFICATIONS = ").append(emailNotification.getMaxNotifications());
                prepareStatement.append("MAXNOTIFICATIONSUNIT = ").append(emailNotification.getMaxNotificationsUnit());

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
                + "NOTIFICATIONCOUNTER, "
                + "MAXNOTIFICATIONS, "
                + "MAXNOTIFICATIONSUNIT ) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
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

    public boolean existApplicationNameConfigurations(Connection conn, Set<String> applicationNames) throws SQLException {

        boolean existConfiguration = false;

        // A real prepare statement later
        // Add chunking later
        if (!applicationNames.isEmpty() && applicationNames.size() <= PrepareStatementHelper.LIST_MAX_SIZE) {

            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("SELECT EXISTS( SELECT 1 FROM EmailNotificationConfiguration WHERE APPLICATIONNAME IN ");
            prepareStatement.append(PrepareStatementHelper.toSQLList(applicationNames)).append(" LIMIT 1 ) ");

            CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
            ResultSet rs = stmt.executeQuery();

            // exist any result?
            rs.absolute(1);
            if (rs.getInt(1) == 1) {
                existConfiguration = true;
            }
        }

        return existConfiguration;
    }

    public boolean existFlowNameConfigurations(Connection conn, Set<String> flowNames) throws SQLException {

        boolean existConfiguration = false;

        // A real prepare statement later
        // Add chunking later
        if (!flowNames.isEmpty() && flowNames.size() <= PrepareStatementHelper.LIST_MAX_SIZE) {

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
        }
        return existConfiguration;
    }

    public boolean existFlowPointNameConfigurations(Connection conn, Set<String> flowPointNames) throws SQLException {
        boolean existConfiguration = false;

        // A real prepare statement later
        // Add chunking later
        if (!flowPointNames.isEmpty() && flowPointNames.size() <= PrepareStatementHelper.LIST_MAX_SIZE) {

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
        }

        return existConfiguration;
    }

    public boolean existEmailNotificationConfiguration(
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

    public ResultSetConverter getApplicationNameConfigurations(
            Connection conn,
            Set<String> applicationNames,
            ResultSetConverter converter
    ) throws SQLException, Exception {

        // Add chunking later
        if (!applicationNames.isEmpty() && applicationNames.size() <= PrepareStatementHelper.LIST_MAX_SIZE) {

            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("SELECT ");
            prepareStatement.append("ID, ");
            prepareStatement.append("NOTIFICATIONEMAIL, ");
            prepareStatement.append("APPLICATIONNAME, ");
            prepareStatement.append("FLOWNAME, ");
            prepareStatement.append("FLOWPOINTNAME, ");
            prepareStatement.append("REGISTRATIONDATE, ");
            prepareStatement.append("NOTIFICATIONSENTDATE, ");
            prepareStatement.append("NOTIFICATIONCOUNTER, ");
            prepareStatement.append("MAXNOTIFICATIONS, ");
            prepareStatement.append("MAXNOTIFICATIONSUNIT ");
            prepareStatement.append("FROM EmailNotificationConfiguration ");
            prepareStatement.append("WHERE APPLICATIONNAME IN ").append(PrepareStatementHelper.toSQLList(applicationNames));

            CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
            ResultSet rs = stmt.executeQuery();
            converter.convert(rs);
        }

        return converter;
    }

    public ResultSetConverter getFlowNameConfigurations(
            Connection conn,
            Set<String> flowNames,
            List<String> notTheseIds,
            ResultSetConverter converter
    ) throws SQLException, Exception {

        // Add chunking later
        if (!flowNames.isEmpty() && flowNames.size() <= PrepareStatementHelper.LIST_MAX_SIZE) {

            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("SELECT ");
            prepareStatement.append("ID, ");
            prepareStatement.append("NOTIFICATIONEMAIL, ");
            prepareStatement.append("APPLICATIONNAME, ");
            prepareStatement.append("FLOWNAME, ");
            prepareStatement.append("FLOWPOINTNAME, ");
            prepareStatement.append("REGISTRATIONDATE, ");
            prepareStatement.append("NOTIFICATIONSENTDATE, ");
            prepareStatement.append("NOTIFICATIONCOUNTER, ");
            prepareStatement.append("MAXNOTIFICATIONS, ");
            prepareStatement.append("MAXNOTIFICATIONSUNIT ");
            prepareStatement.append("FROM EmailNotificationConfiguration ");
            prepareStatement.append("WHERE ID NOT IN ").append(PrepareStatementHelper.toSQLList(notTheseIds));
            prepareStatement.append(" AND ");
            prepareStatement.append("FLOWNAME IN ").append(PrepareStatementHelper.toSQLList(flowNames));

            CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
            ResultSet rs = stmt.executeQuery();
            converter.convert(rs);
        }
        return converter;
    }

    public ResultSetConverter getFlowPointNameConfigurations(
            Connection conn,
            Set<String> flowPointNames,
            List<String> notTheseIds,
            ResultSetConverter converter
    ) throws SQLException, Exception {

        // Add chunking later
        if (!flowPointNames.isEmpty() && flowPointNames.size() <= PrepareStatementHelper.LIST_MAX_SIZE) {

            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("SELECT ");
            prepareStatement.append("ID, ");
            prepareStatement.append("NOTIFICATIONEMAIL, ");
            prepareStatement.append("APPLICATIONNAME, ");
            prepareStatement.append("FLOWNAME, ");
            prepareStatement.append("FLOWPOINTNAME, ");
            prepareStatement.append("REGISTRATIONDATE, ");
            prepareStatement.append("NOTIFICATIONSENTDATE, ");
            prepareStatement.append("NOTIFICATIONCOUNTER, ");
            prepareStatement.append("MAXNOTIFICATIONS, ");
            prepareStatement.append("MAXNOTIFICATIONSUNIT ");
            prepareStatement.append("FROM EmailNotificationConfiguration ");
            prepareStatement.append("WHERE ID NOT IN ").append(PrepareStatementHelper.toSQLList(notTheseIds));
            prepareStatement.append(" AND ");
            prepareStatement.append("FLOWPOINTNAME IN ").append(PrepareStatementHelper.toSQLList(flowPointNames));

            // Prepare statement
            CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
            ResultSet rs = stmt.executeQuery();
            converter.convert(rs);
        }

        return converter;
    }

    public void updateEmailNotificationsAfterEmailSent(
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
            stmt.setTimestamp(1, emailNotification.getNotificationSentTimestamp());
            stmt.setInt(2, emailNotification.getNotificationCounter() + 1);
            stmt.setLong(3, emailNotification.getId());

            stmt.executeUpdate();

        }

    }

}
