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
package com.erbjuder.logger.server.entity.interfaces;

import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author Stefan Andersson
 */
public interface LogMessageData extends Comparable<LogMessageData> {

    public Long getId();

    public void setId(Long id);

    public int getPartitionId();

    public void setPartitionId(int id);

    public Long getLogMessageId();

    public void setLogMessageId(Long logMessageId);

    public boolean isModified();

    public void setModified(boolean contentModified);

    public boolean isSearchable();

    public void setSearchable(boolean searchable);

    public long getContentSize();

    public void setContentSize(long contentSize);

    public Timestamp getUtcLocalTimeStamp();

    public void setUtcLocalTimeStamp(Timestamp utcLocalTimeStamp);

    public Timestamp getUtcServerTimeStamp();

    public void setUtcServerTimeStamp(Timestamp utcServerTimeStamp);

    public String getLabel();

    public void setLabel(String label);

    public String getMimeType();

    public void setMimeType(String mimeType);

    public String getContent();

    public void setContent(String content);

    public Date getExpiredDate();

    public void setExpiredDate(Date date);

}
