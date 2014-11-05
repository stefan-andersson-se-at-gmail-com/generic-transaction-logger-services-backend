/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erbjuder.logger.server.common.helper;

import java.io.Serializable;
import java.io.StringWriter;

/**
 *
 * @author Stefan Andersson
 */
public class JSONPrettyPrintWriter extends StringWriter implements Serializable{

    private int indent = 0;

    @Override
    public void write(int c) {
        if (((char) c) == '[' || ((char) c) == '{') {
            super.write(c);
            super.write('\n');
            indent++;
            writeIndentation();
        } else if (((char) c) == ',') {
            super.write(c);
            super.write('\n');
            writeIndentation();
        } else if (((char) c) == ']' || ((char) c) == '}') {
            super.write('\n');
            indent--;
            writeIndentation();
            super.write(c);
        } else {
            super.write(c);
        }

    }

    private void writeIndentation() {
        for (int i = 0; i < indent; i++) {
            super.write("   ");
        }
    }
}
