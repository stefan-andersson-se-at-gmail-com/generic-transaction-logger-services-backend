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
package com.erbjuder.logger.server.common.helper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Stefan Andersson
 */
public class LogMessageQueries {

    public ResultSet search_logMessageList(
            String fromDate,
            String toDate,
            Integer page,
            Integer pageSize,
            String transactionReferenceId,
            Boolean viewError,
            List<String> viewApplicationNames,
            List<String> viewFlowNames,
            List<String> viewFlowPointNames,
            List<String> notViewApplicationNames,
            List<String> notViewFlowNames,
            List<String> notViewFlowPointNames,
            List<String> freeTextSearchList,
            List<String> dataPartitionList) throws Exception {

        ResultSet rs = null;

        try (Connection conn = MysqlConnection.getConnection()) {

            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("SELECT ");
            prepareStatement.append("ID, PARTITION_ID, APPLICATIONNAME, EXPIREDDATE, FLOWNAME, FLOWPOINTNAME, ");
            prepareStatement.append("ISERROR, TRANSACTIONREFERENCEID, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP ");
            prepareStatement.append("FROM ").append("LogMessage ");

            List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
            prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List(sqlPartitionSyntaxList)).append(" WHERE ");

            // Between date
            prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");

            // Transaction ref ID
            if (transactionReferenceId != null && !transactionReferenceId.isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append("TRANSACTIONREFERENCEID LIKE ").append(PrepareStatementHelper.toSQLValue(transactionReferenceId)).append(" ");
            }

            // view error
            if (viewError != null) {
                prepareStatement.append("AND ");
                prepareStatement.append("ISERROR = ").append(PrepareStatementHelper.toSQLValue(viewError.toString())).append(" ");
            }

            // application names
            if (viewApplicationNames != null && !viewApplicationNames.isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append("APPLICATIONNAME IN ").append(PrepareStatementHelper.toSQL_Partition_List(viewApplicationNames)).append(" ");
            }

            // not application names
            if (notViewApplicationNames != null && !notViewApplicationNames.isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append("APPLICATIONNAME NOT IN ").append(PrepareStatementHelper.toSQL_Partition_List(notViewApplicationNames)).append(" ");
            }

            // flow names
            if (viewFlowNames != null && !viewFlowNames.isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append("FLOWNAME IN ").append(PrepareStatementHelper.toSQL_Partition_List(viewFlowNames)).append(" ");
            }

            // not flow names
            if (notViewFlowNames != null && !notViewFlowNames.isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append("FLOWNAME NOT IN ").append(PrepareStatementHelper.toSQL_Partition_List(notViewFlowNames)).append(" ");
            }

            // flow point names
            if (viewFlowPointNames != null && !viewFlowPointNames.isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append("FLOWPOINTNAME IN ").append(PrepareStatementHelper.toSQL_Partition_List(viewFlowPointNames)).append(" ");
            }

            // not flow point names
            if (notViewFlowPointNames != null && !notViewFlowPointNames.isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append("FLOWPOINTNAME NOT IN ").append(PrepareStatementHelper.toSQL_Partition_List(notViewFlowPointNames)).append(" ");
            }

            // Free text search
            if (freeTextSearchList != null && !freeTextSearchList.isEmpty()
                    && dataPartitionList != null && !dataPartitionList.isEmpty()) {

                prepareStatement.append("AND ( ");
                int size = dataPartitionList.size();
                for (int i = 0; i < size; i++) {

                    String logMessageDataPartition = dataPartitionList.get(i);
                    StringBuilder partitionBuilder = this.search_LogMessageIdsFromPartition(
                            fromDate,
                            toDate,
                            "ID",
                            logMessageDataPartition,
                            freeTextSearchList
                    );

                    prepareStatement.append("ID = ").append(" ( ").append(partitionBuilder.toString()).append(" ) ");

                    if (i < size - 1) {
                        prepareStatement.append("OR ");
                    }

                }

                prepareStatement.append(" ) ");
            }

            // 
            // Order by
            prepareStatement.append("ORDER BY UTCSERVERTIMESTAMP DESC ");

            // Pagination: Assume that first page <==> 1
            int pageOffset = page - 1;
            if (pageOffset < 0) {
                pageOffset = 0;
            } else {
                pageOffset = pageOffset * pageSize;
            }
            prepareStatement.append("LIMIT ").append(pageSize).append(" OFFSET ").append(pageOffset).append(" ");

            CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
            rs = stmt.executeQuery();
            conn.close();

        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return rs;

        }
        return rs;

    }

    public List<ResultSet> fetch_LogMessageData(
            String logMessageId,
            int partitionId,
            List<String> dataPartitionList) {

        List<ResultSet> rsList = new ArrayList<>();
        try (Connection conn = MysqlConnection.getConnection()) {

            String partitionBefore = DatabasePartitionHelper.mysql_partition_prefix + (partitionId - 1);
            String partition = DatabasePartitionHelper.mysql_partition_prefix + (partitionId);
            String partitionAfter = DatabasePartitionHelper.mysql_partition_prefix + (partitionId + 1);
            List<String> sqlPartitionSyntaxList = new ArrayList();
            sqlPartitionSyntaxList.add(partitionBefore);
            sqlPartitionSyntaxList.add(partition);
            sqlPartitionSyntaxList.add(partitionAfter);

            for (String databasePartition : dataPartitionList) {

                StringBuilder prepareStatement = new StringBuilder();
                prepareStatement.append("SELECT ");
                prepareStatement.append("ID, PARTITION_ID, CONTENT, LABEL, MIMETYPE, MODIFIED, ");
                prepareStatement.append("CONTENTSIZE, SEARCHABLE, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP, LOGMESSAGE_ID ");
                prepareStatement.append("FROM ").append(databasePartition).append(" ");

                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List(sqlPartitionSyntaxList)).append(" ");
                prepareStatement.append("WHERE LOGMESSAGE_ID = ").append(logMessageId);

                ResultSet rs = null;
                CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
                rs = stmt.executeQuery();

                if (rs != null) {
                    rsList.add(rs);
                }
            }

            conn.close();
        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            return rsList;
        } catch (Exception e) {
            e.printStackTrace();
            return rsList;

        }
        return rsList;
    }

    private StringBuilder search_LogMessageIdsFromPartition(
            String fromDate,
            String toDate,
            String logMessageIdLabel,
            String logMessageDataPartition,
            List<String> freeTextSearchList
    ) throws ParseException {

        StringBuilder prepareStatement = new StringBuilder();
        String partitionID = logMessageDataPartition + "_ID";

        prepareStatement.append("SELECT ");
        prepareStatement.append("DISTINCT ( LOGMESSAGE_ID ) as ").append(partitionID).append(" ");
        prepareStatement.append("FROM ").append(logMessageDataPartition).append(" ");

        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
        prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List(sqlPartitionSyntaxList)).append(" WHERE ");

        // Between date
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");
        prepareStatement.append("AND ");
        prepareStatement.append(logMessageDataPartition).append(".LOGMESSAGE_ID = ").append("LogMessage.").append(logMessageIdLabel).append(" ");

        int size = freeTextSearchList.size();
        if (size > 0) {
            prepareStatement.append("AND ( ");

            for (int i = 0; i < size; i++) {

                String freeText = PrepareStatementHelper.toSQLContainsValue(freeTextSearchList.get(i));
                prepareStatement.append("( LABEL LIKE ").append(freeText).append(" ");
                prepareStatement.append("OR ");
                prepareStatement.append("CONTENT LIKE ").append(freeText).append(" ) ");

                if (i < size - 1) {
                    prepareStatement.append("AND ");
                }
            }

            prepareStatement.append(" ) ");

        }

        return prepareStatement;

    }

    public ResultSet search_LogMessageDataPartition(
            String fromDate,
            String toDate,
            String logMessageId,
            String logMessageDataPartition,
            List<String> freeTextSearchList
    ) throws Exception {

        ResultSet rs = null;
        try (Connection conn = MysqlConnection.getConnection()) {

            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("SELECT ");
            prepareStatement.append("ID, PARTITION_ID, LABEL, CONTENT, MIMETYPE, CONTENTSIZE, MODIFIED, ");
            prepareStatement.append("SEARCHABLE, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP, LOGMESSAGE_ID ");
            prepareStatement.append("FROM ").append(logMessageDataPartition).append(" ");

            List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
            prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List(sqlPartitionSyntaxList)).append(" WHERE ");

            // Between date
            prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");

            if (logMessageId != null && !logMessageId.isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append(logMessageDataPartition).append(".LOGMESSAGE_ID = ").append(logMessageId).append(" ");
            }

            int size = freeTextSearchList.size();
            if (size > 0) {
                prepareStatement.append("AND ( ");

                for (int i = 0; i < size; i++) {

                    String freeText = PrepareStatementHelper.toSQLContainsValue(freeTextSearchList.get(i));
                    prepareStatement.append("( LABEL LIKE ").append(freeText).append(" ");
                    prepareStatement.append("OR ");
                    prepareStatement.append("CONTENT LIKE ").append(freeText).append(" ) ");

                    if (i < size - 1) {
                        prepareStatement.append("AND ");
                    }
                }

                prepareStatement.append(") ");

            }

            CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
            rs = stmt.executeQuery();
            conn.close();

        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return rs;

        }
        return rs;

    }

    public ResultSet search_ApplicationNames(
            String fromDate,
            String toDate,
            List<String> applicationNames) {

        ResultSet rs = null;
        try (Connection conn = MysqlConnection.getConnection()) {
            
            StringBuilder prepareStatement = search_logMessagesFromApplicationNames(
                    fromDate,
                    toDate,
                    applicationNames);

            CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
            rs = stmt.executeQuery();
            conn.close();

        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return rs;

        }
        return rs;

    }

    private StringBuilder search_logMessagesFromApplicationNames(
            String fromDate,
            String toDate,
            List<String> applicationNames
    ) throws ParseException {

        StringBuilder prepareStatement = new StringBuilder();

        prepareStatement.append("SELECT ");
        prepareStatement.append("ID, PARTITION_ID, APPLICATIONNAME, EXPIREDDATE, FLOWNAME, FLOWPOINTNAME, ");
        prepareStatement.append("ISERROR, TRANSACTIONREFERENCEID, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP ");
        prepareStatement.append("FROM ").append("LogMessage ").append(" ");

        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
        prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List(sqlPartitionSyntaxList)).append(" WHERE ");

        // Between date
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");
        
        for (String applicationName : applicationNames) {
            prepareStatement.append("AND ");
            
            System.err.println("APP NAME={ " +applicationName+ " }");
            prepareStatement.append("APPLICATIONNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(applicationName)).append(" ");
        }

        System.err.println("APP NAMES=[ " +prepareStatement.toString()+ " ]");
        return prepareStatement;

    }

    private StringBuilder search_logMessagesFromFlowNames(
            String fromDate,
            String toDate,
            List<String> flowNames
    ) throws ParseException {

        StringBuilder prepareStatement = new StringBuilder();

        prepareStatement.append("SELECT ");
        prepareStatement.append("ID, PARTITION_ID, APPLICATIONNAME, EXPIREDDATE, FLOWNAME, FLOWPOINTNAME, ");
        prepareStatement.append("ISERROR, TRANSACTIONREFERENCEID, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP ");
        prepareStatement.append("FROM ").append("LogMessage ").append(" ");

        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
        prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List(sqlPartitionSyntaxList)).append(" WHERE ");

        // Between date
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");

        for (String flowName : flowNames) {
            prepareStatement.append("AND ");
            prepareStatement.append("FLOWNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(flowName)).append(" ");
        }

        return prepareStatement;

    }

    private StringBuilder search_logMessagesFromFlowPointNames(
            String fromDate,
            String toDate,
            List<String> flowPointNames
    ) throws ParseException {

        StringBuilder prepareStatement = new StringBuilder();

        prepareStatement.append("SELECT ");
        prepareStatement.append("ID, PARTITION_ID, APPLICATIONNAME, EXPIREDDATE, FLOWNAME, FLOWPOINTNAME, ");
        prepareStatement.append("ISERROR, TRANSACTIONREFERENCEID, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP ");
        prepareStatement.append("FROM ").append("LogMessage ").append(" ");

        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
        prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List(sqlPartitionSyntaxList)).append(" WHERE ");

        // Between date
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");

        for (String flowPointName : flowPointNames) {
            prepareStatement.append("AND ");
            prepareStatement.append("FLOWPOINTNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(flowPointName)).append(" ");
        }

        return prepareStatement;

    }

    private StringBuilder search_PartitionContainsValue(
            String fromDate,
            String toDate,
            String freeText,
            String databasePartition
    ) throws ParseException {

        StringBuilder prepareStatement = new StringBuilder();

        prepareStatement.append("SELECT ");
        prepareStatement.append("ID, PARTITION_ID, CONTENT, LABEL, MIMETYPE, MODIFIED, ");
        prepareStatement.append("CONTENTSIZE, SEARCHABLE, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP, LOGMESSAGE_ID");
        prepareStatement.append("FROM ").append(databasePartition).append(" ");

        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
        prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List(sqlPartitionSyntaxList)).append(" WHERE ");

        // Between date
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");

        prepareStatement.append("AND ");
        prepareStatement.append("( ");
        prepareStatement.append("CONTENT LIKE ").append(PrepareStatementHelper.toSQLContainsValue(freeText)).append(" OR ");
        prepareStatement.append("LABEL LIKE ").append(PrepareStatementHelper.toSQLContainsValue(freeText)).append(" OR ");
        prepareStatement.append("CONTENTSIZE LIKE ").append(PrepareStatementHelper.toSQLContainsValue(freeText)).append(" OR ");
        prepareStatement.append("MIMETYPE LIKE ").append(PrepareStatementHelper.toSQLContainsValue(freeText)).append(" OR ");
        prepareStatement.append(") ");
        return prepareStatement;

    }
}
