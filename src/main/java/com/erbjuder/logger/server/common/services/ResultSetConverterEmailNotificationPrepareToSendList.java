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
package com.erbjuder.logger.server.common.services;

import com.erbjuder.logger.server.bean.EmailNotificationPrepareToSend;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stefan Andersson
 */
public class ResultSetConverterEmailNotificationPrepareToSendList implements ResultSetConverter {

    private final List<EmailNotificationPrepareToSend> list = new ArrayList<>();

    public List<EmailNotificationPrepareToSend> getResult() {
        return list;
    }

    @Override
    public void convert(ResultSet rs) throws Exception {

        try {

            // we will need the column names.
            java.sql.ResultSetMetaData rsmd = rs.getMetaData();
            //loop through the ResultSet
            while (rs.next()) {

                //figure out how many columns there are
                int numColumns = rsmd.getColumnCount();

                //each row in the ResultSet will be converted to a Object
                EmailNotificationPrepareToSend obj = new EmailNotificationPrepareToSend();

                // loop through all the columns 
                for (int i = 1; i < numColumns + 1; i++) {
                    String column_name = rsmd.getColumnName(i);

                    if (column_name.equals("ID")) {
                        obj.setId(rs.getBigDecimal(column_name).longValueExact());
                    } else if (column_name.equals("LOGMESSAGE_APPLICATIONNAME")) {
                        obj.setLogmessageApplicationName(rs.getString(column_name));
                    } else if (column_name.equals("LOGMESSAGE_TRANSACTIONREFERENCEID")) {
                        obj.setLogmessageTransactionReferenceId(rs.getString(column_name));
                    } else if (column_name.equals("SENT")) {
                        obj.setSent(rs.getBoolean(column_name));
                    }
                }//end foreach
                list.add(obj);
            }//end while

        } catch (SQLException ex) {
            Logger.getLogger(ResultSetConverterEmailNotificationPrepareToSendList.class.getName()).log(Level.SEVERE, ex.getMessage());
        }

    }
}
