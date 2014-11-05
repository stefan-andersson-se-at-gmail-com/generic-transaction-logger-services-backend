/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erbjuder.logger.server.rest.helper;

import java.util.List;

/**
 *
 * @author Stefan Andersson
 */
public class SQLPrepareStatementHelper {

    public static String toSQLStartsWithValue(String value) {
        return "'" + value + "%'";
    }

        public static String toSQLContainsValue(String value) {
        return "'" + value + "%'";
    }
    
    public static String toSQLValue(String value) {
        return "'" + value + "'";
    }

    public static String toSQLList(List<String> list) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (String value : list) {
            builder.append(SQLPrepareStatementHelper.toSQLValue(value)).append(",");
        }
        builder.deleteCharAt(builder.lastIndexOf(",")).append(")");
        return builder.toString();
    }

}
