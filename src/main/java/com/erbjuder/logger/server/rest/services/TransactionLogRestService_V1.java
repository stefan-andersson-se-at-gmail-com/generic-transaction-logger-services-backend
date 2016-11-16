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

import com.erbjuder.logger.server.common.helper.DataBase;
import com.erbjuder.logger.server.common.services.LogMessageQueries;
import com.erbjuder.logger.server.common.services.LogMessageServiceBase;
import com.erbjuder.logger.server.common.services.ResultSetConverterJSONArray;
import com.generic.global.transactionlogger.Transactions;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;

/**
 *
 * @author server-1
 */
@Path("/v1/logmsg")
public class TransactionLogRestService_V1 {

// http://localhost:8080/log_message_services_backend-1.10-SNAPSHOT-Dev/resources/v1/logmsg/json/search?fromDate=2015-10-31%2000:00:00&toDate=2015-10-31%2023:59:59&page=1&pageSize=10&&viewError=false
// http://localhost:8080/log_message_services_backend-1.10-SNAPSHOT-Dev/resources/v1/logmsg/jsonp/search?fromDate=2015-10-31%2000:00:00&toDate=2015-10-31%2023:59:59&page=1&pageSize=10&&viewError=false&search=534_   
    private JSONArray logMessageSearch(
            Long id,
            Integer partitionId,
            String fromDate,
            String toDate,
            Integer page,
            Integer pageSize,
            String transactionReferenceId,
            Integer viewError,
            List<String> viewApplicationNames,
            List<String> viewFlowNames,
            List<String> viewFlowPointName,
            List<String> notViewApplicationNames,
            List<String> notViewFlowNames,
            List<String> notViewFlowPointName,
            List<String> freeTextSearchList,
            List<String> dataBaseSearchList
    ) throws Exception {

//            System.err.println("[ Got REST call ]");
//            System.err.println("fromDate=[" + fromDate + "]");
//            System.err.println("toDate=[" + toDate + "]");
//            System.err.println("transactionReferenceId=[" + transactionReferenceId + "]");
//            System.err.println("applicationNameList=[" + viewApplicationNames + "]");
//            System.err.println("viewError[" + viewError + "]");
        // 
        // Use dafault if no partition list are provided
        if (dataBaseSearchList == null || dataBaseSearchList.isEmpty()) {
            dataBaseSearchList = getDefaultSearchableDatabases();
        }

        LogMessageQueries loggerSchema = new LogMessageQueries();
        ResultSetConverterJSONArray converter = new ResultSetConverterJSONArray();
        loggerSchema.fetch_logMessageList(
                id,
                partitionId,
                fromDate,
                toDate,
                page,
                pageSize,
                transactionReferenceId,
                viewError,
                viewApplicationNames,
                viewFlowNames,
                viewFlowPointName,
                notViewApplicationNames,
                notViewFlowNames,
                notViewFlowPointName,
                freeTextSearchList,
                dataBaseSearchList,
                converter
        );

        return converter.getResult();

    }

    @GET
    @Path("json/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response jsonSearch(
            @QueryParam("Id") Long id,
            @QueryParam("partitionId") Integer partitionId,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("transactionReferenceId") String transactionReferenceId,
            @QueryParam("viewError") Integer viewError,
            @QueryParam("viewAppName") List<String> viewApplicationNames,
            @QueryParam("viewFlowName") List<String> viewFlowNames,
            @QueryParam("viewFlowPointName") List<String> viewFlowPointName,
            @QueryParam("notViewAppName") List<String> notViewApplicationNames,
            @QueryParam("notViewFlowName") List<String> notViewFlowNames,
            @QueryParam("notViewFlowPointName") List<String> notViewFlowPointName,
            @QueryParam("search") List<String> freeTextSearchList,
            @QueryParam("dbSearchList") List<String> dataBaseSearchList
    ) {
        try {
            JSONArray jsonResult = this.logMessageSearch(
                    id,
                    partitionId,
                    fromDate,
                    toDate,
                    page,
                    pageSize,
                    transactionReferenceId,
                    viewError,
                    viewApplicationNames,
                    viewFlowNames,
                    viewFlowPointName,
                    notViewApplicationNames,
                    notViewFlowNames,
                    notViewFlowPointName,
                    freeTextSearchList,
                    dataBaseSearchList
            );

            return Response.ok(jsonResult.toString()).build();

        } catch (Exception ex) {
            Logger.getLogger(TransactionLogRestService_V1.class.getName()).log(Level.SEVERE, ex.getMessage());
            return Response.serverError().build();
        }
    }

