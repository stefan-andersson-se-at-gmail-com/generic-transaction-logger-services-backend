/*
 * Copyright (C) 2017 Stefan Andersson
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
package com.erbjuder.logger.server.rest.services;

import java.util.Date;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

/**
 *
 * @author Stefan Andersson
 */
//@Stateless
//public class EmailNotificationTimer {
//
//    @Resource
//    TimerService timerService;
//
//    @EJB
//    EmailNotificationLogMessageBase emailNotificationPrepareToSend;
//
//    private Date lastProgrammaticTimeout;
//    private Date lastAutomaticTimeout;
//    private static final Logger logger = Logger.getLogger(EmailNotificationTimer.class.getName());
//
//    public void setTimer(long intervalDuration) {
//        logger.info("Setting a programmatic timeout for "
//                + intervalDuration + " milliseconds from now.");
//        Timer timer = timerService.createTimer(intervalDuration,
//                "Created new programmatic timer");
//    }
//
//    @Timeout
//    public void programmaticTimeout(Timer timer) {
//        this.setLastProgrammaticTimeout(new Date());
//        logger.info("Programmatic timeout occurred.");
//    }
//
//    /**
//     * Automatic timer once a day
//     */
//    //@Schedule(second = "*/30", minute = "*", hour = "*", persistent = false)
//    //@Schedule(second = "0", minute = "0", hour = "0", dayOfWeek = "*", dayOfMonth = "1,15", month = "*", year = "*")
//    //@Schedule(second = "0", minute = "0", hour = "19", dayOfWeek = "*", dayOfMonth = "*", month = "*", year = "*")
//    public void automaticTimeout() {
//        emailNotificationPrepareToSend.sendPreparedNotifications();
//    }
//
//    public String getLastProgrammaticTimeout() {
//        if (lastProgrammaticTimeout != null) {
//            return lastProgrammaticTimeout.toString();
//        } else {
//            return "never";
//        }
//    }
//
//    public void setLastProgrammaticTimeout(Date lastTimeout) {
//        this.lastProgrammaticTimeout = lastTimeout;
//    }
//
//    public String getLastAutomaticTimeout() {
//        if (lastAutomaticTimeout != null) {
//            return lastAutomaticTimeout.toString();
//        } else {
//            return "never";
//        }
//    }
//
//    public void setLastAutomaticTimeout(Date lastAutomaticTimeout) {
//        this.lastAutomaticTimeout = lastAutomaticTimeout;
//    }
//
//}
