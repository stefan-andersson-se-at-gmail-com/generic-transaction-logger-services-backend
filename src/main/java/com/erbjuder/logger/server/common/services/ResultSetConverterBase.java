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
package com.erbjuder.logger.server.common.services;

//import org.codehaus.jettison.json.JSONArray;
//import org.codehaus.jettison.json.JSONObject;
import com.erbjuder.logger.server.common.helper.DataBase;
import com.erbjuder.logger.server.entity.impl.LogMessage;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_01;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_02;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_03;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_04;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_05;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_06;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_07;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_08;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_09;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_10;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_11;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_12;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_13;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_14;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_15;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_16;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Partition_17;
import com.erbjuder.logger.server.entity.interfaces.LogMessageData;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Stefan Andersson
 */
public class ResultSetConverterBase {

    /*
     */
    public List<String> toStringList(ResultSet rs) throws Exception {

        List<String> list = new ArrayList<>();
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

                    } else if (rsmd.getColumnType(i) == java.sql.Types.NULL) {
                        builder.append("");

                    } else {
                        builder.append(rs.getObject(column_name));

                    }
                }//end foreach
                list.add(builder.toString());
            }//end while
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(ResultSetConverterBase.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return list; //return String list

    }

    public List<LogMessage> toLogMessages(List<ResultSet> rsList) throws Exception {
        List<LogMessage> logMessages = new ArrayList<>();
        for (ResultSet rs : rsList) {
            logMessages = toLogMessageInternal(rs, logMessages);
        }
        return logMessages;
    }

    public List<LogMessage> toLogMessages(ResultSet rs) throws Exception {
        return toLogMessageInternal(rs, new ArrayList<>());
    }

    private List<LogMessage> toLogMessageInternal(ResultSet rs, List<LogMessage> logMessages) {

        try {

            // we will need the column names.
            java.sql.ResultSetMetaData rsmd = rs.getMetaData();
            //loop through the ResultSet
            while (rs.next()) {

                //figure out how many columns there are
                int numColumns = rsmd.getColumnCount();

                //each row in the ResultSet will be converted to a Object
                LogMessage obj = new LogMessage();

                // loop through all the columns 
                for (int i = 1; i < numColumns + 1; i++) {
                    String column_name = rsmd.getColumnName(i);

                    if (column_name.equals("ID")) {
                        obj.setId(rs.getBigDecimal(column_name).longValueExact());
                    }

                    if (column_name.equals("PARTITION_ID")) {
                        obj.setPartitionId(rs.getInt(column_name));
                    }

                    if (column_name.equals("APPLICATIONNAME")) {
                        obj.setApplicationName(rs.getNString(column_name));
                    }

                    if (column_name.equals("EXPIREDDATE")) {
                        obj.setExpiredDate(rs.getDate(column_name));
                    }

                    if (column_name.equals("FLOWNAME")) {
                        obj.setFlowName(rs.getNString(column_name));
                    }

                    if (column_name.equals("FLOWPOINTNAME")) {
                        obj.setFlowPointName(rs.getNString(column_name));
                    }

                    if (column_name.equals("ISERROR")) {
                        obj.setIsError(rs.getBoolean(column_name));
                    }

                    if (column_name.equals("TRANSACTIONREFERENCEID")) {
                        obj.setTransactionReferenceID(rs.getNString(column_name));
                    }

                    if (column_name.equals("UTCLOCALTIMESTAMP")) {
                        obj.setUtcLocalTimeStamp(rs.getTimestamp(column_name));
                    }

                    if (column_name.equals("UTCSERVERTIMESTAMP")) {
                        obj.setUtcServerTimeStamp(rs.getTimestamp(column_name));
                    }
                }//end foreach
                logMessages.add(obj);
            }//end while
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(ResultSetConverterBase.class.getName()).log(Level.SEVERE, ex.getMessage());
        }

        return logMessages;
    }

    public List<LogMessageData> toLogMessageData(List<ResultSet> rsList) throws Exception {
        List<LogMessageData> logMessageData = new ArrayList<>();
        for (ResultSet rs : rsList) {
            logMessageData = toLogMessageDataInternal(rs, logMessageData);
        }
        return logMessageData;
    }

    public List<LogMessageData> toLogMessageData(ResultSet rs) throws Exception {
        return toLogMessageDataInternal(rs, new ArrayList<>());
    }

    private List<LogMessageData> toLogMessageDataInternal(ResultSet rs, List<LogMessageData> logMessageData) {

        try {

            // we will need the column names.
            java.sql.ResultSetMetaData rsmd = rs.getMetaData();
            //loop through the ResultSet
            while (rs.next()) {

                //figure out how many columns there are
                int numColumns = rsmd.getColumnCount();
                String tableName = rsmd.getTableName(1);
                LogMessageData obj = null;
                if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_01_NAME)) {
                    obj = new LogMessageData_Partition_01();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_02_NAME)) {
                    obj = new LogMessageData_Partition_02();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_03_NAME)) {
                    obj = new LogMessageData_Partition_03();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_04_NAME)) {
                    obj = new LogMessageData_Partition_04();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_05_NAME)) {
                    obj = new LogMessageData_Partition_05();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_06_NAME)) {
                    obj = new LogMessageData_Partition_06();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_07_NAME)) {
                    obj = new LogMessageData_Partition_07();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_08_NAME)) {
                    obj = new LogMessageData_Partition_08();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_09_NAME)) {
                    obj = new LogMessageData_Partition_09();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_10_NAME)) {
                    obj = new LogMessageData_Partition_10();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_11_NAME)) {
                    obj = new LogMessageData_Partition_11();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_12_NAME)) {
                    obj = new LogMessageData_Partition_12();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_13_NAME)) {
                    obj = new LogMessageData_Partition_13();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_14_NAME)) {
                    obj = new LogMessageData_Partition_14();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_15_NAME)) {
                    obj = new LogMessageData_Partition_15();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_16_NAME)) {
                    obj = new LogMessageData_Partition_16();
                } else if (tableName.equals(DataBase.LOGMESSAGEDATA_PARTITION_17_NAME)) {
                    obj = new LogMessageData_Partition_17();
                }

                // loop through all the columns 
                for (int i = 1; i < numColumns + 1; i++) {
                    String column_name = rsmd.getColumnName(i);

                    if (column_name.equals("ID")) {
                        obj.setId(rs.getBigDecimal(column_name).longValueExact());
                    } else if (column_name.equals("PARTITION_ID")) {
                        obj.setPartitionId(rs.getInt(column_name));
                    } else if (column_name.equals("CONTENT")) {
                        obj.setContent(rs.getString(column_name));
                    } else if (column_name.equals("LABEL")) {
                        obj.setLabel(rs.getString(column_name));
                    } else if (column_name.equals("MIMETYPE")) {
                        obj.setMimeType(rs.getString(column_name));
                    } else if (column_name.equals("MODIFIED")) {
                        obj.setModified(rs.getBoolean(column_name));
                    } else if (column_name.equals("CONTENTSIZE")) {
                        obj.setContentSize(rs.getBigDecimal(column_name).longValueExact());
                    } else if (column_name.equals("SEARCHABLE")) {
                        obj.setSearchable(rs.getBoolean(column_name));
                    }

                    if (column_name.equals("UTCLOCALTIMESTAMP")) {
                        obj.setUtcLocalTimeStamp(rs.getTimestamp(column_name));
                    } else if (column_name.equals("UTCSERVERTIMESTAMP")) {
                        obj.setUtcServerTimeStamp(rs.getTimestamp(column_name));
                    } else if (column_name.equals("LOGMESSAGE_ID")) {
                        obj.setLogMessageId(rs.getLong(column_name));
                    }
                }//end foreach
                logMessageData.add(obj);
            }//end while
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(ResultSetConverterBase.class.getName()).log(Level.SEVERE, ex.getMessage());
        }

        return logMessageData;
    }

    public JSONArray toJSONArray(List<ResultSet> rsList) throws Exception {
        JSONArray json = new JSONArray();
        for (ResultSet rs : rsList) {
            json = toJSONArray(rs, json);
        }
        return json;
    }

    /**
     * This will convert database records into a JSON Array Simply pass in a
     * ResultSet from a database connection and it loop return a JSON array.
     *
     * It important to check to make sure that all DataType that are being used
     * is properly encoding.
     *
     * varchar is currently the only dataType that is being encode by ESAPI
     *
     * @param rs - database ResultSet
     * @return - JSON array
     * @throws Exception
     */
    public JSONArray toJSONArray(ResultSet rs) throws Exception {
        return this.toJSONArray(rs, new JSONArray());

    }

    private JSONArray toJSONArray(ResultSet rs, JSONArray json) throws Exception {

        String temp = null;
        try {

            // we will need the column names, this will save the table meta-data like column nmae.
            java.sql.ResultSetMetaData rsmd = rs.getMetaData();
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
                json.add(obj);
            }//end while
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(ResultSetConverterBase.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return json; //return JSON array
    }
}
