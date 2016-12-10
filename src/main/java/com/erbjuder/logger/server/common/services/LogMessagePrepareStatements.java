package com.erbjuder.logger.server.common.services;

import com.erbjuder.logger.server.common.helper.DatabasePartitionHelper;
import java.text.ParseException;
import java.util.List;

/*
 * Copyright (C) 2015 Stefan Andersson
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
/**
 *
 * @author Stefan Andersson
 */
public class LogMessagePrepareStatements {

    public static StringBuilder fetch_LogMessageIdsFromPartition_IdVariable(
            String fromDate,
            String toDate,
            String logMessageIdLabel,
            List<String> viewLables,
            List<String> viewMimeTypes,
            List<String> notViewLables,
            List<String> notViewMimeTypes,
            String logMessageDataSizePartition,
            List<String> freeTextSearchList
    ) throws ParseException {
        StringBuilder prepareStatement = new StringBuilder();
        String partitionID = logMessageDataSizePartition + "_ID";
        prepareStatement.append("SELECT ");
        prepareStatement.append("DISTINCT ( LOGMESSAGE_ID ) as ").append(partitionID).append(" ");
        prepareStatement.append("FROM ").append(logMessageDataSizePartition).append(" ");
        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
        prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" WHERE ");
        prepareStatement.append(logMessageDataSizePartition).append(".LOGMESSAGE_ID = ").append("LogMessage.").append(logMessageIdLabel).append(" ");
        prepareStatement.append("AND ");
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");

        // viewLables
        if (viewLables != null && !viewLables.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("LABEL IN ").append(PrepareStatementHelper.toSQLList(viewLables)).append(" ");
        }

        // viewMimeTypes
        if (viewMimeTypes != null && !viewMimeTypes.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("MIMETYPE IN ").append(PrepareStatementHelper.toSQLList(viewMimeTypes)).append(" ");
        }

        // notViewLables
        if (notViewLables != null && !notViewLables.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("LABEL NOT IN ").append(PrepareStatementHelper.toSQLList(notViewLables)).append(" ");
        }

        // notViewMimeTypes
        if (notViewMimeTypes != null && !notViewMimeTypes.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("MIMETYPE NOT IN ").append(PrepareStatementHelper.toSQLList(notViewMimeTypes)).append(" ");
        }

