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


import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Stefan Andersson
 */
@Stateless
public class LogMessageFacadeImpl extends LogMessageFacadeBaseImpl {

    
    @PersistenceContext(unitName = "TransactionLogger")
    private EntityManager em;  
    // Logger
    private static final Logger logger = Logger.getLogger(LogMessageFacadeImpl.class.getName());

    
    @Override
    public Logger getLogger() {
        return logger;
    }
    
    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public LogMessageFacadeImpl() {
        super();
    }
}
