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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stefan Andersson
 */
public class LogMessageQueries {

    private static final Logger logger = Logger.getLogger(LogMessageQueries.class.getName());

    public ResultSetConverter fetch_labelContent(
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
            List<String> notViewApplicationNames,
            List<String> notViewFlowNames,
            List<String> notViewFlowPointNames,
            List<Tuple> tuples,
            List<String> dataSizePartitionList,
            ResultSetConverter converter
    ) {

        StringBuilder prepareStatement = new StringBuilder();
        try (Connection conn = MysqlConnection.getConnectionRead()) {

            prepareStatement.append("SELECT ");
            prepareStatement.append("ID, PARTITION_ID, APPLICATIONNAME, EXPIREDDATE, FLOWNAME, FLOWPOINTNAME, ");
            prepareStatement.append("ISERROR, TRANSACTIONREFERENCEID, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP, PAYLOADSIZE ");
            prepareStatement.append("FROM ").append("LogMessage ");

            // PartitionId
            if (partitionId != null) {
                appendPartitionId(partitionId, prepareStatement);
            } else {
                appendPartitionId(fromDate, toDate, prepareStatement);
            }

            // Between date
            prepareStatement.append("WHERE ");
            appendBetweenDate(fromDate, toDate, prepareStatement);

            // Transaction ref ID
            appendTransactionReferenceId(transactionReferenceId, prepareStatement);

            // view error
            appendViewError(viewError, prepareStatement);

            // application names
            appendViewApplicationNames(viewApplicationNames, prepareStatement);

            // not application names
            appendNotViewApplicationNames(notViewApplicationNames, prepareStatement);

            // flow names
            appendViewFlowNames(viewFlowNames, prepareStatement);

            // not flow names
            appendNotViewFlowNames(notViewFlowNames, prepareStatement);

            // flow point names
            appendViewFlowPointNames(viewFlowPointNames, prepareStatement);

            // not flow point names
            appendNotViewFlowPointNames(notViewFlowPointNames, prepareStatement);

            for (String fromTable : dataSizePartitionList) {

                prepareStatement.append("AND LogMessage.ID IN ");
                prepareStatement.append("( ");

                prepareStatement.append("SELECT ");
                prepareStatement.append("DISTINCT ").append(fromTable).append(".LOGMESSAGE_ID").append(" ");
                prepareStatement.append("FROM ").append(fromTable).append(" ");

                // PartitionId
                if (partitionId != null) {
                    appendPartitionId(partitionId, prepareStatement);
                } else {
                    appendPartitionId(fromDate, toDate, prepareStatement);
                }

                prepareStatement.append("WHERE ");
                prepareStatement.append(fromTable).append(".LOGMESSAGE_ID = ").append("LogMessage.ID").append(" ");

                if (!tuples.isEmpty()) {
                    prepareStatement.append("AND ");
                }

                int size = tuples.size();
                for (int i = 0; i < size; i++) {

                    Tuple tuple = tuples.get(i);
                    prepareStatement.append(" ( ");
                    prepareStatement.append("LABEL = ").append(PrepareStatementHelper.toSQLValue(tuple.getLabel())).append(" ");
                    prepareStatement.append("AND ");
                    prepareStatement.append("CONTENT = ").append(PrepareStatementHelper.toSQLValue(tuple.getSearch())).append(" ");
                    prepareStatement.append(" ) ");

                    if (i < size - 1) {
                        prepareStatement.append(" OR ");
                    }
                }

                prepareStatement.append(") ");

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

            //System.err.println(prepareStatement.toString());
            CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
            ResultSet rs = stmt.executeQuery();
            converter.convert(rs);
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException sqlError) {
            logger.log(Level.SEVERE, sqlError.getMessage());
            logger.log(Level.SEVERE, prepareStatement.toString());
            return converter;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            logger.log(Level.SEVERE, prepareStatement.toString());
            return converter;

        }
        return converter;

    }

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

        StringBuilder prepareStatement = new StringBuilder();
        try (Connection conn = MysqlConnection.getConnectionRead()) {

            prepareStatement.append("SELECT ");
            prepareStatement.append("DISTINCT APPLICATIONNAME ");
            prepareStatement.append("FROM ").append("LogMessage ");

            List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
            if (sqlPartitionSyntaxList.size() < DatabasePartitionHelper.MAX_PARTITIONS) {
                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" ");
            }

            // Between date
            prepareStatement.append("WHERE ");
            prepareStatement.append("APPLICATIONNAME IS NOT NULL ").append("AND ");

            appendBetweenDate(fromDate, toDate, prepareStatement);

            // Transaction ref ID
            appendTransactionReferenceId(transactionReferenceId, prepareStatement);

            // view error
            appendViewError(viewError, prepareStatement);

            // application names
            appendViewApplicationNames(viewApplicationNames, prepareStatement);

            // not application names
            appendNotViewApplicationNames(notViewApplicationNames, prepareStatement);

            // flow names
            appendViewFlowNames(viewFlowNames, prepareStatement);

            // not flow names
            appendNotViewFlowNames(notViewFlowNames, prepareStatement);

            // flow point names
            appendViewFlowPointNames(viewFlowPointNames, prepareStatement);

            // not flow point names
            appendNotViewFlowPointNames(notViewFlowPointNames, prepareStatement);

            // Free text search
            if ((freeTextSearchList != null && !freeTextSearchList.isEmpty())
                    || (viewLables != null && !viewLables.isEmpty())
                    || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                    || (notViewLables != null && !notViewLables.isEmpty())
                    || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                    && dataSizePartitionList != null && !dataSizePartitionList.isEmpty()) {

                prepareStatement.append("AND ( ");
                int dataSizePartitionSize = dataSizePartitionList.size();
                for (int i = 0; i < dataSizePartitionSize; i++) {

                    int freeTextSearchSize = freeTextSearchList.size();
                    prepareStatement.append("( ");
                    for (int j = 0; j < freeTextSearchSize; j++) {

                        StringBuilder partitionBuilder = fetch_LogMessageIdsFromPartition_IdVariable(
                                fromDate,
                                toDate,
                                "ID",
                                viewLables,
                                viewMimeTypes,
                                notViewLables,
                                notViewMimeTypes,
                                dataSizePartitionList.get(i),
                                freeTextSearchList.get(j)
                        );

                        prepareStatement.append("ID = ").append(" ( ").append(partitionBuilder.toString()).append(" ) ");

                        if (j < freeTextSearchSize - 1) {
                            prepareStatement.append("AND ");
                        }
                    }

                    prepareStatement.append(") ");
                    if (i < dataSizePartitionSize - 1) {
                        prepareStatement.append("OR ");
                    }

                }

                prepareStatement.append(" ) ");
            }

            // System.err.println("APP_NAME : " + prepareStatement.toString());
            CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
            ResultSet rs = stmt.executeQuery();
            converter.convert(rs);
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException sqlError) {
            logger.log(Level.SEVERE, sqlError.getMessage());
            logger.log(Level.SEVERE, prepareStatement.toString());
            return converter;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            logger.log(Level.SEVERE, prepareStatement.toString());
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

        StringBuilder prepareStatement = new StringBuilder();
        try (Connection conn = MysqlConnection.getConnectionRead()) {

            prepareStatement.append("SELECT ");
            prepareStatement.append("DISTINCT FLOWNAME ");
            prepareStatement.append("FROM ").append("LogMessage ");

            List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
            if (sqlPartitionSyntaxList.size() < DatabasePartitionHelper.MAX_PARTITIONS) {
                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" ");
            }

            // Between date
            prepareStatement.append("WHERE ");
            prepareStatement.append("FLOWNAME IS NOT NULL ").append("AND ");

            appendBetweenDate(fromDate, toDate, prepareStatement);

            // Transaction ref ID
            appendTransactionReferenceId(transactionReferenceId, prepareStatement);

            // view error
            appendViewError(viewError, prepareStatement);

            // application names
            appendViewApplicationNames(viewApplicationNames, prepareStatement);

            // not application names
            appendNotViewApplicationNames(notViewApplicationNames, prepareStatement);

            // flow names
            appendViewFlowNames(viewFlowNames, prepareStatement);

            // not flow names
            appendNotViewFlowNames(notViewFlowNames, prepareStatement);

            // flow point names
            appendViewFlowPointNames(viewFlowPointNames, prepareStatement);

            // not flow point names
            appendNotViewFlowPointNames(notViewFlowPointNames, prepareStatement);

            // Free text search
            if ((freeTextSearchList != null && !freeTextSearchList.isEmpty())
                    || (viewLables != null && !viewLables.isEmpty())
                    || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                    || (notViewLables != null && !notViewLables.isEmpty())
                    || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                    && dataSizePartitionList != null && !dataSizePartitionList.isEmpty()) {

                prepareStatement.append("AND ( ");
                int dataSizePartitionSize = dataSizePartitionList.size();
                for (int i = 0; i < dataSizePartitionSize; i++) {

                    int freeTextSearchSize = freeTextSearchList.size();
                    prepareStatement.append("( ");
                    for (int j = 0; j < freeTextSearchSize; j++) {
                        StringBuilder partitionBuilder = fetch_LogMessageIdsFromPartition_IdVariable(
                                fromDate,
                                toDate,
                                "ID",
                                viewLables,
                                viewMimeTypes,
                                notViewLables,
                                notViewMimeTypes,
                                dataSizePartitionList.get(i),
                                freeTextSearchList.get(j)
                        );

                        prepareStatement.append("ID = ").append(" ( ").append(partitionBuilder.toString()).append(" ) ");

                        if (j < freeTextSearchSize - 1) {
                            prepareStatement.append("AND ");
                        }
                    }

                    prepareStatement.append(") ");
                    if (i < dataSizePartitionSize - 1) {
                        prepareStatement.append("OR ");
                    }

                }

                prepareStatement.append(" ) ");
            }

            // System.err.println("FLOW_NAME : " + prepareStatement.toString());
            CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
            ResultSet rs = stmt.executeQuery();
            converter.convert(rs);
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException sqlError) {
            logger.log(Level.SEVERE, sqlError.getMessage());
            logger.log(Level.SEVERE, prepareStatement.toString());
            return converter;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            logger.log(Level.SEVERE, prepareStatement.toString());
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

        StringBuilder prepareStatement = new StringBuilder();
        try (Connection conn = MysqlConnection.getConnectionRead()) {

            prepareStatement.append("SELECT ");
            prepareStatement.append("DISTINCT FLOWPOINTNAME ");
            prepareStatement.append("FROM ").append("LogMessage ");

            List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
            if (sqlPartitionSyntaxList.size() < DatabasePartitionHelper.MAX_PARTITIONS) {
                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" ");
            }

            // Between date
            prepareStatement.append("WHERE ");
            prepareStatement.append("FLOWPOINTNAME IS NOT NULL ").append("AND ");

            appendBetweenDate(fromDate, toDate, prepareStatement);

            // Transaction ref ID
            appendTransactionReferenceId(transactionReferenceId, prepareStatement);

            // view error
            appendViewError(viewError, prepareStatement);

            // application names
            appendViewApplicationNames(viewApplicationNames, prepareStatement);

            // not application names
            appendNotViewApplicationNames(notViewApplicationNames, prepareStatement);

            // flow names
            appendViewFlowNames(viewFlowNames, prepareStatement);

            // not flow names
            appendNotViewFlowNames(notViewFlowNames, prepareStatement);

            // flow point names
            appendViewFlowPointNames(viewFlowPointNames, prepareStatement);

            // not flow point names
            appendNotViewFlowPointNames(notViewFlowPointNames, prepareStatement);

            // Free text search
            if ((freeTextSearchList != null && !freeTextSearchList.isEmpty())
                    || (viewLables != null && !viewLables.isEmpty())
                    || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                    || (notViewLables != null && !notViewLables.isEmpty())
                    || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                    && dataSizePartitionList != null && !dataSizePartitionList.isEmpty()) {

                prepareStatement.append("AND ( ");
                int dataSizePartitionSize = dataSizePartitionList.size();
                for (int i = 0; i < dataSizePartitionSize; i++) {

                    int freeTextSearchSize = freeTextSearchList.size();
                    prepareStatement.append("( ");
                    for (int j = 0; j < freeTextSearchSize; j++) {

                        StringBuilder partitionBuilder = fetch_LogMessageIdsFromPartition_IdVariable(
                                fromDate,
                                toDate,
                                "ID",
                                viewLables,
                                viewMimeTypes,
                                notViewLables,
                                notViewMimeTypes,
                                dataSizePartitionList.get(i),
                                freeTextSearchList.get(j)
                        );

                        prepareStatement.append("ID = ").append(" ( ").append(partitionBuilder.toString()).append(" ) ");

                        if (j < freeTextSearchSize - 1) {
                            prepareStatement.append("AND ");
                        }

                    }

                    prepareStatement.append(") ");
                    if (i < dataSizePartitionSize - 1) {
                        prepareStatement.append("OR ");
                    }

                }

                prepareStatement.append(" ) ");
            }

            // System.err.println("FLOW_POINT_NAME : " + prepareStatement.toString());
            CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
            ResultSet rs = stmt.executeQuery();
            converter.convert(rs);
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException sqlError) {
            logger.log(Level.SEVERE, sqlError.getMessage());
            logger.log(Level.SEVERE, prepareStatement.toString());
            return converter;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            logger.log(Level.SEVERE, prepareStatement.toString());
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

        StringBuilder prepareStatement = new StringBuilder();
        try (Connection conn = MysqlConnection.getConnectionRead()) {

            prepareStatement.append("SELECT ");
            prepareStatement.append("ID, PARTITION_ID, APPLICATIONNAME, EXPIREDDATE, FLOWNAME, FLOWPOINTNAME, ");
            prepareStatement.append("ISERROR, TRANSACTIONREFERENCEID, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP, PAYLOADSIZE ");
            prepareStatement.append("FROM ").append("LogMessage ");

            // PartitionId
            if (partitionId != null) {
                appendPartitionId(partitionId, prepareStatement);
            } else {
                appendPartitionId(fromDate, toDate, prepareStatement);
            }

            // Between date
            prepareStatement.append("WHERE ");
            appendBetweenDate(fromDate, toDate, prepareStatement);

            // Transaction ref ID
            appendTransactionReferenceId(transactionReferenceId, prepareStatement);

            // view error
            appendViewError(viewError, prepareStatement);

            // application names
            appendViewApplicationNames(viewApplicationNames, prepareStatement);

            // not application names
            appendNotViewApplicationNames(notViewApplicationNames, prepareStatement);

            // flow names
            appendViewFlowNames(viewFlowNames, prepareStatement);

            // not flow names
            appendNotViewFlowNames(notViewFlowNames, prepareStatement);

            // flow point names
            appendViewFlowPointNames(viewFlowPointNames, prepareStatement);

            // not flow point names
            appendNotViewFlowPointNames(notViewFlowPointNames, prepareStatement);

            // Free text search
            if ((freeTextSearchList != null && !freeTextSearchList.isEmpty())
                    || (viewLables != null && !viewLables.isEmpty())
                    || (viewMimeTypes != null && !viewMimeTypes.isEmpty())
                    || (notViewLables != null && !notViewLables.isEmpty())
                    || (notViewMimeTypes != null && !notViewMimeTypes.isEmpty())
                    && dataSizePartitionList != null && !dataSizePartitionList.isEmpty()) {

                prepareStatement.append("AND ( ");
                int dataSizePartitionSize = dataSizePartitionList.size();
                for (int i = 0; i < dataSizePartitionSize; i++) {

                    int freeTextSearchSize = freeTextSearchList.size();
                    prepareStatement.append("( ");
                    for (int j = 0; j < freeTextSearchSize; j++) {

                        StringBuilder partitionBuilder;
                        if (id != null) {

                            partitionBuilder = fetch_LogMessageIdsFromPartition_IdValue(
                                    fromDate,
                                    toDate,
                                    id,
                                    viewLables,
                                    viewMimeTypes,
                                    notViewLables,
                                    notViewMimeTypes,
                                    dataSizePartitionList.get(i),
                                    freeTextSearchList.get(j)
                            );

                            prepareStatement.append("ID = ").append(" ( ").append(partitionBuilder.toString()).append(" ) ");

                        } else {
                            partitionBuilder = fetch_LogMessageIdsFromPartition_IdVariable(
                                    fromDate,
                                    toDate,
                                    "ID",
                                    viewLables,
                                    viewMimeTypes,
                                    notViewLables,
                                    notViewMimeTypes,
                                    dataSizePartitionList.get(i),
                                    freeTextSearchList.get(j)
                            );

                            prepareStatement.append("ID = ").append(" ( ").append(partitionBuilder.toString()).append(" ) ");
                        }

                        if (j < freeTextSearchSize - 1) {
                            prepareStatement.append("AND ");
                        }
                    }

                    prepareStatement.append(") ");
                    if (i < dataSizePartitionSize - 1) {
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

            // System.err.println("MSG LIST : " + prepareStatement.toString());
            CallableStatement stmt = conn.prepareCall(prepareStatement.toString());
            ResultSet rs = stmt.executeQuery();
            converter.convert(rs);
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException sqlError) {
            logger.log(Level.SEVERE, sqlError.getMessage());
            logger.log(Level.SEVERE, prepareStatement.toString());
            return converter;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            logger.log(Level.SEVERE, prepareStatement.toString());
            return converter;

        }
        return converter;

    }

    public ResultSetConverter fetch_LogMessageData(
            Long logMessageId,
            String logMessageUtcServerTimestamp,
            Integer partitionId,
            List<String> dataSizePartitionList,
            ResultSetConverter converter) {

        try (Connection conn = MysqlConnection.getConnectionRead()) {

            List<String> sqlPartitionSyntaxList = new ArrayList();
            if (logMessageUtcServerTimestamp != null && !logMessageUtcServerTimestamp.isEmpty()) {

                // Prefered way!
                sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(logMessageUtcServerTimestamp);

            } else {

                // Backward compatibility stuff
                sqlPartitionSyntaxList.add(DatabasePartitionHelper.getPartitionSyntax(partitionId - 1));
                sqlPartitionSyntaxList.add(DatabasePartitionHelper.getPartitionSyntax(partitionId));
                sqlPartitionSyntaxList.add(DatabasePartitionHelper.getPartitionSyntax(partitionId + 1));

            }

            for (String databaseSizePartition : dataSizePartitionList) {

                StringBuilder prepareStatement = new StringBuilder();
                prepareStatement.append("SELECT ");
                prepareStatement.append("ID, PARTITION_ID, CONTENT, LABEL, MIMETYPE, MODIFIED, ");
                prepareStatement.append("CONTENTSIZE, SEARCHABLE, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP, LOGMESSAGE_ID ");
                prepareStatement.append("FROM ").append(databaseSizePartition).append(" ");

                prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" ");
                prepareStatement.append("WHERE LOGMESSAGE_ID = ").append(logMessageId.toString());

                // System.err.println("DATA_VIEW : " + prepareStatement.toString());
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
            logger.log(Level.SEVERE, sqlError.getMessage());
            return converter;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
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

        StringBuilder prepareStatement = new StringBuilder();
        try (Connection conn = MysqlConnection.getConnectionRead()) {

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
            logger.log(Level.SEVERE, sqlError.getMessage());
            logger.log(Level.SEVERE, prepareStatement.toString());
            return converter;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            logger.log(Level.SEVERE, prepareStatement.toString());
            return converter;

        }
        return converter;

    }

    private StringBuilder fetch_LogMessageIdsFromPartition_IdVariable(
            String fromDate,
            String toDate,
            String logMessageIdLabel,
            List<String> viewLables,
            List<String> viewMimeTypes,
            List<String> notViewLables,
            List<String> notViewMimeTypes,
            String logMessageDataSizePartition,
            String freeText
    ) throws ParseException {

        // fetch partiotion(s)
        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT ");
        prepareStatement.append("DISTINCT ( A.LOGMESSAGE_ID ) ");
        prepareStatement.append("FROM ").append(logMessageDataSizePartition).append(" ");

        // add partition
        if (sqlPartitionSyntaxList.size() < DatabasePartitionHelper.MAX_PARTITIONS) {
            prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" ");
        }
        prepareStatement.append("AS A ");
        prepareStatement.append("WHERE ");
        prepareStatement.append("A.LOGMESSAGE_ID = LogMessage.").append(logMessageIdLabel).append(" ");

        // viewLables
        appendViewLabels(viewLables, prepareStatement);

        // viewMimeTypes
        appendViewMimentypes(viewMimeTypes, prepareStatement);

        // notViewLables
        appendNotViewLabels(notViewLables, prepareStatement);

        // notViewMimeTypes
        appendNotViewMimentypes(notViewMimeTypes, prepareStatement);

        prepareStatement.append("AND ");
        freeText = PrepareStatementHelper.toSQLContainsValue(freeText);
        prepareStatement.append("( A.LABEL LIKE ").append(freeText).append(" ");
        prepareStatement.append("OR ");
        prepareStatement.append("A.CONTENT LIKE ").append(freeText).append(" ) ");

        return prepareStatement;
    }

    private StringBuilder fetch_LogMessageIdsFromPartition_IdValue(
            String fromDate,
            String toDate,
            Long logMessageIdValue,
            List<String> viewLables,
            List<String> viewMimeTypes,
            List<String> notViewLables,
            List<String> notViewMimeTypes,
            String logMessageDataSizePartition,
            String freeText
    ) throws ParseException {

        // fetch partiotion(s)
        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT ");
        prepareStatement.append("DISTINCT ( LOGMESSAGE_ID ) ");
        prepareStatement.append("FROM ").append(logMessageDataSizePartition).append(" ");

        // add partition
        if (sqlPartitionSyntaxList.size() < DatabasePartitionHelper.MAX_PARTITIONS) {
            prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" ");
        }
        prepareStatement.append("AS A ");
        prepareStatement.append("WHERE ");
        prepareStatement.append("A.LOGMESSAGE_ID = ").append(logMessageIdValue).append(" ");

        // viewLables
        appendViewLabels(viewLables, prepareStatement);

        // viewMimeTypes
        appendViewMimentypes(viewMimeTypes, prepareStatement);

        // notViewLables
        appendNotViewLabels(notViewLables, prepareStatement);

        // notViewMimeTypes
        appendNotViewMimentypes(notViewMimeTypes, prepareStatement);

        prepareStatement.append("AND ");
        freeText = PrepareStatementHelper.toSQLContainsValue(freeText);
        prepareStatement.append("( A.LABEL LIKE ").append(freeText).append(" ");
        prepareStatement.append("OR ");
        prepareStatement.append("A.CONTENT LIKE ").append(freeText).append(" ) ");

        return prepareStatement;
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

    private void appendId(Long id, StringBuilder prepareStatement) throws ParseException {
        if (id != null) {
            prepareStatement.append("ID = ").append(id).append(" ");
            prepareStatement.append("AND ");
        }
    }

    private void appendBetweenDate(String fromDate, String toDate, StringBuilder prepareStatement) throws ParseException {
        // Between date
        if (fromDate != null && toDate != null && !fromDate.isEmpty() && !toDate.isEmpty()) {
            prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");
        }
    }

    private void appendTransactionReferenceId(String transactionReferenceId, StringBuilder prepareStatement) throws ParseException {

        // Transaction ref ID
        if (transactionReferenceId != null && !transactionReferenceId.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("TRANSACTIONREFERENCEID LIKE ").append(PrepareStatementHelper.toSQLValue(transactionReferenceId)).append(" ");
        }
    }

    private void appendViewError(Integer viewError, StringBuilder prepareStatement) throws ParseException {

        // view error
        if (viewError != null) {
            prepareStatement.append("AND ");
            prepareStatement.append("ISERROR = ").append(PrepareStatementHelper.toSQLValue(viewError.toString())).append(" ");
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

    private void appendNotViewApplicationNames(List notViewApplicationNames, StringBuilder prepareStatement) throws ParseException {

        // not application names
        if (notViewApplicationNames
                != null && !notViewApplicationNames.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("APPLICATIONNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewApplicationNames)).append(" ");
        }
    }

    private void appendViewFlowNames(List viewFlowNames, StringBuilder prepareStatement) throws ParseException {

        // flow names
        if (viewFlowNames
                != null && !viewFlowNames.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("FLOWNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowNames)).append(" ");
        }
    }

    private void appendNotViewFlowNames(List notViewFlowNames, StringBuilder prepareStatement) throws ParseException {

        // not flow names
        if (notViewFlowNames
                != null && !notViewFlowNames.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("FLOWNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowNames)).append(" ");
        }
    }

    private void appendViewFlowPointNames(List viewFlowPointNames, StringBuilder prepareStatement) throws ParseException {

        // flow point names
        if (viewFlowPointNames
                != null && !viewFlowPointNames.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("FLOWPOINTNAME IN ").append(PrepareStatementHelper.toSQLList(viewFlowPointNames)).append(" ");
        }
    }

    private void appendNotViewFlowPointNames(List notViewFlowPointNames, StringBuilder prepareStatement) throws ParseException {

        // not flow point names
        if (notViewFlowPointNames
                != null && !notViewFlowPointNames.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("FLOWPOINTNAME NOT IN ").append(PrepareStatementHelper.toSQLList(notViewFlowPointNames)).append(" ");
        }
    }

    private void appendViewLabels(List<String> viewLables, StringBuilder prepareStatement) {

        if (viewLables != null && !viewLables.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("LABEL IN ").append(PrepareStatementHelper.toSQLList(viewLables)).append(" ");
        }
    }

    private void appendViewMimentypes(List<String> viewMimeTypes, StringBuilder prepareStatement) {

        if (viewMimeTypes != null && !viewMimeTypes.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("MIMETYPE IN ").append(PrepareStatementHelper.toSQLList(viewMimeTypes)).append(" ");
        }
    }

    private void appendNotViewLabels(List<String> notViewLables, StringBuilder prepareStatement) {

        if (notViewLables != null && !notViewLables.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("LABEL NOT IN ").append(PrepareStatementHelper.toSQLList(notViewLables)).append(" ");
        }
    }

    private void appendNotViewMimentypes(List<String> notViewMimeTypes, StringBuilder prepareStatement) {

        if (notViewMimeTypes != null && !notViewMimeTypes.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("MIMETYPE NOT IN ").append(PrepareStatementHelper.toSQLList(notViewMimeTypes)).append(" ");
        }
    }
}