    @GET
    @Path("jsonp/search")
    @Produces("application/javascript")
    public Response jsonpSearch(
            @QueryParam("callback") String callback,
            @QueryParam("Id") Long id,
            @QueryParam("partitionId") Integer partitionId,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("transactionReferenceId") String transactionReferenceId,
            @QueryParam("viewError") Integer viewError,
            @QueryParam("viewAppName") List<String> viewApplicationNames,
            @QueryParam("viewFlowName") List<String> viewFlowNames,
            @QueryParam("viewFlowPointName") List<String> viewFlowPointName,
            @QueryParam("notViewAppName") List<String> notViewApplicationNames,
            @QueryParam("notViewFlowName") List<String> notViewFlowNames,
            @QueryParam("notViewFlowPointName") List<String> notViewFlowPointName,
            @QueryParam("search") List<String> freeTextSearchList,
            @QueryParam("dbSearchList") List<String> dataBaseSearchList
    ) {

        try {

            String internalCallbackName = "callback";
            if (callback != null && !callback.isEmpty()) {
                internalCallbackName = callback;
            }

            JSONArray jsonResult = this.logMessageSearch(
                    id,
                    partitionId,
                    fromDate,
                    toDate,
                    page,
                    pageSize,
                    transactionReferenceId,
                    viewError,
                    viewApplicationNames,
                    viewFlowNames,
                    viewFlowPointName,
                    notViewApplicationNames,
                    notViewFlowNames,
                    notViewFlowPointName,
                    freeTextSearchList,
                    dataBaseSearchList
            );

            return Response.ok(internalCallbackName + "(" + jsonResult.toString() + ")").build();

        } catch (Exception ex) {
            Logger.getLogger(TransactionLogRestService_V1.class.getName()).log(Level.SEVERE, ex.getMessage());
            return Response.serverError().build();
        }

    }

    // http://localhost:8080/log_message_services_one_dev/resources/v1/logmsg/json/view?logMessageId=16877&logMessagePartitionId=304
    // http://localhost:8080/log_message_services_one_dev/resources/v1/logmsg/jsonp/view?logMessageId=16877&logMessagePartitionId=304
    // http://erbjuder.com/log_message_services_one_dev/resources/v1/logmsg/jsonp/view?logMessageId=11658273&logMessagePartitionId=103
    private JSONArray logMessageDataView(
            Long logMessageId,
            Integer logMessagePartitionId,
            List<String> dataBaseSearchList) throws Exception {
        // 
        // Use dafault if no partition list are provided
        if (dataBaseSearchList == null || dataBaseSearchList.isEmpty()) {
            dataBaseSearchList = getDefaultSearchableDatabases();
        }
//
//            System.err.println("[ Got REST call ]");
//            System.err.println("logMessageId=[" + logMessageId + "]");
//            System.err.println("dataBaseSearchList=[" + dataBaseSearchList + "]");

        LogMessageQueries loggerSchema = new LogMessageQueries();
        ResultSetConverterJSONArray converter = new ResultSetConverterJSONArray();
        loggerSchema.fetch_LogMessageData(
                logMessageId,
                logMessagePartitionId,
                dataBaseSearchList,
                converter);

        return converter.getResult();
    }

    @GET
    @Path("json/view")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dataView(
            @QueryParam("logMessageId") Long logMessageId,
            @QueryParam("logMessagePartitionId") Integer logMessagePartitionId,
            @QueryParam("dbSearchList") List<String> dataBaseSearchList
    ) {
        try {

            JSONArray jsonResult = this.logMessageDataView(
                    logMessageId,
                    logMessagePartitionId,
                    dataBaseSearchList
            );

            return Response.ok(jsonResult.toString()).build();

        } catch (Exception ex) {
            Logger.getLogger(TransactionLogRestService_V1.class.getName()).log(Level.SEVERE, ex.getMessage());
            return Response.serverError().build();
        }
    }

