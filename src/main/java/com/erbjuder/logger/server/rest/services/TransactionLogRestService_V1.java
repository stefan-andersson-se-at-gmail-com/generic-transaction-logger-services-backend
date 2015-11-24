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
import com.erbjuder.logger.server.common.helper.LogMessageQueries;
import com.erbjuder.logger.server.common.helper.ResultSetConverter;
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
@Path("/v1/rest/logmsg")
public class TransactionLogRestService_V1 {

// http://localhost:8080/log_message_services_backend-1.10-SNAPSHOT-Dev/resources/v1/rest/logmsg/search?fromDate=2015-10-31%2000:00:00&toDate=2015-10-31%2023:59:59&page=1&pageSize=10&&viewError=false
// http://localhost:8080/log_message_services_backend-1.10-SNAPSHOT-Dev/resources/v1/rest/logmsg/search?fromDate=2015-10-31%2000:00:00&toDate=2015-10-31%2023:59:59&page=1&pageSize=10&&viewError=false&search=534_   
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("transactionReferenceId") String transactionReferenceId,
            @QueryParam("viewError") Boolean viewError,
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

            System.err.println("[ Got REST call ]");
            System.err.println("fromDate=[" + fromDate + "]");
            System.err.println("toDate=[" + toDate + "]");
            System.err.println("transactionReferenceId=[" + transactionReferenceId + "]");
            System.err.println("applicationNameList=[" + viewApplicationNames + "]");
            System.err.println("viewError[" + viewError + "]");

            // 
            // Use dafault if no partition list are provided
            if (dataBaseSearchList == null || dataBaseSearchList.isEmpty()) {
                dataBaseSearchList = getDefaultSearchableDatabases();
            }

            LogMessageQueries loggerSchema = new LogMessageQueries();
            ResultSetConverter converter = new ResultSetConverter();
            JSONArray jsonResult = converter.toJSONArray(loggerSchema.fetch_logMessageList(
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
            ));

            return Response.ok(jsonResult.toString()).build();

        } catch (Exception ex) {
            Logger.getLogger(TransactionLogRestService_V1.class.getName()).log(Level.SEVERE, ex.getMessage());
            return Response.serverError().build();
        }
    }

    // http://localhost:8080/log_message_services_backend-1.10-SNAPSHOT-Dev/resources/v1/rest/logmsg/view?logMessageId=16877&logMessagePartitionId=304
    @GET
    @Path("/view")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(
            @QueryParam("logMessageId") String logMessageId,
            @QueryParam("logMessagePartitionId") int logMessagePartitionId,
            @QueryParam("dbSearchList") List<String> dataBaseSearchList
    ) {
        try {

            // 
            // Use dafault if no partition list are provided
            if (dataBaseSearchList == null || dataBaseSearchList.isEmpty()) {
                dataBaseSearchList = getDefaultSearchableDatabases();
            }

            System.err.println("[ Got REST call ]");
            System.err.println("logMessageId=[" + logMessageId + "]");
            System.err.println("dataBaseSearchList=[" + dataBaseSearchList + "]");

            LogMessageQueries loggerSchema = new LogMessageQueries();
            ResultSetConverter converter = new ResultSetConverter();
            JSONArray jsonResult = converter.toJSONArray(loggerSchema.fetch_LogMessageData(
                    logMessageId,
                    logMessagePartitionId,
                    dataBaseSearchList));

            return Response.ok(jsonResult.toString()).build();

        } catch (Exception ex) {
            Logger.getLogger(TransactionLogRestService_V1.class.getName()).log(Level.SEVERE, ex.getMessage());
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/persist")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response persist(Transactions transactions) {

        System.err.println("[ Got REST call POST ]");
        System.err.println("Transactions=[" + transactions + "]");

        return Response.ok("OK").build();
    }

    private List<String> getDefaultSearchableDatabases() {
        List<String> defaultSearchableDatabases = new ArrayList<String>();
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
