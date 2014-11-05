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

import com.generic.global.flow.FlowPointBindings.FlowPointBinding;
import com.generic.global.flow.Response;
import com.generic.global.flow.ServiceFault;
import com.erbjuder.logger.server.entity.impl.ApplicationFlowConfiguration;
import com.erbjuder.logger.server.facade.interfaces.ApplicationFlowConfigurationFacade;
import java.util.logging.Logger;

/**
 *
 * @author Stefan Andersson
 */
public abstract class FlowPointBindingServiceBase {

    private static final Logger logger = Logger.getLogger(FlowPointBindingServiceBase.class.getName());

    public abstract ApplicationFlowConfigurationFacade getFlowConfigurationFacade();

    public Response persist(com.generic.global.flow.FlowPointBindings flowPointBindings)
            throws ServiceFault {
//
//        List<FlowPointBinding> flowPointBindingList = flowPointBindings.getFlowPointBinding();
//        for (FlowPointBinding flowPointBinding : flowPointBindingList) {
//
//            String flowName = flowPointBinding.getFlowName();
//            try {
//
//                //
//                // Find stored flow configuration
//                ApplicationFlowConfiguration flowConfiguration = getFlowConfigurationFacade().findByName(flowName);
//                flowConfiguration = createFlowBinding(flowConfiguration, flowPointBinding);
//                
//                if (flowConfiguration.getGraph().existRootNodes()) {
//                    getFlowConfigurationFacade().edit(flowConfiguration);
//                } else {
//                    logger.log(Level.SEVERE, "Can't persist flow=[ No root node ]");
//                    throw ServiceUtil.createServiceFault("Can't persist flow=[ No root node ]", null, null);
//                }
//
//            } catch (Exception e) {
//                logger.log(Level.SEVERE, "Can't persist flow=[ " + flowName + " ]" + e.getMessage());
//                throw ServiceUtil.createServiceFault("Can't persist flow=[ " + flowName + " ]", "E:2", e.getMessage());
//            }
//        }

        Response response = new Response();
        response.setReturn(true);
        return response;

    }

    private ApplicationFlowConfiguration createFlowBinding(ApplicationFlowConfiguration flowConfiguration, FlowPointBinding flowPointBinding)
            throws ServiceFault {

//        //
//        // Binding 'fromNode <---> toNode'
//        if (flowConfiguration == null) {
//            throw ServiceUtil.createServiceFault("FlowConfiguration don't exist!", null, null);
//
//
//        } else if (flowConfiguration.getGraph() != null) {
//            throw ServiceUtil.createServiceFault("Graph already exist!", null, null);
//
//
//        } else {
//            Graph graph = new Graph();
//            
//            //@ToDo Fix This!
//            //graph.setApplicationFlowConfiguration(flowConfiguration);
//            flowConfiguration.setGraph(graph);
//
//            List<FromPoint> fromNodes = flowPointBinding.getBinding().getFromPoint();
//            for (FromPoint fromNode : fromNodes) {
//
//                String fromNodeName = fromNode.getFlowPointName();
//                Node entityFromNode = flowConfiguration.findDefinedNode(fromNodeName);
//                flowConfiguration.getGraph().insertNode(entityFromNode);
//
//                List<String> toNodeNames = fromNode.getToPoints().getFlowPointName();
//                for (String toNodeName : toNodeNames) {
//
//                    Node entityToNode = flowConfiguration.findDefinedNode(toNodeName);
//                    flowConfiguration.getGraph().insertNode(entityToNode);
//
//                    // 
//                    // bind to graph
//                    flowConfiguration.getGraph().insertUnEdge(entityFromNode, entityToNode);
//
//                }
//            }
//        }
//
//        return flowConfiguration;
        return null;

    }
}
