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

import java.util.List;
import java.util.Stack;
import javax.persistence.PersistenceUnit;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This code is responsible for parsing the persistence.xml into
 * PersistenceUnits
 */
public class JPAHandler extends DefaultHandler {

    /**
     * The Persistence Units that we have parsed
     */
    //private final Stack<PersistenceUnitImpl> persistenceUnits = new Stack<PersistenceUnitImpl>();
    private final Stack<PersistenceUnitImpl> persistenceUnits = new Stack<PersistenceUnitImpl>();
    /**
     * The name of the current element
     */
    private String elementName;
    /**
     * The version of the persistence.xml file
     */
    private final String jpaVersion;
    /**
     * A StringBuilder for caching the information from getCharacters
     */
    private StringBuilder builder = new StringBuilder();
    /**
     * The bundle that contains this persistence descriptor
     */
    private Bundle bundle;

    /**
     * Create a new JPA Handler for the given peristence.xml
     *
     * @param data
     * @param version the version of the JPA schema used in the xml
     */
    public JPAHandler(Bundle b, String version) {
        bundle = b;
        jpaVersion = version;
    }

    /**
     * Collect up the characters, as element's characters may be split across
     * multiple calls. Isn't SAX lovely...
     *
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        builder.append(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes)
            throws SAXException {
        //Do this setting first as we use it later.
        elementName = (localName == null || "".equals(localName)) ? name : localName;

        if ("persistence-unit".equals(elementName)) {
            persistenceUnits.push(new PersistenceUnitImpl(bundle, attributes.getValue("name"), attributes.getValue("transaction-type"), jpaVersion));
        } else if ("exclude-unlisted-classes".equals(elementName)) {
            persistenceUnits.peek().setExcludeUnlisted(true);
        } else if ("property".equals(elementName)) {
            persistenceUnits.peek().addProperty(attributes.getValue("name"), attributes.getValue("value"));
        }

    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        String s = builder.toString().trim();
        //This step is VERY important, otherwise we pollute subsequent
        //elements
        builder = new StringBuilder();

        if ("".equals(s)) {
            return;
        }

        PersistenceUnitImpl pu = persistenceUnits.peek();

        if ("provider".equals(elementName)) {
            pu.setProviderClassName(s);
        } else if ("jta-data-source".equals(elementName)) {
            pu.setJtaDataSource(s);
        } else if ("non-jta-data-source".equals(elementName)) {
            pu.setNonJtaDataSource(s);
        } else if ("mapping-file".equals(elementName)) {
            pu.addMappingFileName(s);
        } else if ("jar-file".equals(elementName)) {
            pu.addJarFileName(s);
        } else if ("class".equals(elementName)) {
            pu.addClassName(s);
        } else if ("exclude-unlisted-classes".equals(elementName)) {
            pu.setExcludeUnlisted(Boolean.parseBoolean(s));
        } else if ("2.0".equals(jpaVersion) && "shared-cache-mode".equals(elementName)) {
            pu.setSharedCacheMode(s);
        } else if ("2.0".equals(jpaVersion) && "validation-mode".equals(elementName)) {
            pu.setValidationMode(s);
        }
    }

    @Override
    public void error(SAXParseException spe) throws SAXException {
        // We throw this exception to be caught further up and logged
        // as an error there
        throw spe;
    }

    /**
     * @return The collection of persistence units that we have parsed
     */
    public List<PersistenceUnitImpl> getPersistenceUnits() {
        return persistenceUnits.subList(0, persistenceUnits.size());
    }

    public PersistenceUnitImpl getPersistenceUnitsByName(String unit_name) throws IllegalArgumentException {
        PersistenceUnitImpl result = null;
        for (PersistenceUnitImpl persistenceUnit : getPersistenceUnits()) {
            if (persistenceUnit.getUnitName().equalsIgnoreCase(unit_name)) {
                result = persistenceUnit;
                break;
            }
        }

        if (result != null) {
            return result;
        } else {
            throw new IllegalArgumentException();
        }
    }

}
