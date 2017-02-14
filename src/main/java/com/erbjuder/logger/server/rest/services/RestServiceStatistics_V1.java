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

import com.erbjuder.logger.server.common.services.ResultSetConverterJSONArray;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Stefan Andersson
 */
@Path("/v1/statistic")
public class RestServiceStatistics_V1 {

    @GET
    @Path("jsonp/uniqueApplicationNameTransactions")
    @Produces("application/javascript")
    public Response jsonpUniqueApplicationNameTransactions(
            @QueryParam("callback") String callback,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("viewAppName") String viewApplicationName
    ) {
        try {

            String internalCallbackName = "callback";
            if (callback != null && !callback.isEmpty()) {
                internalCallbackName = callback;
            }

            ResultSetConverterJSONArray converter = new ResultSetConverterJSONArray();
            new TransactionLogStatisticBase().uniqueApplicationNameTransactions(
                    fromDate,
                    toDate,
                    page,
                    pageSize,
                    viewApplicationName,
                    converter
            );

            return Response.ok(internalCallbackName + "(" + converter.getResult().toString() + ")").build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("json/uniqueApplicationNameTransactions")
    @Produces("application/javascript")
    public Response jsonUniqueApplicationNameTransactions(
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("viewAppName") String viewApplicationName
    ) {
        try {

            ResultSetConverterJSONArray converter = new ResultSetConverterJSONArray();
            new TransactionLogStatisticBase().uniqueApplicationNameTransactions(
                    fromDate,
                    toDate,
                    page,
                    pageSize,
                    viewApplicationName,
                    converter
            );

            return Response.ok(converter.getResult().toString()).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

}
