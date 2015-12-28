/*
 * Copyright (C) 2015 Stefan Andersson
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
package com.erbjuder.logger.server.soap.services;

/**
 *
 * @author Stefan Andersson
 */
public class PrimaryKeySequence {

    private long startPK;
    private long endPK;
    private int numOfPrimaryKeys;

    public PrimaryKeySequence(long startPK, long endPK) {
        this.startPK = startPK;
        this.endPK = endPK;
    }

    public long getStartPK() {
        return startPK;
    }

    public long getEndPK() {
        return endPK;
    }

    public void setStartPK(long startPK) {
        this.startPK = startPK;
    }

    public void setEndPK(long endPK) {
        this.endPK = endPK;
    }

    public long getNumOfPrimaryKeys() {
        return getEndPK() - getStartPK();
    }

}
