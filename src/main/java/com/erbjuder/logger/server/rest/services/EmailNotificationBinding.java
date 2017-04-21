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
package com.erbjuder.logger.server.rest.services;

import com.erbjuder.logger.server.bean.EmailNotification;
import com.erbjuder.logger.server.bean.EmailNotificationLogMessage;
import com.erbjuder.logger.server.common.helper.TimeStampUtils;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Stefan Andersson
 */
public class EmailNotificationBinding {

    private EmailNotification emailNotification;
    private List<EmailNotificationLogMessage> emailNotificationLogMessages = new ArrayList<>();
    private Timestamp minTime = TimeStampUtils.createSystemNanoTimeStamp();

    public EmailNotification getEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(EmailNotification emailNotification) {
        this.emailNotification = emailNotification;
    }

    public List<EmailNotificationLogMessage> getEmailNotificationLogMessages() {
        return emailNotificationLogMessages;
    }

    public void setEmailNotificationLogMessages(List<EmailNotificationLogMessage> emailNotificationLogMessages) {
        this.calculateMinTime(emailNotificationLogMessages);
        this.emailNotificationLogMessages = emailNotificationLogMessages;
    }

    public void addEmailNotificationLogMessage(EmailNotificationLogMessage emailNotificationLogMessage) {
        this.calculateMinTime(emailNotificationLogMessage);
        this.emailNotificationLogMessages.add(emailNotificationLogMessage);
    }

    public Timestamp getMinTime() {
        return minTime;
    }

    private void calculateMinTime(EmailNotificationLogMessage emailNotificationLogMessage) {
        Timestamp timestamp = emailNotificationLogMessage.getUtcServerTimestamp();
        if (minTime.getTime() > timestamp.getTime()) {
            this.minTime = timestamp;
        }
    }

    private void calculateMinTime(List<EmailNotificationLogMessage> emailNotificationLogMessages) {
        for (EmailNotificationLogMessage emailNotificationLogMessage : emailNotificationLogMessages) {
            calculateMinTime(emailNotificationLogMessage);
        }
    }

}
