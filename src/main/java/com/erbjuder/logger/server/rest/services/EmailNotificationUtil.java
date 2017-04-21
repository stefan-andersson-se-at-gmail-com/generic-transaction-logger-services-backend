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
import com.erbjuder.logger.server.common.helper.TimeStampUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;

/**
 *
 * @author Stefan Andersson
 */
public class EmailNotificationUtil {

    public String emailPageTemplateError_Header() {

        StringBuilder builder = new StringBuilder();
        builder.append("You have register yourself as notifier when error occurs.\n");
        builder.append("One or more error match your notification pattern.\n");
        return builder.toString();
 
    }

    public String emailPageTemplateError_Body() {

        StringBuilder builder = new StringBuilder();
        builder.append("\n\n");
        builder.append("Pattern [PATTERN_NAME] match.\n");
        builder.append("Link to view error messages(s) : \n");
        builder.append("[VIEW_LINK] .\n");
        builder.append("Link to unregister yourself! \n");
        builder.append("[REMOVE_LINK] .\n");
        return builder.toString();

    }

    public String emailPageSubstitute_Body(
            String templatePage,
            EmailNotification emailNotification,
            Timestamp minTimestamp) throws UnsupportedEncodingException {

        templatePage = templatePage.replaceFirst("\\[PATTERN_NAME\\]", emailNotification.toString());
        templatePage = templatePage.replaceFirst("\\[VIEW_LINK\\]", emailPageViewLink(emailNotification, minTimestamp));
        templatePage = templatePage.replaceFirst("\\[REMOVE_LINK\\]", emailPageRemoveLink(emailNotification));
        return templatePage;
    }

    public String emailPageTemplatePersist() {

        StringBuilder builder = new StringBuilder();
        builder.append("<html><head><meta http-equiv='content-type' content='text/html; charset=utf-8'/>");
        builder.append("<title></title></head><body lang='en-US' dir='ltr'><br/>");
        builder.append("You is registered as a notifier when error(s) occurs in : ");
        builder.append("[APPLICATION_NAME].<br/><br/>");
        builder.append("Follow provided link to view error messages(s) : <br/>");
        builder.append("<a href='[VIEW_LINK]' />.<br/><br/>");
        builder.append("To unregister yourself! <br/> <a href='[REMOVE_LINK] '/>.</p>");
        builder.append("<p style='margin-bottom: 0in'></p></body></html>");

        return builder.toString();

    }

    public String emailPageTemplateDelete() {

        StringBuilder builder = new StringBuilder();
        builder.append("<html><head><meta http-equiv='content-type' content='text/html; charset=utf-8'/>");
        builder.append("<title></title></head><body lang='en-US' dir='ltr'><br/>");
        builder.append("You is registered as a notifier when error(s) occurs in : ");
        builder.append("[APPLICATION_NAME].<br/><br/>");
        builder.append("Follow provided link to view error messages(s) : <br/>");
        builder.append("<a href='[VIEW_LINK]'/>.<br/><br/>");
        builder.append("To unregister yourself! <br/> <a href='[REMOVE_LINK]' />.</p>");
        builder.append("<p style='margin-bottom: 0in'></p></body></html>");

        return builder.toString();

    }

    private String emailPageViewLink(EmailNotification emailNotification, Timestamp minTimestamp) throws UnsupportedEncodingException {

        StringBuilder builder = new StringBuilder();
        builder.append("http://erbjuder.com/log_message_services_client_js_msg_dev/index.html?");
        builder.append("fromDate=").append(URLEncoder.encode(TimeStampUtils.timeStampToString(minTimestamp), "UTF-8")).append("&");
         

        String patternAppName = emailNotification.getApplicationName().trim();
        String patternFlowName = emailNotification.getFlowName().trim();
        String patternFlowPointName = emailNotification.getFlowPointName().trim();

        if (!patternAppName.isEmpty() && !patternFlowName.isEmpty() && !patternFlowPointName.isEmpty()) {
            builder.append("viewAppName=").append(URLEncoder.encode(patternAppName, "UTF-8")).append("&");
            builder.append("viewFlowName=").append(URLEncoder.encode(patternFlowName, "UTF-8")).append("&");
            builder.append("viewFlowPointName=").append(URLEncoder.encode(patternFlowPointName, "UTF-8")).append("&");

        } else if (!patternAppName.isEmpty() && !patternFlowName.isEmpty() && patternFlowPointName.isEmpty()) {
            builder.append("viewAppName=").append(URLEncoder.encode(patternAppName, "UTF-8")).append("&");
            builder.append("viewFlowName=").append(URLEncoder.encode(patternFlowName, "UTF-8")).append("&");

        } else if (!patternAppName.isEmpty() && patternFlowName.isEmpty() && patternFlowPointName.isEmpty()) {
            builder.append("viewAppName=").append(URLEncoder.encode(patternAppName, "UTF-8")).append("&");

        } else if (patternAppName.isEmpty() && !patternFlowName.isEmpty() && !patternFlowPointName.isEmpty()) {
            builder.append("viewFlowName=").append(URLEncoder.encode(patternFlowName, "UTF-8")).append("&");
            builder.append("viewFlowPointName=").append(URLEncoder.encode(patternFlowPointName, "UTF-8")).append("&");

        } else if (patternAppName.isEmpty() && !patternFlowName.isEmpty() && patternFlowPointName.isEmpty()) {
            builder.append("viewFlowName=").append(URLEncoder.encode(patternFlowName, "UTF-8")).append("&");

        } else if (patternAppName.isEmpty() && patternFlowName.isEmpty() && !patternFlowPointName.isEmpty()) {
            builder.append("viewFlowPointName=").append(URLEncoder.encode(patternFlowPointName, "UTF-8")).append("&");

        } else if (!patternAppName.isEmpty() && patternFlowName.isEmpty() && !patternFlowPointName.isEmpty()) {
            builder.append("viewAppName=").append(URLEncoder.encode(patternAppName, "UTF-8")).append("&");
            builder.append("viewFlowPointName=").append(URLEncoder.encode(patternFlowPointName, "UTF-8")).append("&");
        }

        builder.append("viewError=").append("1").append("&");
        return builder.toString();
    }

    private String emailPageRemoveLink(EmailNotification emailNotification) throws UnsupportedEncodingException {

        StringBuilder builder = new StringBuilder();
        builder.append("http://erbjuder.com/log_message_services_client_js_notification_dev/index.html?");
        builder.append("id=").append(emailNotification.getId()).append('&');
        builder.append("notificationEmail=").append(emailNotification.getNotificationEmail()).append('&');
        builder.append("page=").append("1").append('&');
        builder.append("pageSize=").append("200").append("&");
        return builder.toString();
    }

}
