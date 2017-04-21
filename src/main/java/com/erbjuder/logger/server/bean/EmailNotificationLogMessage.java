/*
 * Copyright (C) 2017 Stefan Andersson
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
package com.erbjuder.logger.server.bean;

import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * @author Stefan Andersson
 */
public class EmailNotificationLogMessage {

    private Long id;
    private Long logMessageId = null;
    private Boolean isError = null;
    private String flowName = "";
    private String flowPointName = "";
    private String applicationName = "";
    private Timestamp utcServerTimestamp = null;
    private Timestamp utcLocalTimestamp = null;
    private String transactionReferenceID = null;
    private java.sql.Date expiredDate = null;
    private Integer partitionId = null;
    private boolean sent = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLogMessageId() {
        return logMessageId;
    }

    public void setLogMessageId(Long logMessageId) {
        this.logMessageId = logMessageId;
    }

    public Boolean getIsError() {
        return isError;
    }

    public void setIsError(Boolean isError) {
        this.isError = isError;
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

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Timestamp getUtcServerTimestamp() {
        return utcServerTimestamp;
    }

    public void setUtcServerTimestamp(Timestamp utcServerTimestamp) {
        this.utcServerTimestamp = utcServerTimestamp;
    }

    public Timestamp getUtcLocalTimestamp() {
        return utcLocalTimestamp;
    }

    public void setUtcLocalTimestamp(Timestamp utcLocalTimestamp) {
        this.utcLocalTimestamp = utcLocalTimestamp;
    }

    public String getTransactionReferenceID() {
        return transactionReferenceID;
    }

    public void setTransactionReferenceID(String transactionReferenceID) {
        this.transactionReferenceID = transactionReferenceID;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[ ");
        builder.append(this.getId());
        builder.append(",");
        builder.append(this.getApplicationName());
        builder.append(",");
        builder.append(this.getFlowName());
        builder.append(",");
        builder.append(this.getFlowPointName());
        builder.append(" ]");
        return builder.toString();

    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EmailNotificationLogMessage)) {
            return false;
        }
        EmailNotificationLogMessage other = (EmailNotificationLogMessage) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

}
