
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
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

/**
 *
 * @author Stefan Andersson
 */
/*
@Stateless
public class TopicQueueMessageSender {

    @Resource(mappedName = "jms/transactionLoggerTopicConnectionFactory")
    private TopicConnectionFactory topicConnectionFactory;

    @Resource(mappedName = "jms/transactionLoggerQueueTopic")
    private Topic topic;

    private static final Logger logger = Logger.getLogger(TopicQueueMessageSender.class.getName());

    public void produceMessages(InternalTransactionHeaders internalTransactionHeaders) {

        TopicConnection connection = null;
        try {
            connection = topicConnectionFactory.createTopicConnection();
        } catch (JMSException | NullPointerException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        TopicSession session = null;
        try {
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException | NullPointerException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        TopicPublisher messagePublisher = null;
        try {
            messagePublisher = session.createPublisher(topic);
        } catch (JMSException | NullPointerException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        ObjectMessage objectMessage = null;
        try {
            objectMessage = session.createObjectMessage();
        } catch (JMSException | NullPointerException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        try {
            objectMessage.setObject((Serializable) internalTransactionHeaders);
            messagePublisher.send(objectMessage);

            messagePublisher.close();
            connection.close();
            session.close();
        } catch (JMSException | NullPointerException ex) {
            Logger.getLogger(TopicQueueMessageSender.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
*/