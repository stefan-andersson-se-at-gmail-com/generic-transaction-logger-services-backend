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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Stefan Andersson
 */
@Entity
@Table(name = "Graph")
public class Graph implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(targetEntity = GlobalFlowConfiguration.class)
    private GlobalFlowConfiguration globalFlowConfiguration;
    @OneToMany(targetEntity = Node.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Node> nodes = new ArrayList<>();
    @OneToMany(targetEntity = Edge.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Edge> edges = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GlobalFlowConfiguration getGlobalFlowConfiguration() {
        return globalFlowConfiguration;
    }

    public void setGlobalFlowConfiguration(GlobalFlowConfiguration globalFlowConfiguration) {
        this.globalFlowConfiguration = globalFlowConfiguration;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public boolean insertBiNode(Node node)
            throws IllegalArgumentException {
        return insertNode(node);
    }

    public boolean insertNode(Node node) {

        boolean isAdded = false;
        if (this.getGlobalFlowConfiguration().getNodes().contains(node) == false) {
            throw new IllegalArgumentException("Node is not in defiened nodes ");
        }
        if (!nodes.contains(node)) {
            node.setPartOfGraph(true);
            nodes.add(node);
            isAdded = true;
        }

        return isAdded;
    }

    public boolean insertBiEdge(Node fromNode, Node toNode)
            throws IllegalArgumentException {
        return insertUnEdge(fromNode, toNode) && insertUnEdge(toNode, fromNode);
    }

    public boolean insertUnEdge(Node from, Node to) throws IllegalArgumentException {

        if (nodes.contains(from) == false) {
            throw new IllegalArgumentException("from is not in graph");
        }
        if (nodes.contains(to) == false) {
            throw new IllegalArgumentException("to is not in graph");
        }

        Edge edge = new Edge().bind(from, to);
        if (from.findEdge(to) != null) {
            return false;
        } else {

            from.addEdge(edge);
            to.addEdge(edge);

            edges.add(edge);
            return true;
        }
    }

    public boolean existRootNodes() {
        return !getRootNodes().isEmpty();
    }

    public List<Node> getRootNodes() {
        List<Node> rootNodes = new ArrayList<Node>();
        for (Node node : nodes) {
            if (node.getIncomingEdges().isEmpty()) {
                node.setRootNode(true);
                rootNodes.add(node);
            }
        }
        return rootNodes;
    }

    public Node findNodeByName(String name) {
        Node match = null;
        for (Node node : nodes) {
            if (name.equals(node.getName())) {
                match = node;
                break;
            }
        }
        return match;
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
        if (!(object instanceof Graph)) {
            return false;
        }
        Graph other = (Graph) object;
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
        for (Node node : getNodes()) {
            nodeList.add(node.toJSON());
        }

        JSONArray edgeList = new JSONArray();
        for (Edge edge : getEdges()) {
            edgeList.add(edge.toJSON());
        }

        JSONObject json = new JSONObject();
        json.put("id", getId());
        json.put("flowConfigurationId", this.getGlobalFlowConfiguration() == null ? "null" : this.getGlobalFlowConfiguration().getId());
        json.put("nodes", nodeList);
        json.put("edges", edgeList);
        return json;
    }
}
