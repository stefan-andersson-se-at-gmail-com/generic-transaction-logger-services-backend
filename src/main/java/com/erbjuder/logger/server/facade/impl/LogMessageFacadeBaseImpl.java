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

import com.erbjuder.logger.server.common.helper.DataBase;
import com.erbjuder.logger.server.common.helper.DataBaseSearchController;
import com.erbjuder.logger.server.common.helper.FreeTextSearchController;
import com.erbjuder.logger.server.entity.impl.LogMessage;
import com.erbjuder.logger.server.facade.interfaces.LogMessageFacade;
import com.erbjuder.logger.server.facade.pojo.FlowAndTransactionId;
import com.erbjuder.logger.server.facade.pojo.LogMessageStatistic;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.Metamodel;

/**
 *
 * @author Stefan Andersson
 */
public abstract class LogMessageFacadeBaseImpl extends AbstractFacadeImpl<LogMessage> implements LogMessageFacade {

    public LogMessageFacadeBaseImpl() {
        super(LogMessage.class);
    }

    public abstract Logger getLogger();

    @Override
    public List<LogMessage> findExpired(Date toDate) {

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
    public List<String> getFlowNames(long fromDate, long toDate, String startsWithName) {

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(entityClass);
        Root<LogMessage> entity = cq.from(entityClass);

        String inFromDate = new java.sql.Timestamp(fromDate).toString();
        String inToDate = new java.sql.Timestamp(toDate).toString();

        Path flowNamePath = entity.get("flowName");
        Path utcServerTimeStamp = entity.get("utcServerTimeStamp");
        Path utcLocalTimeStamp = entity.get("utcLocalTimeStamp");

        Predicate flowNamePredicate = cb.like(flowNamePath, startsWithName + "%");
        Predicate timeBetween = cb.between(utcServerTimeStamp, inFromDate, inToDate);

        cq.select(entity.get("flowName"));
        cq.distinct(true);
        cq.where(cb.and(timeBetween, flowNamePredicate));
        cq.orderBy(cb.desc(utcServerTimeStamp), cb.desc(utcLocalTimeStamp));

        Query query = em.createQuery(cq);
        List<String> result = query.getResultList();
        return result;

    }

    @Override
    public List<String> getFlowPointNames(long fromDate, long toDate, String startsWithName) {

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(entityClass);
        Root<LogMessage> entity = cq.from(entityClass);

        String inFromDate = new java.sql.Timestamp(fromDate).toString();
        String inToDate = new java.sql.Timestamp(toDate).toString();

        Path flowPointNamePath = entity.get("flowPointName");
        Path utcServerTimeStamp = entity.get("utcServerTimeStamp");
        Path utcLocalTimeStamp = entity.get("utcLocalTimeStamp");

        Predicate flowPointNamePredicate = cb.like(flowPointNamePath, startsWithName + "%");
        Predicate timeBetween = cb.between(utcServerTimeStamp, inFromDate, inToDate);

        cq.select(entity.get("flowPointName"));
        cq.distinct(true);
        cq.where(cb.and(timeBetween, flowPointNamePredicate));
        cq.orderBy(cb.desc(utcServerTimeStamp), cb.desc(utcLocalTimeStamp));

        Query query = em.createQuery(cq);
        List<String> result = query.getResultList();
        return result;

    }

    @Override
    public List<String> getApplicationNames(long fromDate, long toDate, String startsWithName) {

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(entityClass);
        Root<LogMessage> entity = cq.from(entityClass);

        String inFromDate = new java.sql.Timestamp(fromDate).toString();
        String inToDate = new java.sql.Timestamp(toDate).toString();

        Path applicationNamePath = entity.get("applicationName");
        Path utcServerTimeStamp = entity.get("utcServerTimeStamp");
        Path utcLocalTimeStamp = entity.get("utcLocalTimeStamp");

        Predicate applicationNameFieldPredicate = cb.like(applicationNamePath, startsWithName + "%");
        Predicate timeBetween = cb.between(utcServerTimeStamp, inFromDate, inToDate);

        cq.select(entity.get("applicationName"));
        cq.distinct(true);
        cq.where(cb.and(timeBetween, applicationNameFieldPredicate));
        cq.orderBy(cb.desc(utcServerTimeStamp), cb.desc(utcLocalTimeStamp));

        Query query = em.createQuery(cq);
        List<String> result = query.getResultList();
        return result;

    }

    @Override
    public List<LogMessage> findFlows(
            Collection<String> flowNames,
            Collection<String> flowPointNames,
            int[] range) {

        if (!flowNames.isEmpty() && !flowPointNames.isEmpty()) {
            EntityManager em = getEntityManager();
            Metamodel m = em.getMetamodel();
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(entityClass);

            Root entity = query.from(entityClass);
            entity.alias("q1");

            query.select(entity);
            query.where(builder.and(
                    builder.not(builder.like(entity.get("flowName"), "")),
                    entity.get("flowName").in(flowNames),
                    entity.get("flowPointName").in(flowPointNames)));

            query.orderBy(builder.desc(entity.get("utcServerTimeStamp")));

            TypedQuery typedQuery = getEntityManager().createQuery(query);
            typedQuery.setMaxResults(range[1] - range[0]);
            typedQuery.setFirstResult(range[0]);
            return typedQuery.getResultList();
        } else {
            return new ArrayList<LogMessage>();
        }
    }

    @Override
    public int countFlows(
            Collection<String> flowNames,
            Collection<String> flowPointNames) {

        if (!flowNames.isEmpty() && !flowPointNames.isEmpty()) {
            EntityManager em = getEntityManager();
            Metamodel m = em.getMetamodel();
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(entityClass);

            Root entity = query.from(entityClass);

            query.select(builder.count(entity));
            query.where(builder.and(
                    builder.not(builder.like(entity.get("flowName"), "")),
                    entity.get("flowName").in(flowNames),
                    entity.get("flowPointName").in(flowPointNames)));

            query.orderBy(builder.desc(entity.get("utcServerTimeStamp")));

            TypedQuery typedQuery = getEntityManager().createQuery(query);
            return ((Long) typedQuery.getSingleResult()).intValue();
        } else {
            return 0;
        }
    }

    @Override
    public int countFlowAndTransactionId() {

        StringBuilder nativeQuery = new StringBuilder();
        nativeQuery.append("SELECT A.FLOWNAME, A.TRANSACTIONREFERENCEID ");
        nativeQuery.append("FROM LogMessage as A ");
        nativeQuery.append("JOIN FlowConfiguration as B ");
        nativeQuery.append("ON A.FLOWNAME LIKE B.FLOWNAME ");
        nativeQuery.append("JOIN Graph as C ");
        nativeQuery.append("ON C.FLOWCONFIGURATION_ID = B.ID ");
        nativeQuery.append("where not A.FLOWNAME like \"\" ");
        nativeQuery.append("group by A.FLOWNAME, A.TRANSACTIONREFERENCEID ");
        nativeQuery.append("order by A.UTCSERVERTIMESTAMP DESC ");

        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery(nativeQuery.toString()); // , FlowAndTransactionId); <-- to use this you must anotate the class as @ENTITY
        List<Object[]> rawResultList = query.getResultList();
        return rawResultList.size();
    }

    @Override
    public List<FlowAndTransactionId> getFlowAndTransactionId(int[] range) {

//        String q = SELECT A.FLOWNAME, A.TRANSACTIONREFERENCEID 
//                    FROM transactionlogger_dev.LogMessage as A 
//                        JOIN transactionlogger_dev.FlowConfiguration as B 
//                            ON A.FLOWNAME LIKE B.FLOWNAME
//						JOIN transactionlogger_dev.Graph as C 
//                            ON C.FLOWCONFIGURATION_ID LIKE B.ID 
//                    where not A.FLOWNAME like "" 
//					group by A.FLOWNAME, A.TRANSACTIONREFERENCEID 
//					order by A.UTCSERVERTIMESTAMP DESC;
        StringBuilder nativeQuery = new StringBuilder();
        nativeQuery.append("SELECT A.FLOWNAME, A.TRANSACTIONREFERENCEID ");
        nativeQuery.append("FROM LogMessage as A ");
        nativeQuery.append("JOIN FlowConfiguration as B ");
        nativeQuery.append("ON A.FLOWNAME LIKE B.FLOWNAME ");
        nativeQuery.append("JOIN Graph as C ");
        nativeQuery.append("ON C.FLOWCONFIGURATION_ID = B.ID ");
        nativeQuery.append("where not A.FLOWNAME like \"\" ");
        nativeQuery.append("group by A.FLOWNAME, A.TRANSACTIONREFERENCEID ");
        nativeQuery.append("order by A.UTCSERVERTIMESTAMP DESC ");

        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery(nativeQuery.toString()); // , FlowAndTransactionId); <-- to use this you must anotate the class as @ENTITY
        query.setMaxResults(range[1] - range[0]);
        query.setFirstResult(range[0]);
        List<Object[]> rawResultList = query.getResultList();
        List<FlowAndTransactionId> resultList = new ArrayList<FlowAndTransactionId>(rawResultList.size());
        for (Object[] result : rawResultList) {
            resultList.add(new FlowAndTransactionId((String) result[0], (String) result[1]));
        }
        return resultList;
    }

    @Override
    public List<LogMessage> getFlowLogMessages(List<FlowAndTransactionId> flowAndTransactionIds) {

//        SELECT * 
//                    FROM transactionlogger_dev.LogMessage as A 
//                    where ( 
//							A.FLOWNAME like 'sms_product_order_information' 
//							AND
//							A.TRANSACTIONREFERENCEID like '43fde4e1-999f-44b2-82d4-089d4250946f'
//							) 
//							or
//						(
//							A.FLOWNAME like 'sms_product_order_information' 
//							AND
//							A.TRANSACTIONREFERENCEID like 'ca054deb-c07b-416e-9dd1-5a66b930896c'
//						)
//					order by A.ID DESC, A.UTCSERVERTIMESTAMP DESC;
        // fast return
        if (flowAndTransactionIds == null || flowAndTransactionIds.isEmpty()) {
            return new ArrayList<LogMessage>();
        }

        // Search 
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(entityClass);
        Root<LogMessage> root = cq.from(entityClass);

        Path id = root.get("id");
        Path flowName = root.get("flowName");
        Path utcServerTimeStamp = root.get("utcServerTimeStamp");
        Path transactionReferenceId = root.get("transactionReferenceID");

        cq.select(root);
        Predicate current = null;
        Predicate predicate = null;
        for (FlowAndTransactionId flowAndTransactionId : flowAndTransactionIds) {

            current = cb.and(cb.like(flowName, flowAndTransactionId.getFlowName()),
                    cb.like(transactionReferenceId, flowAndTransactionId.getTransactionReferenceId()));

            if (predicate == null) {
                predicate = current;
            } else {
                predicate = cb.or(predicate, current);
            }
        }

        cq.where(predicate);
        cq.orderBy(cb.desc(utcServerTimeStamp));

        Query query = em.createQuery(cq);
        List<LogMessage> list = query.getResultList();
        return list;
    }

    @Override
    public int countNumberOfTransactions(
            String applicationName,
            String flowName,
            String flowPointName,
            String transactionReferenceId,
            long fromDate,
            long toDate,
            FreeTextSearchController freeTextSearch,
            DataBaseSearchController dataBaseSearchController,
            Boolean viewError,
            int maxResult
    ) {
        return 0;
    }

    @Override
    public List<LogMessageStatistic> numberOfTransactions(
            int[] range,
            String applicationName,
            String flowName,
            String flowPointName,
            String transactionReferenceId,
            long fromDate,
            long toDate,
            FreeTextSearchController freeTextSearch,
            DataBaseSearchController dataBaseSearchController,
            Boolean viewError
    ) {
//       SELECT 
//          APPLICATIONNAME, 
//          Count(Distinct TRANSACTIONREFERENCEID) as numberOfTransactions, 
//          Count(TRANSACTIONREFERENCEID) as numberOfMessages, 
//          SUM(IF(ISERROR = 0,1,0)) as numberOfSuccess, 
//          SUM(IF(ISERROR = 1,1,0)) as numberOfError 
//      FROM transactionlogger_prod.LogMessage
//      where UTCLOCALTIMESTAMP between '2014-07-04 00:00:00.000000' and '2014-07-04 23:59:59.999999'
//      group by APPLICATIONNAME;

        StringBuilder nativeQuery = new StringBuilder();
        nativeQuery.append("SELECT ");
        nativeQuery.append("APPLICATIONNAME, ");
        nativeQuery.append("Count(Distinct TRANSACTIONREFERENCEID) as numberOfTransactions, ");
        nativeQuery.append("Count(TRANSACTIONREFERENCEID) as numberOfMessages, ");
        nativeQuery.append("SUM(IF(ISERROR = 0,1,0)) as numberOfSuccess, ");
        nativeQuery.append("SUM(IF(ISERROR = 1,1,0)) as numberOfError ");
        nativeQuery.append("FROM LogMessage ");
        nativeQuery.append("WHERE ");

        String inFromDate = new java.sql.Timestamp(fromDate).toString();
        String inToDate = new java.sql.Timestamp(toDate).toString();

        String tmp_query;
        String AND_OP = " AND ";
        String whereConditions = null;
        if (viewError != null) {
            tmp_query = "ISERROR = " + " '" + viewError + "' ";
            whereConditions = whereConditions == null ? tmp_query : whereConditions + AND_OP + tmp_query;
        }

        if (inFromDate != null && !inFromDate.isEmpty() && inToDate != null && !inToDate.isEmpty()) {
            tmp_query = "UTCSERVERTIMESTAMP between " + " '" + inFromDate + "' " + AND_OP  + " '" + inToDate + "' ";
            whereConditions = whereConditions == null ? tmp_query : whereConditions + AND_OP + tmp_query;
        }

        if (applicationName != null && !applicationName.isEmpty()) {
            tmp_query = "APPLICATIONNAME LIKE " + " '" + applicationName + "%' ";
            whereConditions = whereConditions == null ? tmp_query : whereConditions + AND_OP + tmp_query;
        }

        if (transactionReferenceId != null && !transactionReferenceId.isEmpty()) {
            tmp_query = "TRANSACTIONREFERENCEID LIKE " + " '" + applicationName + "%' ";
            whereConditions = whereConditions == null ? tmp_query : whereConditions + AND_OP + tmp_query;
        }

        if (flowName != null && !flowName.isEmpty()) {
            tmp_query = "FLOWNAME LIKE " + " '" + flowName + "%' ";
            whereConditions = whereConditions == null ? tmp_query : whereConditions + AND_OP + tmp_query;
        }

        if (flowPointName != null && !flowPointName.isEmpty()) {
            tmp_query = "FLOWPOINTNAME LIKE " + " '" + flowPointName + "%' ";
            whereConditions = whereConditions == null ? tmp_query : whereConditions + AND_OP + tmp_query;
        }

        if (whereConditions != null) {
            nativeQuery.append(whereConditions);
        }

        nativeQuery.append("GROUP BY APPLICATIONNAME ");
         
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery(nativeQuery.toString()); // , FlowAndTransactionId); <-- to use this you must anotate the class as @ENTITY
        query.setMaxResults(range[1] - range[0]);
        query.setFirstResult(range[0]);
        List<Object[]> rawResultList = query.getResultList();

        List<LogMessageStatistic> resultList = new ArrayList<LogMessageStatistic>(rawResultList.size());
        for (Object[] result : rawResultList) {
            resultList.add(new LogMessageStatistic(
                    (String) result[0],
                    (Long) result[1],
                    (Long) result[2],
                    (BigDecimal) result[3],
                    (BigDecimal) result[4]
            ));
        }
        return resultList;
    }

    @Override
    public List<LogMessage> findRange(
            int[] range,
            String applicationName,
            String flowName,
            String flowPointName,
            String transactionReferenceId,
            long fromDate,
            long toDate,
            FreeTextSearchController freeTextSearch,
            DataBaseSearchController dataBaseSearchController,
            Boolean viewError) {

        String inFromDate = new java.sql.Timestamp(fromDate).toString();
        String inToDate = new java.sql.Timestamp(toDate).toString();

        EntityManager em = getEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = builder.createQuery(LogMessage.class);

        Root<LogMessage> entity = criteriaQuery.from(LogMessage.class);
        criteriaQuery.select(entity);
        criteriaQuery.distinct(true);

        // 
        // LogMessage
        Path applicationNamePath = entity.get("applicationName");
        Path utcServerTimeStampPath = entity.get("utcServerTimeStamp");
        Path flowNamePath = entity.get("flowName");
        Path flowPointNamePath = entity.get("flowPointName");
        Path transactionReferenceIDPath = entity.get("transactionReferenceID");
        Path isErrorPath = entity.get("isError");
        Predicate whereConditions = null;

        if (viewError != null) {
            Predicate predicate = builder.equal(isErrorPath, viewError);
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (inFromDate != null && !inFromDate.isEmpty() && inToDate != null && !inToDate.isEmpty()) {
            Predicate predicate = builder.between(utcServerTimeStampPath, inFromDate, inToDate);
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (applicationName != null && !applicationName.isEmpty()) {
            Predicate predicate = builder.like(applicationNamePath, applicationName + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (transactionReferenceId != null && !transactionReferenceId.isEmpty()) {
            Predicate predicate = builder.like(transactionReferenceIDPath, transactionReferenceId + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (flowName != null && !flowName.isEmpty()) {
            Predicate predicate = builder.like(flowNamePath, flowName + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (flowPointName != null && !flowPointName.isEmpty()) {
            Predicate predicate = builder.like(flowPointNamePath, flowPointName + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        Predicate andIn = null;
        Set<Class> dataBaseSelectionSet = dataBaseSearchController.getValidDataBaseSelectionList();
        for (String inFreeText : freeTextSearch.getValidQueryList()) {

            List<Predicate> inList = new ArrayList<Predicate>();
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_01_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_01_CLASS)));

            }

            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_02_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_02_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_03_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_03_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_04_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_04_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_05_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_05_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_06_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_06_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_07_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_07_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_08_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_08_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_09_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_09_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_10_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_10_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_11_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_11_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_12_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_12_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_13_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_13_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_14_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_14_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_15_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_15_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_16_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_16_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_17_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_17_CLASS)));
            }

            Predicate tmp = null;
            for (Predicate in : inList) {
                tmp = tmp == null ? in : builder.or(tmp, in);
            }
            if (tmp != null) {
                andIn = andIn == null ? tmp : builder.and(andIn, tmp);
            }
        }

        if (whereConditions != null && andIn
                != null) {
            whereConditions = builder.and(whereConditions, andIn);
        } else if (whereConditions == null && andIn
                != null) {
            whereConditions = andIn;
        }

        if (whereConditions
                != null) {
            criteriaQuery.where(whereConditions);
        }

        if (transactionReferenceId == null || transactionReferenceId.isEmpty()) {
            criteriaQuery.orderBy(new Order[]{builder.desc(entity.get("utcServerTimeStamp")), builder.desc(entity.get("utcLocalTimeStamp"))});
        } else {
            criteriaQuery.orderBy(new Order[]{builder.desc(entity.get("utcLocalTimeStamp")), builder.desc(entity.get("utcServerTimeStamp"))});
        }

        Query q = em.createQuery(criteriaQuery);

        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    @Override
    public int count(
            String applicationName,
            String flowName,
            String flowPointName,
            String transactionReferenceId,
            long fromDate,
            long toDate,
            FreeTextSearchController freeTextSearch,
            DataBaseSearchController dataBaseSearchController,
            Boolean viewError,
            int maxResult) {

        String inFromDate = new java.sql.Timestamp(fromDate).toString();
        String inToDate = new java.sql.Timestamp(toDate).toString();

        EntityManager em = getEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = builder.createQuery(LogMessage.class);

        Root<LogMessage> entity = criteriaQuery.from(LogMessage.class);

        //criteriaQuery.select(builder.countDistinct(entity));
        criteriaQuery.select(entity);
        criteriaQuery.distinct(true);

        // 
        // LogMessage
        Path applicationNamePath = entity.get("applicationName");
        Path utcServerTimeStampPath = entity.get("utcServerTimeStamp");
        Path flowNamePath = entity.get("flowName");
        Path flowPointNamePath = entity.get("flowPointName");
        Path transactionReferenceIDPath = entity.get("transactionReferenceID");
        Path isErrorPath = entity.get("isError");
        Predicate whereConditions = null;

        if (viewError
                != null) {
            Predicate predicate = builder.equal(isErrorPath, viewError);
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (inFromDate
                != null && !inFromDate.isEmpty()
                && inToDate != null && !inToDate.isEmpty()) {
            Predicate predicate = builder.between(utcServerTimeStampPath, inFromDate, inToDate);
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (applicationName
                != null && !applicationName.isEmpty()) {
            Predicate predicate = builder.like(applicationNamePath, applicationName + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (transactionReferenceId
                != null && !transactionReferenceId.isEmpty()) {
            Predicate predicate = builder.like(transactionReferenceIDPath, transactionReferenceId + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (flowName
                != null && !flowName.isEmpty()) {
            Predicate predicate = builder.like(flowNamePath, flowName + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (flowPointName
                != null && !flowPointName.isEmpty()) {
            Predicate predicate = builder.like(flowPointNamePath, flowPointName + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        Predicate andIn = null;
        Set<Class> dataBaseSelectionSet = dataBaseSearchController.getValidDataBaseSelectionList();
        for (String inFreeText : freeTextSearch.getValidQueryList()) {

            List<Predicate> inList = new ArrayList<Predicate>();
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_01_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_01_CLASS)));

            }

            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_02_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_02_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_03_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_03_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_04_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_04_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_05_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_05_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_06_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_06_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_07_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_07_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_08_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_08_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_09_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_09_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_10_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_10_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_11_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_11_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_12_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_12_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_13_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_13_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_14_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_14_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_15_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_15_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_16_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_16_CLASS)));
            }
            if (dataBaseSelectionSet.contains(DataBase.LOGMESSAGEDATA_PARTITION_17_CLASS)) {
                inList.add(entity.get("id").in(subQueryLogMessageDataQuery_1(
                        builder,
                        criteriaQuery,
                        applicationName,
                        flowName,
                        flowPointName,
                        transactionReferenceId,
                        inFromDate,
                        inToDate,
                        inFreeText,
                        viewError,
                        DataBase.LOGMESSAGEDATA_PARTITION_17_CLASS)));
            }

            Predicate tmp = null;
            for (Predicate in : inList) {
                tmp = tmp == null ? in : builder.or(tmp, in);
            }
            if (tmp != null) {
                andIn = andIn == null ? tmp : builder.and(andIn, tmp);
            }
        }

        if (whereConditions != null && andIn
                != null) {
            whereConditions = builder.and(whereConditions, andIn);
        } else if (whereConditions == null && andIn
                != null) {
            whereConditions = andIn;
        }

        if (whereConditions
                != null) {
            criteriaQuery.where(whereConditions);
        }

        Query q = em.createQuery(criteriaQuery);
        q.setMaxResults(maxResult);
        return q.getResultList().size();
        //return ((Long) q.getSingleResult()).intValue();

    }

    private Subquery<LogMessage> subQueryLogMessageData(
            CriteriaBuilder builder,
            CriteriaQuery criteriaQuery,
            String inApplicationName,
            String inFlowName,
            String inFlowPointName,
            String inTransactionReferenceId,
            String inFromDate,
            String inToDate,
            String inFreeText,
            Boolean inViewError) {

        Subquery<LogMessage> subquery = criteriaQuery.subquery(LogMessage.class);
        Root entity = subquery.from(LogMessage.class);

        subquery.select(entity.get("id"));
        entity.alias(
                "MSG_DATA");

        Predicate whereConditions = null;

        // 
        // LogMessage
        Path applicationNamePath = entity.get("applicationName");
        Path utcServerTimeStampPath = entity.get("utcServerTimeStamp");
        Path flowNamePath = entity.get("flowName");
        Path flowPointNamePath = entity.get("flowPointName");
        Path transactionReferenceIDPath = entity.get("transactionReferenceID");
        Path isErrorPath = entity.get("isError");

        if (inViewError
                != null) {
            Predicate predicate = builder.equal(isErrorPath, inViewError);
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (inFromDate
                != null && !inFromDate.isEmpty()
                && inToDate != null && !inToDate.isEmpty()) {
            Predicate predicate = builder.between(utcServerTimeStampPath, inFromDate, inToDate);
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (inApplicationName
                != null && !inApplicationName.isEmpty()) {
            Predicate predicate = builder.like(applicationNamePath, inApplicationName + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (inTransactionReferenceId
                != null && !inTransactionReferenceId.isEmpty()) {
            Predicate predicate = builder.like(transactionReferenceIDPath, inTransactionReferenceId + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (inFlowName
                != null && !inFlowName.isEmpty()) {
            Predicate predicate = builder.like(flowNamePath, inFlowName + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (inFlowPointName
                != null && !inFlowPointName.isEmpty()) {
            Predicate predicate = builder.like(flowPointNamePath, inFlowPointName + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        // LogData
        Join logData = entity.join("logMessageData");

        logData.alias("MSG_DATA_OBJ");
        Path logDataLabelPath = logData.get("label");
        Path logDataContentPath = logData.get("content");

        Predicate ORCondition = null;

        Predicate pred_1 = builder.like(flowNamePath, "%" + inFreeText + "%");
        Predicate pred_2 = builder.like(flowPointNamePath, "%" + inFreeText + "%");
        Predicate pred_3 = builder.like(applicationNamePath, "%" + inFreeText + "%");
        Predicate pred_4 = builder.like(transactionReferenceIDPath, "%" + inFreeText + "%");
        Predicate pred_5 = builder.like(logDataContentPath, "%" + inFreeText + "%");
        Predicate pred_6 = builder.like(logDataLabelPath, "%" + inFreeText + "%");

        Predicate tmp = builder.or(
                pred_1,
                pred_2,
                pred_3,
                pred_4,
                pred_5,
                pred_6
        );

        ORCondition = ORCondition == null ? tmp : builder.and(ORCondition, tmp);

        if (whereConditions != null && ORCondition
                != null) {
            whereConditions = builder.and(whereConditions, ORCondition);
        } else if (whereConditions == null && ORCondition
                != null) {
            whereConditions = ORCondition;
        }

        if (whereConditions
                != null) {
            subquery.where(whereConditions);
        }

        return subquery;
    }

  
    private Subquery<List> subQueryLogMessageData_2(
            CriteriaBuilder builder,
            CriteriaQuery criteriaQuery,
            String inFreeText,
            String inFromDate,
            String inToDate,
            Class dataClass) {

        Subquery<List> subquery = criteriaQuery.subquery(List.class);
        Root logData = subquery.from(dataClass);

        subquery.select(logData.get("logMessage").get("id"));

        Path utcServerTimeStampPath = logData.get("utcServerTimeStamp");
        Path logDataLabelPath = logData.get("label");
        Path logDataContentPath = logData.get("content");

        Predicate pred_1 = builder.like(logDataContentPath, "%" + inFreeText + "%");
        Predicate pred_2 = builder.like(logDataLabelPath, "%" + inFreeText + "%");
        Predicate orConditions = builder.or(pred_1, pred_2);

        Predicate predicate = builder.between(utcServerTimeStampPath, inFromDate, inToDate);
        Predicate whereConditions = builder.and(predicate, orConditions);

        subquery.groupBy(logData.get("logMessage").get("id"));
        subquery.where(whereConditions);

        return subquery;
    }

    private Subquery<LogMessage> subQueryLogMessageDataQuery_1(
            CriteriaBuilder builder,
            CriteriaQuery criteriaQuery,
            String inApplicationName,
            String inFlowName,
            String inFlowPointName,
            String inTransactionReferenceId,
            String inFromDate,
            String inToDate,
            String inFreeText,
            Boolean inViewError,
            Class dataClass) {

        Subquery<LogMessage> subquery = criteriaQuery.subquery(LogMessage.class);
        Root entity = subquery.from(LogMessage.class);

        subquery.select(entity.get("id"));
        entity.alias("MSG_DATA");

        Predicate whereConditions = null;

        // 
        // LogMessage
        Path applicationNamePath = entity.get("applicationName");
        Path utcServerTimeStampPath = entity.get("utcServerTimeStamp");
        Path flowNamePath = entity.get("flowName");
        Path flowPointNamePath = entity.get("flowPointName");
        Path transactionReferenceIDPath = entity.get("transactionReferenceID");
        Path isErrorPath = entity.get("isError");

        if (inViewError
                != null) {
            Predicate predicate = builder.equal(isErrorPath, inViewError);
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (inFromDate
                != null && !inFromDate.isEmpty()
                && inToDate != null && !inToDate.isEmpty()) {
            Predicate predicate = builder.between(utcServerTimeStampPath, inFromDate, inToDate);
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (inApplicationName
                != null && !inApplicationName.isEmpty()) {
            Predicate predicate = builder.like(applicationNamePath, inApplicationName + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (inTransactionReferenceId
                != null && !inTransactionReferenceId.isEmpty()) {
            Predicate predicate = builder.like(transactionReferenceIDPath, inTransactionReferenceId + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (inFlowName
                != null && !inFlowName.isEmpty()) {
            Predicate predicate = builder.like(flowNamePath, inFlowName + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        if (inFlowPointName
                != null && !inFlowPointName.isEmpty()) {
            Predicate predicate = builder.like(flowPointNamePath, inFlowPointName + "%");
            whereConditions = whereConditions == null ? predicate : builder.and(whereConditions, predicate);
        }

        // LogData
        char c[] = dataClass.getSimpleName().toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        String clazz = new String(c);
        Join logData = entity.join(clazz);

        logData.alias("MSG_DATA_OBJ");
        Path logDataLabelPath = logData.get("label");
        Path logDataContentPath = logData.get("content");

        Predicate ORCondition = null;

        Predicate pred_1 = builder.like(flowNamePath, "%" + inFreeText + "%");
        Predicate pred_2 = builder.like(flowPointNamePath, "%" + inFreeText + "%");
        Predicate pred_3 = builder.like(applicationNamePath, "%" + inFreeText + "%");
        Predicate pred_4 = builder.like(transactionReferenceIDPath, "%" + inFreeText + "%");
        Predicate pred_5 = builder.like(logDataContentPath, "%" + inFreeText + "%");
        Predicate pred_6 = builder.like(logDataLabelPath, "%" + inFreeText + "%");

        Predicate tmp = builder.or(
                pred_1,
                pred_2,
                pred_3,
                pred_4,
                pred_5,
                pred_6
        );

        ORCondition = ORCondition == null ? tmp : builder.and(ORCondition, tmp);

        if (whereConditions != null && ORCondition
                != null) {
            whereConditions = builder.and(whereConditions, ORCondition);
        } else if (whereConditions == null && ORCondition
                != null) {
            whereConditions = ORCondition;
        }

        if (whereConditions
                != null) {
            subquery.where(whereConditions);
        }

        return subquery;
    }

}

//
//Predicate pred_1 = null;
//        Predicate pred_2 = null;
//        Predicate pred_3 = null;
//        Predicate pred_4 = null;
//        Predicate pred_5 = null;
//        Predicate pred_6 = null;
//
//        for (String inFreeText : freeTextSearch.getSearchTextList()) {
//
//            if (inFreeText != null && !inFreeText.isEmpty()) {
//
//                Predicate tmpPred_1 = builder.like(flowNamePath, "%" + inFreeText + "%");
//                pred_1 = pred_1 == null ? tmpPred_1 : builder.or(pred_1, tmpPred_1);
//
//                Predicate tmpPred_2 = builder.like(flowPointNamePath, "%" + inFreeText + "%");
//                pred_2 = pred_2 == null ? tmpPred_2 : builder.or(pred_2, tmpPred_2);
//
//                Predicate tmpPred_3 = builder.like(applicationNamePath, "%" + inFreeText + "%");
//                pred_3 = pred_3 == null ? tmpPred_3 : builder.or(pred_3, tmpPred_3);
//
//                Predicate tmpPred_4 = builder.like(transactionReferenceIDPath, "%" + inFreeText + "%");
//                pred_4 = pred_4 == null ? tmpPred_4 : builder.or(pred_4, tmpPred_4);
//
//                Predicate tmpPred_5 = builder.like(metaLabelPath, "%" + inFreeText + "%");
//                pred_5 = pred_5 == null ? tmpPred_5 : builder.or(pred_5, tmpPred_5);
//
//                Predicate tmpPred_6 = builder.like(metaValuePath, "%" + inFreeText + "%");
//                pred_6 = pred_6 == null ? tmpPred_6 : builder.or(pred_6, tmpPred_6);
//            }
//        }
//
//        Predicate ORCondition = null;
//        if (pred_1 != null && pred_2 != null && pred_3 != null && pred_4 != null && pred_5 != null && pred_6 != null) {
//
//            ORCondition = builder.and(
//                    pred_1,
//                    pred_2,
//                    pred_3,
//                    pred_4,
//                    pred_5,
//                    pred_6
//            );
//        }
