package cn.newphy.commons.hibernate.impl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.engine.Mapping;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.Type;

import cn.newphy.commons.hibernate.EntityRowMapper;

public class HibernateEnityRowMapper<T> extends EntityRowMapper<T> {
	private static Log log = LogFactory.getLog(HibernateEnityRowMapper.class);

	private static Map<Class<?>, HibernateEnityRowMapper<?>> hermMap = new HashMap<Class<?>, HibernateEnityRowMapper<?>>();

	@SuppressWarnings("unchecked")
	public static <T> HibernateEnityRowMapper<T> getHibernateEntityRowMapper(SessionFactory sessionFactory, Class<T> entityClass) {
		if (hermMap.isEmpty()) {
			prepareMap(sessionFactory);
		}
		return (HibernateEnityRowMapper<T>) hermMap.get(entityClass);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static synchronized void prepareMap(SessionFactory sessionFactory) {
		if (hermMap.isEmpty()) {
			EntityMode[] modes = new EntityMode[] { EntityMode.POJO, EntityMode.DOM4J, EntityMode.MAP };
			Map<String, ClassMetadata> map = sessionFactory.getAllClassMetadata();
			for (String name : map.keySet()) {
				ClassMetadata classMetadata = map.get(name);
				Class<?> persisterClass = null;
				for (EntityMode mode : modes) {
					persisterClass = classMetadata.getMappedClass(mode);
					if (persisterClass != null) {
						break;
					}
				}
				if (persisterClass == null) {
					log.warn("不能找到[" + name + "]的mappedClass");
					continue;
				}
				hermMap.put(persisterClass, new HibernateEnityRowMapper(persisterClass, sessionFactory, (AbstractEntityPersister) classMetadata));
				Class<?> parentClass = persisterClass;
				while (!(parentClass = parentClass.getSuperclass()).equals(Object.class)) {
					if (hermMap.containsKey(parentClass) || sessionFactory.getClassMetadata(parentClass) != null) {
						continue;
					}
					hermMap.put(parentClass, 
							new HibernateEnityRowMapper(parentClass, sessionFactory, (AbstractEntityPersister) classMetadata));
				}
			}
		}
	}

	private AbstractEntityPersister classMetadata;
	private SessionFactory sessionFactory;

	private HibernateEnityRowMapper(Class<T> entityClass, SessionFactory sessionFactory, AbstractEntityPersister classMetadata) {
		super(entityClass);
		this.sessionFactory = sessionFactory;
		this.classMetadata = classMetadata;
		prepare();
	}

	@Override
	protected void prepare() {
		Map<String, PropertyDescriptor> pdMap = new HashMap<String, PropertyDescriptor>();
		for (PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(entityClass)) {
			pdMap.put(pd.getName(), pd);
		}

		List<String> propertyNames = new ArrayList<String>();
		propertyNames.add(classMetadata.getIdentifierPropertyName());
		propertyNames.addAll(Arrays.asList(classMetadata.getPropertyNames()));
		for (String propertyName : propertyNames) {
			if (pdMap.containsKey(propertyName)) {
				Type type = classMetadata.getPropertyType(propertyName);
				// 不是复杂类型
				if (!type.isAnyType() && !type.isAssociationType() && !type.isCollectionType() && !type.isComponentType() && !type.isEntityType()
								&& !type.isXMLElement()) {
					String[] propertyColumns = classMetadata.getPropertyColumnNames(propertyName);
					if (propertyColumns.length == 1 && propertyColumns[0] != null) {
						String columnName = propertyColumns[0];
						int[] sqlTypes = type.sqlTypes((Mapping) sessionFactory);
						// 简单类型
						if (sqlTypes.length == 1) {
							mapColumn(propertyName, columnName.toLowerCase(), sqlTypes[0]);
						}
					}
				}
			}
		}
	}

}
