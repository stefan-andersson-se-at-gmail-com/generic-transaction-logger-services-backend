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
package com.erbjuder.logger.server.soap.helper;

import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 *
 * @author server-1
 */
public class PayloadSoapHandler implements SOAPHandler<SOAPMessageContext> {

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        try {
            SOAPMessage msg = context.getMessage();
            SOAPEnvelope env = msg.getSOAPPart().getEnvelope();
            SOAPBody body = env.getBody(); //Now when you have SOAP body you can do whatever you want..
            System.err.println("Got soap payload");

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return true;
    }

    @Override
    public Set<QName> getHeaders() {
       return null;
    }

    @Override
    public boolean handleFault(SOAPMessageContext msg) {
         return true;
    }

    @Override
    public void close(MessageContext context) {
         
    }
}
