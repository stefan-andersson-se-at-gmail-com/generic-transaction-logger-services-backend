/*
 * Copyright (C) 2016 Stefan Andersson
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

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Stefan Andersson
 */
@XmlRootElement(name = "EmailNotification")       //only needed if we also want to generate XML
public class EmailNotification implements Serializable {

    private Long id;
    private String notificationEmail = "";
    private String applicationName = "";
    private String flowName = "";
    private String flowPointName = "";
    private java.sql.Date reqistrationDate;
    private java.sql.Timestamp notificationSentTimestamp;
    private Integer notificationCounter = 0;
    private Integer maxNotifications = 3;
    private String maxNotificationsUnit = "day";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
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

    public java.sql.Date getReqistrationDate() {
        return reqistrationDate;
    }

    public void setReqistrationDate(java.sql.Date reqistrationDate) {
        this.reqistrationDate = reqistrationDate;
    }

    public java.sql.Timestamp getNotificationSentTimestamp() {
        return notificationSentTimestamp;
    }

    public void setNotificationSentTimestamp(java.sql.Timestamp notificationSentDate) {
        this.notificationSentTimestamp = notificationSentDate;
    }

    public Integer getNotificationCounter() {
        return notificationCounter;
    }

    public void setNotificationCounter(Integer notificationCounter) {
        this.notificationCounter = notificationCounter;
    }

    public Integer getMaxNotifications() {
        return maxNotifications;
    }

    public void setMaxNotifications(Integer maxNotifications) {
        this.maxNotifications = maxNotifications;
    }

    public String getMaxNotificationsUnit() {
        return maxNotificationsUnit;
    }

    public void setMaxNotificationsUnit(String maxNotificationsUnit) {
        this.maxNotificationsUnit = maxNotificationsUnit;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[ ");
        builder.append(this.getApplicationName());
        builder.append(",");
        builder.append(this.getFlowName());
        builder.append(",");
        builder.append(this.getFlowPointName());
        builder.append(" ]");
        return builder.toString();

    }

    @Override
    public boolean equals(Object obj) {

        boolean result = false;
        if (obj instanceof EmailNotification) {
            EmailNotification arg = (EmailNotification) obj;

            result = this.getNotificationEmail().equalsIgnoreCase(arg.getNotificationEmail())
                    && this.getApplicationName().equalsIgnoreCase(arg.getApplicationName())
                    && this.getFlowName().equalsIgnoreCase(arg.getFlowName())
                    && this.getFlowPointName().equalsIgnoreCase(arg.getFlowPointName());

        }

        return result;
    }

    @Override
    public int hashCode() {
        return this.getNotificationEmail().hashCode()
                + this.getApplicationName().hashCode()
                + this.getFlowName().hashCode()
                + this.getFlowPointName().hashCode();

    }

}
