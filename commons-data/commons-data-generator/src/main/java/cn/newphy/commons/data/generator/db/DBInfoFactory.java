package cn.newphy.commons.data.generator.db;

import cn.newphy.commons.data.generator.model.ds.DSType;
import cn.newphy.commons.data.generator.model.ds.Ds;

public class DBInfoFactory {

	public static DBInfo getDBInfo(Ds ds) {
		if(ds == null) {
			return null;
		}
		DSType dsType = ds.getDSType();
		switch (dsType) {
		case MYSQL:
			return new MysqlDBInfo(ds);
		default:
			throw new UnsupportedOperationException();
		}
	}
}
