alter table transactionlogger_dev.LogMessageData add UTCLOCALTIMESTAMP datetime;
alter table transactionlogger_dev.LogMessageData add UTCSERVERTIMESTAMP datetime;

alter table transactionlogger_test.LogMessageData add UTCLOCALTIMESTAMP datetime;
alter table transactionlogger_test.LogMessageData add UTCSERVERTIMESTAMP datetime;

alter table transactionlogger_qa.LogMessageData add UTCLOCALTIMESTAMP datetime;
alter table transactionlogger_qa.LogMessageData add UTCSERVERTIMESTAMP datetime;

alter table transactionlogger_prod.LogMessageData add UTCLOCALTIMESTAMP datetime;
alter table transactionlogger_prod.LogMessageData add UTCSERVERTIMESTAMP datetime;



update transactionlogger_dev.LogMessageData t1
	join transactionlogger_dev.LogMessage t2
	ON t1.LOGMESSAGE_ID = t2.id
set t1.UTCLOCALTIMESTAMP = t2.UTCLOCALTIMESTAMP, t1.UTCSERVERTIMESTAMP = t2.UTCSERVERTIMESTAMP 
where 
	 t1.UTCLOCALTIMESTAMP is null 
	OR 
	 t1.UTCLOCALTIMESTAMP LIKE '0000-00-00 00:00:00'
	or 
	 t1.UTCSERVERTIMESTAMP is null 
	or
	 t1.UTCSERVERTIMESTAMP LIKE '0000-00-00 00:00:00'	
;



update transactionlogger_test.LogMessageData t1
	join transactionlogger_test.LogMessage t2
	ON t1.LOGMESSAGE_ID = t2.id
set t1.UTCLOCALTIMESTAMP = t2.UTCLOCALTIMESTAMP, t1.UTCSERVERTIMESTAMP = t2.UTCSERVERTIMESTAMP 
where 
	 t1.UTCLOCALTIMESTAMP is null 
	OR 
	 t1.UTCLOCALTIMESTAMP LIKE '0000-00-00 00:00:00'
	or 
	 t1.UTCSERVERTIMESTAMP is null 
	or
	 t1.UTCSERVERTIMESTAMP LIKE '0000-00-00 00:00:00'	
;


update transactionlogger_qa.LogMessageData t1
	join transactionlogger_qa.LogMessage t2
	ON t1.LOGMESSAGE_ID = t2.id
set t1.UTCLOCALTIMESTAMP = t2.UTCLOCALTIMESTAMP, t1.UTCSERVERTIMESTAMP = t2.UTCSERVERTIMESTAMP 
where 
	 t1.UTCLOCALTIMESTAMP is null 
	OR 
	 t1.UTCLOCALTIMESTAMP LIKE '0000-00-00 00:00:00'
	or 
	 t1.UTCSERVERTIMESTAMP is null 
	or
	 t1.UTCSERVERTIMESTAMP LIKE '0000-00-00 00:00:00'	
;


update transactionlogger_prod.LogMessageData t1
	join transactionlogger_prod.LogMessage t2
	ON t1.LOGMESSAGE_ID = t2.id
set t1.UTCLOCALTIMESTAMP = t2.UTCLOCALTIMESTAMP, t1.UTCSERVERTIMESTAMP = t2.UTCSERVERTIMESTAMP 
where 
	 t1.UTCLOCALTIMESTAMP is null 
	OR 
	 t1.UTCLOCALTIMESTAMP LIKE '0000-00-00 00:00:00'
	or 
	 t1.UTCSERVERTIMESTAMP is null 
	or
	 t1.UTCSERVERTIMESTAMP LIKE '0000-00-00 00:00:00'	
;


alter table transactionlogger_dev.LogMessageData_Max20B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max40B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max60B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max80B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max100B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max150B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max200B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max255B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max64KB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max1MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max2MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max3MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max4MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max5MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max10MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max16MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_dev.LogMessageData_Max4GB modify CONTENTSIZE bigint(20) ;


alter table transactionlogger_test.LogMessageData_Max20B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max40B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max60B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max80B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max100B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max150B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max200B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max255B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max64KB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max1MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max2MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max3MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max4MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max5MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max10MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max16MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_test.LogMessageData_Max4GB modify CONTENTSIZE bigint(20) ;

alter table transactionlogger_qa.LogMessageData_Max20B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max40B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max60B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max80B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max100B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max150B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max200B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max255B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max64KB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max1MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max2MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max3MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max4MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max5MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max10MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max16MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_qa.LogMessageData_Max4GB modify CONTENTSIZE bigint(20) ;

