package cn.newphy.commons.hibernate;

public enum EntityDaoType {

	SYSTEM, 			// 系统指定，缺省
	JDBC,				// JDBC Dao 
	HIBERNATE,  	// Hibernate Dao
	IBATIS, 				// Ibatis Dao
	JPA					// Jpa Dao
}
