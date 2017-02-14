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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Stefan Andersson
 */
public class TupleSearchListUtil {

    public static List<Tuple> queryParamTupleList2TupleList(List<String> queryParamTupleList) {

        List<Tuple> tupleList = new ArrayList<>();
        for (String tupleString : queryParamTupleList) {

            String label = "";
            String search = "";

            // Stripp wrapper character
            if (tupleString.contains("[") && tupleString.contains("]")) {
                tupleString = tupleString.substring(1);
                tupleString = tupleString.substring(0, tupleString.length() - 1);

            } else if (tupleString.contains("{") && tupleString.contains("}")) {
                tupleString = tupleString.substring(1);
                tupleString = tupleString.substring(0, tupleString.length() - 1);

            }
            
              

            // Split data into label and content
            if (tupleString.contains(",")) {

                int index = tupleString.indexOf(",");
                label = tupleString.substring(0, index);
                search = tupleString.substring(index + 1, tupleString.length());

            } else if (tupleString.contains(";")) {

                int index = tupleString.indexOf(";");
                label = tupleString.substring(0, index);
                search = tupleString.substring(index + 1, tupleString.length());

            }

            
            if (!label.isEmpty() && !search.isEmpty()) {
                tupleList.add(new Tuple(label.trim(), search.trim()));
            }

        }

        return tupleList;
    }

    public static List<String> calculateDataBaseSearchList(List<Tuple> tuples) {
        HashSet<String> set = new HashSet();
        for (Tuple tuple : tuples) {
            Long contentSize = new Long(tuple.getSearch().getBytes().length);
            set.add(DataBase.logMessageDataPartitionNameFromContentSize(contentSize));
        }

        return new ArrayList(set);

    }

    

    public static void main(String[] arg) {
        List<String> list = new ArrayList<>();
        String data = "[a, 123]";
        list.add(data);
        System.err.println(TupleSearchListUtil.queryParamTupleList2TupleList(list));

    }
}