alter table transactionlogger_prod.LogMessageData_Max20B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max40B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max60B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max80B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max100B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max150B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max200B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max255B modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max64KB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max1MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max2MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max3MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max4MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max5MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max10MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max16MB modify CONTENTSIZE bigint(20) ;
alter table transactionlogger_prod.LogMessageData_Max4GB modify CONTENTSIZE bigint(20) ;









rename table transactionlogger_dev.LogMessageData_Max20B to transactionlogger_dev.LogMessageData_Partition_01;
rename table transactionlogger_dev.LogMessageData_Max40B to transactionlogger_dev.LogMessageData_Partition_02;
rename table transactionlogger_dev.LogMessageData_Max60B to transactionlogger_dev.LogMessageData_Partition_03;
rename table transactionlogger_dev.LogMessageData_Max80B to transactionlogger_dev.LogMessageData_Partition_04;
rename table transactionlogger_dev.LogMessageData_Max100B to transactionlogger_dev.LogMessageData_Partition_05;
rename table transactionlogger_dev.LogMessageData_Max150B to transactionlogger_dev.LogMessageData_Partition_06;
rename table transactionlogger_dev.LogMessageData_Max200B to transactionlogger_dev.LogMessageData_Partition_07;
rename table transactionlogger_dev.LogMessageData_Max255B to transactionlogger_dev.LogMessageData_Partition_08;
rename table transactionlogger_dev.LogMessageData_Max64KB to transactionlogger_dev.LogMessageData_Partition_09;
rename table transactionlogger_dev.LogMessageData_Max1MB to transactionlogger_dev.LogMessageData_Partition_10;
rename table transactionlogger_dev.LogMessageData_Max2MB to transactionlogger_dev.LogMessageData_Partition_11;
rename table transactionlogger_dev.LogMessageData_Max3MB to transactionlogger_dev.LogMessageData_Partition_12;
rename table transactionlogger_dev.LogMessageData_Max4MB to transactionlogger_dev.LogMessageData_Partition_13;
rename table transactionlogger_dev.LogMessageData_Max5MB to transactionlogger_dev.LogMessageData_Partition_14;
rename table transactionlogger_dev.LogMessageData_Max10MB to transactionlogger_dev.LogMessageData_Partition_15;
rename table transactionlogger_dev.LogMessageData_Max16MB to transactionlogger_dev.LogMessageData_Partition_16;
rename table transactionlogger_dev.LogMessageData_Max4GB to transactionlogger_dev.LogMessageData_Partition_17;



rename table transactionlogger_test.LogMessageData_Max20B to transactionlogger_test.LogMessageData_Partition_01;
rename table transactionlogger_test.LogMessageData_Max40B to transactionlogger_test.LogMessageData_Partition_02;
rename table transactionlogger_test.LogMessageData_Max60B to transactionlogger_test.LogMessageData_Partition_03;
rename table transactionlogger_test.LogMessageData_Max80B to transactionlogger_test.LogMessageData_Partition_04;
rename table transactionlogger_test.LogMessageData_Max100B to transactionlogger_test.LogMessageData_Partition_05;
rename table transactionlogger_test.LogMessageData_Max150B to transactionlogger_test.LogMessageData_Partition_06;
rename table transactionlogger_test.LogMessageData_Max200B to transactionlogger_test.LogMessageData_Partition_07;
rename table transactionlogger_test.LogMessageData_Max255B to transactionlogger_test.LogMessageData_Partition_08;
rename table transactionlogger_test.LogMessageData_Max64KB to transactionlogger_test.LogMessageData_Partition_09;
rename table transactionlogger_test.LogMessageData_Max1MB to transactionlogger_test.LogMessageData_Partition_10;
rename table transactionlogger_test.LogMessageData_Max2MB to transactionlogger_test.LogMessageData_Partition_11;
rename table transactionlogger_test.LogMessageData_Max3MB to transactionlogger_test.LogMessageData_Partition_12;
rename table transactionlogger_test.LogMessageData_Max4MB to transactionlogger_test.LogMessageData_Partition_13;
rename table transactionlogger_test.LogMessageData_Max5MB to transactionlogger_test.LogMessageData_Partition_14;
rename table transactionlogger_test.LogMessageData_Max10MB to transactionlogger_test.LogMessageData_Partition_15;
rename table transactionlogger_test.LogMessageData_Max16MB to transactionlogger_test.LogMessageData_Partition_16;
rename table transactionlogger_test.LogMessageData_Max4GB to transactionlogger_test.LogMessageData_Partition_17;



