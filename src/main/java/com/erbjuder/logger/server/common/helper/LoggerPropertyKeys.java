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
package com.erbjuder.logger.server.common.helper;

/**
 *
 * @author Stefan Andersson
 */
public class LoggerPropertyKeys {

    //
    // System
    //
    public static final String system_type = "dev";

    //
    // Database
    //
    public static final String database_type = "database_type";
    
    // Read
    public static final String database_read_jndi_name = "database_read_jndi_name";
    public static final String database_read_url = "database_read_url";
    public static final String database_read_username = "database_read_username";
    public static final String database_read_password = "database_read_password";
    
    // Write
    public static final String database_write_jndi_name = "database_write_jndi_name";
    public static final String database_write_url = "database_write_url";
    public static final String database_write_username = "database_write_username";
    public static final String database_write_password = "database_write_password";

    // Email notification 
    public static final String emailnotification_topic_queue_connectionFactory_jndi_name
            = "emailnotification_topic_queue_connectionFactory_jndi_name";
    public static final String emailnotification_topic_queue_destination_jndi_name
            = "emailnotification_topic_queue_destination_jndi_name";

}