        if (freeTextSearchList != null && freeTextSearchList.size() > 0) {
            int size = freeTextSearchList.size();
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

    public static StringBuilder fetch_LogMessageIdsFromPartition_IdValue(
            String fromDate,
            String toDate,
            Long logMessageIdValue,
            List<String> viewLables,
            List<String> viewMimeTypes,
            List<String> notViewLables,
            List<String> notViewMimeTypes,
            String logMessageDataSizePartition,
            List<String> freeTextSearchList
    ) throws ParseException {
        StringBuilder prepareStatement = new StringBuilder();
        String partitionID = logMessageDataSizePartition + "_ID";
        prepareStatement.append("SELECT ");
        prepareStatement.append("DISTINCT ( LOGMESSAGE_ID ) as ").append(partitionID).append(" ");
        prepareStatement.append("FROM ").append(logMessageDataSizePartition).append(" ");
        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
        prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" WHERE ");
        prepareStatement.append(logMessageDataSizePartition).append(".LOGMESSAGE_ID = ").append(logMessageIdValue).append(" ");
        prepareStatement.append("AND ");
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");

        // viewLables
        if (viewLables != null && !viewLables.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("LABEL IN ").append(PrepareStatementHelper.toSQLList(viewLables)).append(" ");
        }

        // viewMimeTypes
        if (viewMimeTypes != null && !viewMimeTypes.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("MIMETYPE IN ").append(PrepareStatementHelper.toSQLList(viewMimeTypes)).append(" ");
        }

        // notViewLables
        if (notViewLables != null && !notViewLables.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("LABEL NOT IN ").append(PrepareStatementHelper.toSQLList(notViewLables)).append(" ");
        }

        // notViewMimeTypes
        if (notViewMimeTypes != null && !notViewMimeTypes.isEmpty()) {
            prepareStatement.append("AND ");
            prepareStatement.append("MIMETYPE NOT IN ").append(PrepareStatementHelper.toSQLList(notViewMimeTypes)).append(" ");
        }

        if (freeTextSearchList != null && freeTextSearchList.size() > 0) {
            int size = freeTextSearchList.size();
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

    public static StringBuilder fetch_logMessagesFromApplicationNames(
            String fromDate,
            String toDate,
            List<String> applicationNames
    ) throws ParseException {

        StringBuilder prepareStatement = LogMessagePrepareStatements.getPrepareStatementLogMessageHeader(fromDate, toDate);
        if (applicationNames != null && !applicationNames.isEmpty()) {
            for (String applicationName : applicationNames) {
                prepareStatement.append("AND ");
                System.err.println("APP NAME={ " + applicationName + " }");
                prepareStatement.append("APPLICATIONNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(applicationName)).append(" ");
            }
        }
        return prepareStatement;
    }

    public static StringBuilder fetch_ApplicationNames(
            String fromDate,
            String toDate,
            List<String> applicationNames
    ) throws ParseException {

        StringBuilder prepareStatement = LogMessagePrepareStatements.getPrepareStatementApplicationNames(fromDate, toDate);
        if (applicationNames != null && !applicationNames.isEmpty()) {
            for (String applicationName : applicationNames) {
                prepareStatement.append("AND ");
                System.err.println("APP NAME={ " + applicationName + " }");
                prepareStatement.append("APPLICATIONNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(applicationName)).append(" ");
            }
        }
        System.err.println("APP NAMES=[ " + prepareStatement.toString() + " ]");
        return prepareStatement;
    }

    public static StringBuilder fetch_logMessagesFromFlowNames(
            String fromDate,
            String toDate,
            List<String> flowNames
    ) throws ParseException {

        StringBuilder prepareStatement = LogMessagePrepareStatements.getPrepareStatementFlowNames(fromDate, toDate);
        if (flowNames != null && !flowNames.isEmpty()) {
            for (String flowName : flowNames) {
                prepareStatement.append("AND ");
                prepareStatement.append("FLOWNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(flowName)).append(" ");
            }
        }
        return prepareStatement;
    }

    public static StringBuilder fetch_logMessagesFromFlowPointNames(
            String fromDate,
            String toDate,
            List<String> flowPointNames
    ) throws ParseException {

        StringBuilder prepareStatement = LogMessagePrepareStatements.getPrepareStatementFlowPointsNames(fromDate, toDate);
        if (flowPointNames != null && !flowPointNames.isEmpty()) {
            for (String flowPointName : flowPointNames) {
                prepareStatement.append("AND ");
                prepareStatement.append("FLOWPOINTNAME LIKE ").append(PrepareStatementHelper.toSQLStartsWithValue(flowPointName)).append(" ");
            }
        }
        return prepareStatement;
    }

    public static StringBuilder fetch_PartitionContainsValue(
            String fromDate,
            String toDate,
            String freeText,
            String databaseSizePartition
    ) throws ParseException {
        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT ");
        prepareStatement.append("ID, PARTITION_ID, CONTENT, LABEL, MIMETYPE, MODIFIED, ");
        prepareStatement.append("CONTENTSIZE, SEARCHABLE, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP, LOGMESSAGE_ID");
        prepareStatement.append("FROM ").append(databaseSizePartition).append(" ");
        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);

        prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" WHERE ");
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

    private static StringBuilder getPrepareStatementLogMessageHeader(
            String fromDate,
            String toDate) throws ParseException {

        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT ");
        prepareStatement.append("ID, PARTITION_ID, APPLICATIONNAME, EXPIREDDATE, FLOWNAME, FLOWPOINTNAME, ");
        prepareStatement.append("ISERROR, TRANSACTIONREFERENCEID, UTCLOCALTIMESTAMP, UTCSERVERTIMESTAMP ");
        prepareStatement.append("FROM ").append("LogMessage ").append(" ");
        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
        prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" WHERE ");
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");
        return prepareStatement;
    }

    private static StringBuilder getPrepareStatementApplicationNames(
            String fromDate,
            String toDate) throws ParseException {

        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT ");
        prepareStatement.append("DISTINCT APPLICATIONNAME ");
        prepareStatement.append("FROM ").append("LogMessage ").append(" ");
        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
        prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" WHERE ");
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");
        return prepareStatement;
    }

    private static StringBuilder getPrepareStatementFlowNames(
            String fromDate,
            String toDate) throws ParseException {

        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT ");
        prepareStatement.append("DISTINCT FLOWNAME ");
        prepareStatement.append("FROM ").append("LogMessage ").append(" ");
        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
        prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" WHERE ");
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");
        return prepareStatement;
    }

    private static StringBuilder getPrepareStatementFlowPointsNames(
            String fromDate,
            String toDate) throws ParseException {

        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT ");
        prepareStatement.append("DISTINCT FLOWPOINTNAME ");
        prepareStatement.append("FROM ").append("LogMessage ").append(" ");
        List<String> sqlPartitionSyntaxList = DatabasePartitionHelper.getPartitionId_SQL_SyntaxList(fromDate, toDate);
        prepareStatement.append("PARTITION ").append(PrepareStatementHelper.toSQL_Partition_List_Syntax(sqlPartitionSyntaxList)).append(" WHERE ");
        prepareStatement.append("UTCSERVERTIMESTAMP BETWEEN ").append(PrepareStatementHelper.toSQLValue(fromDate)).append(" AND ").append(PrepareStatementHelper.toSQLValue(toDate)).append(" ");
        return prepareStatement;
    }

}
