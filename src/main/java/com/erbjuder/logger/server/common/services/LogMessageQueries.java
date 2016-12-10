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

import com.erbjuder.logger.server.common.helper.DatabasePartitionHelper;
import com.erbjuder.logger.server.common.helper.MysqlConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Stefan Andersson
 */
public class LogMessageQueries {

    public ResultSetConverter fetch_ApplicationNames(
            String fromDate,
            String toDate,
            Integer page,
            Integer pageSize,
            String transactionReferenceId,
            Integer viewError, // 0=false, 1= true and null skip
            List<String> viewApplicationNames,
            List<String> viewFlowNames,
            List<String> viewFlowPointNames,
            List<String> viewLables,
            List<String> viewMimeTypes,
            List<String> notViewApplicationNames,
            List<String> notViewFlowNames,
            List<String> notViewFlowPointNames,
            List<String> notViewLables,
            List<String> notViewMimeTypes,
            List<String> freeTextSearchList,
            List<String> dataSizePartitionList,
            ResultSetConverter converter) {

        try (Connection conn = MysqlConnection.getConnectionRead()) {

            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("SELECT ");
            prepareStatement.append("DISTINCT APPLICATIONNAME ");
            prepareStatement.append("FROM ").append("LogMessage ");
            prepareStatement.append("WHERE ");

            List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
            int orgsqlPartitionSyntaxListSize = sqlPartitionSyntaxList.size();

            // Fetch all application names from first partition 
            if (orgsqlPartitionSyntaxListSize >= 1) {
                String firstPartition = sqlPartitionSyntaxList.get(0);
                sqlPartitionSyntaxList.remove(0);

                prepareStatement.append("APPLICATIONNAME IN ");
                prepareStatement.append(" ( ");
                prepareStatement.append("SELECT ");
                prepareStatement.append("DISTINCT APPLICATIONNAME ");
                prepareStatement.append("FROM ").append("LogMessage ");
                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(firstPartition)).append(" WHERE ");
                prepareStatement.append("UTCSERVERTIMESTAMP >= ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" ");

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
                    for (String applicationName : viewApplicationNames) {
                        prepareStatement.append("AND ");
                        prepareStatement.append("APPLICATIONNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(applicationName)).append(" ");
                    }
                }

                // not application names
                if (notViewApplicationNames != null && !notViewApplicationNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("APPLICATIONNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewApplicationNames)).append(" ");
                }

                // flow names
                if (viewFlowNames != null && !viewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowNames)).append(" ");
                }

                // not flow names
                if (notViewFlowNames != null && !notViewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowNames)).append(" ");
                }

