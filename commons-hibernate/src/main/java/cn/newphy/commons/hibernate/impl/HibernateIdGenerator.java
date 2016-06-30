package cn.newphy.commons.hibernate.impl;

import java.io.Serializable;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.connection.DatasourceConnectionProvider;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.LocalDataSourceConnectionProvider;

import cn.newphy.commons.hibernate.IdGenerator;

public class HibernateIdGenerator implements IdentifierGenerator, Configurable {

	private String name;

	@Override
	public Serializable generate(SessionImplementor session, Object object)
			throws HibernateException {
		// name
		if (name == null) {
			AbstractEntityPersister classMetadata = (AbstractEntityPersister) session
					.getFactory().getClassMetadata(object.getClass());
			name = classMetadata.getTableName();// 表名
		}

		IdGenerator idGenerator = IdGenerator.getIdGenerator(name);
		if (idGenerator.getDataSource() == null) {
			idGenerator.setDataSource(getDataSource(session));
		}
		return idGenerator.getId();
	}

	@Override
	public void configure(Type type, Properties params, Dialect d)
			throws MappingException {
		if (params.get("name") != null) {
			name = params.getProperty("name");
		}
	}

	private DataSource getDataSource(SessionImplementor session) {
		ConnectionProvider cp = session.getFactory().getConnectionProvider();
		DataSource dataSource = null;
		if (cp instanceof DatasourceConnectionProvider) {
			dataSource = ((DatasourceConnectionProvider) cp).getDataSource();
		} else if (cp instanceof LocalDataSourceConnectionProvider) {
			dataSource = ((LocalDataSourceConnectionProvider) cp)
					.getDataSource();
		}
		return dataSource;
	}

}
