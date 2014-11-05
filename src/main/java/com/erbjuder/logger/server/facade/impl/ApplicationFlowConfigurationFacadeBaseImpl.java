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


import com.erbjuder.logger.server.entity.impl.ApplicationFlowConfiguration;
import com.erbjuder.logger.server.entity.impl.LogMessage;
import com.erbjuder.logger.server.facade.interfaces.ApplicationFlowConfigurationFacade;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

/**
 *
 * @author Stefan Andersson
 */
public abstract class ApplicationFlowConfigurationFacadeBaseImpl extends AbstractFacadeImpl<ApplicationFlowConfiguration> implements ApplicationFlowConfigurationFacade {

    public ApplicationFlowConfigurationFacadeBaseImpl() {
        super(ApplicationFlowConfiguration.class);
    }

    @Override
    public List<ApplicationFlowConfiguration> findFlowConfigurations(Collection<String> flowNames) {

        if (flowNames == null || flowNames.isEmpty()) {
            return new ArrayList<ApplicationFlowConfiguration>();
        }

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(entityClass);
        Root<LogMessage> root = cq.from(entityClass);

        Path flowName = root.get("flowName");

        cq.select(root);
        cq.where(flowName.in(flowNames));

        Query query = em.createQuery(cq);
        return query.getResultList();
    }

    @Override
    public List<ApplicationFlowConfiguration> findAllFlowConfigurations() {

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(entityClass);

        Root entity = query.from(entityClass);
        query.select(entity);
        query.where(entity.get("flowName").isNotNull());

        return getEntityManager().createQuery(query).getResultList();
    }

    @Override
    public ApplicationFlowConfiguration findByName(String flowName) {

        ApplicationFlowConfiguration flowConfiguration;
        try {
            CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(entityClass);

            Root entity = query.from(entityClass);
            query.select(entity);
            query.where(builder.equal(entity.get("flowName"), flowName));
            flowConfiguration = (ApplicationFlowConfiguration) getEntityManager().createQuery(query).getSingleResult();

        } catch (Exception e) {
            flowConfiguration = null;
        }

        return flowConfiguration;
    }
}
