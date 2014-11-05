/* 
 * Copyright (C) 2014 erbjuder.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.erbjuder.logger.server.facade.interfaces;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Stefan Andersson
 * @param <T>
 */
public interface AbstractFacade<T> extends Serializable {

    public int count();

  
    public void create(T entity);

    public void edit(T entity);

    public T find(Object id);

    public List<T> findAll();

    public List<T> findExpired(Date toDate);

    public List<T> findRange(int[] range);

  
    public List<T> findRangeFlow(int[] range, String applicationName, String flowName, String flowPointName, String transactionReferenceId, long fromDate, long toDate, String freeText, Boolean viewError);

    public EntityManager getEntityManager();

    public void remove(T entity);
}
