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

import com.erbjuder.logger.server.common.helper.DataBase;
import com.erbjuder.logger.server.common.helper.DatabasePartitionHelper;
import com.erbjuder.logger.server.common.helper.TimeStampUtils;
import com.generic.global.transactionlogger.Transactions;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Base64;
import org.apache.commons.lang3.StringEscapeUtils;


/**
 *
 * @author Stefan Andersson
 */
public class InternalTransactionLogData implements Serializable {

    private final Long primaryKey;
    private final Long foreignKey;
    private String label;
    private String mimeType;
    private String content;
    private Long contentSize;
    private Timestamp utcServerTimestamp;
    private final Timestamp utcClientTimestamp;
    private Integer partitionId;
    private final Boolean modified;
    private final Boolean searchable;
    private final java.sql.Date expiredDate;
    private String logMessageDataPartitionNameFromContentSize;

    public InternalTransactionLogData(
            Long primaryKey,
            Long foreignKey,
            Timestamp utcClientTimestamp,
            Boolean modified,
            Boolean searchable,
            Transactions.Transaction.TransactionLogData transactionLogData,
            java.sql.Date expiredDate) {
        this.primaryKey = primaryKey;
        this.foreignKey = foreignKey;
        this.expiredDate = expiredDate;
        this.utcClientTimestamp = utcClientTimestamp;
        this.modified = modified;
        this.searchable = searchable;
        this.processTransactionLogData(transactionLogData);
    }

    public long getPrimaryKey() {
        return primaryKey;
    }

    public long getForeignKey() {
        return foreignKey;
    }

    public String getContentLabel() {
        return label;
    }

    public String getContentMimeType() {
        return mimeType;
    }

    public String getContent() {
        return content;
    }

    public long getContentSize() {
        return contentSize;
    }

    public java.sql.Date getExpiredDate() {
        return expiredDate;
    }

    public Timestamp getUtcServerTimestamp() {
        return utcServerTimestamp;
    }

    public Timestamp getUtcClientTimestamp() {
        return utcClientTimestamp;
    }

    public int getPartitionId() {
        return partitionId;
    }

    public String getDatabaseName() {
        return this.logMessageDataPartitionNameFromContentSize;
    }

    public Boolean getModified() {
        return modified;
    }

    public Boolean getSearchable() {
        return searchable;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("primaryKey = [ ").append(primaryKey).append(" ] ");
        builder.append("foreignKey = [ ").append(foreignKey).append(" ] ");
        builder.append("label = [ ").append(label).append(" ] ");
        builder.append("mimeType = [ ").append(mimeType).append(" ] ");
        builder.append("contentSize = [ ").append(contentSize).append(" ] ");
        builder.append("utcServerTimestamp = [ ").append(utcServerTimestamp).append(" ] ");
        builder.append("utcClientTimestamp = [ ").append(utcClientTimestamp).append(" ] ");
        builder.append("partitionId = [ ").append(partitionId).append(" ] ");
        builder.append("expiredDate = [ ").append(expiredDate).append(" ] ");
        builder.append("logMessageDataPartitionNameFromContentSize = [ ").append(logMessageDataPartitionNameFromContentSize).append(" ] ");
        builder.append("content = [ ").append(content).append(" ] ");
        return builder.toString();

    }

    private void processTransactionLogData(Transactions.Transaction.TransactionLogData transactionLogData) {

        this.utcServerTimestamp = TimeStampUtils.createSystemNanoTimeStamp();
        this.partitionId = DatabasePartitionHelper.calculatePartitionId(this.utcServerTimestamp);

        this.label = StringEscapeUtils.unescapeXml(transactionLogData.getContentLabel().trim());
        this.mimeType = transactionLogData.getContentMimeType().trim();
        this.content = transactionLogData.getContent();
        this.contentSize = 0L;

        // 
        // Decode all messages
        try {
            content = new String(Base64.getDecoder().decode(content.getBytes()));
            contentSize = new Long(content.getBytes().length);
        } catch (Exception e) {

            content = StringEscapeUtils.unescapeXml(content);
            contentSize = new Long(content.getBytes().length);
        }

        this.logMessageDataPartitionNameFromContentSize = DataBase.logMessageDataPartitionNameFromContentSize(contentSize);
    }

}
