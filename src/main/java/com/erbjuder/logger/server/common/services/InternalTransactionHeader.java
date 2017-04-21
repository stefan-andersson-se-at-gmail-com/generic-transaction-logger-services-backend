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
import com.erbjuder.logger.server.common.helper.DatabasePartitionHelper;
import com.erbjuder.logger.server.common.helper.TimeStampUtils;
import com.generic.global.transactionlogger.Transactions;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author Stefan Andersson
 */
public class InternalTransactionHeader implements Serializable {

    private Long primaryKey = null;
    private Boolean isError = null;
    private String flowName = null;
    private String flowPointName = null;
    private String applicationName = null;
    private Timestamp utcServerTimestamp = null;
    private Timestamp utcClientTimestamp = null;
    private String transactionReferenceID = null;
    private java.sql.Date expiredDate = null;
    private Integer partitionId = null;
    private Long payloadSize = 0L;

    private ArrayList<InternalTransactionLogData> internalTransactionLogData = new ArrayList();
    private Long L0;

    public InternalTransactionHeader(Long primaryKey, Transactions.Transaction transactionHeader) {
        // Mandatory fields
        this.primaryKey = primaryKey;
        this.isError = transactionHeader.isIsError();
        this.transactionReferenceID = transactionHeader.getTransactionReferenceID();
        this.applicationName = transactionHeader.getApplicationName();
        this.utcServerTimestamp = TimeStampUtils.createSystemNanoTimeStamp();
        this.utcClientTimestamp = getUTCClientTimestamp(transactionHeader);

        // Optional fields
        this.flowName = null;
        this.flowPointName = null;
        this.expiredDate = new java.sql.Date(this.calculateExpiredDate(transactionHeader).getTime());
        if (transactionHeader.getTransactionLogPointInfo() != null
                && transactionHeader.getTransactionLogPointInfo().getFlowName() != null
                && transactionHeader.getTransactionLogPointInfo().getFlowPointName() != null
                && (!transactionHeader.getTransactionLogPointInfo().getFlowName().trim().isEmpty()
                || !transactionHeader.getTransactionLogPointInfo().getFlowName().trim().isEmpty())) {
            flowName = transactionHeader.getTransactionLogPointInfo().getFlowName().trim();
            flowPointName = transactionHeader.getTransactionLogPointInfo().getFlowPointName().trim();
        }

        this.partitionId = DatabasePartitionHelper.calculatePartitionId(utcServerTimestamp);
    }

    public Long getPrimaryKey() {
        return primaryKey;
    }

    public Boolean getIsError() {
        return isError;
    }

    public String getFlowName() {
        return flowName;
    }

    public String getFlowPointName() {
        return flowPointName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Timestamp getUtcServerTimestamp() {
        return utcServerTimestamp;
    }

    public Timestamp getUtcClientTimestamp() {
        return utcClientTimestamp;
    }

    public String getTransactionReferenceID() {
        return transactionReferenceID;
    }

    public java.sql.Date getExpiredDate() {
        return expiredDate;
    }

    public int getPartitionId() {
        return partitionId;
    }

    public ArrayList<InternalTransactionLogData> getInternalTransactionLogData() {
        return internalTransactionLogData;
    }

    public Long getPayloadSize() {
        return payloadSize;
    }

    public void setPayloadSize(Long payloadSize) {
        this.payloadSize = payloadSize;
    }

    public void incPayloadSize(Long payloadSize) {
        this.payloadSize = this.payloadSize + payloadSize;
    }

    private Date calculateExpiredDate(Transactions.Transaction transaction) {

        // Optional
        try {

            Date expiredTime = transaction.getExpiryDate().toGregorianCalendar().getTime();
            if (expiredTime != null && Long.MAX_VALUE > expiredTime.getTime() && Long.MIN_VALUE < expiredTime.getTime()) {
                return expiredTime;
            } else {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.add(Calendar.MONTH, LogMessageServiceBase.addNumberOfMonth);
                return calendar.getTime();
            }
        } catch (Exception invalidExiredDateException) {
            // logger.log(Level.INFO, "[ Invalid ExpiryDate! Use default expired time instead! ] ");
            // logger.log(Level.INFO, invalidExiredDateException.getMessage());
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.add(Calendar.MONTH, LogMessageServiceBase.addNumberOfMonth);
            return calendar.getTime();
        }
    }

    private Timestamp getUTCClientTimestamp(Transactions.Transaction transaction) {

        try {

            long UTCLocalTimeStamp = transaction.getUTCLocalTimeStamp().toGregorianCalendar().getTimeInMillis();
            long UTCLocalTimeStampNanoSeconds = 0;
            try {
                UTCLocalTimeStampNanoSeconds = transaction.getUTCLocalTimeStampNanoSeconds();

            } catch (Exception UTCLocalTimeStampNanoSecondsNotPressent) {
                //Skip client nano
                Timestamp timestamp = new Timestamp(UTCLocalTimeStamp);
                UTCLocalTimeStampNanoSeconds = timestamp.getNanos();
            }

            if (Long.MAX_VALUE > UTCLocalTimeStamp && Long.MIN_VALUE < UTCLocalTimeStamp) {
                return TimeStampUtils.createNanoTimeStamp(UTCLocalTimeStamp, UTCLocalTimeStampNanoSeconds);

            } else {
                return TimeStampUtils.createSystemNanoTimeStamp();
            }

        } catch (Exception invalidDateException) {
            return TimeStampUtils.createSystemNanoTimeStamp();
        }
    }

}