    @GET
    @Path("jsonp/view")
    @Produces("application/javascript")
    public Response dataView(
            @QueryParam("callback") String callback,
            @QueryParam("logMessageId") Long logMessageId,
            @QueryParam("logMessagePartitionId") Integer logMessagePartitionId,
            @QueryParam("dbSearchList") List<String> dataBaseSearchList
    ) {

        try {

            String internalCallbackName = "callback";
            if (callback != null && !callback.isEmpty()) {
                internalCallbackName = callback;
            }

            JSONArray jsonResult = this.logMessageDataView(
                    logMessageId,
                    logMessagePartitionId,
                    dataBaseSearchList
            );

            return Response.ok(internalCallbackName + "(" + jsonResult.toString() + ")").build();

        } catch (Exception ex) {
            Logger.getLogger(TransactionLogRestService_V1.class.getName()).log(Level.SEVERE, ex.getMessage());
            return Response.serverError().build();
        }

    }

    private JSONArray flowPointNameSearch(
            String fromDate,
            String toDate,
            Integer page,
            Integer pageSize,
            String transactionReferenceId,
            Integer viewError,
            List<String> viewApplicationNames,
            List<String> viewFlowNames,
            List<String> viewFlowPointName,
            List<String> notViewApplicationNames,
            List<String> notViewFlowNames,
            List<String> notViewFlowPointName,
            List<String> freeTextSearchList,
            List<String> dataBaseSearchList
    ) throws Exception {

        // 
        // Use dafault if no partition list are provided
        if (dataBaseSearchList == null || dataBaseSearchList.isEmpty()) {
            dataBaseSearchList = getDefaultSearchableDatabases();
        }

        LogMessageQueries loggerSchema = new LogMessageQueries();
        ResultSetConverterJSONArray converter = new ResultSetConverterJSONArray();
        loggerSchema.fetch_FlowPointNames(
                fromDate,
                toDate,
                page,
                pageSize,
                transactionReferenceId,
                viewError,
                viewApplicationNames,
                viewFlowNames,
                viewFlowPointName,
                notViewApplicationNames,
                notViewFlowNames,
                notViewFlowPointName,
                freeTextSearchList,
                dataBaseSearchList,
                converter
        );

        return converter.getResult();

    }

    @GET
    @Path("json/flowPointNames")
    @Produces(MediaType.APPLICATION_JSON)
    public Response jsonFlowPointNameSearch(
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("transactionReferenceId") String transactionReferenceId,
            @QueryParam("viewError") Integer viewError,
            @QueryParam("viewAppName") List<String> viewApplicationNames,
            @QueryParam("viewFlowName") List<String> viewFlowNames,
            @QueryParam("viewFlowPointName") List<String> viewFlowPointName,
            @QueryParam("notViewAppName") List<String> notViewApplicationNames,
            @QueryParam("notViewFlowName") List<String> notViewFlowNames,
            @QueryParam("notViewFlowPointName") List<String> notViewFlowPointName,
            @QueryParam("search") List<String> freeTextSearchList,
            @QueryParam("dbSearchList") List<String> dataBaseSearchList
    ) {
        try {
            JSONArray jsonResult = this.flowPointNameSearch(
                    fromDate,
                    toDate,
                    page,
                    pageSize,
                    transactionReferenceId,
                    viewError,
                    viewApplicationNames,
                    viewFlowNames,
                    viewFlowPointName,
                    notViewApplicationNames,
                    notViewFlowNames,
                    notViewFlowPointName,
                    freeTextSearchList,
                    dataBaseSearchList
            );

            return Response.ok(jsonResult.toString()).build();

        } catch (Exception ex) {
            Logger.getLogger(TransactionLogRestService_V1.class.getName()).log(Level.SEVERE, ex.getMessage());
            return Response.serverError().build();
        }
    }

