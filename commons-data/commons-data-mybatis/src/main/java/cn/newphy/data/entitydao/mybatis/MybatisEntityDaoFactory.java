package cn.newphy.data.entitydao.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.InitializingBean;

import cn.newphy.data.entitydao.EntityDao;
import cn.newphy.data.entitydao.EntityDaoFactory;
import cn.newphy.data.entitydao.mybatis.plugins.paginator.PagePlugin;

public class MybatisEntityDaoFactory extends EntityDaoFactory implements InitializingBean {

	private SqlSessionFactory sqlSessionFactory;
	private GlobalConfig configuration;
	private Map<String, MybatisEntityDao<?>> entityDaoMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> EntityDao<T> createEntityDao(Class<T> entityClass) {
		if (entityClass == null) {
			return null;
		}
		String name = entityClass.getSimpleName();
		if (!entityDaoMap.containsKey(name)) {
			synchronized (this) {
				if (!entityDaoMap.containsKey(name)) {
					entityDaoMap.put(name, new MybatisEntityDao<T>(configuration, sqlSessionFactory, entityClass));
				}
			}
		}
		return (EntityDao<T>) entityDaoMap.get(name);
	}
	

	@Override
	public void afterPropertiesSet() throws Exception {
		Configuration configuration = sqlSessionFactory.getConfiguration();
		configuration.addInterceptor(new PagePlugin(this.configuration));
	}




	/**
	 * @return the sqlSessionFactory
	 */
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	/**
	 * @param sqlSessionFactory
	 *            the sqlSessionFactory to set
	 */
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	/**
	 * @return the configuration
	 */
	public GlobalConfig getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration
	 *            the configuration to set
	 */
	public void setConfiguration(GlobalConfig configuration) {
		this.configuration = configuration;
	}

}
