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
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

/**
 *
 * @author Stefan Andersson
 */
public class DatabasePartitionHelper {

    public static final int MAX_PARTITIONS;
    public static final String mysql_partition_prefix;
    private static final SimpleDateFormat formatter;
    private static final Calendar calendar1;
    private static final Calendar calendar2;

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        calendar1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mysql_partition_prefix = "p";
        MAX_PARTITIONS = 732; // 0...731
    }

    private static Date dateStringToDate(String dateString) throws ParseException {
        return formatter.parse(dateString);
    }

    public static int calculatePartitionId(String dateString) throws ParseException {
        return DatabasePartitionHelper.calculatePartitionId(DatabasePartitionHelper.dateStringToDate(dateString));
    }

    public static int calculatePartitionId(Date date) {
        return calculatePartitionId(date.getTime());
    }

    public static int calculatePartitionId(Timestamp timestamp) {
        return calculatePartitionId(timestamp.getTime());
    }

    public static int calculatePartitionId(long milliseconds) {

        //  A.M is morning
        int AM = 14;
        calendar1.setTimeInMillis(milliseconds);
        int day = calendar1.get(Calendar.DAY_OF_YEAR);
        int hours = calendar1.get(Calendar.HOUR_OF_DAY);

        int partitionId = (day * 2) - 1;
        if (AM >= hours) {
            partitionId = partitionId - 1;

        }

        return partitionId;
    }

    /*
     public static String getPartitionId_SQL_Syntax(String dateString) throws ParseException {
     return new StringBuilder().append(mysql_partition_prefix).append(DatabasePartitionHelper.calculatePartitionId(dateString)).toString();
     }

     public static String getPartitionId_SQL_Syntax(Date date) {
     return new StringBuilder().append(mysql_partition_prefix).append(DatabasePartitionHelper.calculatePartitionId(date)).toString();
     }
 

     public static String getPartitionId_SQL_Syntax(long milliseconds) {
     return new StringBuilder().append(mysql_partition_prefix).append(calculatePartitionId(milliseconds)).toString();
     }
     */
    public static List<String> getPartitionId_SQL_SyntaxList(String fromDateString) {

        Set<String> set = new HashSet<>();
        List<String> result = new ArrayList<>();
        try {

            // Determ if time is closer than 30 sec to another partition
            Timestamp timeStamp = Timestamp.valueOf(fromDateString);
            BetweenIndexFromTimeStamp between = TimeStampUtils.createBetweenIndexFromTimeStamp(
                    timeStamp,
                    ChronoField.INSTANT_SECONDS,
                    30
            );

            Timestamp floorTimeStamp = between.getFloorTimestamp();
            Timestamp ceilTimeStamp = between.getCeilTimestamp();

            // fetch partition id(s)
            int floorPartition = DatabasePartitionHelper.calculatePartitionId(floorTimeStamp);
            int currentPartition = DatabasePartitionHelper.calculatePartitionId(timeStamp);
            int ceilPartition = DatabasePartitionHelper.calculatePartitionId(ceilTimeStamp);

            // store
            set.add(getPartitionSyntax(floorPartition));
            set.add(getPartitionSyntax(currentPartition));
            set.add(getPartitionSyntax(ceilPartition));

        } catch (IllegalArgumentException iex) {
            // invalid format! Do nothing
        }

        result.addAll(set);
        Collections.sort(result, new PartitionSyntaxListComparator());
        return result;

    }

    public static List<String> getPartitionId_SQL_SyntaxList(String fromDateString, String toDateString) throws ParseException {

        long fromDate = DatabasePartitionHelper.dateStringToDate(fromDateString).getTime();
        long toDate = DatabasePartitionHelper.dateStringToDate(toDateString).getTime();

        // Fast exist
        if (fromDate > toDate) {
            return new ArrayList<>();
        }

        // No idee to search in the future
        long nowDate = new Date().getTime();
        if (nowDate < toDate) {
            toDate = nowDate;
        }

        // Same year?
        // Set the values for the calendar fields YEAR, MONTH, and DAY_OF_MONTH.
        calendar1.setTimeInMillis(fromDate);
        calendar2.setTimeInMillis(toDate);

        int fromYear = calendar1.get(Calendar.YEAR);
        int toYear = calendar2.get(Calendar.YEAR);

        Set<String> set = new HashSet<>();
        List<String> result = new ArrayList<>();
        int fromPartition = DatabasePartitionHelper.calculatePartitionId(fromDate);
        int toPartition = DatabasePartitionHelper.calculatePartitionId(toDate);

        // Same year
        if (fromYear == toYear) {

            for (int i = fromPartition; i <= toPartition; i++) {
                set.add(getPartitionSyntax(i));
            }

            // Different Year but same month    
        } else if (fromPartition == toPartition) {

            // Add all numbers <0....731>
            for (int i = 0; i < MAX_PARTITIONS; i++) {
                set.add(getPartitionSyntax(i));
            }

            // Different Year different month    
        } else {

            // <0....toPartitionId >
            for (int i = 0; i <= toPartition; i++) {
                set.add(getPartitionSyntax(i));
            }

            // <fromPartitionId.....731 >
            for (int i = fromPartition; i < MAX_PARTITIONS; i++) {
                set.add(getPartitionSyntax(i));
            }

        }

        result.addAll(set);
        Collections.sort(result, new PartitionSyntaxListComparator());
        return result;

    }

    /*
     public static List<String> getPartitionId_SQL_SyntaxList(Date fromDate, Date toDate) {
     return getPartitionSyntaxList(DatabasePartitionHelper.calculatePartitionId(fromDate), DatabasePartitionHelper.calculatePartitionId(toDate));
     }

     public static List<String> getPartitionId_SQL_SyntaxList(Timestamp fromTimestamp, Timestamp toTimestamp) {
     return getPartitionSyntaxList(DatabasePartitionHelper.calculatePartitionId(fromTimestamp), DatabasePartitionHelper.calculatePartitionId(toTimestamp));
     }

     public static List<String> getPartitionId_SQL_SyntaxList(long fromMilliseconds, long toMilliseconds) {
     return getPartitionSyntaxList(calculatePartitionId(fromMilliseconds), calculatePartitionId(toMilliseconds));
     }
     */
    public static List<String> getPartitionSyntaxList(int fromPartition, int toPartition) {
        Set<String> set = new HashSet<>();
        for (int i = fromPartition; i <= toPartition; i++) {
            set.add(getPartitionSyntax(i));
        }
        List<String> sqlPartitionSyntaxList = new ArrayList<>(set);
        return sqlPartitionSyntaxList;
    }

    public static String getPartitionSyntax(int partitionId) {
        return mysql_partition_prefix + partitionId;
    }

    private static long daysBetween(long fromDate, long toDate) {

        // Calculate the difference in millisecond between two UTC dates
        long diffInMilis = fromDate - toDate;

        /*
         * Now we have difference between two date in form of millsecond we can
         * easily convert it Minute / Hour / Days by dividing the difference
         * with appropriate value. 1 Second : 1000 milisecond 1 Hour : 60 * 1000
         * millisecond 1 Day : 24 * 60 * 1000 milisecond
         */
        long diffInSecond = diffInMilis / 1000;
        long diffInMinute = diffInMilis / (60 * 1000);
        long diffInHour = diffInMilis / (60 * 60 * 1000);
        long diffInDays = diffInMilis / (24 * 60 * 60 * 1000);

        // System.out.println("Difference in Seconds : " + diffInSecond);
        // System.out.println("Difference in Minute : " + diffInMinute);
        // System.out.println("Difference in Hours : " + diffInHour);
        // System.out.println("Difference in Days : " + diffInDays);
        return diffInDays;
    }

    public static void main(String[] args) throws ParseException {
        DatabasePartitionHelper helper = new DatabasePartitionHelper();
        // System.err.println(DatabasePartitionHelper.getPartitionId_SQL_SyntaxList("2016-11-15 00:00:01", "2017-11-14 03:18:23").size());
        // System.err.println(DatabasePartitionHelper.getPartitionId_SQL_SyntaxList("2016-11-15 00:00:01", "2017-11-14 03:18:23"));

        /*
        System.err.println(DatabasePartitionHelper.getPartitionId_SQL_SyntaxList("1981-01-01 00:00:01", "1981-01-01 23:59:59"));
        System.err.println(DatabasePartitionHelper.getPartitionId_SQL_SyntaxList("1981-12-31 00:00:01", "1981-12-31 23:59:59"));
        System.err.println(DatabasePartitionHelper.getPartitionId_SQL_SyntaxList("2015-01-01 00:00:01", "2015-01-01 23:59:59"));
        System.err.println(DatabasePartitionHelper.getPartitionId_SQL_SyntaxList("2015-12-31 00:00:01", "2015-12-31 23:59:59"));
        System.err.println(DatabasePartitionHelper.getPartitionId_SQL_SyntaxList("2016-01-01 00:00:01", "2016-01-01 23:59:59"));
        System.err.println(DatabasePartitionHelper.getPartitionId_SQL_SyntaxList("2016-12-31 00:00:01", "2016-12-31 23:59:59"));
        System.err.println(DatabasePartitionHelper.getPartitionId_SQL_SyntaxList("2017-01-01 00:00:01", "2017-01-01 23:59:59"));
        System.err.println(DatabasePartitionHelper.getPartitionId_SQL_SyntaxList("2017-12-31 00:00:01", "2017-12-31 23:59:59"));
         */
        System.err.println(DatabasePartitionHelper.getPartitionId_SQL_SyntaxList("2016-11-20 14:30:24.526985", "2016-11-21 05:30:24.526985"));

    }

    private static class PartitionSyntaxListComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            int obj1 = Integer.parseInt(((String) o1).substring(1));
            int obj2 = Integer.parseInt(((String) o2).substring(1));

            if (obj1 > obj2) {
                return 1;
            } else if (obj1 < obj2) {
                return -1;
            } else {
                return 0;
            }
        }

    }

}
