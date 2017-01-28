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

import com.erbjuder.logger.server.common.helper.DatabasePartitionHelper;
import com.erbjuder.logger.server.common.helper.MysqlConnection;
import com.erbjuder.logger.server.common.services.PrepareStatementHelper;
import com.erbjuder.logger.server.common.services.ResultSetConverter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 *
 * @author Stefan Andersson
 */
public class TransactionLogStatisticBase {

    public ResultSetConverter uniqueApplicationNameTransactions(
            String fromDate,
            String toDate,
            Integer page,
            Integer pageSize,
            String viewApplicationName,
            ResultSetConverter converter
    ) {

        try (Connection conn = MysqlConnection.getConnectionRead()) {

            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("SELECT ");
            prepareStatement.append("MIN(APPLICATIONNAME) as APPLICATIONNAME, ");
            prepareStatement.append("TRANSACTIONREFERENCEID, ");
            prepareStatement.append("MIN(UTCLOCALTIMESTAMP) as CLIENT_TIME_UTC_START,  ");
            prepareStatement.append("MAX(UTCLOCALTIMESTAMP) as CLIENT_TIME_UTC_END, ");
            prepareStatement.append("MIN(UTCSERVERTIMESTAMP) as SERVER_TIME_UTC_START,  ");
            prepareStatement.append("MAX(UTCSERVERTIMESTAMP) as SERVER_TIME_UTC_END, ");
            //prepareStatement.append("CAST( UNIX_TIMESTAMP(MAX(UTCLOCALTIMESTAMP)) - UNIX_TIMESTAMP(MIN(UTCLOCALTIMESTAMP)) as decimal(65,6) ) AS executionTime, ");
            prepareStatement.append("CAST( MAX(UTCLOCALTIMESTAMP) - MIN(UTCLOCALTIMESTAMP) as decimal(65,6) ) AS CLIENT_EXECUTION_TIME, ");
            prepareStatement.append("CAST( MAX(UTCSERVERTIMESTAMP) - MIN(UTCSERVERTIMESTAMP) as decimal(65,6) ) AS SERVER_EXECUTION_TIME, ");

            prepareStatement.append("COUNT(*) AS NUMBER_OF_LOGMESSAGES, ");
            prepareStatement.append("MAX(isError) as HASERROR ");
            prepareStatement.append("FROM ").append("LogMessage ");

            appendPartitionId(fromDate, toDate, prepareStatement);

            prepareStatement.append("WHERE ");
            appendBetweenDate(fromDate, toDate, prepareStatement);

            // application names
            appendViewApplicationName(viewApplicationName, prepareStatement);

            prepareStatement.append("GROUP BY TRANSACTIONREFERENCEID ");

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

    private void appendBetweenDate(String fromDate, String toDate, StringBuilder prepareStatement) throws ParseException {
        // Between date
        if (fromDate != null && toDate != null && !fromDate.isEmpty() && !toDate.isEmpty()) {
            prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");
        }
    }

    private void appendViewApplicationName(String viewApplicationName, StringBuilder prepareStatement) throws ParseException {

        // application names
        if (viewApplicationName
                != null && !viewApplicationName.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("APPLICATIONNAME LIKE ").append(PrepareStatementHelper.toSQLValue(viewApplicationName)).append(" ");
        }
    }

    private void appendViewApplicationNames(List viewApplicationNames, StringBuilder prepareStatement) throws ParseException {

        // application names
        if (viewApplicationNames
                != null && !viewApplicationNames.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("APPLICATIONNAME IN ").append(PrepareStatementHelper.toSQLList(viewApplicationNames)).append(" ");
        }
    }

    private void appendPartitionId(Integer partitionId, StringBuilder prepareStatement) {
        String sqlPartitionSyntax = DatabasePartitionHelper.getPartitionSyntax(partitionId);
        prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntax)).append(" ");
    }

    private void appendPartitionId(String fromDate, String toDate, StringBuilder prepareStatement) throws ParseException {
        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
        if (sqlPartitionSyntaxList.size() < DatabasePartitionHelper.MAX_PARTITIONS) {
            prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" ");
        }
    }

}
