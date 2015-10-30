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
package com.erbjuder.logger.server.soap.services;

import com.generic.global.transactionlogger.Response;
import com.generic.global.transactionlogger.Transactions;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.WebServiceException;

/**
 *
 * @author Stefan Andersson
 */
@WebService(
        serviceName = "TransactionLogSynchronousService",
        portName = "TransactionLogSynchronous_InPort",
        endpointInterface = "com.generic.global.transactionlogger.TransactionLogSynchronous",
        targetNamespace = "urn:generic.com:Global:TransactionLogger",
        wsdlLocation = "WEB-INF/wsdl/Log_Service_Synchronous/Log_Service_Synchronous.wsdl")
@XmlSeeAlso({
    com.generic.global.fault.ObjectFactory.class,
    com.generic.global.transactionlogger.ObjectFactory.class
})

public class TransactionLogSynchronousService extends LogMessageServiceBase {

    private static final Logger logger = Logger.getLogger(TransactionLogSynchronousService.class.getName());

    @WebResult(name = "Response", targetNamespace = "urn:generic.com:Global:TransactionLogger", partName = "response")
    public Response persist(
            @WebParam(name = "Transactions", targetNamespace = "urn:generic.com:Global:TransactionLogger", partName = "Transactions") Transactions transactions) throws WebServiceException {
        logger.log(Level.SEVERE, "[ Got transaction log synchronous event ] ");
        return super.create(transactions);
    }

}
