/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sandvik.logger.server.soap.services;

import com.erbjuder.logger.server.common.services.LogMessageServiceBase;
import com.sandvik.global.transactionlogger.ObjectFactory;
import com.sandvik.global.transactionlogger.Transactions;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author ds38745
 */
@WebService(serviceName = "LogMessageService", portName = "TransactionLogger_InPort", endpointInterface = "com.sandvik.global.transactionlogger.TransactionLogger", targetNamespace = "urn:sandvik.com:Global:TransactionLogger", wsdlLocation = "WEB-INF/wsdl/Sandvik_TransactionLogger_Service/Transactionlogger.wsdl")
@XmlSeeAlso({
    ObjectFactory.class,
    Transactions.class
})

public class LogMessageService extends LogMessageServiceBase {

    public void persist(@WebParam(name = "Transactions", targetNamespace = "urn:sandvik.com:Global:TransactionLogger", partName = "Transactions") Transactions oldTransactions) {
        com.generic.global.transactionlogger.Transactions newTransactions = LogMessageServiceWrapper.translate(oldTransactions);
        new LogMessageServiceBase().create(newTransactions);
    }

}
