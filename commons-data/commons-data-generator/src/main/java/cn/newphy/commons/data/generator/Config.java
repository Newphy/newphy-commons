package cn.newphy.commons.data.generator;

import java.io.IOException;
import java.io.Serializable;

import org.mapdb.DB;

import cn.newphy.commons.data.generator.utils.JsonUtils;

@SuppressWarnings("serial")
public class Config implements Serializable {
	private static final String KEY_CONFIG = "CONFIGURATION";
	
	private static ConfigDao configDao = new ConfigDao();
	private static Config configuration;
	
	public static Config getInstance() {
		if(configuration == null) {
			synchronized (Config.class) {
				if(configuration == null) {
					configuration = configDao.get();
					if(configuration == null) {
						configuration = new Config();
						configDao.save(configuration);
					}
				}
			}
		}
		return configuration;
	}
	
	private boolean initialized = false;

	/**
	 * @return the initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * @param initialized the initialized to set
	 */
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
		configDao.save(this);
	}

	
	private static class ConfigDao extends MapDBDao {
		
		public void save(Config config) {
			DB db = null;
			try {
				db = getDB();
				db.catPut("Configuration", config);
			} finally {
				close(db);
			}
		}
		
		public Config get() {
			DB db = getDB();
			try {
				String json = db.catGet(KEY_CONFIG);
				if(json != null) {
					try {
						Config config = JsonUtils.readObject(json, Config.class);
						return config;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return null;
			} 
			finally {
				close(db);
			}
		}
	}

	
}
