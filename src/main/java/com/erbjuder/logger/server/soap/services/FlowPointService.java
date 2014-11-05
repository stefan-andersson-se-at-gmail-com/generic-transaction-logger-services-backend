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
package com.erbjuder.logger.server.soap.services;

import com.generic.global.flow.Response;
import com.generic.global.flow.ServiceFault;
import com.erbjuder.logger.server.facade.impl.ApplicationFlowConfigurationFacadeImpl;
import com.erbjuder.logger.server.facade.interfaces.ApplicationFlowConfigurationFacade;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.jws.WebService;

/**
 *
 * @author Stefan Andersson
 */
@WebService(serviceName = "FlowPointService", 
                portName = "FlowPoint_InPort", 
                endpointInterface = "com.generic.global.flow.FlowPoint", 
                targetNamespace = "urn:generic.com:Global:Flow", 
                wsdlLocation = "WEB-INF/wsdl/FlowPoint_Service/FlowPoint.wsdl")
public class FlowPointService extends FlowPointServiceBase {

    @EJB
    private ApplicationFlowConfigurationFacadeImpl flowConfigurationFacade;
    private static final Logger logger = Logger.getLogger(FlowPointService.class.getName());

    @Override
    public Response persist(com.generic.global.flow.FlowConfigurations flowConfigurations)
            throws ServiceFault {
        return super.persist(flowConfigurations);
    }

    @Override
    public ApplicationFlowConfigurationFacade getFlowConfigurationFacade() {
        return (ApplicationFlowConfigurationFacade) flowConfigurationFacade;

    }
}
