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

import com.erbjuder.logger.server.bean.EmailNotification;
import com.erbjuder.logger.server.common.helper.MysqlConnection;
import com.erbjuder.logger.server.common.services.PrimaryKeySequence;
import com.erbjuder.logger.server.common.services.ResultSetConverterEmailNotificationPrepareToSendList;
import com.generic.global.transactionlogger.Response;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 *
 * @author Stefan Andersson
 */
@Stateless
public class EmailNotificationPrepareToSendBase {

    private static final Logger logger = Logger.getLogger(EmailNotificationPrepareToSendBase.class.getName());

    public void sendPreparedNotifications() {

        int page = 0;
        int pageSize = 200;

        try (Connection conn = MysqlConnection.getConnectionWrite()) {

            try (PreparedStatement stmt = conn.prepareStatement(getEmailNotificationPrepareToSendStatementMysql_Select())) {

                
                // Fetch Always one time
                ResultSetConverterEmailNotificationPrepareToSendList converter = new ResultSetConverterEmailNotificationPrepareToSendList();
                int pageOffset = page * pageSize;
                stmt.setInt(1, page);
                stmt.setInt(2, pageOffset);
                ResultSet rs = stmt.executeQuery();
                converter.convert(rs);

                // Send email
                
                // Mark as sent
                
                // As long we have more unsent data
                if (!converter.getResult().isEmpty()) {

                    // Still have more?
                    converter = new ResultSetConverterEmailNotificationPrepareToSendList();
                    pageOffset = (page + 1) * pageSize;
                    stmt.setInt(1, page);
                    stmt.setInt(2, pageOffset);
                    rs = stmt.executeQuery();
                    converter.convert(rs);

                    // Send email
                }

            }

        } catch (SQLException sqlError) {
            logger.log(Level.SEVERE, sqlError.getMessage());

        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());

        }

        /*
        int pageOffset = page - 1;
        if (pageOffset < 0) {
            pageOffset = 0;
        } else {
            pageOffset = pageOffset * pageSize;
        }
         */
    }

    public void removePreparedNotifications() {

        int page = 1;
        int pageSize = 200;

        // fetch prepared by pagination
        // 
    }

    private void fetchPreparedToSend() {

    }

    public Response persist(String ApplicationName, String transactionReferenceId) {

        // Default
        Response response = new Response();
        response.setReturn(true);

        // Insert everything, when sending eMial, remove duplicate! 
        try (Connection connection = MysqlConnection.getConnectionWrite()) {

            // Fetch primary keys
            PrimaryKeySequence primaryKeySequence = fetchPrimaryKeySequence(connection, 1);
            long PK = primaryKeySequence.getStartPK();
            try (PreparedStatement preparedStatement = connection.prepareStatement(getEmailNotificationPrepareToSendStatementMysql_Insert())) {

                // Prepare statement
                preparedStatement.setLong(1, PK);
                preparedStatement.setString(2, ApplicationName);
                preparedStatement.setString(3, transactionReferenceId);
                preparedStatement.execute();

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

    private String getEmailNotificationPrepareToSendStatementMysql_Insert() {
        String emailNotificationPrepareStatement
                = "INSERT INTO EmailNotificationPrepareToSend ("
                + "ID, "
                + "LOGMESSAGE_APPLICATIONNAME, "
                + "LOGMESSAGE_TRANSACTIONREFERENCEID) "
                + "values (?, ?, ? )";
        return emailNotificationPrepareStatement;
    }

    private String getEmailNotificationPrepareToSendStatementMysql_Select() {
        String emailNotificationPrepareStatement
                = "SELECT "
                + "ID, "
                + "LOGMESSAGE_APPLICATIONNAME, "
                + "LOGMESSAGE_TRANSACTIONREFERENCEID "
                + "FROM EmailNotificationPrepareToSend WHERE SENT = 0 LIMIT ? OFFSET ?";

        return emailNotificationPrepareStatement;
    }

}
