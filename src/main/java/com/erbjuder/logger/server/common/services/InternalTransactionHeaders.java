package com.erbjuder.logger.server.common.services;

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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

 
/**
 *
 * @author Stefan Andersson
 */
public class InternalTransactionHeaders implements Serializable {

    private ArrayList<InternalTransactionHeader> internalTransactionHeaders = new ArrayList();

    public InternalTransactionHeaders() {
    }

    public ArrayList<InternalTransactionHeader> getInternalTransactionHeaders() {
        return internalTransactionHeaders;
    }

    public void setInternalTransactionHeaders(ArrayList<InternalTransactionHeader> internalTransactionHeaders) {
        this.internalTransactionHeaders = internalTransactionHeaders;
    }

    public void addInternalTransactionHeader(InternalTransactionHeader internalTransactionHeader) {
        this.internalTransactionHeaders.add(internalTransactionHeader);
    }
    
    public void addAllInternalTransactionHeaders(Collection<InternalTransactionHeader> internalTransactionHeaders) {
        this.internalTransactionHeaders.addAll(internalTransactionHeaders);
    }

}
