/*
 * Copyright (C) 2016 Stefan Andersson
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

import com.erbjuder.logger.server.bean.EmailService;
import com.erbjuder.logger.server.common.services.InternalTransactionHeader;
import com.erbjuder.logger.server.common.services.InternalTransactionHeaders;
import com.erbjuder.logger.server.rest.services.EmailNotificationPrepareToSendBase;
import com.erbjuder.logger.server.rest.services.EmailNotificationServiceBase;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 *
 * @author Stefan Andersson
 */
@MessageDriven(mappedName = "jms/transactionLoggerQueueTopic",
        activationConfig = {
            @ActivationConfigProperty(propertyName = "acknowledgeMode",
                    propertyValue = "Auto-acknowledge")
            ,
            @ActivationConfigProperty(propertyName = "destinationType",
                    propertyValue = "javax.jms.Topic")
        })
public class EmailNotificationServiceMessageListener extends EmailNotificationServiceBase implements MessageListener {

    private static final Logger logger = Logger.getLogger(EmailNotificationServiceMessageListener.class.getName());

    @EJB
    protected EmailNotificationPrepareToSendBase emailNotificationPrepareToSendBase;

    @Override
    public void onMessage(Message message) {

        try {

            ObjectMessage objectMessage = (ObjectMessage) message;
            InternalTransactionHeaders internalHeaders = (InternalTransactionHeaders) objectMessage.getObject();
            if (internalHeaders.getSomeMarkedAsError()) {

                InternalTransactionHeader header = internalHeaders.getInternalTransactionHeaders().get(0);
                emailNotificationPrepareToSendBase.persist(
                        header.getApplicationName(),
                        header.getTransactionReferenceID()
                );
            }

        } catch (JMSException | ClassCastException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
