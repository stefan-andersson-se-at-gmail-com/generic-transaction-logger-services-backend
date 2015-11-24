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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Stefan Andersson
 */
public class DataBaseSearchController {

    private List<String> selectedDatabases = new ArrayList<String>();
    private List<String> treatAsSelectedDatabases = new ArrayList<String>();
    private Map<String, String> databases;

    public static final String LBL_MAX_1MB = "Payload betweeen:  0  -  1 Megabyte";
    public static final String LBL_MAX_2MB = "Payload betweeen:  1+ -  2 Megabyte";
    public static final String LBL_MAX_3MB = "Payload betweeen:  2+ -  3 Megabyte";
    public static final String LBL_MAX_4MB = "Payload betweeen:  3+ -  4 Megabyte";
    public static final String LBL_MAX_5MB = "Payload betweeen:  4+ -  5 Megabyte";
    public static final String LBL_MAX_10MB = "Payload betweeen:  5+ - 10 Megabyte";
    public static final String LBL_MAX_16MB = "Payload betweeen: 10+ - 16 Megabyte";
    public static final String LBL_MAX_4GB = "Payload betweeen: 16+ - 4  Gigabyte";

    public DataBaseSearchController() {
        init();
    }

    private void init() {
        databases = new TreeMap<String, String>();
        databases.put(LBL_MAX_3MB, DataBase.LOGMESSAGEDATA_PARTITION_12_NAME);
        databases.put(LBL_MAX_4MB, DataBase.LOGMESSAGEDATA_PARTITION_13_NAME);
        databases.put(LBL_MAX_5MB, DataBase.LOGMESSAGEDATA_PARTITION_14_NAME);
        databases.put(LBL_MAX_10MB, DataBase.LOGMESSAGEDATA_PARTITION_15_NAME);
        databases.put(LBL_MAX_16MB, DataBase.LOGMESSAGEDATA_PARTITION_16_NAME);
        databases.put(LBL_MAX_4GB, DataBase.LOGMESSAGEDATA_PARTITION_17_NAME);
    }

    public List<String> getSelectedDatabases() {
        return selectedDatabases;
    }

    public void setSelectedDatabases(List<String> selectedDatabases) {
        this.selectedDatabases = selectedDatabases;
    }

    public List<String> getTreatAsSelectedDatabases() {
        return treatAsSelectedDatabases;
    }

    public void setTreatAsSelectedDatabases(List<String> treatAsSelectedDatabases) {
        this.treatAsSelectedDatabases = treatAsSelectedDatabases;
    }

    public Map<String, String> getDatabases() {
        return databases;
    }

    public Set<String> getValidDataBaseSelectionList() {
        Set<String> result = new HashSet<String>();
        result.addAll(getTreatAsSelectedDatabases());

        for (String clazz : getSelectedDatabases()) {

            if (clazz.equals(DataBase.LOGMESSAGEDATA_PARTITION_12_NAME)) {
                result.add(DataBase.LOGMESSAGEDATA_PARTITION_12_NAME);
            } else if (clazz.equals(DataBase.LOGMESSAGEDATA_PARTITION_13_NAME)) {
                result.add(DataBase.LOGMESSAGEDATA_PARTITION_13_NAME);
            } else if (clazz.equals(DataBase.LOGMESSAGEDATA_PARTITION_14_NAME)) {
                result.add(DataBase.LOGMESSAGEDATA_PARTITION_14_NAME);
            } else if (clazz.equals(DataBase.LOGMESSAGEDATA_PARTITION_15_NAME)) {
                result.add(DataBase.LOGMESSAGEDATA_PARTITION_15_NAME);
            } else if (clazz.equals(DataBase.LOGMESSAGEDATA_PARTITION_16_NAME)) {
                result.add(DataBase.LOGMESSAGEDATA_PARTITION_16_NAME);
            } else if (clazz.equals(DataBase.LOGMESSAGEDATA_PARTITION_17_NAME)) {
                result.add(DataBase.LOGMESSAGEDATA_PARTITION_17_NAME);
            }
        }

        return result;
    }

    public List<String> getDataBaseSelectedList() {
        return new ArrayList<>(getValidDataBaseSelectionList());
    }

}
