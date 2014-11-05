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
package com.erbjuder.logger.server.facade.interfaces;

import com.erbjuder.logger.server.common.helper.DataBaseSearchController;
import com.erbjuder.logger.server.common.helper.FreeTextSearchController;
import com.erbjuder.logger.server.entity.impl.LogMessage;
import com.erbjuder.logger.server.facade.pojo.FlowAndTransactionId;
import com.erbjuder.logger.server.facade.pojo.LogMessageStatistic;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Stefan Andersson
 */
public interface LogMessageFacade extends AbstractFacade<LogMessage> {

    public int countFlows(Collection<String> flowNames, Collection<String> flowPointNames);

    public List<LogMessage> findFlows(Collection<String> flowNames, Collection<String> flowPointNames, int[] range);

    public List<String> getFlowNames(long fromDate, long toDate, String startsWithName);

    public List<String> getFlowPointNames(long fromDate, long toDate, String startsWithName);

    public List<String> getApplicationNames(long fromDate, long toDate, String startsWithName);

    public int countFlowAndTransactionId();

    public List<FlowAndTransactionId> getFlowAndTransactionId(int[] range);

    public List<LogMessage> getFlowLogMessages(List<FlowAndTransactionId> flowAndTransactionIds);

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
            Boolean viewError);

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
            int maxResult);

    public List<LogMessage> findExpired(Date toDate);

    
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
        );
    
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
    );

    @Override
    public EntityManager getEntityManager();
}