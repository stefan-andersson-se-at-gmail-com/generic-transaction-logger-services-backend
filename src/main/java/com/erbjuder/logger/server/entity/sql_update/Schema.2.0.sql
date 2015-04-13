-- MySQL Workbench Forward Engineering
DROP DATABASE `transactionlogger_dev`;
CREATE DATABASE `transactionlogger_dev`;
USE transactionlogger_dev;


SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema transactionlogger
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Table `APPLICATIONFLOWPOINT`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `APPLICATIONFLOWPOINT` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ApplicationFlowConfiguration`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ApplicationFlowConfiguration` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `DESCRIPTION` VARCHAR(255) NULL DEFAULT NULL,
  `LINK` VARCHAR(255) NULL DEFAULT NULL,
  `NAME` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `Node`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Node` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `DESCRIPTION` VARCHAR(255) NULL DEFAULT NULL,
  `NAME` VARCHAR(255) NULL DEFAULT NULL,
  `PARTOFGRAPH` TINYINT(1) NULL DEFAULT '0',
  `ROOTNODE` TINYINT(1) NULL DEFAULT '0',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ApplicationFlowConfiguration_Node`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ApplicationFlowConfiguration_Node` (
  `ApplicationFlowConfiguration_ID` BIGINT(20) NOT NULL,
  `nodes_ID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`ApplicationFlowConfiguration_ID`, `nodes_ID`),
  INDEX `FK_ApplicationFlowConfiguration_Node_nodes_ID` (`nodes_ID` ASC),
  CONSTRAINT `FK_ApplicationFlowConfiguration_Node_nodes_ID`
    FOREIGN KEY (`nodes_ID`)
    REFERENCES `Node` (`ID`),
  CONSTRAINT `pplctnFlwConfigurationNodepplctnFlwConfigurationID`
    FOREIGN KEY (`ApplicationFlowConfiguration_ID`)
    REFERENCES `ApplicationFlowConfiguration` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `Edge`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Edge` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `FROMNODE_ID` BIGINT(20) NULL DEFAULT NULL,
  `TONODE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_Edge_TONODE_ID` (`TONODE_ID` ASC),
  INDEX `FK_Edge_FROMNODE_ID` (`FROMNODE_ID` ASC),
  CONSTRAINT `FK_Edge_FROMNODE_ID`
    FOREIGN KEY (`FROMNODE_ID`)
    REFERENCES `Node` (`ID`),
  CONSTRAINT `FK_Edge_TONODE_ID`
    FOREIGN KEY (`TONODE_ID`)
    REFERENCES `Node` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `EdgeConstraints`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `EdgeConstraints` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `EDGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_EdgeConstraints_EDGE_ID` (`EDGE_ID` ASC),
  CONSTRAINT `FK_EdgeConstraints_EDGE_ID`
    FOREIGN KEY (`EDGE_ID`)
    REFERENCES `Edge` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `GlobalFlowConfiguration`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `GlobalFlowConfiguration` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `DESCRIPTION` VARCHAR(255) NULL DEFAULT NULL,
  `LINK` VARCHAR(255) NULL DEFAULT NULL,
  `NAME` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `GlobalFlowConfiguration_ApplicationFlowConfiguration`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `GlobalFlowConfiguration_ApplicationFlowConfiguration` (
  `GlobalFlowConfiguration_ID` BIGINT(20) NOT NULL,
  `applicationFlowConfigurations_ID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`GlobalFlowConfiguration_ID`, `applicationFlowConfigurations_ID`),
  INDEX `GlblFlwCnfgrtnpplctnFlwCnfgrtionpplctnFlwCnfgrtnsD` (`applicationFlowConfigurations_ID` ASC),
  CONSTRAINT `GlblFlwCnfgrtnpplctnFlwCnfgrtionpplctnFlwCnfgrtnsD`
    FOREIGN KEY (`applicationFlowConfigurations_ID`)
    REFERENCES `ApplicationFlowConfiguration` (`ID`),
  CONSTRAINT `GlblFlwCnfgrtnpplctnFlwCnfigurationGlblFlwCnfgrtnD`
    FOREIGN KEY (`GlobalFlowConfiguration_ID`)
    REFERENCES `GlobalFlowConfiguration` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `Graph`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Graph` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `GLOBALFLOWCONFIGURATION_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_Graph_GLOBALFLOWCONFIGURATION_ID` (`GLOBALFLOWCONFIGURATION_ID` ASC),
  CONSTRAINT `FK_Graph_GLOBALFLOWCONFIGURATION_ID`
    FOREIGN KEY (`GLOBALFLOWCONFIGURATION_ID`)
    REFERENCES `GlobalFlowConfiguration` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `Graph_Edge`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Graph_Edge` (
  `Graph_ID` BIGINT(20) NOT NULL,
  `edges_ID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`Graph_ID`, `edges_ID`),
  INDEX `FK_Graph_Edge_edges_ID` (`edges_ID` ASC),
  CONSTRAINT `FK_Graph_Edge_edges_ID`
    FOREIGN KEY (`edges_ID`)
    REFERENCES `Edge` (`ID`),
  CONSTRAINT `FK_Graph_Edge_Graph_ID`
    FOREIGN KEY (`Graph_ID`)
    REFERENCES `Graph` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `Graph_Node`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Graph_Node` (
  `Graph_ID` BIGINT(20) NOT NULL,
  `nodes_ID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`Graph_ID`, `nodes_ID`),
  INDEX `FK_Graph_Node_nodes_ID` (`nodes_ID` ASC),
  CONSTRAINT `FK_Graph_Node_Graph_ID`
    FOREIGN KEY (`Graph_ID`)
    REFERENCES `Graph` (`ID`),
  CONSTRAINT `FK_Graph_Node_nodes_ID`
    FOREIGN KEY (`nodes_ID`)
    REFERENCES `Node` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessage`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessage` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `APPLICATIONNAME` VARCHAR(255) NULL DEFAULT NULL,
  `EXPIREDDATE` DATE NULL DEFAULT NULL,
  `FLOWNAME` VARCHAR(255) NULL DEFAULT NULL,
  `FLOWPOINTNAME` VARCHAR(255) NULL DEFAULT NULL,
  `ISERROR` TINYINT(1) NULL DEFAULT '0',
  `TRANSACTIONREFERENCEID` VARCHAR(255) NULL DEFAULT NULL,
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_01`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_01` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` VARCHAR(20) NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_01_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_01_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_02`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_02` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` VARCHAR(40) NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_02_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_02_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_03`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_03` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` VARCHAR(60) NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_03_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_03_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_04`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_04` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` VARCHAR(80) NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_04_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_04_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_05`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_05` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` VARCHAR(100) NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_05_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_05_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_06`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_06` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` VARCHAR(150) NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_06_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_06_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_07`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_07` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` VARCHAR(200) NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_07_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_07_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_08`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_08` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` TINYTEXT NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_08_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_08_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_09`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_09` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` TEXT NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_09_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_09_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_10`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_10` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` MEDIUMTEXT NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_10_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_10_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_11`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_11` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` MEDIUMTEXT NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_11_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_11_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_12`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_12` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` MEDIUMTEXT NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_12_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_12_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_13`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_13` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` MEDIUMTEXT NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_13_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_13_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_14`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_14` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` MEDIUMTEXT NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_14_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_14_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_15`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_15` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` MEDIUMTEXT NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_15_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_15_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_16`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_16` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` MEDIUMTEXT NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_16_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_16_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `LogMessageData_Partition_17`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `LogMessageData_Partition_17` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CONTENT` LONGTEXT NULL DEFAULT NULL,
  `CONTENTSIZE` BIGINT(20) NULL DEFAULT NULL,
  `LABEL` VARCHAR(255) NULL DEFAULT NULL,
  `MIMETYPE` VARCHAR(255) NULL DEFAULT NULL,
  `MODIFIED` TINYINT(1) NULL DEFAULT '0',
  `SEARCHABLE` TINYINT(1) NULL DEFAULT '0',
  `UTCLOCALTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `UTCSERVERTIMESTAMP` DATETIME(6) NULL DEFAULT NULL,
  `LOGMESSAGE_ID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_LogMessageData_Partition_17_LOGMESSAGE_ID` (`LOGMESSAGE_ID` ASC),
  CONSTRAINT `FK_LogMessageData_Partition_17_LOGMESSAGE_ID`
    FOREIGN KEY (`LOGMESSAGE_ID`)
    REFERENCES `LogMessage` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PK_ALLOCATE_SIZE_10`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PK_ALLOCATE_SIZE_10` (
  `ENTITY` VARCHAR(50) NOT NULL,
  `SEQ_COUNT` DECIMAL(38,0) NULL DEFAULT NULL,
  PRIMARY KEY (`ENTITY`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PK_ALLOCATE_SIZE_100`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PK_ALLOCATE_SIZE_100` (
  `ENTITY` VARCHAR(50) NOT NULL,
  `SEQ_COUNT` DECIMAL(38,0) NULL DEFAULT NULL,
  PRIMARY KEY (`ENTITY`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PK_ALLOCATE_SIZE_1000`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PK_ALLOCATE_SIZE_1000` (
  `ENTITY` VARCHAR(50) NOT NULL,
  `SEQ_COUNT` DECIMAL(38,0) NULL DEFAULT NULL,
  PRIMARY KEY (`ENTITY`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PK_ALLOCATE_SIZE_2000`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PK_ALLOCATE_SIZE_2000` (
  `ENTITY` VARCHAR(50) NOT NULL,
  `SEQ_COUNT` DECIMAL(38,0) NULL DEFAULT NULL,
  PRIMARY KEY (`ENTITY`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PK_ALLOCATE_SIZE_5`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PK_ALLOCATE_SIZE_5` (
  `ENTITY` VARCHAR(50) NOT NULL,
  `SEQ_COUNT` DECIMAL(38,0) NULL DEFAULT NULL,
  PRIMARY KEY (`ENTITY`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PK_ALLOCATE_SIZE_50`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PK_ALLOCATE_SIZE_50` (
  `ENTITY` VARCHAR(50) NOT NULL,
  `SEQ_COUNT` DECIMAL(38,0) NULL DEFAULT NULL,
  PRIMARY KEY (`ENTITY`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PK_ALLOCATE_SIZE_500`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PK_ALLOCATE_SIZE_500` (
  `ENTITY` VARCHAR(50) NOT NULL,
  `SEQ_COUNT` DECIMAL(38,0) NULL DEFAULT NULL,
  PRIMARY KEY (`ENTITY`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `PK_ALLOCATE_SIZE_5000`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PK_ALLOCATE_SIZE_5000` (
  `ENTITY` VARCHAR(50) NOT NULL,
  `SEQ_COUNT` DECIMAL(38,0) NULL DEFAULT NULL,
  PRIMARY KEY (`ENTITY`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `SEQUENCE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SEQUENCE` (
  `SEQ_NAME` VARCHAR(50) NOT NULL,
  `SEQ_COUNT` DECIMAL(38,0) NULL DEFAULT NULL,
  PRIMARY KEY (`SEQ_NAME`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