                // flow point names
                if (viewFlowPointNames != null && !viewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowPointNames)).append(" ");
                }

                // not flow point names
                if (notViewFlowPointNames != null && !notViewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowPointNames)).append(" ");
                }

                // Free text search
                if ((freeTextSearchList != null && !freeTextSearchList.isEmpty())
                        || (viewLables != null && !viewLables.isEmpty())
                        || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                        || (notViewLables != null && !notViewLables.isEmpty())
                        || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                        && dataSizePartitionList != null && !dataSizePartitionList.isEmpty()) {

                    prepareStatement.append("AND ( ");
                    int size = dataSizePartitionList.size();
                    for (int i = 0; i < size; i++) {

                        String logMessageDataPartition = dataSizePartitionList.get(i);
                        StringBuilder partitionBuilder = LogMessagePrepareStatements.fetch_LogMessageIdsFromPartition_IdVariable(
                                fromDate,
                                toDate,
                                "ID",
                                viewLables,
                                viewMimeTypes,
                                notViewLables,
                                notViewMimeTypes,
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
                prepareStatement.append(" ) ");

            }

            if (orgsqlPartitionSyntaxListSize >= 2) {
                String lastPartition = sqlPartitionSyntaxList.get(sqlPartitionSyntaxList.size() - 1);
                sqlPartitionSyntaxList.remove(sqlPartitionSyntaxList.size() - 1);

                // Remove first and last
                prepareStatement.append("OR ");

                // Fetch all application names from last partition 
                prepareStatement.append("APPLICATIONNAME IN ");
                prepareStatement.append(" ( ");
                prepareStatement.append("SELECT ");
                prepareStatement.append("DISTINCT APPLICATIONNAME ");
                prepareStatement.append("FROM ").append("LogMessage ");
                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(lastPartition)).append(" WHERE ");
                prepareStatement.append("UTCSERVERTIMESTAMP < ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");

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
                    for (String applicationName : viewApplicationNames) {
                        prepareStatement.append("AND ");
                        prepareStatement.append("APPLICATIONNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(applicationName)).append(" ");
                    }
                }

                // not application names
                if (notViewApplicationNames != null && !notViewApplicationNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("APPLICATIONNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewApplicationNames)).append(" ");
                }

                // flow names
                if (viewFlowNames != null && !viewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowNames)).append(" ");
                }

                // not flow names
                if (notViewFlowNames != null && !notViewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowNames)).append(" ");
                }

                // flow point names
                if (viewFlowPointNames != null && !viewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowPointNames)).append(" ");
                }

                // not flow point names
                if (notViewFlowPointNames != null && !notViewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowPointNames)).append(" ");
                }

                // Free text search
                if ((freeTextSearchList != null && !freeTextSearchList.isEmpty())
                        || (viewLables != null && !viewLables.isEmpty())
                        || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                        || (notViewLables != null && !notViewLables.isEmpty())
                        || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                        && dataSizePartitionList != null && !dataSizePartitionList.isEmpty()) {

                    prepareStatement.append("AND ( ");
                    int size = dataSizePartitionList.size();
                    for (int i = 0; i < size; i++) {

                        String logMessageDataPartition = dataSizePartitionList.get(i);
                        StringBuilder partitionBuilder = LogMessagePrepareStatements.fetch_LogMessageIdsFromPartition_IdVariable(
                                fromDate,
                                toDate,
                                "ID",
                                viewLables,
                                viewMimeTypes,
                                notViewLables,
                                notViewMimeTypes,
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

                prepareStatement.append(" ) ");

            }

            if (orgsqlPartitionSyntaxListSize >= 3) {

                prepareStatement.append("OR ");

                // Fetch all application names in between  
                prepareStatement.append("APPLICATIONNAME IN ");
                prepareStatement.append(" ( ");
                prepareStatement.append("SELECT ");
                prepareStatement.append("DISTINCT APPLICATIONNAME ");
                prepareStatement.append("FROM ").append("LogMessage ");
                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" ");

                if ((transactionReferenceId != null && !viewApplicationNames.isEmpty())
                        || (viewFlowNames != null && !viewFlowNames.isEmpty())
                        || (viewFlowPointNames != null && !viewFlowPointNames.isEmpty())
                        || (viewLables != null && !viewLables.isEmpty())
                        || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                        || (notViewApplicationNames != null && !notViewApplicationNames.isEmpty())
                        || (notViewFlowNames != null && !notViewFlowNames.isEmpty())
                        || (notViewFlowPointNames != null && !notViewFlowPointNames.isEmpty())
                        || (notViewLables != null && !notViewLables.isEmpty())
                        || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                        || (freeTextSearchList != null && !freeTextSearchList.isEmpty())) {
                    prepareStatement.append("WHERE ");
                }

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
                    for (String applicationName : viewApplicationNames) {
                        prepareStatement.append("AND ");
                        prepareStatement.append("APPLICATIONNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(applicationName)).append(" ");
                    }
                }

                // not application names
                if (notViewApplicationNames != null && !notViewApplicationNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("APPLICATIONNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewApplicationNames)).append(" ");
                }

                // flow names
                if (viewFlowNames != null && !viewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowNames)).append(" ");
                }

                // not flow names
                if (notViewFlowNames != null && !notViewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowNames)).append(" ");
                }

                // flow point names
                if (viewFlowPointNames != null && !viewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowPointNames)).append(" ");
                }

                // not flow point names
                if (notViewFlowPointNames != null && !notViewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowPointNames)).append(" ");
                }

                // Free text search
               if ((freeTextSearchList != null && !freeTextSearchList.isEmpty())
                        || (viewLables != null && !viewLables.isEmpty())
                        || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                        || (notViewLables != null && !notViewLables.isEmpty())
                        || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                        && dataSizePartitionList != null && !dataSizePartitionList.isEmpty()) {

                    prepareStatement.append("AND ( ");
                    int size = dataSizePartitionList.size();
                    for (int i = 0; i < size; i++) {

                        String logMessageDataPartition = dataSizePartitionList.get(i);
                        StringBuilder partitionBuilder = LogMessagePrepareStatements.fetch_LogMessageIdsFromPartition_IdVariable(
                                fromDate,
                                toDate,
                                "ID",
                                viewLables,
                                viewMimeTypes,
                                notViewLables,
                                notViewMimeTypes,
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

                prepareStatement.append(" ) ");

            }

            // Pagination: Assume that first page <==> 1
            int pageOffset = page - 1;
            if (pageOffset < 0) {
                pageOffset = 0;
            } else {
                pageOffset = pageOffset * pageSize;
            }
            prepareStatement.append("LIMIT ").append(pageSize).append(" OFFSET ").append(pageOffset).append(" ");

             
            System.err.println(prepareStatement.toString());
            
            
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

    public ResultSetConverter fetch_FlowNames(
            String fromDate,
            String toDate,
            Integer page,
            Integer pageSize,
            String transactionReferenceId,
            Integer viewError, // 0=false, 1= true and null skip
            List<String> viewApplicationNames,
            List<String> viewFlowNames,
            List<String> viewFlowPointNames,
            List<String> viewLables,
            List<String> viewMimeTypes,
            List<String> notViewApplicationNames,
            List<String> notViewFlowNames,
            List<String> notViewFlowPointNames,
            List<String> notViewLables,
            List<String> notViewMimeTypes,
            List<String> freeTextSearchList,
            List<String> dataSizePartitionList,
            ResultSetConverter converter) {

        try (Connection conn = MysqlConnection.getConnectionRead()) {

            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("SELECT ");
            prepareStatement.append("DISTINCT FLOWNAME ");
            prepareStatement.append("FROM ").append("LogMessage ");
            prepareStatement.append("WHERE ");

            List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
            int orgsqlPartitionSyntaxListSize = sqlPartitionSyntaxList.size();

            // Fetch all application names from first partition 
            if (orgsqlPartitionSyntaxListSize >= 1) {
                String firstPartition = sqlPartitionSyntaxList.get(0);
                sqlPartitionSyntaxList.remove(0);

                prepareStatement.append("FLOWNAME IN ");
                prepareStatement.append(" ( ");
                prepareStatement.append("SELECT ");
                prepareStatement.append("DISTINCT FLOWNAME ");
                prepareStatement.append("FROM ").append("LogMessage ");
                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(firstPartition)).append(" WHERE ");
                prepareStatement.append("UTCSERVERTIMESTAMP >= ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" ");

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
                    for (String applicationName : viewApplicationNames) {
                        prepareStatement.append("AND ");
                        prepareStatement.append("APPLICATIONNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(applicationName)).append(" ");
                    }
                }

                // not application names
                if (notViewApplicationNames != null && !notViewApplicationNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("APPLICATIONNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewApplicationNames)).append(" ");
                }

                // flow names
                if (viewFlowNames != null && !viewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowNames)).append(" ");
                }

                // not flow names
                if (notViewFlowNames != null && !notViewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowNames)).append(" ");
                }

                // flow point names
                if (viewFlowPointNames != null && !viewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowPointNames)).append(" ");
                }

                // not flow point names
                if (notViewFlowPointNames != null && !notViewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowPointNames)).append(" ");
                }

                // Free text search
               if ((freeTextSearchList != null && !freeTextSearchList.isEmpty())
                        || (viewLables != null && !viewLables.isEmpty())
                        || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                        || (notViewLables != null && !notViewLables.isEmpty())
                        || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                        && dataSizePartitionList != null && !dataSizePartitionList.isEmpty()) {

                    prepareStatement.append("AND ( ");
                    int size = dataSizePartitionList.size();
                    for (int i = 0; i < size; i++) {

                        String logMessageDataPartition = dataSizePartitionList.get(i);
                        StringBuilder partitionBuilder = LogMessagePrepareStatements.fetch_LogMessageIdsFromPartition_IdVariable(
                                fromDate,
                                toDate,
                                "ID",
                                viewLables,
                                viewMimeTypes,
                                notViewLables,
                                notViewMimeTypes,
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
                prepareStatement.append(" ) ");

            }

            if (orgsqlPartitionSyntaxListSize >= 2) {
                String lastPartition = sqlPartitionSyntaxList.get(sqlPartitionSyntaxList.size() - 1);
                sqlPartitionSyntaxList.remove(sqlPartitionSyntaxList.size() - 1);

                // Remove first and last
                prepareStatement.append("OR ");

                // Fetch all application names from last partition 
                prepareStatement.append("FLOWNAME IN ");
                prepareStatement.append(" ( ");
                prepareStatement.append("SELECT ");
                prepareStatement.append("DISTINCT FLOWNAME ");
                prepareStatement.append("FROM ").append("LogMessage ");
                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(lastPartition)).append(" WHERE ");
                prepareStatement.append("UTCSERVERTIMESTAMP < ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");

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
                    for (String applicationName : viewApplicationNames) {
                        prepareStatement.append("AND ");
                        prepareStatement.append("APPLICATIONNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(applicationName)).append(" ");
                    }
                }

                // not application names
                if (notViewApplicationNames != null && !notViewApplicationNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("APPLICATIONNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewApplicationNames)).append(" ");
                }

                // flow names
                if (viewFlowNames != null && !viewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowNames)).append(" ");
                }

                // not flow names
                if (notViewFlowNames != null && !notViewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowNames)).append(" ");
                }

                // flow point names
                if (viewFlowPointNames != null && !viewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowPointNames)).append(" ");
                }

                // not flow point names
                if (notViewFlowPointNames != null && !notViewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowPointNames)).append(" ");
                }

                // Free text search
               if ((freeTextSearchList != null && !freeTextSearchList.isEmpty())
                        || (viewLables != null && !viewLables.isEmpty())
                        || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                        || (notViewLables != null && !notViewLables.isEmpty())
                        || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                        && dataSizePartitionList != null && !dataSizePartitionList.isEmpty()) {

                    prepareStatement.append("AND ( ");
                    int size = dataSizePartitionList.size();
                    for (int i = 0; i < size; i++) {

                        String logMessageDataPartition = dataSizePartitionList.get(i);
                        StringBuilder partitionBuilder = LogMessagePrepareStatements.fetch_LogMessageIdsFromPartition_IdVariable(
                                fromDate,
                                toDate,
                                "ID",
                                viewLables,
                                viewMimeTypes,
                                notViewLables,
                                notViewMimeTypes,
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

                prepareStatement.append(" ) ");

            }

            if (orgsqlPartitionSyntaxListSize >= 3) {

                prepareStatement.append("OR ");

                // Fetch all application names in between  
                prepareStatement.append("FLOWNAME IN ");
                prepareStatement.append(" ( ");
                prepareStatement.append("SELECT ");
                prepareStatement.append("DISTINCT FLOWNAME ");
                prepareStatement.append("FROM ").append("LogMessage ");
                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" ");

                if ((transactionReferenceId != null && !viewApplicationNames.isEmpty())
                        || (viewFlowNames != null && !viewFlowNames.isEmpty())
                        || (viewFlowPointNames != null && !viewFlowPointNames.isEmpty())
                        || (viewLables != null && !viewLables.isEmpty())
                        || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                        || (notViewApplicationNames != null && !notViewApplicationNames.isEmpty())
                        || (notViewFlowNames != null && !notViewFlowNames.isEmpty())
                        || (notViewFlowPointNames != null && !notViewFlowPointNames.isEmpty())
                        || (notViewLables != null && !notViewLables.isEmpty())
                        || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                        || (freeTextSearchList != null && !freeTextSearchList.isEmpty())) {
                    prepareStatement.append("WHERE ");
                }

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
                    for (String applicationName : viewApplicationNames) {
                        prepareStatement.append("AND ");
                        prepareStatement.append("APPLICATIONNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(applicationName)).append(" ");
                    }
                }

                // not application names
                if (notViewApplicationNames != null && !notViewApplicationNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("APPLICATIONNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewApplicationNames)).append(" ");
                }

                // flow names
                if (viewFlowNames != null && !viewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowNames)).append(" ");
                }

                // not flow names
                if (notViewFlowNames != null && !notViewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowNames)).append(" ");
                }

                // flow point names
                if (viewFlowPointNames != null && !viewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowPointNames)).append(" ");
                }

                // not flow point names
                if (notViewFlowPointNames != null && !notViewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowPointNames)).append(" ");
                }

                // Free text search
              if ((freeTextSearchList != null && !freeTextSearchList.isEmpty())
                        || (viewLables != null && !viewLables.isEmpty())
                        || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                        || (notViewLables != null && !notViewLables.isEmpty())
                        || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                        && dataSizePartitionList != null && !dataSizePartitionList.isEmpty()) {

                    prepareStatement.append("AND ( ");
                    int size = dataSizePartitionList.size();
                    for (int i = 0; i < size; i++) {

                        String logMessageDataPartition = dataSizePartitionList.get(i);
                        StringBuilder partitionBuilder = LogMessagePrepareStatements.fetch_LogMessageIdsFromPartition_IdVariable(
                                fromDate,
                                toDate,
                                "ID",
                                viewLables,
                                viewMimeTypes,
                                notViewLables,
                                notViewMimeTypes,
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

                prepareStatement.append(" ) ");

            }

            // Pagination: Assume that first page <==> 1
            int pageOffset = page - 1;
            if (pageOffset < 0) {
                pageOffset = 0;
            } else {
                pageOffset = pageOffset * pageSize;
            }
            prepareStatement.append("LIMIT ").append(pageSize).append(" OFFSET ").append(pageOffset).append(" ");

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

    public ResultSetConverter fetch_FlowPointNames(
            String fromDate,
            String toDate,
            Integer page,
            Integer pageSize,
            String transactionReferenceId,
            Integer viewError, // 0=false, 1= true and null skip
            List<String> viewApplicationNames,
            List<String> viewFlowNames,
            List<String> viewFlowPointNames,
            List<String> viewLables,
            List<String> viewMimeTypes,
            List<String> notViewApplicationNames,
            List<String> notViewFlowNames,
            List<String> notViewFlowPointNames,
            List<String> notViewLables,
            List<String> notViewMimeTypes,
            List<String> freeTextSearchList,
            List<String> dataSizePartitionList,
            ResultSetConverter converter) {

        try (Connection conn = MysqlConnection.getConnectionRead()) {

            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("SELECT ");
            prepareStatement.append("DISTINCT FLOWPOINTNAME ");
            prepareStatement.append("FROM ").append("LogMessage ");
            prepareStatement.append("WHERE ");

            List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
            int orgsqlPartitionSyntaxListSize = sqlPartitionSyntaxList.size();

            // Fetch all application names from first partition 
            if (orgsqlPartitionSyntaxListSize >= 1) {
                String firstPartition = sqlPartitionSyntaxList.get(0);
                sqlPartitionSyntaxList.remove(0);

                prepareStatement.append("FLOWPOINTNAME IN ");
                prepareStatement.append(" ( ");
                prepareStatement.append("SELECT ");
                prepareStatement.append("DISTINCT FLOWPOINTNAME ");
                prepareStatement.append("FROM ").append("LogMessage ");
                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(firstPartition)).append(" WHERE ");
                prepareStatement.append("UTCSERVERTIMESTAMP >= ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" ");

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
                    for (String applicationName : viewApplicationNames) {
                        prepareStatement.append("AND ");
                        prepareStatement.append("APPLICATIONNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(applicationName)).append(" ");
                    }
                }

                // not application names
                if (notViewApplicationNames != null && !notViewApplicationNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("APPLICATIONNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewApplicationNames)).append(" ");
                }

                // flow names
                if (viewFlowNames != null && !viewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowNames)).append(" ");
                }

                // not flow names
                if (notViewFlowNames != null && !notViewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowNames)).append(" ");
                }

                // flow point names
                if (viewFlowPointNames != null && !viewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowPointNames)).append(" ");
                }

                // not flow point names
                if (notViewFlowPointNames != null && !notViewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowPointNames)).append(" ");
                }

                // Free text search
               if ((freeTextSearchList != null && !freeTextSearchList.isEmpty())
                        || (viewLables != null && !viewLables.isEmpty())
                        || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                        || (notViewLables != null && !notViewLables.isEmpty())
                        || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                        && dataSizePartitionList != null && !dataSizePartitionList.isEmpty()) {

                    prepareStatement.append("AND ( ");
                    int size = dataSizePartitionList.size();
                    for (int i = 0; i < size; i++) {

                        String logMessageDataPartition = dataSizePartitionList.get(i);
                        StringBuilder partitionBuilder = LogMessagePrepareStatements.fetch_LogMessageIdsFromPartition_IdVariable(
                                fromDate,
                                toDate,
                                "ID",
                                viewLables,
                                viewMimeTypes,
                                notViewLables,
                                notViewMimeTypes,
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
                prepareStatement.append(" ) ");

            }

            if (orgsqlPartitionSyntaxListSize >= 2) {
                String lastPartition = sqlPartitionSyntaxList.get(sqlPartitionSyntaxList.size() - 1);
                sqlPartitionSyntaxList.remove(sqlPartitionSyntaxList.size() - 1);

                // Remove first and last
                prepareStatement.append("OR ");

                // Fetch all application names from last partition 
                prepareStatement.append("FLOWPOINTNAME IN ");
                prepareStatement.append(" ( ");
                prepareStatement.append("SELECT ");
                prepareStatement.append("DISTINCT FLOWPOINTNAME ");
                prepareStatement.append("FROM ").append("LogMessage ");
                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(lastPartition)).append(" WHERE ");
                prepareStatement.append("UTCSERVERTIMESTAMP < ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");

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
                    for (String applicationName : viewApplicationNames) {
                        prepareStatement.append("AND ");
                        prepareStatement.append("APPLICATIONNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(applicationName)).append(" ");
                    }
                }

                // not application names
                if (notViewApplicationNames != null && !notViewApplicationNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("APPLICATIONNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewApplicationNames)).append(" ");
                }

                // flow names
                if (viewFlowNames != null && !viewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowNames)).append(" ");
                }

                // not flow names
                if (notViewFlowNames != null && !notViewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowNames)).append(" ");
                }

                // flow point names
                if (viewFlowPointNames != null && !viewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowPointNames)).append(" ");
                }

                // not flow point names
                if (notViewFlowPointNames != null && !notViewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowPointNames)).append(" ");
                }

                // Free text search
               if ((freeTextSearchList != null && !freeTextSearchList.isEmpty())
                        || (viewLables != null && !viewLables.isEmpty())
                        || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                        || (notViewLables != null && !notViewLables.isEmpty())
                        || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                        && dataSizePartitionList != null && !dataSizePartitionList.isEmpty()) {
                   
                    prepareStatement.append("AND ( ");
                    int size = dataSizePartitionList.size();
                    for (int i = 0; i < size; i++) {

                        String logMessageDataPartition = dataSizePartitionList.get(i);
                        StringBuilder partitionBuilder = LogMessagePrepareStatements.fetch_LogMessageIdsFromPartition_IdVariable(
                                fromDate,
                                toDate,
                                "ID",
                                viewLables,
                                viewMimeTypes,
                                notViewLables,
                                notViewMimeTypes,
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

                prepareStatement.append(" ) ");

            }

            if (orgsqlPartitionSyntaxListSize >= 3) {

                prepareStatement.append("OR ");

                // Fetch all application names in between  
                prepareStatement.append("FLOWPOINTNAME IN ");
                prepareStatement.append(" ( ");
                prepareStatement.append("SELECT ");
                prepareStatement.append("DISTINCT FLOWPOINTNAME ");
                prepareStatement.append("FROM ").append("LogMessage ");
                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" ");

                if ((transactionReferenceId != null && !viewApplicationNames.isEmpty())
                        || (viewFlowNames != null && !viewFlowNames.isEmpty())
                        || (viewFlowPointNames != null && !viewFlowPointNames.isEmpty())
                        || (viewLables != null && !viewLables.isEmpty())
                        || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                        || (notViewApplicationNames != null && !notViewApplicationNames.isEmpty())
                        || (notViewFlowNames != null && !notViewFlowNames.isEmpty())
                        || (notViewFlowPointNames != null && !notViewFlowPointNames.isEmpty())
                        || (notViewLables != null && !notViewLables.isEmpty())
                        || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                        || (freeTextSearchList != null && !freeTextSearchList.isEmpty())) {
                    prepareStatement.append("WHERE ");
                }

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
                    for (String applicationName : viewApplicationNames) {
                        prepareStatement.append("AND ");
                        prepareStatement.append("APPLICATIONNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(applicationName)).append(" ");
                    }
                }

                // not application names
                if (notViewApplicationNames != null && !notViewApplicationNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("APPLICATIONNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewApplicationNames)).append(" ");
                }

                // flow names
                if (viewFlowNames != null && !viewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowNames)).append(" ");
                }

                // not flow names
                if (notViewFlowNames != null && !notViewFlowNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowNames)).append(" ");
                }

                // flow point names
                if (viewFlowPointNames != null && !viewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowPointNames)).append(" ");
                }

                // not flow point names
                if (notViewFlowPointNames != null && !notViewFlowPointNames.isEmpty()) {
                    prepareStatement.append("AND ");
                    prepareStatement.append("FLOWPOINTNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowPointNames)).append(" ");
                }

                // Free text search
                if ((freeTextSearchList != null && !freeTextSearchList.isEmpty())
                        || (viewLables != null && !viewLables.isEmpty())
                        || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                        || (notViewLables != null && !notViewLables.isEmpty())
                        || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                        && dataSizePartitionList != null && !dataSizePartitionList.isEmpty()) {

                    prepareStatement.append("AND ( ");
                    int size = dataSizePartitionList.size();
                    for (int i = 0; i < size; i++) {

                        String logMessageDataPartition = dataSizePartitionList.get(i);
                        StringBuilder partitionBuilder = LogMessagePrepareStatements.fetch_LogMessageIdsFromPartition_IdVariable(
                                fromDate,
                                toDate,
                                "ID",
                                viewLables,
                                viewMimeTypes,
                                notViewLables,
                                notViewMimeTypes,
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

                prepareStatement.append(" ) ");

            }

            // Pagination: Assume that first page <==> 1
            int pageOffset = page - 1;
            if (pageOffset < 0) {
                pageOffset = 0;
            } else {
                pageOffset = pageOffset * pageSize;
            }
            prepareStatement.append("LIMIT ").append(pageSize).append(" OFFSET ").append(pageOffset).append(" ");

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

    public ResultSetConverter fetch_logMessageList(
            Long id,
            Integer partitionId,
            String fromDate,
            String toDate,
            Integer page,
            Integer pageSize,
            String transactionReferenceId,
            Integer viewError, // 0=false, 1= true and null skip
            List<String> viewApplicationNames,
            List<String> viewFlowNames,
            List<String> viewFlowPointNames,
            List<String> viewLables,
            List<String> viewMimeTypes,
            List<String> notViewApplicationNames,
            List<String> notViewFlowNames,
            List<String> notViewFlowPointNames,
            List<String> notViewLables,
            List<String> notViewMimeTypes,
            List<String> freeTextSearchList,
            List<String> dataSizePartitionList,
            ResultSetConverter converter) throws Exception {

        try (Connection conn = MysqlConnection.getConnectionRead()) {

            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("SELECT ");
            prepareStatement.append("ID, PARTITION_ID, APPLICATIONNAME, EXPIREDDATE, FLOWNAME, FLOWPOINTNAME, ");
            prepareStatement.append("ISERROR, TRANSACTIONREFERENCEID, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP ");
            prepareStatement.append("FROM ").append("LogMessage ");

            if (partitionId != null) {
                String sqlPartitionSyntax = DatabasePartitionHelper.getPartitionSyntax(partitionId);
                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntax)).append(" WHERE ");
            } else {
                List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" WHERE ");
            }

            if (id != null) {
                prepareStatement.append("ID = ").append(id).append(" ");
                prepareStatement.append("AND ");
            }

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
                prepareStatement.append("APPLICATIONNAME IN ").append(PrepareStatementHelper.toSQLList(viewApplicationNames)).append(" ");
            }

            // not application names
            if (notViewApplicationNames != null && !notViewApplicationNames.isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append("APPLICATIONNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewApplicationNames)).append(" ");
            }

            // flow names
            if (viewFlowNames != null && !viewFlowNames.isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append("FLOWNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowNames)).append(" ");
            }

            // not flow names
            if (notViewFlowNames != null && !notViewFlowNames.isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append("FLOWNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowNames)).append(" ");
            }

            // flow point names
            if (viewFlowPointNames != null && !viewFlowPointNames.isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append("FLOWPOINTNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowPointNames)).append(" ");
            }

            // not flow point names
            if (notViewFlowPointNames != null && !notViewFlowPointNames.isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append("FLOWPOINTNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowPointNames)).append(" ");
            }

            // Free text search
            if ((freeTextSearchList != null && !freeTextSearchList.isEmpty())
                        || (viewLables != null && !viewLables.isEmpty())
                        || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                        || (notViewLables != null && !notViewLables.isEmpty())
                        || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                        && dataSizePartitionList != null && !dataSizePartitionList.isEmpty()) {

                prepareStatement.append("AND ( ");
                int size = dataSizePartitionList.size();
                for (int i = 0; i < size; i++) {

                    String logMessageDataPartition = dataSizePartitionList.get(i);

                    StringBuilder partitionBuilder;
                    if (id != null) {

                        partitionBuilder = LogMessagePrepareStatements.fetch_LogMessageIdsFromPartition_IdValue(
                                fromDate,
                                toDate,
                                id,
                                viewLables,
                                viewMimeTypes,
                                notViewLables,
                                notViewMimeTypes,
                                logMessageDataPartition,
                                freeTextSearchList
                        );

                    } else {
                        partitionBuilder = LogMessagePrepareStatements.fetch_LogMessageIdsFromPartition_IdVariable(
                                fromDate,
                                toDate,
                                "ID",
                                viewLables,
                                viewMimeTypes,
                                notViewLables,
                                notViewMimeTypes,
                                logMessageDataPartition,
                                freeTextSearchList
                        );
                    }

                    prepareStatement.append("ID = ").append(" ( ").append(partitionBuilder.toString()).append(" ) ");

                    if (i < size - 1) {
                        prepareStatement.append("OR ");
                    }

                }

                prepareStatement.append(" ) ");
            }

            // 
            // Order by
            if (transactionReferenceId == null || transactionReferenceId.isEmpty()) {
                // prepareStatement.append("ORDER BY UTCSERVERTIMESTAMP DESC, UTCLOCALTIMESTAMP DESC ");
                prepareStatement.append("ORDER BY UTCSERVERTIMESTAMP DESC ");
            } else {
                // prepareStatement.append("ORDER BY UTCLOCALTIMESTAMP DESC, UTCSERVERTIMESTAMP DESC ");
                prepareStatement.append("ORDER BY UTCLOCALTIMESTAMP DESC ");
            }

            // Pagination: Assume that first page <==> 1
            int pageOffset = page - 1;
            if (pageOffset < 0) {
                pageOffset = 0;
            } else {
                pageOffset = pageOffset * pageSize;
            }
            prepareStatement.append("LIMIT ").append(pageSize).append(" OFFSET ").append(pageOffset).append(" ");

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

    public ResultSetConverter fetch_LogMessageData(
            Long logMessageId,
            Integer partitionId,
            List<String> dataSizePartitionList,
            ResultSetConverter converter) {

        try (Connection conn = MysqlConnection.getConnectionRead()) {

            // should be from propertie file 
            String partitionBefore;
            String partition;
            String partitionAfter;

            int partitionNumber = 732;
            int modulo = partitionId % partitionNumber;
            if (modulo == 0) {
                partitionBefore = DatabasePartitionHelper.mysql_partition_prefix + (partitionId - 1);
                partition = DatabasePartitionHelper.mysql_partition_prefix + (partitionId);
                partitionAfter = DatabasePartitionHelper.mysql_partition_prefix + (1);
            } else if (modulo == 1) {
                partitionBefore = DatabasePartitionHelper.mysql_partition_prefix + (partitionNumber);
                partition = DatabasePartitionHelper.mysql_partition_prefix + (partitionId);
                partitionAfter = DatabasePartitionHelper.mysql_partition_prefix + (partitionId + 1);
            } else {
                partitionBefore = DatabasePartitionHelper.mysql_partition_prefix + (partitionId - 1);
                partition = DatabasePartitionHelper.mysql_partition_prefix + (partitionId);
                partitionAfter = DatabasePartitionHelper.mysql_partition_prefix + (partitionId + 1);
            }

            List<String> sqlPartitionSyntaxList = new ArrayList();
            sqlPartitionSyntaxList.add(partitionBefore);
            sqlPartitionSyntaxList.add(partition);
            sqlPartitionSyntaxList.add(partitionAfter);

            for (String databaseSizePartition : dataSizePartitionList) {

                StringBuilder prepareStatement = new StringBuilder();
                prepareStatement.append("SELECT ");
                prepareStatement.append("ID, PARTITION_ID, CONTENT, LABEL, MIMETYPE, MODIFIED, ");
                prepareStatement.append("CONTENTSIZE, SEARCHABLE, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP, LOGMESSAGE_ID ");
                prepareStatement.append("FROM ").append(databaseSizePartition).append(" ");

                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" ");
                prepareStatement.append("WHERE LOGMESSAGE_ID = ").append(logMessageId.toString());

                CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
                ResultSet rs = stmt.executeQuery();

                if (rs != null) {

                    converter.convert(rs);
                    rs.close();
                    stmt.close();

                }
            }

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

    public ResultSetConverter fetch_LogMessageDataFromSizePartition(
            String fromDate,
            String toDate,
            String logMessageId,
            String logMessageDataSizePartition,
            List<String> freeTextSearchList,
            ResultSetConverter converter
    ) throws Exception {

        try (Connection conn = MysqlConnection.getConnectionRead()) {

            StringBuilder prepareStatement = new StringBuilder();
            prepareStatement.append("SELECT ");
            prepareStatement.append("ID, PARTITION_ID, LABEL, CONTENT, MIMETYPE, CONTENTSIZE, MODIFIED, ");
            prepareStatement.append("SEARCHABLE, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP, LOGMESSAGE_ID ");
            prepareStatement.append("FROM ").append(logMessageDataSizePartition).append(" ");

            List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
            prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" WHERE ");

            // Between date
            prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");

            if (logMessageId != null && !logMessageId.isEmpty()) {
                prepareStatement.append("AND ");
                prepareStatement.append(logMessageDataSizePartition).append(".LOGMESSAGE_ID = ").append(logMessageId).append(" ");
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

}
