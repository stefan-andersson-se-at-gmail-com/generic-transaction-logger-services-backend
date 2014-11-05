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
package com.erbjuder.logger.server.soap.services;

import com.generic.global.flow.FlowConfigurations;
import com.generic.global.flow.Response;
import com.generic.global.flow.ServiceFault;
import com.erbjuder.logger.server.entity.impl.ApplicationFlowConfiguration;
import com.erbjuder.logger.server.facade.interfaces.ApplicationFlowConfigurationFacade;
import com.erbjuder.logger.server.soap.helper.ServiceUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stefan Andersson
 */
public abstract class FlowPointServiceBase {

//    @EJB
//    private FlowConfigurationFacadeImpl flowConfigurationFacade;
    private static final Logger logger = Logger.getLogger(FlowPointServiceBase.class.getName());

    public abstract ApplicationFlowConfigurationFacade getFlowConfigurationFacade();

    public Response persist(com.generic.global.flow.FlowConfigurations flowConfigurations)
            throws ServiceFault {

        FlowConfigurations.FlowConfiguration flowConfiguration = flowConfigurations.getFlowConfiguration();
        String flowName = flowConfiguration.getFlowName().toLowerCase();
        try {

            ApplicationFlowConfigurationFacade flowConfigurationFacade = getFlowConfigurationFacade();

            //  
            //
            // Lookup and Persist
            ApplicationFlowConfiguration entityFlowConfig = null;
            if (flowConfigurationFacade.findByName(flowName) == null) {
                entityFlowConfig = createFlowConfiguration(flowConfiguration);
                flowConfigurationFacade.create(entityFlowConfig);
            } else {
                logger.log(Level.SEVERE, "Flow already exist! =[ " + flowName + " ]");
                throw ServiceUtil.createServiceFault("Flow [ " + flowName + " ] already exist!", null, null);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Can't persist flow=[ " + flowName + " ]" + e.getMessage());
            throw ServiceUtil.createServiceFault("Can't persist flow [ " + flowName + " ]", "E:1", e.getMessage());
        }

        Response response = new Response();
        response.setReturn(true);
        return response;
    }

    private ApplicationFlowConfiguration createFlowConfiguration(FlowConfigurations.FlowConfiguration flowConfiguration) {

//        ApplicationFlowConfiguration entityFlowConfig = new com.generic.logger.server.entity.impl.ApplicationFlowConfiguration();
//        entityFlowConfig.setFlowName(flowConfiguration.getFlowName().toLowerCase());
//        entityFlowConfig.setFlowDescription(flowConfiguration.getFlowDescription());
//
//        List<Node> allDefinedNodes = new ArrayList<Node>();
//        for (FlowPoint flowPoint : flowConfiguration.getFlowPoint()) {
//
//            Node node = new Node();
//            node.setName(flowPoint.getFlowPointName().toLowerCase());
//            node.setDescription(flowPoint.getFlowPointDescription());
//            allDefinedNodes.add(node);
//
//             @ToDo FIX ME!
//            node.setFlowConfiguration(entityFlowConfig);
//        }
//
//        
//         bind
//        entityFlowConfig.setAllDefinedNodes(allDefinedNodes);
//        return entityFlowConfig;
        return null;
    }
}
