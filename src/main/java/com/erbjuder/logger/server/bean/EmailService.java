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
package com.erbjuder.logger.server.bean;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Stefan Andersson
 */
@Named
@Stateless
public class EmailService {

    @Resource(lookup = "mail/Session")
    private Session mailSession;

    private static final Logger logger = Logger.getLogger(EmailService.class.getName());

    // @Asynchronous
    public void sendEmail(String to, String subject, String body) {

        try {
            
            
            System.err.println(mailSession.getProperty("mail.from").getClass());
            String from = "transaction.logger@gmail.com";
            String host = "mailout.comhem.se";

            // Create properties, get Session
            Properties props = mailSession.getProperties();
            props.clear();
            props.put("mail.debug", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "25");
            Session session = mailSession; // Session.getInstance(props);

            // Instantiate a message
            Message msg = new MimeMessage(session);

            //Set message attributes
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setFrom(new InternetAddress(from));// mailSession.getProperty("mail.from")));
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            // Set message content
            msg.setContent( body, "text/plain"); // text/html");

            //Send the message
            Transport.send(msg);

        } catch (MessagingException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }
}
