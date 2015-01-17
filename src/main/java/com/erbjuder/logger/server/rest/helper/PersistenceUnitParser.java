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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author server-1
 */
public class PersistenceUnitParser {

    public static final String FILE_NAME = "persistence.xml";
    public static final String PERSISTENCE_JAR_PATH = "/META-INF/" + FILE_NAME;
    public static final String PERSISTENCE_WAR_PATH_1 = "/WEB-INF/classes/" + FILE_NAME;
    public static final String PERSISTENCE_WAR_PATH_2 = "/WEB-INF/classes/" + PERSISTENCE_JAR_PATH;
                                                            
    private String unitName = "";
    private static PersistenceUnitImpl persistenceUnit = null;

    public PersistenceUnitParser(String unitName) {
        this.unitName = unitName;
        initPersistenceUnit();
    }

    public String getDataSourceString() throws ParserConfigurationException {
        String dataSource = null;

        String dataJtaDataSourceString = persistenceUnit.getJtaDataSource();
        String nonJtaDataSourceString = persistenceUnit.getNonJtaDataSource();
        if (!dataJtaDataSourceString.isEmpty()) {
            dataSource = dataJtaDataSourceString;
        } else if (!nonJtaDataSourceString.isEmpty()) {
            dataSource = nonJtaDataSourceString;
        } else {
            throw new ParserConfigurationException();
        }

        return dataSource;
    }

    private void initPersistenceUnit() {
        Exception warEx = null;
        Exception jarEx = null;
        //
        // Parse Path(s)
        if (persistenceUnit == null) { 
            try {
                JPAHandler handler = this.parserPath(PersistenceUnitParser.FILE_NAME);
                persistenceUnit = handler.getPersistenceUnitsByName(unitName);
            } catch (NullPointerException | SAXException | ParserConfigurationException | IOException | IllegalArgumentException ex) {
                Logger.getLogger(PersistenceUnitParser.class.getName()).log(Level.SEVERE, null, ex);
                warEx = ex;
                persistenceUnit = null;
            }
        }

        //
        // Parse Path(s)
        if (persistenceUnit == null) {

            try {
                JPAHandler handler = this.parserPath(PersistenceUnitParser.PERSISTENCE_JAR_PATH);
                persistenceUnit = handler.getPersistenceUnitsByName(unitName);
            } catch (NullPointerException | SAXException | ParserConfigurationException | IOException | IllegalArgumentException ex) {
                Logger.getLogger(PersistenceUnitParser.class.getName()).log(Level.SEVERE, null, ex);
                warEx = ex;
                persistenceUnit = null;
            }
        }

        //
        // Parse Path(s)
        if (persistenceUnit == null) {

            try {
                JPAHandler handler = this.parserPath(PersistenceUnitParser.PERSISTENCE_WAR_PATH_1);
                persistenceUnit = handler.getPersistenceUnitsByName(unitName);
            } catch (NullPointerException | SAXException | ParserConfigurationException | IOException | IllegalArgumentException ex) {
                Logger.getLogger(PersistenceUnitParser.class.getName()).log(Level.SEVERE, null, ex);
                warEx = ex;
                persistenceUnit = null;
            }
        }

        //
        // Parse Path(s)
        if (persistenceUnit == null) {

            try {
                JPAHandler handler = this.parserPath(PersistenceUnitParser.PERSISTENCE_WAR_PATH_2);
                persistenceUnit = handler.getPersistenceUnitsByName(unitName);
            } catch (NullPointerException | SAXException | ParserConfigurationException | IOException | IllegalArgumentException ex) {
                Logger.getLogger(PersistenceUnitParser.class.getName()).log(Level.SEVERE, null, ex);
                warEx = ex;
                persistenceUnit = null;
            }
        }

        // 
        // Still null? Write exceptione
        if (persistenceUnit == null) {
            Logger.getLogger(PersistenceUnitParser.class.getName()).log(Level.SEVERE, "PersistanceUnit still NULL");
            Logger.getLogger(PersistenceUnitParser.class.getName()).log(Level.SEVERE, null, warEx);
            Logger.getLogger(PersistenceUnitParser.class.getName()).log(Level.SEVERE, null, jarEx);
        }
    }

    private SAXParser getSAxParser() throws SAXException, MalformedURLException, ParserConfigurationException {

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
//        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema-instance");// XMLConstants.W3C_XML_SCHEMA_NS_URI);
//        javax.xml.validation.Schema schema = schemaFactory.newSchema(new URL("http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"));
//        parserFactory.setSchema(schema);
//        parserFactory.setNamespaceAware(true);
        SAXParser saxParser = parserFactory.newSAXParser();

        return saxParser;
    }

    private JPAHandler parserPath(String path) throws FileNotFoundException, MalformedURLException, SAXException, ParserConfigurationException, IOException  {

        InputStream in = this.getClass().getClassLoader().getResourceAsStream(path);
        if (in != null) {
            URL xml = new File(path).toURI().toURL();
            JPAHandler handler = new JPAHandler(new Bundle(xml, xml), "1.0");
            getSAxParser().parse(in, handler);
            return handler;
        } else {

            throw new FileNotFoundException("property file '" + path
                    + "' not found in the classpath");
        }

    }

}
