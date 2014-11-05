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
package com.erbjuder.logger.server.facade.impl;

import com.erbjuder.logger.server.entity.impl.Node;
import com.erbjuder.logger.server.facade.interfaces.FlowPointFacade;
import javax.persistence.EntityManager;

/**
 *
 * @author Stefan Andersson
 */
public abstract class FlowPointFacadeBaseImpl extends AbstractFacadeImpl<Node> implements FlowPointFacade {

    @Override
    public abstract EntityManager getEntityManager();

    public FlowPointFacadeBaseImpl() {
        super(Node.class);
    }
}
