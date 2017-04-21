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
import com.erbjuder.logger.server.common.services.PrepareStatementHelper;
import static java.lang.Math.ceil;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 *
 * @author Stefan Andersson
 */
public class EmailNotificationPreProcessor {

    List<EmailNotification> emailNotifications2Update = new ArrayList();
    private Map<String, List<EmailNotificationBinding>> eMailBindingStorage = new TreeMap<>();

    public Map<String, List<EmailNotificationBinding>> getBindingStorage() {
        return this.eMailBindingStorage;
    }

    public List<String> getIdStringList(Set<EmailNotification> emailNotifications) {

        List<String> ids = new ArrayList();
        for (EmailNotification emailNotification : emailNotifications) {
            ids.add(emailNotification.getId().toString());
        }
        return ids;
    }
    
    public void clear(){
        emailNotifications2Update.clear();
        eMailBindingStorage.clear();
    }
    
    public List<EmailNotification> getEmailNotifications2Update() {
        return emailNotifications2Update;
    }

    public void process(
            Set<EmailNotification> emailNotifications,
            List<EmailNotificationLogMessage> emailNotificationLogMessages
    ) {

        // For all rules
        for (EmailNotification emailNotification : emailNotifications) {

            // Rules that are valid, Match/bind logMessages
            EmailNotificationBinding binding = new EmailNotificationBinding();
            binding.setEmailNotification(emailNotification);
            if (validToUse(emailNotification)) {
                
                for (EmailNotificationLogMessage emailNotificationLogMessage : emailNotificationLogMessages) {
                    if (this.isPatternMatch(emailNotification, emailNotificationLogMessage)) {
                        binding.addEmailNotificationLogMessage(emailNotificationLogMessage);
                    }
                }
            }

            // Save Binding
            if (!binding.getEmailNotificationLogMessages().isEmpty()) {
                
                // Update sent time
                emailNotification.setNotificationSentTimestamp(TimeStampUtils.createSystemNanoTimeStamp());
                
                // to send email
                String eMail = emailNotification.getNotificationEmail();
                List<EmailNotificationBinding> bindings = eMailBindingStorage.get(eMail);
                if (bindings == null) {
                    bindings = new ArrayList<>();
                    bindings.add(binding);
                    eMailBindingStorage.put(eMail, bindings);
                } else {
                    bindings.add(binding);
                }
                
                // To update
                emailNotifications2Update.add(emailNotification);
            }
        }
    }

    private boolean isPatternMatch(EmailNotification emailNotification, EmailNotificationLogMessage emailNotificationLogMessage) {

        String patternAppName = emailNotification.getApplicationName().trim();
        String patternFlowName = emailNotification.getFlowName().trim();
        String patternFlowPointName = emailNotification.getFlowPointName().trim();

        String logMsgAppName = emailNotificationLogMessage.getApplicationName().trim();
        String logMsgFlowName = emailNotificationLogMessage.getFlowName().trim();
        String logMsgFlowPointName = emailNotificationLogMessage.getFlowPointName().trim();

        boolean isMatch = false;
        if (!patternAppName.isEmpty() && !patternFlowName.isEmpty() && !patternFlowPointName.isEmpty()) {
            isMatch = patternAppName.equals(logMsgAppName) && patternFlowName.equals(logMsgFlowName) && patternFlowPointName.equals(logMsgFlowPointName);

        } else if (!patternAppName.isEmpty() && !patternFlowName.isEmpty() && patternFlowPointName.isEmpty()) {
            isMatch = patternAppName.equals(logMsgAppName) && patternFlowName.equals(logMsgFlowName);

        } else if (!patternAppName.isEmpty() && patternFlowName.isEmpty() && patternFlowPointName.isEmpty()) {
            isMatch = patternAppName.equals(logMsgAppName);

        } else if (patternAppName.isEmpty() && !patternFlowName.isEmpty() && !patternFlowPointName.isEmpty()) {
            isMatch = patternFlowName.equals(logMsgFlowName) && patternFlowPointName.equals(logMsgFlowPointName);

        } else if (patternAppName.isEmpty() && !patternFlowName.isEmpty() && patternFlowPointName.isEmpty()) {
            isMatch = patternFlowName.equals(logMsgFlowName);

        } else if (patternAppName.isEmpty() && patternFlowName.isEmpty() && !patternFlowPointName.isEmpty()) {
            isMatch = patternFlowPointName.equals(logMsgFlowPointName);

        } else if (!patternAppName.isEmpty() && patternFlowName.isEmpty() && !patternFlowPointName.isEmpty()) {
            isMatch = patternAppName.equals(logMsgAppName) && patternFlowPointName.equals(logMsgFlowPointName);

        }

        return isMatch;
    }

    private boolean validToUse(EmailNotification emailNotification) {

        boolean isValid = false;
        int maxNotifications = emailNotification.getMaxNotifications();
        String maxUnit = emailNotification.getMaxNotificationsUnit();
        Timestamp lastSentTime = emailNotification.getNotificationSentTimestamp();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(lastSentTime.getTime());

        int hours = 24;
        int minutes = 60;
        int seconds = 60;

        if ("min".equals(maxUnit)) {
            float sum = seconds / maxNotifications;
            calendar.add(Calendar.SECOND, (int) ceil(sum));

        } else if ("hour".equals(maxUnit)) {

            float sum = minutes / maxNotifications;

            if (sum < 1.0) {
                sum = sum * seconds;
                calendar.add(Calendar.SECOND, (int) ceil(sum));
            } else {

                calendar.add(Calendar.MINUTE, (int) ceil(sum));
            }

        } else if ("day".equals(maxUnit)) {

            float sum = hours / maxNotifications;

            if (sum < 1.0) {

                sum = sum * minutes;
                if (sum < 1) {
                    sum = sum * seconds;
                    calendar.add(Calendar.SECOND, (int) ceil(sum));
                } else {
                    calendar.add(Calendar.MINUTE, (int) ceil(sum));
                }
            } else {
                calendar.add(Calendar.HOUR, (int) ceil(sum));
            }
        }

        if (System.currentTimeMillis() >= calendar.getTimeInMillis()) {
            isValid = true;
        }

        return isValid;
    }

}
