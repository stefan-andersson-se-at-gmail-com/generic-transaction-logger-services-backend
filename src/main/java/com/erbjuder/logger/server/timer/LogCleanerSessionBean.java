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
import com.erbjuder.logger.server.common.helper.MysqlConnection;
import com.erbjuder.logger.server.common.services.PrepareStatementHelper;
import com.erbjuder.logger.server.common.services.ResultSetConverter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import com.erbjuder.logger.server.common.services.ResultSetConverterStringList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.ejb.Stateful;

/**
 *
 * @author Stefan Andersson
 */
@Singleton
@Startup
public class LogCleanerSessionBean {

    @Resource
    TimerService timerService;
    private final int[] timeRange = new int[]{20, 21, 22, 23, 0, 1, 2, 3, 4, 5};
    private static final String EVERY = "*";
    private static final Logger logger = Logger.getLogger(LogCleanerSessionBean.class.getName());

    @PostConstruct
    public void initialization() {
        start();
    }

    @PreDestroy
    public void terminate() {
        stop();
    }

    @Timeout
    public void programmaticTimeout(Timer timer) {
        stop();
        System.err.println("[Clean Start " + LogCleanerSessionBean.class.getName() + " ]");
        clean();
        System.err.println("[Clean End " + LogCleanerSessionBean.class.getName() + " ]");
        start();
    }

    public void stop() {
        try {

            // logger.log(Level.SEVERE, "[Stop timer]");
            Timer timer = null;
            Iterator<Timer> timerIterator = timerService.getAllTimers().iterator();
            while (timerIterator.hasNext()) {
                timer = timerIterator.next();
                if (timer.getInfo().equals(LogCleanerSessionBean.class.getName())) {
                    break;
                }
            }

            if (timer != null) {
                timer.cancel();
                timerService.getTimers().remove(timer);
            }

        } catch (IllegalArgumentException | IllegalStateException | EJBException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            logger.log(Level.SEVERE, "[Exception stop timer]");
        }
    }

    public void start() {

        try {
            // logger.log(Level.SEVERE, "[Start timer]");
            Random rand = new Random();
            int timePos = rand.nextInt(timeRange.length - 1);

            TimerConfig timerConfig = new TimerConfig();
            timerConfig.setPersistent(false);
            timerConfig.setInfo(LogCleanerSessionBean.class.getName());
            ScheduleExpression expression = new ScheduleExpression();
            //expression.year(EVERY).month(EVERY).dayOfMonth(EVERY).hour(timeRange[timePos]).minute(EVERY).second(EVERY);
            expression.year(EVERY).month(EVERY).dayOfMonth(EVERY).hour(EVERY).minute(EVERY).second("*/30");
            timerService.createCalendarTimer(expression, timerConfig);

        } catch (IllegalArgumentException | IllegalStateException | EJBException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            logger.log(Level.SEVERE, "[Exception start timer]");
        }

    }

    public void cleanLogMessages(Connection readConn, Connection writeConn) throws SQLException, Exception {

        int page = 1;
        int pageSize = 200;
        boolean done = false;

        // Select     
        ResultSetConverterStringList converter = new ResultSetConverterStringList();
        fetchLogMessages(readConn, page, pageSize, converter);
        if (converter.getResult().size() > 0) {
            deleteLogMessages(writeConn, converter.getResult());

            writeConn.commit();
            readConn.commit();

            for (String databaseName : DataBase.getAllDatabaseNames()) {
                deleteLogMessageData(writeConn, databaseName, converter.getResult());
                writeConn.commit();
            }

            // Still more to do?
            while (!done) {

                if (converter.getResult().isEmpty()) {
                    done = true;

                } else {

                    // page += 1;
                    converter.getResult().clear();
                    fetchLogMessages(readConn, page, pageSize, converter);
                    if (converter.getResult().size() > 0) {
                        deleteLogMessages(writeConn, converter.getResult());

                        writeConn.commit();
                        readConn.commit();

                        for (String databaseName : DataBase.getAllDatabaseNames()) {
                            deleteLogMessageData(writeConn, databaseName, converter.getResult());
                            writeConn.commit();
                        }

                    }
                }
            }
        }

    }

    private ResultSetConverter fetchLogMessages(Connection conn, int page, int pageSize, ResultSetConverter converter) throws SQLException, Exception {

        int pageOffset = page - 1;
        if (pageOffset < 0) {
            pageOffset = 0;
        } else {
            pageOffset = pageOffset * pageSize;
        }

        // Select     
        String prepaterStatement_select = getLogMessagePrepaterStatementMysql_Select();
        try (PreparedStatement stmt_select = conn.prepareStatement(prepaterStatement_select)) {
            stmt_select.setDate(1, new java.sql.Date(new Date().getTime()));
            stmt_select.setInt(2, pageSize);
            stmt_select.setInt(3, pageOffset);
            try (ResultSet rs = stmt_select.executeQuery()) {
                converter.convert(rs);
                rs.close();
            }
            stmt_select.close();
        }

        return converter;
    }

