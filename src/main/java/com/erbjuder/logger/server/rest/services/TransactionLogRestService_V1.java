/* 
 * Copyright (C) 2014 erbjuder.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.erbjuder.logger.server.rest.services;

import com.erbjuder.logger.server.rest.services.dao.LoggerSchema;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.GET;
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

//    @PersistenceUnit(unitName = "TransactionLogger")
//    private EntityManagerFactory entityManagerFactory;

  
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate, 
            @QueryParam("transactionReferenceId") String transactionReferenceId,
            @QueryParam("viewError") Boolean viewError, 
            @QueryParam("applicationNameList") List<String> applicationNames, 
            @QueryParam("flowNameList") List<String> flowNames, 
            @QueryParam("flowPointNameList") List<String> flowPointName, 
            @QueryParam("FreeTextSearchList") List<String> freeTextSearchList,  
            @QueryParam("DataBaseSearchList") List<String> dataBaseSearchList 
            ) {
        try {
 
              
          
         LoggerSchema loggerSchema = new LoggerSchema();
            JSONArray jsonResult = loggerSchema.search_logMessageList(
                    fromDate, 
                    toDate, 
                    transactionReferenceId, 
                    viewError, 
                    applicationNames, 
                    flowNames, 
                    flowPointName, 
                    freeTextSearchList, 
                    dataBaseSearchList
            );
            return Response.ok(jsonResult.toString()).build();
            
            
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

}
 