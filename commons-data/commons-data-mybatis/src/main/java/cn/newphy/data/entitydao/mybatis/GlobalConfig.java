package cn.newphy.data.entitydao.mybatis;

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.InitializingBean;

import cn.newphy.data.entitydao.DialectType;
import cn.newphy.data.entitydao.mybatis.TableNameStrategy.CamelCaseTableNameStrategy;

public class GlobalConfig implements InitializingBean {
	
	private SqlSessionFactory sqlSessionFactory;
	
	private DialectType dialectType;
	
	private TableNameStrategy tableNameStrategy = new CamelCaseTableNameStrategy();
	
	public DialectType getDialectType() {
		return dialectType;
	}


	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}


	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		Environment env = sqlSessionFactory.getConfiguration().getEnvironment();
		DataSource ds = env.getDataSource();
		Connection conn = ds.getConnection();
		try {
			if(conn != null) {
				String driverName = conn.getMetaData().getDriverName();
				if(driverName != null) {
					String sdriver = driverName.toLowerCase();
					if(sdriver.contains("mysql")) {
						dialectType = DialectType.MYSQL;
					}
				}
			}
		} finally {
			if(conn != null) {
				conn.close();
			}
		}
	}


	/**
	 * @return the tableNameStrategy
	 */
	public TableNameStrategy getTableNameStrategy() {
		return tableNameStrategy;
	}


	/**
	 * @param tableNameStrategy the tableNameStrategy to set
	 */
	public void setTableNameStrategy(TableNameStrategy tableNameStrategy) {
		this.tableNameStrategy = tableNameStrategy;
	}
	
	
	
	
	
}
