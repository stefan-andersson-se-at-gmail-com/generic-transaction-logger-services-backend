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

/**
 *
 * @author Stefan Andersson
 */
public class EmailNotificationPrepareToSend {
    private Long id;
    private String logmessageApplicationName = "";
    private String logmessageTransactionReferenceId = "";
    private boolean sent = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogmessageApplicationName() {
        return logmessageApplicationName;
    }

    public void setLogmessageApplicationName(String logmessageApplicationName) {
        this.logmessageApplicationName = logmessageApplicationName;
    }

    public String getLogmessageTransactionReferenceId() {
        return logmessageTransactionReferenceId;
    }

    public void setLogmessageTransactionReferenceId(String logmessageTransactionReferenceId) {
        this.logmessageTransactionReferenceId = logmessageTransactionReferenceId;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
