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

/**
 *
 * @author server-1
 */
@Entity
@Table(name = "GlobalFlowConfiguration")
public class GlobalFlowConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String link;
    @OneToMany(targetEntity = ApplicationFlowConfiguration.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ApplicationFlowConfiguration> applicationFlowConfigurations;
    @OneToOne(targetEntity = Graph.class, mappedBy = "globalFlowConfiguration", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Graph graph;

    public GlobalFlowConfiguration() {
        this.applicationFlowConfigurations = new ArrayList<>();
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

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public List<ApplicationFlowConfiguration> getApplicationFlowConfigurations() {
        return applicationFlowConfigurations;
    }

    public void setApplicationFlowConfigurations(List<ApplicationFlowConfiguration> applicationFlowConfigurations) {
        this.applicationFlowConfigurations = applicationFlowConfigurations;
    }

    public List<Node> getNodes() {
        List<Node> result = new ArrayList<>();
        for (ApplicationFlowConfiguration appConfig : getApplicationFlowConfigurations()) {
            result.addAll(appConfig.getNodes());
        }

        return result;
    }
    
    
}
