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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Stefan Andersson
 */
public class ResultSetConverterJSONArray implements ResultSetConverter {

    private final JSONArray list = new JSONArray();

    public JSONArray getResult() {
        return list;
    }

    @Override
    public void convert(ResultSet rs) throws Exception {

        try {

            // we will need the column names, this will save the table meta-data like column nmae.
            java.sql.ResultSetMetaData rsmd = rs.getMetaData();
            // System.err.println("Result set metadata " + rsmd );

            //loop through the ResultSet
            while (rs.next()) {

                //figure out how many columns there are
                int numColumns = rsmd.getColumnCount();

                //each row in the ResultSet will be converted to a JSON Object
                JSONObject obj = new JSONObject();

                // loop through all the columns and place them into the JSON Object
                for (int i = 1; i < numColumns + 1; i++) {
                    String column_name = rsmd.getColumnName(i);

                    if (rsmd.getColumnType(i) == java.sql.Types.ARRAY) {
                        obj.put(column_name, rs.getArray(column_name).toString());

                    } else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT) {
                        obj.put(column_name, ((Long) rs.getLong(column_name)));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.BOOLEAN) {
                        obj.put(column_name, ((Boolean) rs.getBoolean(column_name)).toString());

                    } else if (rsmd.getColumnType(i) == java.sql.Types.BLOB) {
                        obj.put(column_name, rs.getBlob(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.DOUBLE) {
                        obj.put(column_name, ((Double) rs.getDouble(column_name)));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.FLOAT) {
                        obj.put(column_name, ((Float) rs.getFloat(column_name)));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.INTEGER) {
                        obj.put(column_name, ((Integer) rs.getInt(column_name)));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.NVARCHAR) {
                        obj.put(column_name, rs.getNString(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.CHAR) {
                        obj.put(column_name, rs.getString(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR) {
//                        temp = rs.getString(column_name); //saving column data to temp variable
//                        temp = ESAPI.encoder().canonicalize(temp); //decoding data to base state
//                        temp = ESAPI.encoder().encodeForHTML(temp); //encoding to be browser safe
//                        obj.put(column_name, temp); //putting data into JSON object
//                    
                        obj.put(column_name, (String) rs.getString(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.LONGNVARCHAR) {
                        obj.put(column_name, rs.getString(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.TINYINT) {
                        obj.put(column_name, ((Short) rs.getShort(column_name)));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.SMALLINT) {
                        obj.put(column_name, ((Integer) rs.getInt(column_name)));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.DATE) {
                        obj.put(column_name, rs.getString(column_name));//rs.getDate(column_name).toString());

                    } else if (rsmd.getColumnType(i) == java.sql.Types.TIME) {
                        obj.put(column_name, rs.getString(column_name)); // TimeStampUtils.dateTimeToString(rs.getTime(column_name)));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP) {
                        obj.put(column_name, rs.getString(column_name)); // TimeStampUtils.timeStampToString(rs.getTimestamp(column_name)));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.NUMERIC) {
                        obj.put(column_name, rs.getBigDecimal(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.DECIMAL) {
                        obj.put(column_name, rs.getBigDecimal(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.BIT) {
                        obj.put(column_name, rs.getInt(column_name));

                    } else if (rsmd.getColumnType(i) == java.sql.Types.NULL) {
                        obj.put(column_name, "");
                    } else {
                        obj.put(column_name, rs.getString(column_name));
                    }
                }//end foreach
               
                list.add(obj);

            }//end while

        } catch (Exception ex) {
            Logger.getLogger(ResultSetConverterJSONArray.class.getName()).log(Level.SEVERE, ex.getMessage());
        }

    }

}
