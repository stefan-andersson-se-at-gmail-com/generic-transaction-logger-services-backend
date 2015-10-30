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
import com.erbjuder.logger.server.entity.impl.LogMessage;
import com.erbjuder.logger.server.entity.impl.LogMessageData_Incomplete;
import com.erbjuder.logger.server.entity.interfaces.LogMessageData;
import com.erbjuder.logger.server.facade.interfaces.LogMessageDataFacade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Stefan Andersson
 */
public abstract class LogMessageDataFacadeBaseImpl implements LogMessageDataFacade {

    public LogMessageDataFacadeBaseImpl() {
    }

    public abstract Logger getLogger();

    public LogMessageData getLogMessageData(LogMessageData_Incomplete logMessageData_Incomplete) {
        LogMessageData logMessageData = null;
        try {
            logMessageData = (LogMessageData) this.getEntityManager().find(logMessageData_Incomplete.getFromClass(), logMessageData_Incomplete.getId());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return logMessageData;
    }

    @Override
    public List<LogMessageData> findExpired(Date toDate) {
        ArrayList<LogMessageData> result = new ArrayList<LogMessageData>();
        return result;
    }

    public List<LogMessageData> getLogMessageData(LogMessage logMessage, Set<Class> fetchFromLogMessage) {

        List<LogMessageData> data = new ArrayList<LogMessageData>();
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_01_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_01());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_01(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_02_CLASS)) {
//
//            data.addAll(logMessage.getLogMessageData_Partition_02());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_02(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_03_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_03());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_03(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_04_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_04());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_04(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_05_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_05());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_05(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_06_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_06());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_06(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_07_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_07());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_07(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_08_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_08());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_08(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_09_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_09());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_09(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_10_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_10());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_10(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_11_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_11());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_11(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_12_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_12());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_12(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_13_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_13());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_13(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_14_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_14());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_14(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_15_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_15());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_15(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_16_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_16());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_16(logMessage));
//        }
//
//        if (fetchFromLogMessage.contains(DataBase.LOGMESSAGEDATA_PARTITION_17_CLASS)) {
//            data.addAll(logMessage.getLogMessageData_Partition_17());
//        } else {
//            data.addAll(this.getIncompleteLogMessageData_Partition_17(logMessage));
//        }

        Collections.sort(data);
        return data;
    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_05(LogMessage logMessage) {

        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_05_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_05_CLASS);

    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_15(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_15_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_15_CLASS);
    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_06(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_06_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_06_CLASS);
    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_16(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_16_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_16_CLASS);

    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_10(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_10_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_10_CLASS);

    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_07(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_07_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_07_CLASS);

    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_01(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_01_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_01_CLASS);

    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_08(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_08_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_08_CLASS);

    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_11(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_11_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_11_CLASS);

    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_12(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_12_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_12_CLASS);

    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_02(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_02_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_02_CLASS);

    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_17(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_17_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_17_CLASS);

    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_13(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_13_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_13_CLASS);

    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_14(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_14_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_14_CLASS);

    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_03(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_03_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_03_CLASS);

    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_09(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_09_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_09_CLASS);
    }

    public List<LogMessageData> getIncompleteLogMessageData_Partition_04(LogMessage logMessage) {
        EntityManager em = getEntityManager();
        StringBuilder nativeQuery = this.createQuery(logMessage.getId(), DataBase.LOGMESSAGEDATA_PARTITION_04_CLASS);
        Query query = em.createNativeQuery(nativeQuery.toString());

        return createLogMessageData_Incomplete(logMessage, query.getResultList(), DataBase.LOGMESSAGEDATA_PARTITION_04_CLASS);

    }

    private StringBuilder createQuery(long logMessagePK, Class clazz) {

        StringBuilder nativeQuery = new StringBuilder();
        nativeQuery.append("SELECT ");
        nativeQuery.append("ID, LABEL, ");
        nativeQuery.append("MIMETYPE, CONTENTSIZE, MODIFIED, SEARCHABLE,");
        nativeQuery.append("UTCSERVERTIMESTAMP, UTCLOCALTIMESTAMP ");
        nativeQuery.append("FROM ").append(clazz.getSimpleName()).append(" ");
        nativeQuery.append("where LOGMESSAGE_ID = '").append(logMessagePK).append("' ");

        return nativeQuery;

    }

    private List<LogMessageData> createLogMessageData_Incomplete(LogMessage logMessage, List<Object[]> rawResultList, Class fromClass) {

        List<LogMessageData> resultList = null;
        if (rawResultList == null) {
            return new ArrayList<LogMessageData>();
        } else {
            resultList = new ArrayList<LogMessageData>(rawResultList.size());
        }

        for (Object[] result : rawResultList) {

            long id = (Long) result[0];
            String label = (String) result[1];
            String mimeType = (String) result[2];
            long contentSize = (Long) result[3];
            boolean modified = (Boolean) result[4];
            boolean searchable = (Boolean) result[5];
            java.sql.Timestamp utcServerTimeStamp = (java.sql.Timestamp) result[6];
            java.sql.Timestamp utcLocalTimeStamp = (java.sql.Timestamp) result[7];

            resultList.add(new LogMessageData_Incomplete(
                    id,
                    label,
                    mimeType,
                    contentSize,
                    modified,
                    searchable,
                    utcServerTimeStamp,
                    utcLocalTimeStamp,
                    logMessage,
                    fromClass));
        }

        return resultList;

    }

}
