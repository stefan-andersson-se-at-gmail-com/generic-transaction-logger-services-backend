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

import com.erbjuder.logger.server.common.helper.DataBase;
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
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stefan Andersson
 */
public class ResultSetConverterLogMessageDataList implements ResultSetConverter {

    private final List<LogMessageData> list = new ArrayList<>();

    public List<LogMessageData> getResult() {
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
                    }
                    else if (column_name.equals("PARTITION_ID")) {
                        obj.setPartitionId(rs.getInt(column_name));
                    }

                    else if (column_name.equals("CONTENT")) {
                        obj.setContent(rs.getString(column_name));
                    }

                    else if (column_name.equals("LABEL")) {
                        obj.setLabel(rs.getString(column_name));
                    }

                    else if (column_name.equals("MIMETYPE")) {
                        obj.setMimeType(rs.getString(column_name));
                    }

                    else if (column_name.equals("MODIFIED")) {
                        obj.setModified(rs.getBoolean(column_name));
                    }

                    else if (column_name.equals("CONTENTSIZE")) {
                        obj.setContentSize(rs.getBigDecimal(column_name).longValueExact());
                    }

                    else if (column_name.equals("SEARCHABLE")) {
                        obj.setSearchable(rs.getBoolean(column_name));
                    }

                    else if (column_name.equals("UTCLOCALTIMESTAMP")) {
                        obj.setUtcLocalTimeStamp(rs.getTimestamp(column_name));
                    }

                    else if (column_name.equals("UTCSERVERTIMESTAMP")) {
                        obj.setUtcServerTimeStamp(rs.getTimestamp(column_name));
                    }

                    else if (column_name.equals("LOGMESSAGE_ID")) {
                        obj.setLogMessageId(rs.getLong(column_name));
                    }
                }//end foreach
                list.add(obj);
            }//end while

        } catch (Exception ex) {
            Logger.getLogger(ResultSetConverterBase.class.getName()).log(Level.SEVERE, ex.getMessage());
        }

    }

}
