/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erbjuder.logger.server.common.helper;

import com.erbjuder.logger.server.common.helper.DataBase;
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
    private Set<Class> treatAsSelectedDatabases = new HashSet<Class>();
    private Map<String, String> databases;

    public static final String LBL_MAX_1MB =  "Payload betweeen:  0  -  1 Megabyte";
    public static final String LBL_MAX_2MB =  "Payload betweeen:  1+ -  2 Megabyte";
    public static final String LBL_MAX_3MB =  "Payload betweeen:  2+ -  3 Megabyte";
    public static final String LBL_MAX_4MB =  "Payload betweeen:  3+ -  4 Megabyte";
    public static final String LBL_MAX_5MB =  "Payload betweeen:  4+ -  5 Megabyte";
    public static final String LBL_MAX_10MB = "Payload betweeen:  5+ - 10 Megabyte";
    public static final String LBL_MAX_16MB = "Payload betweeen: 10+ - 16 Megabyte";
    public static final String LBL_MAX_4GB =  "Payload betweeen: 16+ - 4  Gigabyte";

    public DataBaseSearchController() {
        init();
    }

    private void init() {
        databases = new TreeMap<String, String>();
        databases.put(LBL_MAX_3MB, DataBase.LOGMESSAGEDATA_PARTITION_12_CLASS.toString());
        databases.put(LBL_MAX_4MB, DataBase.LOGMESSAGEDATA_PARTITION_13_CLASS.toString());
        databases.put(LBL_MAX_5MB, DataBase.LOGMESSAGEDATA_PARTITION_14_CLASS.toString());
        databases.put(LBL_MAX_10MB, DataBase.LOGMESSAGEDATA_PARTITION_15_CLASS.toString());
        databases.put(LBL_MAX_16MB, DataBase.LOGMESSAGEDATA_PARTITION_16_CLASS.toString());
        databases.put(LBL_MAX_4GB, DataBase.LOGMESSAGEDATA_PARTITION_17_CLASS.toString());
    }

    public List<String> getSelectedDatabases() {
        return selectedDatabases;
    }

    public void setSelectedDatabases(List<String> selectedDatabases) {
        this.selectedDatabases = selectedDatabases;
    }

    public Set<Class> getTreatAsSelectedDatabases() {
        return treatAsSelectedDatabases;
    }

    public void setTreatAsSelectedDatabases(Set<Class> treatAsSelectedDatabases) {
        this.treatAsSelectedDatabases = treatAsSelectedDatabases;
    }

    public Map<String, String> getDatabases() {
        return databases;
    }

    public Set<Class> getValidDataBaseSelectionList() {
        Set<Class> result = new HashSet<Class>();
        result.addAll(getTreatAsSelectedDatabases());

        for (String clazz : getSelectedDatabases()) {

            if (clazz.equals(DataBase.LOGMESSAGEDATA_PARTITION_12_CLASS.toString())) {
                result.add(DataBase.LOGMESSAGEDATA_PARTITION_12_CLASS);
            } else if (clazz.equals(DataBase.LOGMESSAGEDATA_PARTITION_13_CLASS.toString())) {
                result.add(DataBase.LOGMESSAGEDATA_PARTITION_13_CLASS);
            } else if (clazz.equals(DataBase.LOGMESSAGEDATA_PARTITION_14_CLASS.toString())) {
                result.add(DataBase.LOGMESSAGEDATA_PARTITION_14_CLASS);
            } else if (clazz.equals(DataBase.LOGMESSAGEDATA_PARTITION_15_CLASS.toString())) {
                result.add(DataBase.LOGMESSAGEDATA_PARTITION_15_CLASS);
            } else if (clazz.equals(DataBase.LOGMESSAGEDATA_PARTITION_16_CLASS.toString())) {
                result.add(DataBase.LOGMESSAGEDATA_PARTITION_16_CLASS);
            } else if (clazz.equals(DataBase.LOGMESSAGEDATA_PARTITION_17_CLASS.toString())) {
                result.add(DataBase.LOGMESSAGEDATA_PARTITION_17_CLASS);
            }
        }

        return result;
    }

}
