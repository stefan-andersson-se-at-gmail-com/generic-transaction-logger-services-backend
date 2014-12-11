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

import com.erbjuder.logger.server.entity.impl.LogMessage;
import com.erbjuder.logger.server.facade.interfaces.AbstractFacade;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Metamodel;
/*
 *
 * @author Stefan Andersson
 */

public abstract class AbstractFacadeImpl<T> implements AbstractFacade<T> {

    public Class<T> entityClass;

    public AbstractFacadeImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    @Override
    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    @Override
    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    @Override
    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        Metamodel m = em.getMetamodel();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(entityClass);

        Root<LogMessage> entity = cq.from(entityClass);

        if (entityClass.equals(LogMessage.class)) {
            Order[] orderBy = new Order[]{
                cb.desc(entity.get("id")),
                cb.desc(entity.get("utcServerTimeStamp"))
            };
            cq.orderBy(orderBy);
        } else {
            cq.orderBy(cb.desc(entity.get("id")));
        }

        return getEntityManager().createQuery(cq).getResultList();
    }

    @Override
    public List<T> findRange(int[] range) {

        EntityManager em = getEntityManager();
        Metamodel m = em.getMetamodel();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(entityClass);

        Root<LogMessage> entity = cq.from(entityClass);
        cq.select(entity);

        if (entityClass.equals(LogMessage.class)) {

            Order[] orderBy = new Order[]{
                cb.desc(entity.get("id")),
                cb.desc(entity.get("utcServerTimeStamp"))
            };
            cq.orderBy(orderBy);
        } else {
            cq.orderBy(cb.desc(entity.get("id")));
        }

        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    @Override
    public List<T> findExpired(Date toDate) {

        EntityManager em = getEntityManager();
        Metamodel m = em.getMetamodel();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(entityClass);

        Root entity = query.from(entityClass);
        query.select(entity);

        Path<Date> expiredPath = entity.get("expiredDate");
        Predicate expiredPredicate = builder.lessThanOrEqualTo(expiredPath, toDate);
        query.where(expiredPredicate);

        javax.persistence.Query q = getEntityManager().createQuery(query);
        return q.getResultList();

    }

    @Override
    public List<T> findRangeFlow(int[] range,
            String applicationName,
            String flowName,
            String flowPointName,
            String transactionReferenceId,
            long fromDate,
            long toDate,
            String freeText,
            Boolean viewError) {

        java.sql.Timestamp sqlFromDate = new java.sql.Timestamp(fromDate);
        java.sql.Timestamp sqlToDate = new java.sql.Timestamp(toDate);

        EntityManager em = getEntityManager();
        Metamodel m = em.getMetamodel();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(entityClass);

        Root<LogMessage> entity = query.from(entityClass);
        query.select(entity);
        query.distinct(true);

        //
        // LogData
        Join logData = entity.join("logMessageData");
        Path logDataLabelPath = logData.get("label");
        Path logDataContentPath = logData.get("content");
        Predicate logDataLabelPredicate = builder.like(logDataLabelPath, "%" + freeText + "%");
        Predicate logDataContentPredicate = builder.like(logDataContentPath, "%" + freeText + "%");

        //
        // Transaction
        Path applicationNameField = entity.get("applicationName");
        Path utcServerTimeStampField = entity.get("utcServerTimeStamp");
        Path flowNamePath = entity.get("flowName");
        Path flowPointNamePath = entity.get("flowPointName");
        Path transactionReferenceIdPath = entity.get("transactionReferenceID");
        Path isErrorPath = entity.get("isError");
        Predicate isErrorPredicate = builder.equal(isErrorPath, viewError);
        Predicate timeStampBetweenPredicate = builder.between(utcServerTimeStampField, sqlFromDate.toString(), sqlToDate.toString());
        Predicate applicationNameEqualPredicate = builder.like(applicationNameField, "%" + applicationName + "%");
        Predicate flowNameEqualPredicate = builder.like(flowNamePath, "%" + flowName + "%");
        Predicate flowPointNameEqualPredicate = builder.like(flowPointNamePath, "%" + flowPointName + "%");
        Predicate transactionReferenceIdPredicate = builder.like(transactionReferenceIdPath, "%" + transactionReferenceId + "%");
        //
        // Build where predicate
        Predicate predicate = null;
        if (freeText != null && !freeText.isEmpty()) {

            predicate
                    = builder.or(logDataLabelPredicate, logDataContentPredicate);
        }

        if (fromDate != -1 && toDate != -1) {
            predicate = predicate == null ? builder.and(timeStampBetweenPredicate) : builder.and(predicate, timeStampBetweenPredicate);
        }
        if (applicationName != null && !applicationName.isEmpty()) {
            predicate = predicate == null ? builder.and(applicationNameEqualPredicate) : builder.and(predicate, applicationNameEqualPredicate);
        }
        if (flowName != null && !flowName.isEmpty()) {
            predicate = predicate == null ? builder.and(flowNameEqualPredicate) : builder.and(predicate, flowNameEqualPredicate);
        }
        if (flowPointName != null && !flowPointName.isEmpty()) {
            predicate = predicate == null ? builder.and(flowPointNameEqualPredicate) : builder.and(predicate, flowPointNameEqualPredicate);
        }
        if (transactionReferenceId != null && !transactionReferenceId.isEmpty()) {
            predicate = predicate == null ? builder.and(transactionReferenceIdPredicate) : builder.and(predicate, transactionReferenceIdPredicate);
        }
        if (viewError != null) {
            predicate = predicate == null ? builder.and(isErrorPredicate) : builder.and(predicate, isErrorPredicate);
        }
        if (entityClass.equals(LogMessage.class)) {

            Order[] orderBy = new Order[]{
                builder.desc(entity.get("id")),
                builder.desc(utcServerTimeStampField)
            };
            query.orderBy(orderBy);

        } else {
            query.orderBy(builder.desc(entity.get("id")));
        }

        javax.persistence.Query q = getEntityManager().createQuery(query);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    @Override
    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
