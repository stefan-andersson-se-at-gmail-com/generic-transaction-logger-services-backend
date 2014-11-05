/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erbjuder.logger.server.common.helper;

import java.sql.Timestamp;
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

}