    private void deleteLogMessages(Connection conn, List<String> idList) throws SQLException, Exception {

        StringBuilder prepaterStatement_delete = getLogMessagePrepaterStatementMysql_Delete();
        prepaterStatement_delete.append(PrepareStatementHelper.toSQLList(idList));
        try (PreparedStatement stmt_delete = conn.prepareStatement(prepaterStatement_delete.toString())) {
            stmt_delete.executeUpdate();
            stmt_delete.close();
        }
    }

    public void cleanLogMessageData(Connection readConn, Connection writeConn, String database) throws SQLException, Exception {

        int page = 1;
        int pageSize = 200;
        boolean done = false;

        // Select     
        ResultSetConverterStringList converter = new ResultSetConverterStringList();
        fetchLogMessageData(readConn, database, page, pageSize, converter);

        if (converter.getResult().size() > 0) {
            deleteLogMessageData(writeConn, database, converter.getResult());

            writeConn.commit();
            readConn.commit();

            // Still more to do?
            while (!done) {

                if (converter.getResult().isEmpty()) {
                    done = true;

                } else {

                    // page += 1;
                    converter.getResult().clear();
                    fetchLogMessageData(readConn, database, page, pageSize, converter);
                    if (converter.getResult().size() > 0) {
                        deleteLogMessageData(writeConn, database, converter.getResult());

                        writeConn.commit();
                        readConn.commit();

                    }
                }
            }
        }
    }

    private ResultSetConverter fetchLogMessageData(Connection conn, String database, int page, int pageSize, ResultSetConverter converter) throws SQLException, Exception {

        int pageOffset = page - 1;
        if (pageOffset < 0) {
            pageOffset = 0;
        } else {
            pageOffset = pageOffset * pageSize;
        }

        // Select     
        String prepaterStatement_select = getLogMessageDataPrepaterStatementMysql_Select(database);
        try (PreparedStatement stmt_select = conn.prepareStatement(prepaterStatement_select)) {
            stmt_select.setDate(1, new java.sql.Date(new Date().getTime()));
            stmt_select.setInt(2, pageSize);
            stmt_select.setInt(3, pageOffset);
            try (ResultSet rs = stmt_select.executeQuery()) {
                converter.convert(rs);
                rs.close();

            }
            stmt_select.close();
        }

        return converter;
    }

    private void deleteLogMessageData(Connection conn, String database, List<String> idList) throws SQLException, Exception {

        StringBuilder prepaterStatement_delete = getLogMessageDataPrepaterStatementMysql_Delete(database);
        prepaterStatement_delete.append(PrepareStatementHelper.toSQLList(idList));
        try (PreparedStatement stmt_delete = conn.prepareStatement(prepaterStatement_delete.toString())) {
            stmt_delete.executeUpdate();
            stmt_delete.close();
        }
    }

    public void clean() {

        try {

            Connection writeConn = MysqlConnection.getConnectionWriteNonTransaction();
            Connection readConn = MysqlConnection.getConnectionReadNonTransaction();
            if (writeConn != null && readConn != null) {

                writeConn.setAutoCommit(false);
                readConn.setAutoCommit(false);

                cleanLogMessages(readConn, writeConn);

                // Close
                writeConn.commit();
                readConn.commit();

                writeConn.setAutoCommit(true);
                readConn.setAutoCommit(true);

                writeConn.close();
                readConn.close();

            }

        } catch (SQLException sqlError) {
            logger.log(Level.SEVERE, sqlError.getMessage());

        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }

    }

    private String getLogMessagePrepaterStatementMysql_Select() {
        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT ID FROM LogMessage WHERE EXPIREDDATE <= ? LIMIT ? OFFSET ? ");
        return prepareStatement.toString();
    }

    private String getLogMessageDataPrepaterStatementMysql_Select(String partitionName) {
        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("SELECT ID FROM ").append(partitionName).append(" WHERE EXPIREDDATE <= ? LIMIT ? OFFSET ? ");
        return prepareStatement.toString();
    }

    private StringBuilder getLogMessagePrepaterStatementMysql_Delete() {
        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("DELETE LOW_PRIORITY FROM LogMessage WHERE ID IN ");
        return prepareStatement;
    }

    private StringBuilder getLogMessageDataPrepaterStatementMysql_Delete(String partitionName) {

        StringBuilder prepareStatement = new StringBuilder();
        prepareStatement.append("DELETE LOW_PRIORITY FROM ").append(partitionName).append(" WHERE LOGMESSAGE_ID IN ");
        return prepareStatement;
    }

}
