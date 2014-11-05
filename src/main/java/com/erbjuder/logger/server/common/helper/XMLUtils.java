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

import com.erbjuder.logger.server.exception.InvalidXML;
import com.sun.xml.messaging.saaj.util.Base64;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Stefan Andersson
 */
public class XMLUtils {

    
    public static String XMLBase64Decoder(String content) {
        String xml = content;
        try {
            xml = new String(Base64.base64Decode(xml));
            return XMLUtils.cleanInvalidXmlChars(xml);
        } catch (Exception e) {
            xml = content;
        }

        return xml;
    }
    
    /**
     * This method ensures that the output String has only valid XML unicode
     * characters as specified by the XML 1.0 standard. For reference, please
     * see
     * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
     * standard</a>. This method will return an empty String if the input is
     * null or empty.
     *
     * @param in The String whose non-valid characters we want to remove.
     * @return The in String, stripped of non-valid characters.
     */
    public static String cleanInvalidXmlChars(String text) {

        if (null == text || text.isEmpty()) {
            return text;
        }

        final int len = text.length();
        char current = 0;
        int codePoint = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            current = text.charAt(i);
            boolean surrogate = false;
            if (Character.isHighSurrogate(current)
                    && i + 1 < len && Character.isLowSurrogate(text.charAt(i + 1))) {
                surrogate = true;
                codePoint = text.codePointAt(i++);
            } else {
                codePoint = current;
            }
            if ((codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
                    || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                    || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                    || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF))) {
                sb.append(current);

                if (surrogate) {
                    sb.append(text.charAt(i));
                }
            } else {

                // 
                // Invalid Char at index transformed into hex 
                //System.err.println("Index=["+ i +"] Char=["+ String.format("%04x", (int)text.charAt(i)) +"] CodePoint=[" + codePoint + "]");
                //sb.append("hex"+String.format("%04x", (int)text.charAt(i)));
            }
        }

        return sb.toString();
    }

    public static String XmlFormatter(String xmlString) throws InvalidXML {
        String result;

        try {

            final InputSource src = new InputSource(new StringReader(xmlString.trim()));
            final Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
            final Boolean keepDeclaration = Boolean.valueOf(xmlString.startsWith("<?xml"));

            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            final LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); // Set this to true if the output needs to be beautified.
            writer.getDomConfig().setParameter("xml-declaration", keepDeclaration); // Set this to true if the declaration is needed to be outputted.

            result = writer.writeToString(document);

        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage());
            throw new InvalidXML(e.getMessage());
        } catch (ClassCastException e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage());
            throw new InvalidXML(e.getMessage());
        } catch (ClassNotFoundException e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage());
            throw new InvalidXML(e.getMessage());
        } catch (IllegalAccessException e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage());
            throw new InvalidXML(e.getMessage());
        } catch (InstantiationException e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage());
            throw new InvalidXML(e.getMessage());
        } catch (ParserConfigurationException e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage());
            throw new InvalidXML(e.getMessage());
        } catch (DOMException e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage());
            throw new InvalidXML(e.getMessage());
        } catch (LSException e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage());
            throw new InvalidXML(e.getMessage());
        } catch (SAXException e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage());
            throw new InvalidXML(e.getMessage());
        }

        return result;
    }
}
