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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

/**
 *
 * @author Stefan Andersson
 */
@ManagedBean(name = "freeTextSearchController")
@SessionScoped
public class FreeTextSearchController implements Serializable {

    
    
    private ArrayList<String> searchTextList = null;

    public FreeTextSearchController() {
        this.init();
    }

    private void init() {
        this.searchTextList = new ArrayList();
        this.searchTextList.add("");

    }

    public ArrayList<String> getSearchTextList() {
        return searchTextList;
    }

    public void setSearchTextList(ArrayList<String> searchTextList) {
        this.searchTextList = searchTextList;
    }

    public void addTextField() {
        this.searchTextList.add("");
    }

    public void removeTextField(ActionEvent e) {
        String index = e.getComponent().getAttributes().get("index").toString();
        this.searchTextList.remove(Integer.parseInt(index));
    }

    public Integer getSize() {
        return this.searchTextList.size();
    }

    public StringWrapper getStringWrapper(int index) {
        return new StringWrapper(index);
    }

    public List<Integer> getIndexSequence() {
        List<Integer> intList = new ArrayList<Integer>();
        for (int i = 0; i < this.searchTextList.size(); i++) {
            intList.add(new Integer(i));
        }
        return intList;
    }

    public List<String> getValidQueryList() {
        List<String> queryList = new ArrayList<String>();
        for (String query : searchTextList) {
            if (query != null && !query.isEmpty()) {
                queryList.add(query);
            }
        }
        return queryList;
    }

    public int getValidQueryListSize() {
        return getValidQueryList().size();
    }

    public class StringWrapper {

        private final int index;

        public StringWrapper(int index) {
            this.index = index;
        }

        public String getContent() {
            return searchTextList.get(index);
        }

        public void setContent(String newContent) {
            searchTextList.set(index, newContent);
        }
    }

}
