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

import com.erbjuder.logger.server.rest.helper.PersistenceUnitParser;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author Stefan Andersson
 */
public class MysqlConnection {

    private static final String persistenceUnitNameRead = "TransactionLoggerRead";
    private static final String persistenceUnitNameWrite = "TransactionLoggerWrite";
    private static DataSource dataSourceRead = null;
    private static DataSource dataSourceWrite = null;
    private static Context contextRead = null;
    private static Context contextWrite = null;

    private static DataSource DataSourceRead() throws Exception {
        /**
         * check to see if the database object is already defined... if it is,
         * then return the connection, no need to look it up again.
         */
        if (dataSourceRead != null) {
            return dataSourceRead;
        }
        try {
            /**
             * This only needs to run one time to get the database object.
             * context is used to lookup the database object
             */
            if (contextRead == null) {
                contextRead = new InitialContext();
            }
            //System.err.println("Shoud be Read : " + new PersistenceUnitParser(persistenceUnitNameRead).getDataSourceString());
            dataSourceRead = (DataSource) contextRead.lookup(new PersistenceUnitParser(persistenceUnitNameRead).getDataSourceString());
        } catch (NamingException | ParserConfigurationException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dataSourceRead;
    }

    private static DataSource DataSourceWrite() throws Exception {
        /**
         * check to see if the database object is already defined... if it is,
         * then return the connection, no need to look it up again.
         */
        if (dataSourceWrite != null) {
            return dataSourceWrite;
        }
        try {
            /**
             * This only needs to run one time to get the database object.
             * context is used to lookup the database object
             */
            if (contextWrite == null) {
                contextWrite = new InitialContext();
            }

            //System.err.println("Shoud be Write : " + new PersistenceUnitParser(persistenceUnitNameWrite).getDataSourceString());
            dataSourceWrite = (DataSource) contextWrite.lookup(new PersistenceUnitParser(persistenceUnitNameWrite).getDataSourceString());
        } catch (NamingException | ParserConfigurationException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dataSourceWrite;
    }

    public static Connection getConnectionRead() {
        Connection conn = null;
        try {
            conn = MysqlConnection.DataSourceRead().getConnection();
        } catch (Exception ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    public static Connection getConnectionWrite() {
        Connection conn = null;
        try {
            conn = MysqlConnection.DataSourceWrite().getConnection();
        } catch (Exception ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

}
