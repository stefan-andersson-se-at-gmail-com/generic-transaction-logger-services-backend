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
@WebService( 
            serviceName = "FlowPointBindingService", 
            portName = "FlowPointBinding_InPort", 
            endpointInterface = "com.generic.global.flow.FlowPointBinding", 
            targetNamespace = "urn:generic.com:Global:Flow", 
            wsdlLocation = "WEB-INF/wsdl/FlowPointBinding_Service/FlowPointBinding.wsdl")
public class FlowPointBindingService extends FlowPointBindingServiceBase {

    @EJB
    private ApplicationFlowConfigurationFacadeImpl flowConfigurationFacade;
    private static final Logger logger = Logger.getLogger(FlowPointBindingService.class.getName());

    @Override
    public Response persist(com.generic.global.flow.FlowPointBindings flowPointBindings)
            throws ServiceFault {
        return super.persist(flowPointBindings);
    }

    @Override
    public ApplicationFlowConfigurationFacade getFlowConfigurationFacade() {
        return flowConfigurationFacade;
    }
}
