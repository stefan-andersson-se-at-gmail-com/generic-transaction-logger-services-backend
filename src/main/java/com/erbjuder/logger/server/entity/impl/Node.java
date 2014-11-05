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
import javax.persistence.Transient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Stefan Andersson
 */
@Entity
@Table(name = "Node")
public class Node implements Serializable {


    @OneToMany(targetEntity = Edge.class, mappedBy = "fromNode", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Edge> incomingEdges = new ArrayList<>();
    @OneToMany(targetEntity = Edge.class, mappedBy = "toNode", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Edge> outgoingEdges = new ArrayList<>();
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private boolean partOfGraph = false;
    private boolean rootNode = false;

    @Transient
    private ArrayList<LogMessage> logMessages = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isRootNode() {
        return rootNode;
    }

    public void setRootNode(boolean isRootNode) {
        this.rootNode = isRootNode;
    }

 
    public boolean isPartOfGraph() {
        return partOfGraph;
    }

    public void setPartOfGraph(boolean partOfGraph) {
        this.partOfGraph = partOfGraph;
    }


    public List<Edge> getIncomingEdges() {
        return incomingEdges;
    }

    public void setIncomingEdges(List<Edge> incomingEdges) {
        this.incomingEdges = incomingEdges;
    }

    public List<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }

    public void setOutgoingEdges(List<Edge> outgoingEdges) {
        this.outgoingEdges = outgoingEdges;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<LogMessage> getLogMessages() {
        return logMessages;
    }

    public void setLogMessages(ArrayList<LogMessage> logMessages) {
        this.logMessages = logMessages;
    }

    public boolean addEdge(Edge edge) {

        if (this == edge.getFromNode()) {
            outgoingEdges.add(edge);
        } else if (this == edge.getToNode()) {
            incomingEdges.add(edge);
        } else {
            return false;
        }

        return true;
    }

    public Edge findEdge(Node dest) {
        for (Edge edge : outgoingEdges) {
            if (edge.getToNode() == dest) {
                return edge;
            }
        }
        return null;
    }

    public Edge findEdge(Edge edge) {
        if (outgoingEdges.contains(edge)) {
            return edge;
        } else {
            return null;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Node)) {
            return false;
        }
        Node other = (Node) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return toJSONString();
//        StringBuilder tmp = new StringBuilder("Node(");
//        tmp.append(name);
//        tmp.append("), in:[");
//        for (int i = 0; i < incomingEdges.size(); i++) {
//            Edge e = incomingEdges.get(i);
//            if (i > 0) {
//                tmp.append(',');
//            }
//            tmp.append('{');
//            tmp.append(e.getFromNode().getName());
//            tmp.append('}');
//        }
//        tmp.append("], out:[");
//        for (int i = 0; i < outgoingEdges.size(); i++) {
//            Edge e = outgoingEdges.get(i);
//            if (i > 0) {
//                tmp.append(',');
//            }
//            tmp.append('{');
//            tmp.append(e.getToNode().getName());
//            tmp.append('}');
//        }
//        tmp.append(']');
//        return tmp.toString();
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

        JSONArray edgeInList = new JSONArray();
        for (Edge edge : this.getIncomingEdges()) {
            edgeInList.add(edge.toJSON());
        }

        JSONArray edgeOutList = new JSONArray();
        for (Edge edge : this.getOutgoingEdges()) {
            edgeOutList.add(edge.toJSON());
        }

        JSONArray logMessagesList = new JSONArray();
        for (LogMessage logMessage : this.getLogMessages()) {
            logMessagesList.add(logMessage.toJSON());
        }

        JSONObject node = new JSONObject();
        node.put("id", this.getId());
        node.put("name", this.getName());
        node.put("description", this.getDescription());
        node.put("isRoot", this.isRootNode());
        node.put("isPartOfGraph", this.isPartOfGraph());
 //       node.put("graphId", this.getGraph() == null ? "null" : this.getGraph().getId());
        node.put("incomingEdges", edgeInList);
        node.put("outgoingEdges", edgeOutList);
        node.put("logMessages", logMessagesList);
        return node;

    }
}
