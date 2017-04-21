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

import com.erbjuder.logger.server.bean.EmailNotificationLogMessage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Stefan Andersson
 */
public class EmailNotificationLogMessagePreProcessor {

    private final Set<String> applicationNames = new HashSet<>();
    private final Set<String> flowNames = new HashSet<>();
    private final Set<String> flowPointNames = new HashSet<>();

    public void clear() {
        applicationNames.clear();
        flowNames.clear();
        flowPointNames.clear();
    }

    public void process(List<EmailNotificationLogMessage> emailNotificationLogMessages) {

        for (EmailNotificationLogMessage emailNotificationLogMessage : emailNotificationLogMessages) {

            String appName = emailNotificationLogMessage.getApplicationName();
            String flowName = emailNotificationLogMessage.getFlowName();
            String flowPointName = emailNotificationLogMessage.getFlowPointName();

            // Save parts
            if (!appName.isEmpty()) {
                applicationNames.add(appName);
            }
            if (!flowName.isEmpty()) {
                flowNames.add(flowName);
            }
            if (!flowPointName.isEmpty()) {
                flowPointNames.add(flowPointName);
            }
        }
    }

    public Set<String> getApplicationNames() {
        return applicationNames;
    }

    public Set<String> getFlowNames() {
        return flowNames;
    }

    public Set<String> getFlowPointNames() {
        return flowPointNames;
    }
}
