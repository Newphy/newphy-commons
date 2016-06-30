package cn.newphy.commons.hibernate.impl;

import org.hibernate.SessionFactory;

import cn.newphy.commons.hibernate.EntityDao;
import cn.newphy.commons.hibernate.EntityDaoFactory;

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
