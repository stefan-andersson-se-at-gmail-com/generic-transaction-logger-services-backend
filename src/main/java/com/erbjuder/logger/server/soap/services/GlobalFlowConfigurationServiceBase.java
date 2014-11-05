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

import com.generic.global.flow.GlobalFlowConfigurations;
import com.generic.global.flow.Response;
import com.generic.global.flow.ServiceFault;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author server-1
 */
@XmlSeeAlso({
    com.generic.global.flow.ObjectFactory.class,
    com.generic.global.fault.ObjectFactory.class
})
public class GlobalFlowConfigurationServiceBase {

    public Response persist(GlobalFlowConfigurations flowConfigurations)
            throws ServiceFault {
        return null;
    }
}
