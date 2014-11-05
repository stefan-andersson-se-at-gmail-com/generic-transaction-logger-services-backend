/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
