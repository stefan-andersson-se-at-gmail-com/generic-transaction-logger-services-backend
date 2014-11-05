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
package com.erbjuder.logger.server.entity.impl;

import com.erbjuder.logger.server.common.helper.JSONPrettyPrintWriter;
import java.io.IOException;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.json.simple.JSONObject;

/**
 *
 * @author Stefan Andersson
 */
@Entity
@Table(name = "Edge")
public class Edge implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(targetEntity = EdgeConstraints.class, mappedBy = "edge", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private EdgeConstraints constraints;
    @ManyToOne(targetEntity = Node.class)
    private Node fromNode;
    @ManyToOne(targetEntity = Node.class)
    private Node toNode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Node getFromNode() {
        return fromNode;
    }

    public void setFromNode(Node fromNode) {
        this.fromNode = fromNode;
    }

    public Node getToNode() {
        return toNode;
    }

    public void setToNode(Node toNode) {
        this.toNode = toNode;
    }

    /**
     *
     * @param fromNode
     * @param toNode
     * @return
     */
    public Edge bind(Node fromNode, Node toNode) {
        this.setFromNode(fromNode);
        this.setToNode(toNode);
        return this;
    }

    public EdgeConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(EdgeConstraints constraints) {
        this.constraints = constraints;
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
        if (!(object instanceof Edge)) {
            return false;
        }
        Edge other = (Edge) object;
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
        JSONObject edge = new JSONObject();
        edge.put("id", this.id);
        edge.put("from", this.getFromNode().getId());
        edge.put("to", this.getToNode().getId());
        return edge;

    }
}
