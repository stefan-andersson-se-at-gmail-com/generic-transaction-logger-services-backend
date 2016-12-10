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
package com.erbjuder.logger.server.rest.helper;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIESOR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.net.URL;
//import org.osgi.framework.Bundle;

/**
 * A bean to hold metadata about the location of a persistence.xml file
 */
public class Bundle 
{
  /** The location of the persistence.xml file */
  private final URL persistenceXML;
  /** The location of the persistence unit root */
  private final URL persistenceUnitRoot;

  
  /**
   * Construct a new PersistenceLocationData
   * @param persistenceXML
   * @param persistenceUnitRoot
   * @param persistenceBundle
   */
  public Bundle(URL persistenceXML, URL persistenceUnitRoot)
  {
    this.persistenceXML = persistenceXML;
    this.persistenceUnitRoot = persistenceUnitRoot;
  }
  
  /**
   * @return A URL to the persistence.xml file
   */
  public URL getPersistenceXML()
  {
    return persistenceXML;
  }
  
  /**
   * @return A URL to the persistence unit root
   */
  public URL getPersistenceUnitRoot()
  {
    return persistenceUnitRoot;
  }
  
  
}

