/*
 * Copyright (C) 2016 Stefan Andersson
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

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stefan Andersson
 */
public class ResultSetConverterStringList implements ResultSetConverter {

    private final List<String> list = new ArrayList<>();

    public List<String> getResult() {
        return list;
    }

    @Override
    public void convert(ResultSet rs) throws Exception {

        try {

            // we will need the column names, this will save the table meta-data like column nmae.
            java.sql.ResultSetMetaData rsmd = rs.getMetaData();
            //loop through the ResultSet
            while (rs.next()) {

                //figure out how many columns there are
                int numColumns = rsmd.getColumnCount();

                //each row in the ResultSet will be converted to a JSON Object
                StringBuilder builder = new StringBuilder();
                // loop through all the columns and place them into the JSON Object
                for (int i = 1; i < numColumns + 1; i++) {
                    String column_name = rsmd.getColumnName(i);

                    if (rsmd.getColumnType(i) == java.sql.Types.ARRAY) {
                        builder.append(rs.getArray(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT) {
                        builder.append(rs.getInt(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.BOOLEAN) {
                        builder.append(rs.getBoolean(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.BLOB) {
                        builder.append(rs.getBlob(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.DOUBLE) {
                        builder.append(rs.getDouble(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.FLOAT) {
                        builder.append(rs.getFloat(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.INTEGER) {
                        builder.append(rs.getInt(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.NVARCHAR) {
                        builder.append(rs.getNString(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR) {
//                        temp = rs.getString(column_name); //saving column data to temp variable
//                        temp = ESAPI.encoder().canonicalize(temp); //decoding data to base state
//                        temp = ESAPI.encoder().encodeForHTML(temp); //encoding to be browser safe
//                        obj.put(column_name, temp); //putting data into JSON object
//                    
                        builder.append(rs.getString(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.TINYINT) {
                        builder.append(rs.getInt(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.SMALLINT) {
                        builder.append(rs.getInt(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.DATE) {
                        builder.append(rs.getDate(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.TIME) {
                        builder.append(rs.getTime(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP) {
                        builder.append(rs.getTimestamp(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.NUMERIC) {
                        builder.append(rs.getBigDecimal(column_name));

                    } else {
                        builder.append(rs.getObject(column_name));

                    }
                }//end foreach
                list.add(builder.toString());
            }//end while

        } catch (Exception ex) {
            Logger.getLogger(ResultSetConverterBase.class.getName()).log(Level.SEVERE, ex.getMessage());
        }

        Collections.sort(list);
    }
}
