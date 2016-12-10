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

import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Date;

/**
 *
 * @author Stefan Andersson
 */
public class TimeStampUtils {

    protected static final long MilliMuliplier = 1000L;
    protected static final long MicroMuliplier = MilliMuliplier * 1000L;
    protected static final long NanoMuliplier = MicroMuliplier * 1000L;
    protected static final long MysqlNanoMuliplier = MicroMuliplier;

    // number of seconds passed 1970/1/1 00:00:00 GMT.
    private static long sec0;
    // fraction of seconds passed 1970/1/1 00:00:00 GMT, offset by
    // the base System.nanoTime (nano0), in nanosecond unit.
    private static long nano0;

    static {
        // initialize base time in second and fraction of second (ns).
        long curTime = System.currentTimeMillis();
        sec0 = curTime / MilliMuliplier;
        nano0 = (curTime % MilliMuliplier) * MicroMuliplier - System.nanoTime();
    }

    public static Timestamp createSystemNanoTimeStamp() {
        long nano_delta = nano0 + System.nanoTime();
        long sec1 = sec0 + (nano_delta / NanoMuliplier);
        long nano1 = nano_delta % NanoMuliplier;

        Timestamp rtnTs = new Timestamp(sec1 * MilliMuliplier);
        rtnTs.setNanos((int) nano1);
        return rtnTs;

    }

    public static Timestamp createNanoTimeStamp(long milliseconds, long nanoSeconds) {
        long nano1 = nanoSeconds % NanoMuliplier;
        Timestamp rtnTs = new Timestamp(milliseconds);
        rtnTs.setNanos((int) nano1);
        return rtnTs;

    }

    public static Timestamp createNanoTimeStamp(Timestamp timestamp, long nanoSeconds) {
        long nano1 = nanoSeconds % NanoMuliplier;
        timestamp.setNanos((int) nano1);
        return timestamp;
    }

    public static Timestamp createNanoTimeStamp(Date date, long nanoSeconds) {
        return createNanoTimeStamp(date.getTime(), nanoSeconds);
    }

    public static Timestamp createMysqlNanoTimeStamp(long milliseconds, long nanoSeconds) {
        long nano1 = nanoSeconds % MysqlNanoMuliplier;
        Timestamp rtnTs = new Timestamp(milliseconds);
        rtnTs.setNanos((int) nano1);
        return rtnTs;
    }

    public static Timestamp createMysqlNanoTimeStamp(Timestamp timestamp, long nanoSeconds) {
        long nano1 = nanoSeconds % MysqlNanoMuliplier;
        timestamp.setNanos((int) nano1);
        return timestamp;
    }

    public static Timestamp createMysqlNanoTimeStamp(Date date, long nanoSeconds) {
        return createMysqlNanoTimeStamp(date.getTime(), nanoSeconds);
    }

    public static String dateTimeToString(Date date) {
        Format format = getSimpleDateFormater();
        String timeToSeconds = format.format(date);
        return timeToSeconds;
    }

    public static String timeStampToString(Timestamp timestamp) {
        Format format = TimeStampUtils.getSimpleDateFormater();
        String timeToSeconds = format.format(timestamp);
        String nanoTime = Integer.toString(timestamp.getNanos());
        return timeToSeconds + "." + nanoTime;
    }

    public static void createFloorIndexTimeStamp(
            ZonedDateTime input,
            TemporalField roundTo,
            int roundIncrement
    ) {

        /* Extract the field being rounded. */
        int field = input.get(roundTo);

        /* Distance from previous floor. */
        int r = field % roundIncrement;

        /* Find floor. Truncate values to base unit of field. */
        ZonedDateTime floor
                = input.plus(-r, roundTo.getBaseUnit())
                        .truncatedTo(roundTo.getBaseUnit());

        /*
         * Do a half-up rounding.
         *  
         * If (input - floor) < (ceiling - input) 
         * (i.e. floor is closer to input than ceiling)
         *  then return floor, otherwise return ceiling.
         */
        // ZonedDateTime roundedTime = Duration.between(floor, input).compareTo(Duration.between(input, ceiling)) < 0 ? floor : ceiling;
        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String formatted = floor.format(formatter);
        System.out.println(formatted);

    }

    public static BetweenIndexFromTimeStamp createBetweenIndexFromTimeStamp(
            Timestamp timeStamp,
            TemporalField roundTo, // position, iex minutes
            int roundIncrement // time iex 5
    ) {

        ZonedDateTime input = ZonedDateTime.ofInstant(timeStamp.toInstant(), ZoneId.of("UTC"));

        /* Extract the field being rounded. */
        int field = input.get(roundTo);

        /* Distance from previous floor. */
        int r = field % roundIncrement;

        /* Find floor. Truncate values to base unit of field. */
        ZonedDateTime floor
                = input.plus(-r, roundTo.getBaseUnit())
                        .truncatedTo(roundTo.getBaseUnit());

        /* Find ceiling. Truncate values to base unit of field. */
        ZonedDateTime ceil
                = input.plus(roundIncrement - r, roundTo.getBaseUnit())
                        .truncatedTo(roundTo.getBaseUnit());

        return new BetweenIndexFromTimeStamp(floor, ceil);

    }

    private static SimpleDateFormat getSimpleDateFormater() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    private static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public static void main(String[] args) {
        Timestamp now = TimeStampUtils.createSystemNanoTimeStamp();
        BetweenIndexFromTimeStamp between_1 = TimeStampUtils.createBetweenIndexFromTimeStamp(
                now,
                ChronoField.MINUTE_OF_HOUR,
                5
        );

        System.err.println(TimeStampUtils.timeStampToString(now));
        System.err.println(TimeStampUtils.getDateTimeFormatter().format( between_1.getFloorZonedDateTime()));
        System.err.println(TimeStampUtils.getDateTimeFormatter().format( between_1.getCeilZonedDateTime()));
        System.err.println(TimeStampUtils.getSimpleDateFormater().format(between_1.getFloorTimestamp()));
        System.err.println(TimeStampUtils.getSimpleDateFormater().format(between_1.getCeilTimestamp()));

    }

}
