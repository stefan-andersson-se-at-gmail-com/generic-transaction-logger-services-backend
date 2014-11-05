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
