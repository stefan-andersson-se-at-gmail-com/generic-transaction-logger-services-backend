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

    private static String persistenceUnitName = "TransactionLogger";
    private static DataSource dataSource = null;
    private static Context context = null;

    private static DataSource DataSource() throws Exception {
        /**
         * check to see if the database object is already defined... if it is,
         * then return the connection, no need to look it up again.
         */
        if (dataSource != null) {
            return dataSource;
        }
        try {
            /**
             * This only needs to run one time to get the database object.
             * context is used to lookup the database object
             */
            if (context == null) {
                context = new InitialContext();
            }

            dataSource = (DataSource) context.lookup(new PersistenceUnitParser(persistenceUnitName).getDataSourceString());
        } catch (NamingException | ParserConfigurationException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dataSource;
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = MysqlConnection.DataSource().getConnection();
            return conn;
        } catch (Exception ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

}
