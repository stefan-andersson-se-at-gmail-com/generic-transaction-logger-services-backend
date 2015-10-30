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
package com.erbjuder.logger.server.timer;

import com.erbjuder.logger.server.common.helper.DataBase;
import com.erbjuder.logger.server.rest.services.dao.MysqlConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

/**
 *
 * @author Stefan Andersson
 */
public class LogCleanerSessionBeanBase extends MysqlConnection {

    @Resource
    TimerService timerService;
    private Date lastProgrammaticTimeout;
    private Date lastAutomaticTimeout;
    private static final Logger logger = Logger.getLogger(LogCleanerSessionBeanBase.class.getName());

    public void setTimer(long intervalDuration) {
        logger.info("Setting a programmatic timeout for "
                + intervalDuration + " milliseconds from now.");
        Timer timer = timerService.createTimer(intervalDuration,
                "Created new programmatic timer");
    }

    @Timeout
    public void programmaticTimeout(Timer timer) {
        this.setLastProgrammaticTimeout(new Date());
        logger.info("Programmatic timeout occurred.");
    }

    /**
     * Automatic timer once a day
     */
    //@Schedule(second = "50", minute = "*", hour = "*", dayOfWeek = "*", dayOfMonth = "*", month = "*", year = "*")
    //@Schedule(second = "0", minute = "0", hour = "0", dayOfWeek = "*", dayOfMonth = "1,15", month = "*", year = "*")
    @Schedule(second = "0", minute = "0", hour = "19", dayOfWeek = "*", dayOfMonth = "*", month = "*", year = "*")
    public void automaticTimeout() {

        logger.info("[ Automatic clean timeout occured ]");
        Date currentDate = new Date();
        this.setLastAutomaticTimeout(currentDate);
        java.sql.Date sqlDate = new java.sql.Date(currentDate.getTime());

        try (Connection connection = MysqlConnection()) {

            String logMessageDelete = getLogMessagePrepaterStatementMysqlDelete();
            PreparedStatement logMessagePreparedStatement = connection.prepareStatement(logMessageDelete);
            logMessagePreparedStatement.setDate(1, sqlDate);
            logMessagePreparedStatement.executeQuery();

            String deletePartition_1 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_01_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_1 = connection.prepareStatement(deletePartition_1);
            preparedStatementDeletePartition_1.setDate(1, sqlDate);
            preparedStatementDeletePartition_1.executeQuery();

            String deletePartition_2 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_02_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_2 = connection.prepareStatement(deletePartition_2);
            preparedStatementDeletePartition_2.setDate(1, sqlDate);
            preparedStatementDeletePartition_2.executeQuery();

            String deletePartition_3 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_03_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_3 = connection.prepareStatement(deletePartition_3);
            preparedStatementDeletePartition_3.setDate(1, sqlDate);
            preparedStatementDeletePartition_3.executeQuery();

            String deletePartition_4 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_04_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_4 = connection.prepareStatement(deletePartition_4);
            preparedStatementDeletePartition_4.setDate(1, sqlDate);
            preparedStatementDeletePartition_4.executeQuery();

            String deletePartition_5 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_05_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_5 = connection.prepareStatement(deletePartition_5);
            preparedStatementDeletePartition_5.setDate(1, sqlDate);
            preparedStatementDeletePartition_5.executeQuery();

            String deletePartition_6 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_06_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_6 = connection.prepareStatement(deletePartition_6);
            preparedStatementDeletePartition_6.setDate(1, sqlDate);
            preparedStatementDeletePartition_6.executeQuery();

            String deletePartition_7 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_07_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_7 = connection.prepareStatement(deletePartition_7);
            preparedStatementDeletePartition_7.setDate(1, sqlDate);
            preparedStatementDeletePartition_7.executeQuery();

            String deletePartition_8 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_08_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_8 = connection.prepareStatement(deletePartition_8);
            preparedStatementDeletePartition_8.setDate(1, sqlDate);
            preparedStatementDeletePartition_8.executeQuery();

            String deletePartition_9 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_09_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_9 = connection.prepareStatement(deletePartition_9);
            preparedStatementDeletePartition_9.setDate(1, sqlDate);
            preparedStatementDeletePartition_9.executeQuery();

            String deletePartition_10 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_10_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_10 = connection.prepareStatement(deletePartition_10);
            preparedStatementDeletePartition_10.setDate(1, sqlDate);
            preparedStatementDeletePartition_10.executeQuery();

            String deletePartition_11 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_11_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_11 = connection.prepareStatement(deletePartition_11);
            preparedStatementDeletePartition_11.setDate(1, sqlDate);
            preparedStatementDeletePartition_11.executeQuery();

            String deletePartition_12 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_12_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_12 = connection.prepareStatement(deletePartition_12);
            preparedStatementDeletePartition_12.setDate(1, sqlDate);
            preparedStatementDeletePartition_12.executeQuery();

            String deletePartition_13 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_13_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_13 = connection.prepareStatement(deletePartition_13);
            preparedStatementDeletePartition_13.setDate(1, sqlDate);
            preparedStatementDeletePartition_13.executeQuery();

            String deletePartition_14 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_14_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_14 = connection.prepareStatement(deletePartition_14);
            preparedStatementDeletePartition_14.setDate(1, sqlDate);
            preparedStatementDeletePartition_14.executeQuery();

            String deletePartition_15 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_15_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_15 = connection.prepareStatement(deletePartition_15);
            preparedStatementDeletePartition_15.setDate(1, sqlDate);
            preparedStatementDeletePartition_15.executeQuery();

            String deletePartition_16 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_16_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_16 = connection.prepareStatement(deletePartition_16);
            preparedStatementDeletePartition_16.setDate(1, sqlDate);
            preparedStatementDeletePartition_16.executeQuery();

            String deletePartition_17 = getLogMessageDataPrepaterStatementMysqlDelete(DataBase.LOGMESSAGEDATA_PARTITION_17_CLASS.getSimpleName());
            PreparedStatement preparedStatementDeletePartition_17 = connection.prepareStatement(deletePartition_17);
            preparedStatementDeletePartition_17.setDate(1, sqlDate);
            preparedStatementDeletePartition_17.executeQuery();

            connection.commit();
            connection.close();
            
        } catch (SQLException sqlError) {
            sqlError.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getLastProgrammaticTimeout() {
        if (lastProgrammaticTimeout != null) {
            return lastProgrammaticTimeout.toString();
        } else {
            return "never";
        }
    }

    public void setLastProgrammaticTimeout(Date lastTimeout) {
        this.lastProgrammaticTimeout = lastTimeout;
    }

    public String getLastAutomaticTimeout() {
        if (lastAutomaticTimeout != null) {
            return lastAutomaticTimeout.toString();
        } else {
            return "never";
        }
    }

    public void setLastAutomaticTimeout(Date lastAutomaticTimeout) {
        this.lastAutomaticTimeout = lastAutomaticTimeout;
    }

    private String getLogMessagePrepaterStatementMysqlDelete() {
        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("DELETE FROM LOGMESSAGE WHERE EXPIREDDATE <= ? ");
        return prepareStatement.toString();
    }

    private String getLogMessageDataPrepaterStatementMysqlDelete(String partitionName) {
        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("DELETE FROM ").append(partitionName).append(" WHERE EXPIREDDATE <= ? ");
        return prepareStatement.toString();
    }

}
