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
package com.erbjuder.logger.server.entity.impl;

import com.erbjuder.logger.server.facade.impl.LogMessageDataFacadeImpl;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.naming.InitialContext;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author Stefan Andersson
 */
@Entity
@Table(name = "LogMessage")
public class LogMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private java.sql.Timestamp utcLocalTimeStamp;
    private java.sql.Timestamp utcServerTimeStamp;
    private String transactionReferenceID;
    private String applicationName;
    private boolean isError;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date expiredDate;
    private String flowName;
    private String flowPointName;

    @OneToMany(targetEntity = LogMessageData_Partition_01.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_01> logMessageData_Partition_01 = new ArrayList<LogMessageData_Partition_01>();

    @OneToMany(targetEntity = LogMessageData_Partition_02.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_02> logMessageData_Partition_02 = new ArrayList<LogMessageData_Partition_02>();

    @OneToMany(targetEntity = LogMessageData_Partition_03.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_03> logMessageData_Partition_03 = new ArrayList<LogMessageData_Partition_03>();

    @OneToMany(targetEntity = LogMessageData_Partition_04.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_04> logMessageData_Partition_04 = new ArrayList<LogMessageData_Partition_04>();

    @OneToMany(targetEntity = LogMessageData_Partition_05.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_05> logMessageData_Partition_05 = new ArrayList<LogMessageData_Partition_05>();

    @OneToMany(targetEntity = LogMessageData_Partition_06.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_06> logMessageData_Partition_06 = new ArrayList<LogMessageData_Partition_06>();

    @OneToMany(targetEntity = LogMessageData_Partition_07.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_07> logMessageData_Partition_07 = new ArrayList<LogMessageData_Partition_07>();

    @OneToMany(targetEntity = LogMessageData_Partition_08.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_08> logMessageData_Partition_08 = new ArrayList<LogMessageData_Partition_08>();

    @OneToMany(targetEntity = LogMessageData_Partition_09.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_09> logMessageData_Partition_09 = new ArrayList<LogMessageData_Partition_09>();

    @OneToMany(targetEntity = LogMessageData_Partition_10.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_10> logMessageData_Partition_10 = new ArrayList<LogMessageData_Partition_10>();

    @OneToMany(targetEntity = LogMessageData_Partition_11.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_11> logMessageData_Partition_11 = new ArrayList<LogMessageData_Partition_11>();

    @OneToMany(targetEntity = LogMessageData_Partition_12.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_12> logMessageData_Partition_12 = new ArrayList<LogMessageData_Partition_12>();

    @OneToMany(targetEntity = LogMessageData_Partition_13.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_13> logMessageData_Partition_13 = new ArrayList<LogMessageData_Partition_13>();

    @OneToMany(targetEntity = LogMessageData_Partition_14.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_14> logMessageData_Partition_14 = new ArrayList<LogMessageData_Partition_14>();

    @OneToMany(targetEntity = LogMessageData_Partition_15.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_15> logMessageData_Partition_15 = new ArrayList<LogMessageData_Partition_15>();

    @OneToMany(targetEntity = LogMessageData_Partition_16.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_16> logMessageData_Partition_16 = new ArrayList<LogMessageData_Partition_16>();

    @OneToMany(targetEntity = LogMessageData_Partition_17.class, mappedBy = "logMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LogMessageData_Partition_17> logMessageData_Partition_17 = new ArrayList<LogMessageData_Partition_17>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getUtcLocalTimeStamp() {
        return utcLocalTimeStamp;
    }

    public void setUtcLocalTimeStamp(Timestamp utcLocalTimeStamp) {
        this.utcLocalTimeStamp = utcLocalTimeStamp;
    }

    public Timestamp getUtcServerTimeStamp() {
        return utcServerTimeStamp;
    }

    public void setUtcServerTimeStamp(Timestamp utcServerTimeStamp) {
        this.utcServerTimeStamp = utcServerTimeStamp;
    }

    public String getTransactionReferenceID() {
        return transactionReferenceID;
    }

    public void setTransactionReferenceID(String transactionReferenceID) {
        this.transactionReferenceID = transactionReferenceID;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public boolean isIsError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = new java.sql.Date(expiredDate.getTime());
    }

    public List<com.erbjuder.logger.server.entity.interfaces.LogMessageData> getLogMessageData(Set<Class> classSelectionList) {
        try {

            LogMessageDataFacadeImpl logMessageDataFacade = (LogMessageDataFacadeImpl) new InitialContext().lookup("java:module/LogMessageDataFacadeImpl");
            return logMessageDataFacade.getLogMessageData(this, classSelectionList);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public List<LogMessageData_Partition_01> getLogMessageData_Partition_01() {
        return logMessageData_Partition_01;
    }

    public void setLogMessageData_Partition_01(List<LogMessageData_Partition_01> logMessageData_Partition_01) {
        this.logMessageData_Partition_01 = logMessageData_Partition_01;
    }

    public List<LogMessageData_Partition_02> getLogMessageData_Partition_02() {
        return logMessageData_Partition_02;
    }

    public void setLogMessageData_Partition_02(List<LogMessageData_Partition_02> logMessageData_Partition_02) {
        this.logMessageData_Partition_02 = logMessageData_Partition_02;
    }

    public List<LogMessageData_Partition_03> getLogMessageData_Partition_03() {
        return logMessageData_Partition_03;
    }

    public void setLogMessageData_Partition_03(List<LogMessageData_Partition_03> logMessageData_Partition_03) {
        this.logMessageData_Partition_03 = logMessageData_Partition_03;
    }

    public List<LogMessageData_Partition_04> getLogMessageData_Partition_04() {
        return logMessageData_Partition_04;
    }

    public void setLogMessageData_Partition_04(List<LogMessageData_Partition_04> logMessageData_Partition_04) {
        this.logMessageData_Partition_04 = logMessageData_Partition_04;
    }

    public List<LogMessageData_Partition_05> getLogMessageData_Partition_05() {
        return logMessageData_Partition_05;
    }

    public void setLogMessageData_Partition_05(List<LogMessageData_Partition_05> logMessageData_Partition_05) {
        this.logMessageData_Partition_05 = logMessageData_Partition_05;
    }

    public List<LogMessageData_Partition_06> getLogMessageData_Partition_06() {
        return logMessageData_Partition_06;
    }

    public void setLogMessageData_Partition_06(List<LogMessageData_Partition_06> logMessageData_Partition_06) {
        this.logMessageData_Partition_06 = logMessageData_Partition_06;
    }

    public List<LogMessageData_Partition_07> getLogMessageData_Partition_07() {
        return logMessageData_Partition_07;
    }

    public void setLogMessageData_Partition_07(List<LogMessageData_Partition_07> logMessageData_Partition_07) {
        this.logMessageData_Partition_07 = logMessageData_Partition_07;
    }

    public List<LogMessageData_Partition_08> getLogMessageData_Partition_08() {
        return logMessageData_Partition_08;
    }

    public void setLogMessageData_Partition_08(List<LogMessageData_Partition_08> logMessageData_Partition_08) {
        this.logMessageData_Partition_08 = logMessageData_Partition_08;
    }

    public List<LogMessageData_Partition_09> getLogMessageData_Partition_09() {
        return logMessageData_Partition_09;
    }

    public void setLogMessageData_Partition_09(List<LogMessageData_Partition_09> logMessageData_Partition_09) {
        this.logMessageData_Partition_09 = logMessageData_Partition_09;
    }

    public List<LogMessageData_Partition_10> getLogMessageData_Partition_10() {
        return logMessageData_Partition_10;
    }

    public void setLogMessageData_Partition_10(ArrayList<LogMessageData_Partition_10> logMessageData_Partition_10) {
        this.logMessageData_Partition_10 = logMessageData_Partition_10;
    }

    public List<LogMessageData_Partition_11> getLogMessageData_Partition_11() {
        return logMessageData_Partition_11;
    }

    public void setLogMessageData_Partition_11(ArrayList<LogMessageData_Partition_11> logMessageData_Partition_11) {
        this.logMessageData_Partition_11 = logMessageData_Partition_11;
    }

    public List<LogMessageData_Partition_12> getLogMessageData_Partition_12() {
        return logMessageData_Partition_12;
    }

    public void setLogMessageData_Partition_12(ArrayList<LogMessageData_Partition_12> logMessageData_Partition_12) {
        this.logMessageData_Partition_12 = logMessageData_Partition_12;
    }

    public List<LogMessageData_Partition_13> getLogMessageData_Partition_13() {
        return logMessageData_Partition_13;
    }

    public void setLogMessageData_Partition_13(ArrayList<LogMessageData_Partition_13> logMessageData_Partition_13) {
        this.logMessageData_Partition_13 = logMessageData_Partition_13;
    }

    public List<LogMessageData_Partition_14> getLogMessageData_Partition_14() {
        return logMessageData_Partition_14;
    }

    public void setLogMessageData_Partition_14(ArrayList<LogMessageData_Partition_14> logMessageData_Partition_14) {
        this.logMessageData_Partition_14 = logMessageData_Partition_14;
    }

    public List<LogMessageData_Partition_15> getLogMessageData_Partition_15() {
        return logMessageData_Partition_15;
    }

    public void setLogMessageData_Partition_15(ArrayList<LogMessageData_Partition_15> logMessageData_Partition_15) {
        this.logMessageData_Partition_15 = logMessageData_Partition_15;
    }

    public List<LogMessageData_Partition_16> getLogMessageData_Partition_16() {
        return logMessageData_Partition_16;
    }

    public void setLogMessageData_Partition_16(List<LogMessageData_Partition_16> logMessageData_Partition_16) {
        this.logMessageData_Partition_16 = logMessageData_Partition_16;
    }

    public List<LogMessageData_Partition_17> getLogMessageData_Partition_17() {
        return logMessageData_Partition_17;
    }

    public void setLogMessageData_Partition_17(List<LogMessageData_Partition_17> logMessageData_Partition_17) {
        this.logMessageData_Partition_17 = logMessageData_Partition_17;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFlowPointName() {
        return flowPointName;
    }

    public void setFlowPointName(String flowPointName) {
        this.flowPointName = flowPointName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LogMessage)) {
            return false;
        }
        LogMessage other = (LogMessage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getCanonicalName() + "@" + this.hashCode();
    }
}
