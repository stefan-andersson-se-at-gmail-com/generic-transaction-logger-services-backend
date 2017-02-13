
/*
 * Copyright (C) 2015 Stefan Andersson
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
package com.erbjuder.logger.server.queue;

import com.erbjuder.logger.server.common.services.InternalTransactionHeaders;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 *
 * @author Stefan Andersson
 */
/*
@MessageDriven(mappedName = "jms/transactionLoggerQueueTopic",
        activationConfig = {
            @ActivationConfigProperty(propertyName = "acknowledgeMode",
                    propertyValue = "Auto-acknowledge")
            ,
            @ActivationConfigProperty(propertyName = "destinationType",
                    propertyValue = "javax.jms.Topic")
        })
public class StatisticalMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {

            InternalTransactionHeaders internalHeaders = (InternalTransactionHeaders) objectMessage.getObject();

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
*/