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

import java.util.List;

/**
 *
 * @author Stefan Andersson
 */
public class PrepareStatementHelper {

    public static String toSQLStartsWithValue(String value) {
        return "'" + value + "%'";
    }

    public static String toSQLContainsValue(String value) {
        return "'%" + value + "%'";
    }

    public static String toSQLValue(String value) {
        return "'" + value + "'";
    }

    public static String toSQLList(List<String> list) {
        StringBuilder builder = new StringBuilder();

        if (!list.isEmpty()) {
            builder.append("(");
            for (String value : list) {
                builder.append(PrepareStatementHelper.toSQLValue(value)).append(",");
            }
            builder.deleteCharAt(builder.lastIndexOf(",")).append(")");
        }
        return builder.toString();
    }

    public static String toSQL_Partition_List_Syntax(String partitionId) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        builder.append(partitionId);
        builder.append(")");

        return builder.toString();
    }

    public static String toSQL_Partition_List_Syntax(List<String> list) {
        StringBuilder builder = new StringBuilder();

        if (!list.isEmpty()) {
            builder.append("(");
            for (String value : list) {
                builder.append(value).append(",");
            }
            builder.deleteCharAt(builder.lastIndexOf(",")).append(")");
        }
        return builder.toString();
    }

}
