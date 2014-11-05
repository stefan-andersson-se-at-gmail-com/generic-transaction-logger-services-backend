/* 
 * Copyright (C) 2014 erbjuder.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.erbjuder.logger.server.rest.services.dao;

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

    private static DataSource MysqlDS = null; //hold the database object
    private static Context context = null; //used to lookup the database connection in weblogic 

    private static DataSource MysqlDataSource() throws Exception {
        /**
         * check to see if the database object is already defined... if it is,
         * then return the connection, no need to look it up again.
         */
        if (MysqlDS != null) {
            return MysqlDS;
        }
        try {
            /**
             * This only needs to run one time to get the database object.
             * context is used to lookup the database object
             */
            if (context == null) {
                context = new InitialContext();
            }
            
            MysqlDS = (DataSource) context.lookup(new PersistenceUnitParser("TransactionLogger").getDataSourceString());
        } catch (NamingException | ParserConfigurationException ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return MysqlDS;
    }

    protected static Connection MysqlConnection() {
        Connection conn = null;
        try {
            conn = MysqlDataSource().getConnection();
            return conn;
        } catch (Exception ex) {
            Logger.getLogger(MysqlConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

}
