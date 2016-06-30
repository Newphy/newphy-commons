package cn.newphy.commons.hibernate;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;

import cn.newphy.commons.hibernate.jdbc.JdbcUtils;

public abstract class EntityRowMapper<T> implements RowMapper<T> {
	private static Log log = LogFactory.getLog(EntityRowMapper.class);

	protected final Class<T> entityClass;
	private Map<String, PropertyColumnMapper> mappedColumns = new HashMap<String, PropertyColumnMapper>();

	public EntityRowMapper(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * 设置隐射的字段名称
	 * 
	 * @param propertyName
	 * @param columnName
	 * @param type
	 * @return
	 */
	public EntityRowMapper<T> mapColumn(String propertyName, String columnName, int type) {
		mappedColumns.put(propertyName, new PropertyColumnMapper(propertyName, columnName, type));
		return this;
	}

	/**
	 * 设置隐射的字段名称
	 * 
	 * @param propertyName
	 * @param columnName
	 * @return
	 */
	public EntityRowMapper<T> mapColumn(String propertyName, String columnName) {
		int sqlType = Types.OTHER;
		if (mappedColumns.containsKey(propertyName)) {
			sqlType = mappedColumns.get(propertyName).type;
		} else {
			try {
				PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(entityClass, propertyName);
				Class<?> javaType = pd.getPropertyType();
				sqlType = JdbcUtils.javaType2SqlType(javaType);
			} catch (Exception e) {
				throw new DaoException("can't find property[" + propertyName + "] for Class[" + entityClass.getCanonicalName() + "]");
			}
		}
		return mapColumn(propertyName, columnName, sqlType);
	}

	/**
	 * 对传入实体进行映射
	 * 
	 * @param entity
	 * @param rs
	 * @param index
	 * @return
	 * @throws SQLException
	 */
	public <E> E mapRow(E entity, ResultSet rs, int index) throws SQLException {
		Set<String> columnNames = setColumnName(rs);
		for (String propertyName : mappedColumns.keySet()) {
			PropertyColumnMapper mapper = mappedColumns.get(propertyName);
			PropertyDescriptor propertyDescriptor;
			try {
				propertyDescriptor = PropertyUtils.getPropertyDescriptor(entity, propertyName);
				if (propertyDescriptor == null) {
					continue;
				}
			} catch (Exception e1) {
				continue;
			}

			Class<?> propertyClass = propertyDescriptor.getPropertyType();
			if (columnNames.contains(mapper.columnName)) {
				try {
					switch (mapper.type) {
					case Types.TINYINT:
					case Types.SMALLINT:
					case Types.INTEGER:
						Integer i = rs.getInt(mapper.columnName);
						PropertyUtils.setProperty(entity, propertyName, i);
						break;
					case Types.BIGINT:
						Long l = rs.getLong(mapper.columnName);
						PropertyUtils.setProperty(entity, propertyName, l);
						break;
					case Types.DOUBLE:
					case Types.DECIMAL:
						Double d = rs.getDouble(mapper.columnName);
						PropertyUtils.setProperty(entity, propertyName, d);
						break;
					case Types.CHAR:
					case Types.VARCHAR:
					case Types.LONGVARCHAR:
						String s = rs.getString(mapper.columnName);
						PropertyUtils.setProperty(entity, propertyName, s);
						break;
					case Types.DATE: {
						java.sql.Date dt = rs.getDate(mapper.columnName);
						Date date = dt == null ? null : new Date(dt.getTime());
						PropertyUtils.setProperty(entity, propertyName, date);
						break;
					}
					case Types.TIME: {
						Time t = rs.getTime(mapper.columnName);
						PropertyUtils.setProperty(entity, propertyName, t);
						break;
					}
					case Types.TIMESTAMP:
						Timestamp ts = rs.getTimestamp(mapper.columnName);
						if (Timestamp.class.isAssignableFrom(propertyClass)) {
							PropertyUtils.setProperty(entity, propertyName, ts);
						} else if (Date.class.isAssignableFrom(propertyClass)) {
							Date date = ts == null ? null : new Date(ts.getTime());
							PropertyUtils.setProperty(entity, propertyName, date);
						}
						break;
					default:
						Object obj = rs.getObject(mapper.columnName);
						PropertyUtils.setProperty(entity, propertyName, obj);
						break;
					}
				} catch (Exception e) {
					log.warn("set Class[" + entityClass.getCanonicalName() + "] property [" + propertyName + "] value error: " + e.getMessage());
				}
			}
		}
		return entity;
	}

	@Override
	public T mapRow(ResultSet rs, int index) throws SQLException {
		T entity = null;
		try {
			entity = entityClass.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("the Class[" + entityClass.getCanonicalName() + "] don't have an empty constructor!", e);
		}
		return mapRow(entity, rs, index);

	}

	@SuppressWarnings("unchecked")
	@Override
	protected EntityRowMapper<T> clone() throws CloneNotSupportedException {
		EntityRowMapper<T> other = (EntityRowMapper<T>) super.clone();
		return other;
	}

	/**
	 * 初始化EntityRowMapper
	 */
	protected abstract void prepare();

	private Set<String> setColumnName(ResultSet rs) throws SQLException {
		ResultSetMetaData metadata = rs.getMetaData();
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < metadata.getColumnCount(); i++) {
			set.add(metadata.getColumnLabel(i + 1).toLowerCase());
		}
		return set;
	}

	private static class PropertyColumnMapper implements Cloneable {
		@SuppressWarnings("unused")
		private String propertyName;
		private String columnName;
		private int type;

		public PropertyColumnMapper(String propertyName, String columnName, int type) {
			this.columnName = columnName;
			this.propertyName = propertyName;
			this.type = type;
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

	}
}
