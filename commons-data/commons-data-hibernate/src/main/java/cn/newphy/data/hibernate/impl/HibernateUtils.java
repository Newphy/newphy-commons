package cn.newphy.data.hibernate.impl;

import org.hibernate.SessionFactory;
import org.hibernate.persister.entity.AbstractEntityPersister;

public class HibernateUtils {

	/**
	 * 根据属性名获得数据库字段名
	 * 
	 * @param entityClass
	 * @param propertyName
	 * @param sessionFactory
	 * @return
	 */
	public static String[] getColumnNamesByPropertyName(Class<?> entityClass, String propertyName,
			SessionFactory sessionFactory) {
		AbstractEntityPersister classMetadata = (AbstractEntityPersister) sessionFactory
				.getClassMetadata(entityClass);
		return classMetadata.getPropertyColumnNames(propertyName);
	}

	/**
	 * 获得表名
	 * 
	 * @param entityClass
	 * @param sessionFactory
	 * @return
	 */
	public static String getTableName(Class<?> entityClass, SessionFactory sessionFactory) {
		AbstractEntityPersister classMetadata = (AbstractEntityPersister) sessionFactory
				.getClassMetadata(entityClass);
		return classMetadata.getTableName();
	}

	/**
	 * 获得主键
	 * 
	 * @param entityClass
	 * @param sessionFactory
	 * @return
	 */
	public static String[] getPrimaryKeyColumns(Class<?> entityClass, SessionFactory sessionFactory) {
		AbstractEntityPersister classMetadata = (AbstractEntityPersister) sessionFactory
				.getClassMetadata(entityClass);
		return classMetadata.getIdentifierColumnNames();
	}

}