    @GET
    @Path("jsonp/flowPointNames")
    @Produces("application/javascript")
    public Response jsonpFlowPointNameSearch(
            @QueryParam("callback") String callback,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("transactionReferenceId") String transactionReferenceId,
            @QueryParam("viewError") Integer viewError,
            @QueryParam("viewAppName") List<String> viewApplicationNames,
            @QueryParam("viewFlowName") List<String> viewFlowNames,
            @QueryParam("viewFlowPointName") List<String> viewFlowPointName,
            @QueryParam("notViewAppName") List<String> notViewApplicationNames,
            @QueryParam("notViewFlowName") List<String> notViewFlowNames,
            @QueryParam("notViewFlowPointName") List<String> notViewFlowPointName,
            @QueryParam("search") List<String> freeTextSearchList,
            @QueryParam("dbSearchList") List<String> dataBaseSearchList
    ) {

        try {

            String internalCallbackName = "callback";
            if (callback != null && !callback.isEmpty()) {
                internalCallbackName = callback;
            }

            JSONArray jsonResult = this.flowPointNameSearch(
                    fromDate,
                    toDate,
                    page,
                    pageSize,
                    transactionReferenceId,
                    viewError,
                    viewApplicationNames,
                    viewFlowNames,
                    viewFlowPointName,
                    notViewApplicationNames,
                    notViewFlowNames,
                    notViewFlowPointName,
                    freeTextSearchList,
                    dataBaseSearchList
            );

            return Response.ok(internalCallbackName + "(" + jsonResult.toString() + ")").build();

        } catch (Exception ex) {
            Logger.getLogger(TransactionLogRestService_V1.class.getName()).log(Level.SEVERE, ex.getMessage());
            return Response.serverError().build();
        }

    }

    private JSONArray flowNameSearch(
            String fromDate,
            String toDate,
            Integer page,
            Integer pageSize,
            String transactionReferenceId,
            Integer viewError,
            List<String> viewApplicationNames,
            List<String> viewFlowNames,
            List<String> viewFlowPointName,
            List<String> notViewApplicationNames,
            List<String> notViewFlowNames,
            List<String> notViewFlowPointName,
            List<String> freeTextSearchList,
            List<String> dataBaseSearchList
    ) throws Exception {

        // 
        // Use dafault if no partition list are provided
        if (dataBaseSearchList == null || dataBaseSearchList.isEmpty()) {
            dataBaseSearchList = getDefaultSearchableDatabases();
        }

        LogMessageQueries loggerSchema = new LogMessageQueries();
        ResultSetConverterJSONArray converter = new ResultSetConverterJSONArray();
        loggerSchema.fetch_FlowNames(
                fromDate,
                toDate,
                page,
                pageSize,
                transactionReferenceId,
                viewError,
                viewApplicationNames,
                viewFlowNames,
                viewFlowPointName,
                notViewApplicationNames,
                notViewFlowNames,
                notViewFlowPointName,
                freeTextSearchList,
                dataBaseSearchList,
                converter
        );

        return converter.getResult();

    }

    @GET
    @Path("json/flowNames")
    @Produces(MediaType.APPLICATION_JSON)
    public Response jsonFlowNameSearch(
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("transactionReferenceId") String transactionReferenceId,
            @QueryParam("viewError") Integer viewError,
            @QueryParam("viewAppName") List<String> viewApplicationNames,
            @QueryParam("viewFlowName") List<String> viewFlowNames,
            @QueryParam("viewFlowPointName") List<String> viewFlowPointName,
            @QueryParam("notViewAppName") List<String> notViewApplicationNames,
            @QueryParam("notViewFlowName") List<String> notViewFlowNames,
            @QueryParam("notViewFlowPointName") List<String> notViewFlowPointName,
            @QueryParam("search") List<String> freeTextSearchList,
            @QueryParam("dbSearchList") List<String> dataBaseSearchList
    ) {
        try {
            JSONArray jsonResult = this.flowNameSearch(
                    fromDate,
                    toDate,
                    page,
                    pageSize,
                    transactionReferenceId,
                    viewError,
                    viewApplicationNames,
                    viewFlowNames,
                    viewFlowPointName,
                    notViewApplicationNames,
                    notViewFlowNames,
                    notViewFlowPointName,
                    freeTextSearchList,
                    dataBaseSearchList
            );

            return Response.ok(jsonResult.toString()).build();

        } catch (Exception ex) {
            Logger.getLogger(TransactionLogRestService_V1.class.getName()).log(Level.SEVERE, ex.getMessage());
            return Response.serverError().build();
        }
    }

