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
        Format format = getDateFormater();
        String timeToSeconds = format.format(date);
        return timeToSeconds;
    }

    public static String timeStampToString(Timestamp timestamp) {
        Format format = TimeStampUtils.getDateFormater();
        String timeToSeconds = format.format(timestamp);
        String nanoTime = Integer.toString(timestamp.getNanos());
        return timeToSeconds + "." + nanoTime;
    }

    private static SimpleDateFormat getDateFormater() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
