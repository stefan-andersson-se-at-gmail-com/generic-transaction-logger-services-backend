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

import com.erbjuder.logger.server.entity.interfaces.LogMessageData;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author Stefan Andersson
 */
public class LogMessageData_Incomplete implements Serializable, LogMessageData {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Class fromClass;
    protected String label;
    protected String mimeType;
    protected String content = "";
    protected java.sql.Timestamp utcLocalTimeStamp;
    protected java.sql.Timestamp utcServerTimeStamp;
    protected boolean modified = false;
    protected boolean searchable = true;
    protected long contentSize;
    protected LogMessage logMessage;

    public LogMessageData_Incomplete() {
        super();
    }

    public LogMessageData_Incomplete(
            long id,
            String label,
            String mimeType,
            long contentSize,
            boolean modified,
            boolean searchable,
            java.sql.Timestamp utcServerTimeStamp,
            java.sql.Timestamp utcLocalTimeStamp,
            LogMessage logMessage,
            Class fromClass
    ) {
        this.id = id;
        this.fromClass = fromClass;
        //
        // Super
        this.label = label;
        this.contentSize = contentSize;
        this.mimeType = mimeType;
        this.modified = modified;
        this.searchable = searchable;
        this.logMessage = logMessage;
        this.utcLocalTimeStamp = utcLocalTimeStamp;
        this.utcServerTimeStamp = utcServerTimeStamp;

    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Class getFromClass() {
        return fromClass;
    }

    public void setFromClass(Class fromClass) {
        this.fromClass = fromClass;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LogMessageData_Incomplete)) {
            return false;
        }
        LogMessageData_Incomplete other = (LogMessageData_Incomplete) object;
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
    public void setContentSize(long size) {
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
    public LogMessage getLogMessage() {
        return logMessage;
    }

    @Override
    public void setLogMessage(LogMessage logMessage) {
        this.logMessage = logMessage;
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
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
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
