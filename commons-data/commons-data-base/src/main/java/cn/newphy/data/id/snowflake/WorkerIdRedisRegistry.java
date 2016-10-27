package cn.newphy.data.id.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerIdRedisRegistry implements WorkerIdRegistry {
	private Logger logger = LoggerFactory.getLogger(WorkerIdRedisRegistry.class);


	@Override
	public WorkerId register(int datacenterId, String moduleName) {
		return null;
	}


	
	
}
