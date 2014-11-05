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
package com.erbjuder.logger.server.rest.services.dao;

import com.erbjuder.logger.server.rest.helper.SQLPrepareStatementHelper;
import com.erbjuder.logger.server.rest.util.ToJSON;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.json.simple.JSONArray;
//import org.codehaus.jettison.json.JSONArray;

/**
 *
 * @author Stefan Andersson
 */
public class LoggerSchema extends MysqlConnection {

    public JSONArray search_logMessageList(
            String fromDate,
            String toDate,
            String transactionReferenceId,
            Boolean viewError,
            List<String> applicationNames,
            List<String> flowNames,
            List<String> flowPointNames,
            List<String> freeTextSearchList,
            List<String> dataPartitionList) throws Exception {

        PreparedStatement query = null;
        Connection conn = null;
        ToJSON converter = new ToJSON();
        JSONArray json = new JSONArray();
        try {
            conn = MysqlConnection();

            StringBuilder prepStatement = new StringBuilder();
            prepStatement.append("SELECT ");
            prepStatement.append("ID, APPLICATIONNAME, EXPIREDDATE, FLOWNAME, FLOWPOINTNAME, ");
            prepStatement.append("ISERROR, TRANSACTIONREFERENCEID, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP ");
            prepStatement.append("FROM ").append("LogMessage ").append("WHERE ");

            // Between date
            prepStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(SQLPrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(SQLPrepareStatementHelper.toSQLValue(toDate)).append(" ");

            // Transaction ref ID
            if (transactionReferenceId != null && !transactionReferenceId.isEmpty()) {
                prepStatement.append("AND ");
                prepStatement.append("TRANSACTIONREFERENCEID LIKE ").append(SQLPrepareStatementHelper.toSQLValue(transactionReferenceId)).append(" ");
            }

            // view error
            if (viewError != null) {
                prepStatement.append("AND ");
                prepStatement.append("ISERROR = ").append(SQLPrepareStatementHelper.toSQLValue(viewError.toString())).append(" ");
            }

            // application names
            if (applicationNames != null && !applicationNames.isEmpty()) {
                prepStatement.append("AND ");
                prepStatement.append("APPLICATIONNAME IN ").append(SQLPrepareStatementHelper.toSQLList(applicationNames)).append(" ");
            }

            // flow names
            if (flowNames != null && !flowNames.isEmpty()) {
                prepStatement.append("AND ");
                prepStatement.append("FLOWNAME IN ").append(SQLPrepareStatementHelper.toSQLList(flowNames)).append(" ");
            }

            // flow point names
            if (flowPointNames != null && !flowPointNames.isEmpty()) {
                prepStatement.append("AND ");
                prepStatement.append("FLOWPOINTNAME IN ").append(SQLPrepareStatementHelper.toSQLList(flowPointNames)).append(" ");
            }

            // Free text search
            if (freeTextSearchList != null && !freeTextSearchList.isEmpty()
                    && dataPartitionList != null && !dataPartitionList.isEmpty()) {
                for (String freeText : freeTextSearchList) {

                    for (String databasePartition : dataPartitionList) {

                    }
                }
            }

            System.err.println(prepStatement.toString());
            CallableStatement stmt = conn.prepareCall(prepStatement.toString());
            json = converter.toJSONArray(stmt.executeQuery());
            conn.close(); //close connection

        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return json;

        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return json;
    }

    private StringBuilder search_logMessagesFromApplicationNames(
            String fromDate,
            String toDate,
            List<String> applicationNames
    ) {

        StringBuilder prepareStatement = new StringBuilder();

        prepareStatement.append("SELECT ");
        prepareStatement.append("ID, APPLICATIONNAME, EXPIREDDATE, FLOWNAME, FLOWPOINTNAME, ");
        prepareStatement.append("ISERROR, TRANSACTIONREFERENCEID, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP ");
        prepareStatement.append("FROM ").append("LogMessage ").append("WHERE ");

        // Between date
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(SQLPrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(SQLPrepareStatementHelper.toSQLValue(toDate)).append(" ");

        for (String applicationName : applicationNames) {
            prepareStatement.append("AND ");
            prepareStatement.append("APPLICATIONNAME LIKE ").append(SQLPrepareStatementHelper.toSQLStartsWithValue(applicationName)).append(" ");
        }

        return prepareStatement;

    }

    private StringBuilder search_logMessagesFromFlowNames(
            String fromDate,
            String toDate,
            List<String> flowNames
    ) {

        StringBuilder prepareStatement = new StringBuilder();

        prepareStatement.append("SELECT ");
        prepareStatement.append("ID, APPLICATIONNAME, EXPIREDDATE, FLOWNAME, FLOWPOINTNAME, ");
        prepareStatement.append("ISERROR, TRANSACTIONREFERENCEID, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP ");
        prepareStatement.append("FROM ").append("LogMessage ").append("WHERE ");

        // Between date
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(SQLPrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(SQLPrepareStatementHelper.toSQLValue(toDate)).append(" ");

        for (String flowName : flowNames) {
            prepareStatement.append("AND ");
            prepareStatement.append("APPLICATIONNAME LIKE ").append(SQLPrepareStatementHelper.toSQLStartsWithValue(flowName)).append(" ");
        }

        return prepareStatement;

    }

    private StringBuilder search_logMessagesFromFlowPointNames(
            String fromDate,
            String toDate,
            List<String> flowPointNames
    ) {

        StringBuilder prepareStatement = new StringBuilder();

        prepareStatement.append("SELECT ");
        prepareStatement.append("ID, APPLICATIONNAME, EXPIREDDATE, FLOWNAME, FLOWPOINTNAME, ");
        prepareStatement.append("ISERROR, TRANSACTIONREFERENCEID, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP ");
        prepareStatement.append("FROM ").append("LogMessage ").append("WHERE ");

        // Between date
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(SQLPrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(SQLPrepareStatementHelper.toSQLValue(toDate)).append(" ");

        for (String flowPointName : flowPointNames) {
            prepareStatement.append("AND ");
            prepareStatement.append("APPLICATIONNAME LIKE ").append(SQLPrepareStatementHelper.toSQLStartsWithValue(flowPointName)).append(" ");
        }

        return prepareStatement;

    }

    private StringBuilder search_PartitionContainsValue(
            String fromDate,
            String toDate,
            String freeText,
            String databasePartition
    ) {

        StringBuilder prepareStatement = new StringBuilder();

        prepareStatement.append("SELECT ");
        prepareStatement.append("ID, CONTENT, CONTENTDESCRIPTION, CONTENTMIMETYPE, CONTENTMODIFIED, ");
        prepareStatement.append("CONTENTSIZE, SEARCHABLE, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP, LOGMESSAGE_ID");
        prepareStatement.append("FROM ").append(databasePartition).append("WHERE ");
        // Between date
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(SQLPrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(SQLPrepareStatementHelper.toSQLValue(toDate)).append(" ");

        prepareStatement.append("AND ");
        prepareStatement.append("( ");
        prepareStatement.append("CONTENT LIKE ").append(SQLPrepareStatementHelper.toSQLContainsValue(freeText)).append(" OR ");
        prepareStatement.append("CONTENTDESCRIPTION LIKE ").append(SQLPrepareStatementHelper.toSQLContainsValue(freeText)).append(" OR ");
        prepareStatement.append("CONTENTSIZE LIKE ").append(SQLPrepareStatementHelper.toSQLContainsValue(freeText)).append(" OR ");
        prepareStatement.append("CONTENTMIMETYPE LIKE ").append(SQLPrepareStatementHelper.toSQLContainsValue(freeText)).append(" OR ");
        prepareStatement.append(") ");
        return prepareStatement;

    }
}
