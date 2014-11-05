/* 
 * Copyright (C) 2014 erbjuder.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.erbjuder.logger.server.entity.impl;

import com.erbjuder.logger.server.common.helper.JSONPrettyPrintWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author server-1
 */
@Entity
@Table(name = "ApplicationFlowConfiguration")
public class ApplicationFlowConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String link;
    @OneToMany(targetEntity = Node.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Node> nodes;

    public ApplicationFlowConfiguration() {
        this.nodes = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public List<Node> getNodes() {
        return nodes;
    }
//
//    public void setNodes(List<Node> nodes) {
//        this.nodes = nodes;
//    }
//
//    public void addNode(Node node) {
//        if (!this.nodes.contains(node)) {
//            this.nodes.add(node);
//        }
//    }
//
//    public Node findNodeByName(String nodeName) {
//
//        Node result = null;
//        for (Node node : nodes) {
//            if (node.getName().equalsIgnoreCase(nodeName)) {
//                result = node;
//                break;
//            }
//        }
//        return result;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ApplicationFlowConfiguration)) {
            return false;
        }
        ApplicationFlowConfiguration other = (ApplicationFlowConfiguration) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    public String toJSONString() {
        return toJSON().toString();
    }

    public String toJSONPrettyString() {
        String jsonString = "";
        try {
            JSONPrettyPrintWriter writer = new JSONPrettyPrintWriter();
            toJSON().writeJSONString(writer);
            jsonString = writer.toString();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return jsonString;
    }

    public JSONObject toJSON() {
        JSONArray nodeList = new JSONArray();
        for (Node node : this.getNodes()) {
            nodeList.add(node.toJSON());
        }

        JSONObject json = new JSONObject();
        json.put("id", this.getId());
        json.put("flowName", this.getName());
        json.put("flowDescription", this.getDescription());
        json.put("definedNodes", nodeList);
//        json.put("graph", this.getGraph() == null ? "null" : this.getGraph().toJSON());
        return json;
    }
}
