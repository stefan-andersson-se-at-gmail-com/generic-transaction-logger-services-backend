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
package com.erbjuder.logger.server.common.services;

/**
 *
 * @author Stefan Andersson
 */
public class Tuple {

    private String label;
    private String search;
    public Tuple(String label, String search) {
        this.label = label;
        this.search = search;

    }

    public String getLabel() {
        return label;
    }

    public String getSearch() {
        return search;
    }

}
