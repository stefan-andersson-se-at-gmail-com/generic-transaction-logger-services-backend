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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
 
/**
 * An implementation of PersistenceUnit for parsed persistence unit metadata
 *
 */
@SuppressWarnings("unchecked")
public class PersistenceUnitImpl implements ParsedPersistenceUnit {

    /**
     * A map to hold the metadata from the xml
     */
    private final Map<String, Object> metadata = new HashMap<String, Object>();
    /**
     * The bundle defining this persistence unit
     */
    private final Bundle bundle;

 

    /**
     * Create a new persistence unit with the given name, transaction type,
     * location and defining bundle
     *
     * @param name may be null
     * @param transactionType may be null
     * @param location
     * @param version The version of the JPA schema used in persistence.xml
     */
    public PersistenceUnitImpl(Bundle b, String name, String transactionType, String version) {
        this.bundle = b;
        metadata.put(SCHEMA_VERSION, version);

        if (name == null) {
            name = "";
        }

        metadata.put(UNIT_NAME, name);
        if (transactionType != null) {
            metadata.put(TRANSACTION_TYPE, transactionType);
        }

    }

    @Override
    public Bundle getDefiningBundle() {
        return bundle;
    }

    @Override
    public Map<String, Object> getPersistenceXmlMetadata() {
        Map<String, Object> data = new HashMap<String, Object>(metadata);
        if (data.containsKey(MAPPING_FILES)) {
            data.put(MAPPING_FILES, ((ArrayList) metadata.get(MAPPING_FILES)).clone());
        }
        if (data.containsKey(JAR_FILES)) {
            data.put(JAR_FILES, ((ArrayList) metadata.get(JAR_FILES)).clone());
        }
        if (data.containsKey(MANAGED_CLASSES)) {
            data.put(MANAGED_CLASSES, ((ArrayList) metadata.get(MANAGED_CLASSES)).clone());
        }
        if (data.containsKey(PROPERTIES)) {
            data.put(PROPERTIES, ((Properties) metadata.get(PROPERTIES)).clone());
        }

        return data;
    }

    public String getUnitName() {
        String result = (String) metadata.get(UNIT_NAME);
        return result == null ? "" : result;
    }

    /**
     * @param provider
     */
    public void setProviderClassName(String provider) {
        metadata.put(PROVIDER_CLASSNAME, provider);
    }

    /**
     * @param jtaDataSource
     */
    public void setJtaDataSource(String jtaDataSource) {
        metadata.put(JTA_DATASOURCE, jtaDataSource);
    }

    public String getJtaDataSource() {
        String result = (String) metadata.get(JTA_DATASOURCE);
        return result == null ? "" : result;
    }

    /**
     * @param nonJtaDataSource
     */
    public void setNonJtaDataSource(String nonJtaDataSource) {
        metadata.put(NON_JTA_DATASOURCE, nonJtaDataSource);
    }

    public String getNonJtaDataSource() {
        String result = (String) metadata.get(NON_JTA_DATASOURCE);
        return result == null ? "" : result;
    }

    /**
     * @param mappingFileName
     */
    public void addMappingFileName(String mappingFileName) {
        List<String> files = (List<String>) metadata.get(MAPPING_FILES);
        if (files == null) {
            files = new ArrayList<String>();
            metadata.put(MAPPING_FILES, files);
        }
        files.add(mappingFileName);
    }

    /**
     * @param jarFile
     */
    public void addJarFileName(String jarFile) {
        List<String> jars = (List<String>) metadata.get(JAR_FILES);
        if (jars == null) {
            jars = new ArrayList<String>();
            metadata.put(JAR_FILES, jars);
        }

        jars.add(jarFile);
    }

    /**
     * @param className
     */
    public void addClassName(String className) {
        List<String> classes = (List<String>) metadata.get(MANAGED_CLASSES);
        if (classes == null) {
            classes = new ArrayList<String>();
            metadata.put(MANAGED_CLASSES, classes);
        }
        classes.add(className);
    }

    /**
     * @param exclude
     */
    public void setExcludeUnlisted(boolean exclude) {
        metadata.put(EXCLUDE_UNLISTED_CLASSES, exclude);
    }

    /**
     * @param name
     * @param value
     */
    public void addProperty(String name, String value) {
        Properties props = (Properties) metadata.get(PROPERTIES);
        if (props == null) {
            props = new Properties();
            metadata.put(PROPERTIES, props);
        }
        props.setProperty(name, value);
    }

 

    /**
     * @param sharedCacheMode
     */
    public void setSharedCacheMode(String sharedCacheMode) {
        metadata.put(SHARED_CACHE_MODE, sharedCacheMode);
    }

    /**
     * @param validationMode
     */
    public void setValidationMode(String validationMode) {
        metadata.put(VALIDATION_MODE, validationMode);
    }

    @Override
    public String toString() {
        return "Persistence unit " + metadata.get(UNIT_NAME) + " in bundle ";
//                + bundle.getSymbolicName() + "_" + bundle.getVersion();
    }
}
