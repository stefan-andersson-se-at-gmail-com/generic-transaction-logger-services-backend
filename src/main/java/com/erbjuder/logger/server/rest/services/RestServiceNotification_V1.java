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
package com.erbjuder.logger.server.rest.services;

import com.erbjuder.logger.server.bean.EmailNotification;
import com.erbjuder.logger.server.common.services.ResultSetConverterJSONArray;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
//import org.json.JSONArray;
//import org.json.JSONObject;

/**
 *
 * @author server-1
 */
@Path("/v1/notification")
public class RestServiceNotification_V1 {

    @GET
    @Path("jsonp/view")
    @Produces("application/javascript")
    public Response jsonpViewNotifications(
            @QueryParam("callback") String callback,
            @QueryParam("id") Long id,
            @QueryParam("notificationEmail") List<String> notificationEmails,
            @QueryParam("applicationName") List<String> applicationNames,
            @QueryParam("flowName") List<String> flowNames,
            @QueryParam("flowPointName") List<String> flowPointNames,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize
    ) {
        try {

            String internalCallbackName = "callback";
            if (callback != null && !callback.isEmpty()) {
                internalCallbackName = callback;
            }

            ResultSetConverterJSONArray converter = new ResultSetConverterJSONArray();
            new EmailNotificationServiceBase().view(
                    id,
                    notificationEmails,
                    applicationNames,
                    flowNames,
                    flowPointNames,
                    page,
                    pageSize,
                    converter
            );

            return Response.ok(internalCallbackName + "(" + converter.getResult().toString() + ")").build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("json/view")
    @Produces("application/javascript")
    public Response jsonViewNotifications(
            @QueryParam("id") Long id,
            @QueryParam("notificationEmail") List<String> notificationEmails,
            @QueryParam("applicationName") List<String> applicationNames,
            @QueryParam("flowName") List<String> flowNames,
            @QueryParam("flowPointName") List<String> flowPointNames,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize
    ) {

        try {

            ResultSetConverterJSONArray converter = new ResultSetConverterJSONArray();
            new EmailNotificationServiceBase().view(
                    id,
                    notificationEmails,
                    applicationNames,
                    flowNames,
                    flowPointNames,
                    page,
                    pageSize,
                    converter
            );

            return Response.ok(converter.getResult().toString()).build();

        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    // DELET, PUT && POST can't be cross domain
    @DELETE
    @Path("json/remove")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void jsonDeleteNotifications(
            @QueryParam("id") Long id,
            @QueryParam("notificationEmail") String notificationEmail,
            @QueryParam("applicationName") String applicationName,
            @QueryParam("flowName") String flowName,
            @QueryParam("flowPointName") String flowPointName,
            @QueryParam("maxNotifications") String maxNotifications,
            @QueryParam("maxNotificationsUnit") String maxNotificationsUnit) {

        // System.out.println("json/remove request");
        // System.out.println("id = " + id);
        // System.out.println("notificationEmail = " + notificationEmail);
        // validation
        if (id != null && !notificationEmail.isEmpty()) {
            EmailNotification emailNotification = new EmailNotification();
            emailNotification.setId(id);
            emailNotification.setNotificationEmail(notificationEmail);
            emailNotification.setApplicationName(applicationName);
            emailNotification.setFlowName(flowName);
            emailNotification.setFlowPointName(flowPointName);
            emailNotification.setFlowPointName(maxNotifications);
            emailNotification.setFlowPointName(maxNotificationsUnit);

            com.generic.global.transactionlogger.Response serviceResponse
                    = new EmailNotificationServiceBase().remove(emailNotification);

        }
    }

    @DELETE
    @Path("json/removeAndReplyEmailNotifications")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response jsonDeleteAndReplyEmailNotifications(
            @QueryParam("id") Long id,
            @QueryParam("notificationEmail") String notificationEmail,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize) {

        // System.out.println("json/remove request");
        // System.out.println("id = " + id);
        // System.out.println("notificationEmail = " + notificationEmail);
        Response response;
        try {

            // validation
            if (id != null && !notificationEmail.isEmpty()) {
                EmailNotification emailNotification = new EmailNotification();
                emailNotification.setId(id);
                emailNotification.setNotificationEmail(notificationEmail);

                ResultSetConverterJSONArray converter = new ResultSetConverterJSONArray();
                new EmailNotificationServiceBase().removeAndReplyEmailConfigurations(
                        emailNotification,
                        page,
                        pageSize,
                        converter
                );
                response = Response.ok(converter.getResult().toString()).build();
            } else {
                response = Response.serverError().build();
            }

        } catch (Exception e) {
            response = Response.serverError().build();
        }

        return response;
    }

    @POST
    @Path("json/persistAndReplyEmailNotifications")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response jsonPersistAndReplyEmailNotifications(
            @QueryParam("notificationEmail") String notificationEmail,
            @QueryParam("applicationName") String applicationName,
            @QueryParam("flowName") String flowName,
            @QueryParam("flowPointName") String flowPointName,
            @QueryParam("maxNotification") Integer maxNotification,
            @QueryParam("maxNotificationUnit") String maxNotificationUnit,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize) {

        // System.out.println("json/remove request");
        // System.out.println("id = " + id);
        // System.out.println("notificationEmail = " + notificationEmail);
        Response response;
        try {

            // validation
            if (!notificationEmail.isEmpty()) {
                EmailNotification emailNotification = new EmailNotification();
                emailNotification.setNotificationEmail(notificationEmail);
                emailNotification.setApplicationName(applicationName);
                emailNotification.setFlowName(flowName);
                emailNotification.setFlowPointName(flowPointName);
                emailNotification.setMaxNotifications(maxNotification);
                emailNotification.setMaxNotificationsUnit(maxNotificationUnit);

                ResultSetConverterJSONArray converter = new ResultSetConverterJSONArray();
                new EmailNotificationServiceBase().persistAndReplyEmailConfigurations(
                        emailNotification,
                        page,
                        pageSize,
                        converter
                );
                 
                response = Response.ok(converter.getResult().toString()).build();
            } else {
                response = Response.serverError().build();
            }

        } catch (Exception e) {
            response = Response.serverError().build();
        }

        return response;
    }

    // DELET, PUT && POST can't be cross domain ( jsonp )
    @POST
    @Path("json/persist")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response jsonPersistNotifications(Set<EmailNotification> emailNotifications) throws IOException {
        try {

            com.generic.global.transactionlogger.Response serviceResponse = new EmailNotificationServiceBase().persist(emailNotifications);

            if (serviceResponse.isReturn()) {
                return Response.status(Response.Status.OK).build();
            } else {
                return Response.serverError().build();
            }

        } catch (Exception e) {
            return Response.serverError().build();
        }

    }

}
