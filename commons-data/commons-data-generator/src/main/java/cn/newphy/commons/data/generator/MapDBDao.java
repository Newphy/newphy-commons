package cn.newphy.commons.data.generator;

import java.io.File;
import java.io.IOException;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public abstract class MapDBDao {

	private static final String DB_FILE = "data.db";
	private static final String ENCRYPT_PASSWD = "ZnQL9Wq2auGkZskegzKphqtVfose0PJx";
	private static File dbFile;
	
	public MapDBDao() {
	}
	

	
	
	protected DB getDB() {
		try {
			DB db = DBMaker.newFileDB(getDBFile())
					.transactionDisable()
					.encryptionEnable(ENCRYPT_PASSWD)
					.make();
			return db;
		} catch (IOException e) {
			throw new IllegalStateException("创建数据库文件出错", e);
		}
	}
	
	protected void close(DB db) {
		if(db != null) {
			db.close();
		}
	}

	
	private File getDBFile() throws IOException {
		if(dbFile == null) {
			synchronized (MapDBDao.class) {
				if(dbFile == null) {
					String path = System.getProperty("user.dir");
					File dbFile = new File(path, DB_FILE);
					if(!dbFile.exists()) {
						dbFile.createNewFile();
					}
					MapDBDao.dbFile = dbFile;
				}
			}
		}
		return dbFile;
	}

}
