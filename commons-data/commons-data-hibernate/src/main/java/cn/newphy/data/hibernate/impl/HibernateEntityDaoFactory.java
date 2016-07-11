package cn.newphy.data.hibernate.impl;

import org.hibernate.SessionFactory;

import cn.newphy.data.hibernate.EntityDao;
import cn.newphy.data.hibernate.EntityDaoFactory;

public class HibernateEntityDaoFactory extends EntityDaoFactory {

	private SessionFactory sessionFactory;

	@Override
	public <T> EntityDao<T> createEntityDao(Class<T> entityClass) {
		if (sessionFactory == null) {
			throw new IllegalStateException("sessionFactory is null");
		}
		return new HibernateEntityDao<T>(sessionFactory, entityClass);
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
