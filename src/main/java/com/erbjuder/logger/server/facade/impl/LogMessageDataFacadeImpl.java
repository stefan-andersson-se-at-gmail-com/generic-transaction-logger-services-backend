/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class LogMessageDataFacadeImpl extends LogMessageDataFacadeBaseImpl {

    @PersistenceContext(unitName = "TransactionLogger")
    private EntityManager em;
    // Logger
    private static final Logger logger = Logger.getLogger(LogMessageDataFacadeImpl.class.getName());

    
    
    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public LogMessageDataFacadeImpl() {
        super();
    }
}
