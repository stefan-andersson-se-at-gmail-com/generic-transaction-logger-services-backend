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
package com.erbjuder.logger.server.facade.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Stefan Andersson
 */
public class LogMessageStatistic implements Serializable{
    
    private final String applicationName;
    private final long numberOfTransactions;
    private final long numberOfMessages;
    private final BigDecimal numberOfSuccess;
    private final BigDecimal numberOfError;
    
    public LogMessageStatistic(
      String applicationname,
      long numberOfTransactions,
      long numberOfMessages,
      BigDecimal numberOfSuccess,
      BigDecimal numberOfError
    ){
        this.applicationName = applicationname;
        this.numberOfTransactions = numberOfTransactions;
        this.numberOfMessages = numberOfMessages;
        this.numberOfSuccess = numberOfSuccess;
        this.numberOfError = numberOfError;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public long getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public long getNumberOfMessages() {
        return numberOfMessages;
    }

    public BigDecimal getNumberOfSuccess() {
        return numberOfSuccess;
    }

    public BigDecimal getNumberOfError() {
        return numberOfError;
    }
    
    
    
    
}