    @GET
    @Path("jsonp/flowNames")
    @Produces("application/javascript")
    public Response jsonpFlowNameSearch(
            @QueryParam("callback") String callback,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("transactionReferenceId") String transactionReferenceId,
            @QueryParam("viewError") Integer viewError,
            @QueryParam("viewAppName") List<String> viewApplicationNames,
            @QueryParam("viewFlowName") List<String> viewFlowNames,
            @QueryParam("viewFlowPointName") List<String> viewFlowPointName,
            @QueryParam("notViewAppName") List<String> notViewApplicationNames,
            @QueryParam("notViewFlowName") List<String> notViewFlowNames,
            @QueryParam("notViewFlowPointName") List<String> notViewFlowPointName,
            @QueryParam("search") List<String> freeTextSearchList,
            @QueryParam("dbSearchList") List<String> dataBaseSearchList
    ) {

        try {

            String internalCallbackName = "callback";
            if (callback != null && !callback.isEmpty()) {
                internalCallbackName = callback;
            }

            JSONArray jsonResult = this.flowNameSearch(
                    fromDate,
                    toDate,
                    page,
                    pageSize,
                    transactionReferenceId,
                    viewError,
                    viewApplicationNames,
                    viewFlowNames,
                    viewFlowPointName,
                    notViewApplicationNames,
                    notViewFlowNames,
                    notViewFlowPointName,
                    freeTextSearchList,
                    dataBaseSearchList
            );

            return Response.ok(internalCallbackName + "(" + jsonResult.toString() + ")").build();

        } catch (Exception ex) {
            Logger.getLogger(TransactionLogRestService_V1.class.getName()).log(Level.SEVERE, ex.getMessage());
            return Response.serverError().build();
        }

    }

    private JSONArray applicationNameSearch(
            String fromDate,
            String toDate,
            Integer page,
            Integer pageSize,
            String transactionReferenceId,
            Integer viewError,
            List<String> viewApplicationNames,
            List<String> viewFlowNames,
            List<String> viewFlowPointName,
            List<String> notViewApplicationNames,
            List<String> notViewFlowNames,
            List<String> notViewFlowPointName,
            List<String> freeTextSearchList,
            List<String> dataBaseSearchList
    ) throws Exception {

        // 
        // Use dafault if no partition list are provided
        if (dataBaseSearchList == null || dataBaseSearchList.isEmpty()) {
            dataBaseSearchList = getDefaultSearchableDatabases();
        }

        LogMessageQueries loggerSchema = new LogMessageQueries();
        ResultSetConverterJSONArray converter = new ResultSetConverterJSONArray();
        loggerSchema.fetch_ApplicationNames(
                fromDate,
                toDate,
                page,
                pageSize,
                transactionReferenceId,
                viewError,
                viewApplicationNames,
                viewFlowNames,
                viewFlowPointName,
                notViewApplicationNames,
                notViewFlowNames,
                notViewFlowPointName,
                freeTextSearchList,
                dataBaseSearchList,
                converter
        );

        return converter.getResult();

    }

    @GET
    @Path("json/applicationNames")
    @Produces(MediaType.APPLICATION_JSON)
    public Response jsonApplicationNameSearch(
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("transactionReferenceId") String transactionReferenceId,
            @QueryParam("viewError") Integer viewError,
            @QueryParam("viewAppName") List<String> viewApplicationNames,
            @QueryParam("viewFlowName") List<String> viewFlowNames,
            @QueryParam("viewFlowPointName") List<String> viewFlowPointName,
            @QueryParam("notViewAppName") List<String> notViewApplicationNames,
            @QueryParam("notViewFlowName") List<String> notViewFlowNames,
            @QueryParam("notViewFlowPointName") List<String> notViewFlowPointName,
            @QueryParam("search") List<String> freeTextSearchList,
            @QueryParam("dbSearchList") List<String> dataBaseSearchList
    ) {
        try {
            JSONArray jsonResult = this.applicationNameSearch(
                    fromDate,
                    toDate,
                    page,
                    pageSize,
                    transactionReferenceId,
                    viewError,
                    viewApplicationNames,
                    viewFlowNames,
                    viewFlowPointName,
                    notViewApplicationNames,
                    notViewFlowNames,
                    notViewFlowPointName,
                    freeTextSearchList,
                    dataBaseSearchList
            );

            return Response.ok(jsonResult.toString()).build();

        } catch (Exception ex) {
            Logger.getLogger(TransactionLogRestService_V1.class.getName()).log(Level.SEVERE, ex.getMessage());
            return Response.serverError().build();
        }
    }

