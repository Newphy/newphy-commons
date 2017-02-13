package cn.newphy.commons.data.generator.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.newphy.commons.data.generator.model.ds.Ds;
import cn.newphy.commons.data.generator.model.ds.Schema;

public abstract class DBInfo {
	
	private final Ds ds;
	
	public DBInfo(Ds ds) {
		this.ds = ds;
	}

	public abstract List<Schema> getSchemaList();
	
	public abstract Schema getSchema(String schemaName);
	
	public Connection getConnection() throws Exception {
		if (ds == null || ds.getDriverClass() == null || ds.getUrl() == null || ds.getUser() == null) {
			return null;
		}
		String url = ds.getUrl();
		String user = ds.getUser();
		String password = ds.getPasswd();
		String driverClass = ds.getDriverClass();
		Driver driver = (Driver) Class.forName(driverClass).newInstance();
		DriverManager.registerDriver(driver);
		Connection conn = DriverManager.getConnection(url, user, password);
		return conn;
	}
	

	protected <T> List<T> query(String sql, ResultHandler<T> handler) throws Exception {
		Connection con = getConnection();
		if (con == null) {
			return null;
		}
		List<T> list = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			list = new ArrayList<T>();
			while (rs.next()) {
				list.add(handler.doInvoke(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(con, ps, rs);
		}
		return list;
	}

	public void close(Connection con, PreparedStatement ps, ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
			} finally {
				try {
					if (con != null)
						con.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	
}
