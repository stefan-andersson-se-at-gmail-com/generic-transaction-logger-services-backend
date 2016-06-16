/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sandvik.logger.server.soap.services;

import com.erbjuder.logger.server.common.helper.MimeTypes;
import com.generic.global.transactionlogger.Transactions;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

public class LogMessageServiceWrapper {

    public static com.generic.global.transactionlogger.Transactions translate(com.sandvik.global.transactionlogger.Transactions oldTransactions) throws WebServiceException {
        com.generic.global.transactionlogger.Transactions newTransactions = null;
        try {

            newTransactions = LogMessageServiceWrapper.transformTransactions(oldTransactions);

        } catch (Exception e) {
            Logger.getLogger(LogMessageServiceWrapper.class.getName()).log(Level.SEVERE, e.getMessage());
        }

        return newTransactions;
    }

    private static com.generic.global.transactionlogger.Transactions transformTransactions(com.sandvik.global.transactionlogger.Transactions oldTransactions) {
        com.generic.global.transactionlogger.Transactions newTransactions = new com.generic.global.transactionlogger.Transactions();

        for (com.sandvik.global.transactionlogger.Transactions.Transaction oldTransaction : oldTransactions.getTransaction()) {
            if (oldTransaction != null) {
                newTransactions.getTransaction().add(LogMessageServiceWrapper.transformTransaction(oldTransaction));
            }
        }

        return newTransactions;
    }

    private static com.generic.global.transactionlogger.Transactions.Transaction transformTransaction(com.sandvik.global.transactionlogger.Transactions.Transaction oldTransaction) {
        com.generic.global.transactionlogger.Transactions.Transaction newTransaction = new com.generic.global.transactionlogger.Transactions.Transaction();

        newTransaction.setIsError(oldTransaction.isIsError());

        if (oldTransaction.getApplicationName() != null) {
            newTransaction.setApplicationName(oldTransaction.getApplicationName());
        }
        if (oldTransaction.getExpiryDate() != null) {
            newTransaction.setExpiryDate(oldTransaction.getExpiryDate());
        }
        if (oldTransaction.getTransactionReferenceID() != null) {
            newTransaction.setTransactionReferenceID(oldTransaction.getTransactionReferenceID());
        }
        if (oldTransaction.getUTCLocalTimeStamp() != null) {
            newTransaction.setUTCLocalTimeStamp(oldTransaction.getUTCLocalTimeStamp());
        }
        if (oldTransaction.getUTCLocalTimeStampNanoSeconds() != null) {
            newTransaction.setUTCLocalTimeStampNanoSeconds(oldTransaction.getUTCLocalTimeStampNanoSeconds().intValue());
        }

        if (oldTransaction.getTransactionLogData() != null) {
            newTransaction.getTransactionLogData().addAll(LogMessageServiceWrapper.transformLogData(oldTransaction.getTransactionLogData()));
        }
        if (oldTransaction.getTransactionMetaInfo() != null) {
            newTransaction.getTransactionLogData().addAll(LogMessageServiceWrapper.transformMetaInfo(oldTransaction.getTransactionMetaInfo()));
        }

        if (oldTransaction.getTransactionLogPointInfo() != null) {
            newTransaction.setTransactionLogPointInfo(LogMessageServiceWrapper.transformLogPointInfo(oldTransaction.getTransactionLogPointInfo()));
        }
        return newTransaction;
    }

    private static List<Transactions.Transaction.TransactionLogData> transformLogData(List<com.sandvik.global.transactionlogger.Transactions.Transaction.TransactionLogData> oldList) {
        List<com.generic.global.transactionlogger.Transactions.Transaction.TransactionLogData> newLogDataList = new ArrayList<com.generic.global.transactionlogger.Transactions.Transaction.TransactionLogData>();

        for (com.sandvik.global.transactionlogger.Transactions.Transaction.TransactionLogData oldLogData : oldList) {

            if (oldLogData != null && oldLogData.getContent() != null && oldLogData.getContentDescription() != null && oldLogData.getContentMimeType() != null) {
                com.generic.global.transactionlogger.Transactions.Transaction.TransactionLogData newLogData = new com.generic.global.transactionlogger.Transactions.Transaction.TransactionLogData();
                 

                if ("application/base64".equalsIgnoreCase(oldLogData.getContentMimeType())) {
                    newLogData.setContentMimeType(MimeTypes.XML);
                    newLogData.setContent(oldLogData.getContent());
                } else {
                    newLogData.setContentMimeType(oldLogData.getContentMimeType());
                    newLogData.setContent(Base64.getEncoder().encodeToString(oldLogData.getContent().getBytes()));
                }
                newLogData.setContentLabel(oldLogData.getContentDescription());

                newLogDataList.add(newLogData);

            }
        }

        return newLogDataList;
    }

    private static List<Transactions.Transaction.TransactionLogData> transformMetaInfo(List<com.sandvik.global.transactionlogger.Transactions.Transaction.TransactionMetaInfo> oldList) {

        List<com.generic.global.transactionlogger.Transactions.Transaction.TransactionLogData> newLogDataList = new ArrayList<com.generic.global.transactionlogger.Transactions.Transaction.TransactionLogData>();

        for (com.sandvik.global.transactionlogger.Transactions.Transaction.TransactionMetaInfo oldMetaInfo : oldList) {

            if (oldMetaInfo != null && oldMetaInfo.getMetaValue() != null && oldMetaInfo.getMetaLabel() != null) {
                com.generic.global.transactionlogger.Transactions.Transaction.TransactionLogData newLogData = new com.generic.global.transactionlogger.Transactions.Transaction.TransactionLogData();

                newLogData.setContentMimeType(MimeTypes.TEXT);
                newLogData.setContent(Base64.getEncoder().encodeToString(oldMetaInfo.getMetaValue().getBytes()));
                newLogData.setContentLabel(oldMetaInfo.getMetaLabel());

                newLogDataList.add(newLogData);
            }

        }
        return newLogDataList;
    }

    private static com.generic.global.transactionlogger.Transactions.Transaction.TransactionLogPointInfo transformLogPointInfo(com.sandvik.global.transactionlogger.Transactions.Transaction.TransactionLogPointInfo oldLogPointInfo) {
        com.generic.global.transactionlogger.Transactions.Transaction.TransactionLogPointInfo newLogPointInfo = null;
        if (oldLogPointInfo.getFlowName() != null && oldLogPointInfo.getFlowPointName() != null) {
            newLogPointInfo = new com.generic.global.transactionlogger.Transactions.Transaction.TransactionLogPointInfo();
            newLogPointInfo.setFlowName(oldLogPointInfo.getFlowName());
            newLogPointInfo.setFlowPointName(oldLogPointInfo.getFlowPointName());
        }
        return newLogPointInfo;

    }
}