    @GET
    @Path("jsonp/applicationNames")
    @Produces("application/javascript")
    public Response jsonpApplicationNameSearch(
            @QueryParam("callback") String callback,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("transactionReferenceId") String transactionReferenceId,
            @QueryParam("viewError") Integer viewError,
            @QueryParam("viewAppName") List<String> viewApplicationNames,
            @QueryParam("viewFlowName") List<String> viewFlowNames,
            @QueryParam("viewFlowPointName") List<String> viewFlowPointName,
            @QueryParam("notViewAppName") List<String> notViewApplicationNames,
            @QueryParam("notViewFlowName") List<String> notViewFlowNames,
            @QueryParam("notViewFlowPointName") List<String> notViewFlowPointName,
            @QueryParam("search") List<String> freeTextSearchList,
            @QueryParam("dbSearchList") List<String> dataBaseSearchList
    ) {

        try {

            String internalCallbackName = "callback";
            if (callback != null && !callback.isEmpty()) {
                internalCallbackName = callback;
            }

            JSONArray jsonResult = this.applicationNameSearch(
                    fromDate,
                    toDate,
                    page,
                    pageSize,
                    transactionReferenceId,
                    viewError,
                    viewApplicationNames,
                    viewFlowNames,
                    viewFlowPointName,
                    notViewApplicationNames,
                    notViewFlowNames,
                    notViewFlowPointName,
                    freeTextSearchList,
                    dataBaseSearchList
            );

            return Response.ok(internalCallbackName + "(" + jsonResult.toString() + ")").build();

        } catch (Exception ex) {
            Logger.getLogger(TransactionLogRestService_V1.class.getName()).log(Level.SEVERE, ex.getMessage());
            return Response.serverError().build();
        }

    }

    @POST
    @Path("persist")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})

    public Response persist(Transactions transactions
    ) {

        System.err.println("[ Got REST call POST ]");

        com.generic.global.transactionlogger.Response serviceResponse = new LogMessageServiceBase().create(transactions);
        if (serviceResponse.isReturn()) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("notificationOfApplicationName")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response notificationOfApplicationName(@QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("transactionReferenceId") String transactionReferenceId,
            @QueryParam("viewError") Integer viewError,
            @QueryParam("viewAppName") List<String> viewApplicationNames,
            @QueryParam("viewFlowName") List<String> viewFlowNames,
            @QueryParam("viewFlowPointName") List<String> viewFlowPointName,
            @QueryParam("notViewAppName") List<String> notViewApplicationNames,
            @QueryParam("notViewFlowName") List<String> notViewFlowNames) {

        return Response.ok("()").build();
    }

    private List<String> getDefaultSearchableDatabases() {
        List<String> defaultSearchableDatabases = new ArrayList<>();
        defaultSearchableDatabases.add(DataBase.LOGMESSAGEDATA_PARTITION_01_NAME);
        defaultSearchableDatabases.add(DataBase.LOGMESSAGEDATA_PARTITION_02_NAME);
        defaultSearchableDatabases.add(DataBase.LOGMESSAGEDATA_PARTITION_03_NAME);
        defaultSearchableDatabases.add(DataBase.LOGMESSAGEDATA_PARTITION_04_NAME);
        defaultSearchableDatabases.add(DataBase.LOGMESSAGEDATA_PARTITION_05_NAME);
        defaultSearchableDatabases.add(DataBase.LOGMESSAGEDATA_PARTITION_06_NAME);
        defaultSearchableDatabases.add(DataBase.LOGMESSAGEDATA_PARTITION_07_NAME);
        defaultSearchableDatabases.add(DataBase.LOGMESSAGEDATA_PARTITION_08_NAME);
        defaultSearchableDatabases.add(DataBase.LOGMESSAGEDATA_PARTITION_09_NAME);
        defaultSearchableDatabases.add(DataBase.LOGMESSAGEDATA_PARTITION_10_NAME);
        defaultSearchableDatabases.add(DataBase.LOGMESSAGEDATA_PARTITION_11_NAME);
        return defaultSearchableDatabases;

    }

}