rename table transactionlogger_qa.LogMessageData_Max20B to transactionlogger_qa.LogMessageData_Partition_01;
rename table transactionlogger_qa.LogMessageData_Max40B to transactionlogger_qa.LogMessageData_Partition_02;
rename table transactionlogger_qa.LogMessageData_Max60B to transactionlogger_qa.LogMessageData_Partition_03;
rename table transactionlogger_qa.LogMessageData_Max80B to transactionlogger_qa.LogMessageData_Partition_04;
rename table transactionlogger_qa.LogMessageData_Max100B to transactionlogger_qa.LogMessageData_Partition_05;
rename table transactionlogger_qa.LogMessageData_Max150B to transactionlogger_qa.LogMessageData_Partition_06;
rename table transactionlogger_qa.LogMessageData_Max200B to transactionlogger_qa.LogMessageData_Partition_07;
rename table transactionlogger_qa.LogMessageData_Max255B to transactionlogger_qa.LogMessageData_Partition_08;
rename table transactionlogger_qa.LogMessageData_Max64KB to transactionlogger_qa.LogMessageData_Partition_09;
rename table transactionlogger_qa.LogMessageData_Max1MB to transactionlogger_qa.LogMessageData_Partition_10;
rename table transactionlogger_qa.LogMessageData_Max2MB to transactionlogger_qa.LogMessageData_Partition_11;
rename table transactionlogger_qa.LogMessageData_Max3MB to transactionlogger_qa.LogMessageData_Partition_12;
rename table transactionlogger_qa.LogMessageData_Max4MB to transactionlogger_qa.LogMessageData_Partition_13;
rename table transactionlogger_qa.LogMessageData_Max5MB to transactionlogger_qa.LogMessageData_Partition_14;
rename table transactionlogger_qa.LogMessageData_Max10MB to transactionlogger_qa.LogMessageData_Partition_15;
rename table transactionlogger_qa.LogMessageData_Max16MB to transactionlogger_qa.LogMessageData_Partition_16;
rename table transactionlogger_qa.LogMessageData_Max4GB to transactionlogger_qa.LogMessageData_Partition_17;



rename table transactionlogger_prod.LogMessageData_Max20B to transactionlogger_prod.LogMessageData_Partition_01;
rename table transactionlogger_prod.LogMessageData_Max40B to transactionlogger_prod.LogMessageData_Partition_02;
rename table transactionlogger_prod.LogMessageData_Max60B to transactionlogger_prod.LogMessageData_Partition_03;
rename table transactionlogger_prod.LogMessageData_Max80B to transactionlogger_prod.LogMessageData_Partition_04;
rename table transactionlogger_prod.LogMessageData_Max100B to transactionlogger_prod.LogMessageData_Partition_05;
rename table transactionlogger_prod.LogMessageData_Max150B to transactionlogger_prod.LogMessageData_Partition_06;
rename table transactionlogger_prod.LogMessageData_Max200B to transactionlogger_prod.LogMessageData_Partition_07;
rename table transactionlogger_prod.LogMessageData_Max255B to transactionlogger_prod.LogMessageData_Partition_08;
rename table transactionlogger_prod.LogMessageData_Max64KB to transactionlogger_prod.LogMessageData_Partition_09;
rename table transactionlogger_prod.LogMessageData_Max1MB to transactionlogger_prod.LogMessageData_Partition_10;
rename table transactionlogger_prod.LogMessageData_Max2MB to transactionlogger_prod.LogMessageData_Partition_11;
rename table transactionlogger_prod.LogMessageData_Max3MB to transactionlogger_prod.LogMessageData_Partition_12;
rename table transactionlogger_prod.LogMessageData_Max4MB to transactionlogger_prod.LogMessageData_Partition_13;
rename table transactionlogger_prod.LogMessageData_Max5MB to transactionlogger_prod.LogMessageData_Partition_14;
rename table transactionlogger_prod.LogMessageData_Max10MB to transactionlogger_prod.LogMessageData_Partition_15;
rename table transactionlogger_prod.LogMessageData_Max16MB to transactionlogger_prod.LogMessageData_Partition_16;
rename table transactionlogger_prod.LogMessageData_Max4GB to transactionlogger_prod.LogMessageData_Partition_17;
