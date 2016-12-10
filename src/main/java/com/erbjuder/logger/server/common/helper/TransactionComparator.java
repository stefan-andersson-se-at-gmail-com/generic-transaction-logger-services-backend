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

import com.generic.global.transactionlogger.Transactions;
import com.generic.global.transactionlogger.Transactions.Transaction;
import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author server-1
 */
public class TransactionComparator implements Serializable, Comparator<Transaction> {

    @Override
    public int compare(Transactions.Transaction first, Transaction second) {
        return compareUTCLocalTimeStamp(first, second);

    }

    private int compareUTCLocalTimeStamp(Transaction first, Transaction second) {
        // 1 first greater than seconds
        // 0 equal
        // -1 first less than second
        long firstTimeMillis = first.getUTCLocalTimeStamp().toGregorianCalendar().getTimeInMillis();
        long secondTimeMillis = second.getUTCLocalTimeStamp().toGregorianCalendar().getTimeInMillis();
        int result = firstTimeMillis < secondTimeMillis ? -1 : firstTimeMillis == secondTimeMillis ? 0 : 1;
        // Iff equal -> compare nanons
        if (result == 0) {
            result = compareUTCLocalTimeStampNanoSeconds(first, second);
        }
        return result;
    }

    private int compareUTCLocalTimeStampNanoSeconds(Transaction first, Transaction second) {

        // Treat not pressent value as equal 
        int result = 0;
        int firstTimeNanos = first.getUTCLocalTimeStampNanoSeconds();
        int secondTimeNanos = second.getUTCLocalTimeStampNanoSeconds();

        result = firstTimeNanos < secondTimeNanos ? -1 : firstTimeNanos == secondTimeNanos ? 0 : 1;
        return result;
    }
}
