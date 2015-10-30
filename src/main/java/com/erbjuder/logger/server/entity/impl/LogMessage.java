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
package com.erbjuder.logger.server.entity.impl;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author Stefan Andersson
 */
@Entity
@Table(name = "LogMessage")
public class LogMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "PARTITION_ID")
    private int partitionId;
    @Column(name = "UTCLOCALTIMESTAMP")
    private java.sql.Timestamp utcLocalTimeStamp;
    @Column(name = "UTCSERVERTIMESTAMP")
    private java.sql.Timestamp utcServerTimeStamp;
    @Column(name = "EXPIREDDATE")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date expiredDate;
    @Column(name = "TRANSACTIONREFERENCEID")
    private String transactionReferenceID;
    @Column(name = "APPLICATIONNAME")
    private String applicationName;
    @Column(name = "ISERROR")
    private boolean isError;
    @Column(name = "FLOWNAME")
    private String flowName;
    @Column(name = "FLOWPOINTNAME")
    private String flowPointName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    public Timestamp getUtcLocalTimeStamp() {
        return utcLocalTimeStamp;
    }

    public void setUtcLocalTimeStamp(Timestamp utcLocalTimeStamp) {
        this.utcLocalTimeStamp = utcLocalTimeStamp;
    }

    public Timestamp getUtcServerTimeStamp() {
        return utcServerTimeStamp;
    }

    public void setUtcServerTimeStamp(Timestamp utcServerTimeStamp) {
        this.utcServerTimeStamp = utcServerTimeStamp;
    }

    public String getTransactionReferenceID() {
        return transactionReferenceID;
    }

    public void setTransactionReferenceID(String transactionReferenceID) {
        this.transactionReferenceID = transactionReferenceID;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public boolean isIsError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = new java.sql.Date(expiredDate.getTime());
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFlowPointName() {
        return flowPointName;
    }

    public void setFlowPointName(String flowPointName) {
        this.flowPointName = flowPointName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LogMessage)) {
            return false;
        }
        LogMessage other = (LogMessage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getCanonicalName() + "@" + this.hashCode();
    }
}
