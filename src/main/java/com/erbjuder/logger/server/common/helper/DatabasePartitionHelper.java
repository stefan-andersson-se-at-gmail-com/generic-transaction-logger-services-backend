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
package com.erbjuder.logger.server.common.helper;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Stefan Andersson
 */
public class DatabasePartitionHelper {

    private static final String mysql_partition_prefix = "p";
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
    private static Calendar calendar = Calendar.getInstance();

    public static int getPartitionId() {
        return getPartitionId(new Date());
    }

    public static int getPartitionId(String dateString) throws ParseException {
        return getPartitionId(formatter.parse(dateString));
    }

    public static int getPartitionId(Date date) {
        return getPartitionId(date.getTime());
    }

    public static int getPartitionId(Timestamp timestamp) {
        return getPartitionId(timestamp.getTime());
    }

    public static int getPartitionId(long milliseconds) {
        calendar.setTimeInMillis(milliseconds);
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static String getPartitionId_SQL_Syntax(String dateString) throws ParseException {
        return new StringBuilder().append(mysql_partition_prefix).append(getPartitionId(dateString)).toString();
    }

    public static String getPartitionId_SQL_Syntax(Date date) {
        return new StringBuilder().append(mysql_partition_prefix).append(getPartitionId(date)).toString();
    }

    public static String getPartitionId_SQL_Syntax(Timestamp timestamp) {
        return new StringBuilder().append(mysql_partition_prefix).append(getPartitionId(timestamp)).toString();
    }

    public static String getPartitionId_SQL_Syntax(long milliseconds) {
        return new StringBuilder().append(mysql_partition_prefix).append(getPartitionId(milliseconds)).toString();
    }

    public static List<String> getPartitionId_SQL_SyntaxList(String fromDateString, String toDateString) throws ParseException {
        return getPartitionSyntaxList(getPartitionId(fromDateString), getPartitionId(toDateString));
    }

    public static List<String> getPartitionId_SQL_SyntaxList(Date fromDate, Date toDate) {
        return getPartitionSyntaxList(getPartitionId(fromDate), getPartitionId(toDate));
    }

    public static List<String> getPartitionId_SQL_SyntaxList(Timestamp fromTimestamp, Timestamp toTimestamp) {
        return getPartitionSyntaxList(getPartitionId(fromTimestamp), getPartitionId(toTimestamp));
    }

    public static List<String> getPartitionId_SQL_SyntaxList(long fromMilliseconds, long toMilliseconds) {
        return getPartitionSyntaxList(getPartitionId(fromMilliseconds), getPartitionId(toMilliseconds));
    }

    private static List<String> getPartitionSyntaxList(int fromPartition, int toPartition) {
        List<String> sqlPartitionSyntaxList = new ArrayList<>();
        for (int i = fromPartition; i <= toPartition; i++) {
            sqlPartitionSyntaxList.add(getPartitionId_SQL_Syntax(i));
        }

        return sqlPartitionSyntaxList;
    }

}
