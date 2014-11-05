/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erbjuder.logger.server.entity.interfaces;

import com.erbjuder.logger.server.entity.impl.LogMessage;
import java.sql.Timestamp;
import org.json.simple.JSONObject;

/**
 *
 * @author Stefan Andersson
 */
public interface LogMessageData extends Comparable<LogMessageData>{

    public Long getId();

    public void setId(Long id);

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
    
    public LogMessage getLogMessage();

    public void setLogMessage(LogMessage logMessage);

    public String getLabel();

    public void setLabel(String label);

    public String getMimeType();

    public void setMimeType(String mimeType);

    public String getContent();

    public void setContent(String content);

    public String toJSONString();

    public String toJSONPrettyString();

    public JSONObject toJSON();
}
