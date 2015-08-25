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

import com.erbjuder.logger.server.common.helper.DataBase;
import com.erbjuder.logger.server.entity.interfaces.LogMessageData;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Stefan Andersson
 */
@Entity
@Table(name = "LogMessageData_Partition_12")
public class LogMessageData_Partition_12 implements Serializable, LogMessageData {

    @ManyToOne(targetEntity = LogMessage.class)
    private LogMessage logMessage;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
     
    private String label;
    private String mimeType;
    @Column(columnDefinition = DataBase.LOGMESSAGEDATA_PARTITION_12_CONTENT_COLUMN_DEFINITION)
    protected String content;
    private java.sql.Timestamp utcLocalTimeStamp;
    private java.sql.Timestamp utcServerTimeStamp;
    private boolean modified = false;
    private boolean searchable = true;
    private long contentSize;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public LogMessage getLogMessage() {
        return logMessage;
    }

    @Override
    public void setLogMessage(LogMessage logMessage) {
        this.logMessage = logMessage;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LogMessageData_Partition_12)) {
            return false;
        }
        LogMessageData_Partition_12 other = (LogMessageData_Partition_12) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    @Override
    public boolean isSearchable() {
        return searchable;
    }

    @Override
    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    @Override
    public long getContentSize() {
        return contentSize;
    }

    @Override
    public void setContentSize(long contentSize) {
        this.contentSize = contentSize;
    }

          @Override
    public Timestamp getUtcLocalTimeStamp() {
        return utcLocalTimeStamp;
    }

    @Override
    public void setUtcLocalTimeStamp(Timestamp utcLocalTimeStamp) {
        this.utcLocalTimeStamp = utcLocalTimeStamp;
    }

    @Override
    public Timestamp getUtcServerTimeStamp() {
        return utcServerTimeStamp;
    }

    @Override
    public void setUtcServerTimeStamp(Timestamp utcServerTimeStamp) {
        this.utcServerTimeStamp = utcServerTimeStamp;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
        public String toString() {
        return this.getClass().getCanonicalName() + "@" + this.hashCode();
    }

    @Override
    public int compareTo(LogMessageData o) {
        return this.label.compareToIgnoreCase(o.getLabel());
    }

}
