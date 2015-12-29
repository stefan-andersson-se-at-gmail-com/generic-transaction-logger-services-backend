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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Stefan Andersson
 */
public class DatabasePartitionHelper {

    public static final String mysql_partition_prefix = "p";
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Calendar calendar = Calendar.getInstance();

    public static int calculatePartitionId(String dateString) throws ParseException {
        return DatabasePartitionHelper.calculatePartitionId(formatter.parse(dateString));
    }

    public static int calculatePartitionId(Date date) {
        return calculatePartitionId(date.getTime());
    }

    public static int calculatePartitionId(Timestamp timestamp) {
        return calculatePartitionId(timestamp.getTime());
    }

    public static int calculatePartitionId(long milliseconds) {

        //  A.M is morning
        int AM = 12;
        calendar.setTimeInMillis(milliseconds);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);

        // Maximum partition number = 732;
        int partId = day * 2;
        if (AM >= hours) {
            partId = partId - 1;
        }
        return partId;
    }

    public static String getPartitionId_SQL_Syntax(String dateString) throws ParseException {
        return new StringBuilder().append(mysql_partition_prefix).append(DatabasePartitionHelper.calculatePartitionId(dateString)).toString();
    }

    public static String getPartitionId_SQL_Syntax(Date date) {
        return new StringBuilder().append(mysql_partition_prefix).append(DatabasePartitionHelper.calculatePartitionId(date)).toString();
    }

    public static String getPartitionId_SQL_Syntax(Timestamp timestamp) {
        return new StringBuilder().append(mysql_partition_prefix).append(DatabasePartitionHelper.calculatePartitionId(timestamp)).toString();
    }

    public static String getPartitionId_SQL_Syntax(long milliseconds) {
        return new StringBuilder().append(mysql_partition_prefix).append(calculatePartitionId(milliseconds)).toString();
    }

    public static List<String> getPartitionId_SQL_SyntaxList(String fromDateString, String toDateString) throws ParseException {
        return getPartitionSyntaxList(DatabasePartitionHelper.calculatePartitionId(fromDateString), DatabasePartitionHelper.calculatePartitionId(toDateString));
    }

    public static List<String> getPartitionId_SQL_SyntaxList(Date fromDate, Date toDate) {
        return getPartitionSyntaxList(DatabasePartitionHelper.calculatePartitionId(fromDate), DatabasePartitionHelper.calculatePartitionId(toDate));
    }

    public static List<String> getPartitionId_SQL_SyntaxList(Timestamp fromTimestamp, Timestamp toTimestamp) {
        return getPartitionSyntaxList(DatabasePartitionHelper.calculatePartitionId(fromTimestamp), DatabasePartitionHelper.calculatePartitionId(toTimestamp));
    }

    public static List<String> getPartitionId_SQL_SyntaxList(long fromMilliseconds, long toMilliseconds) {
        return getPartitionSyntaxList(calculatePartitionId(fromMilliseconds), calculatePartitionId(toMilliseconds));
    }

    private static List<String> getPartitionSyntaxList(int fromPartition, int toPartition) {
        Set<String> set = new HashSet<>();
        for (int i = fromPartition; i <= toPartition; i++) {
            set.add(mysql_partition_prefix + i);
        }
        List<String> sqlPartitionSyntaxList = new ArrayList<>(set);
        return sqlPartitionSyntaxList;
    }

}
