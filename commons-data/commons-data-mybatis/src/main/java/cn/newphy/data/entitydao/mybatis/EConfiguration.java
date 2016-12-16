package cn.newphy.data.entitydao.mybatis;

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.InitializingBean;

import cn.newphy.data.entitydao.DialectType;
import cn.newphy.data.entitydao.mybatis.builder.MybatisSqlBuilder;
import cn.newphy.data.entitydao.mybatis.builder.MysqlSqlBuilder;

public class EConfiguration implements InitializingBean {
	
	private SqlSessionFactory sqlSessionFactory;
	
	private MybatisSqlBuilder dialect;

	
	public MybatisSqlBuilder getDialect() {
		return dialect;
	}
	
	
	public DialectType getDialectType() {
		return dialect.getType();
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
						dialect = new MysqlSqlBuilder();
					}
				}
			}
		} finally {
			if(conn != null) {
				conn.close();
			}
		}
	}
	
	
	
}
