package cn.newphy.data.id.snowflake;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

public class SnowFlakeIdFactory implements InitializingBean, ApplicationContextAware {
	private static final int MAX_SIZE = (int) SnowFlakeId.maxWorkerId;
	private static final int CONNECT_TIME_OUT = 10000;
	private static ApplicationContext applicationContext;

	/**
	 * 获得SnowFlakeId实例
	 * 
	 * @return
	 */
	public static SnowFlakeId getSnowFlakeId(String moduleName) {
		SnowFlakeIdFactory snowFlakeIdFactory = applicationContext.getBean(SnowFlakeIdFactory.class);
		return snowFlakeIdFactory.createSnowFlakeId(moduleName);
	}

	private ConcurrentHashMap<String, SnowFlakeId> snowFlakeIdMap = new ConcurrentHashMap<>();

	public enum RegistryType {
		ZK, REDIS;
	}

	// workerId注册器
	private WorkerIdRegistry workerIdRegistry;
	// 模块名称
	private String defaultModuleName = "default";
	// 数据中心编号
	private int dataCenter = 0;
	// 注册类型
	private RegistryType registryType = RegistryType.ZK;
	// 注册服务器
	private String registryHosts;

	public SnowFlakeId createSnowFlakeId() {
		return createSnowFlakeId(this.defaultModuleName);
	}

	public SnowFlakeId createSnowFlakeId(String moduleName) {
		if (moduleName == null) {
			moduleName = this.defaultModuleName;
		}
		if (!snowFlakeIdMap.contains(moduleName)) {
			synchronized (this) {
				if (!snowFlakeIdMap.contains(moduleName)) {
					WorkerId workerId = workerIdRegistry.register(dataCenter, moduleName);
					SnowFlakeId snowFlakeId = workerId.getSnowFlakeId();
					snowFlakeIdMap.putIfAbsent(moduleName, snowFlakeId);
				}
			}
		}
		return snowFlakeIdMap.get(moduleName);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		switch (registryType) {
		case ZK: {
			if (!StringUtils.hasText(registryHosts)) {
				throw new IllegalArgumentException("ZooKeeper注册服务器地址为空");
			}
			this.workerIdRegistry = new WorkerIdZookeeperRegistry(registryHosts, CONNECT_TIME_OUT, MAX_SIZE);
			break;
		}
		case REDIS: {
			throw new UnsupportedOperationException("暂不支持的REDIS注册类型");
		}
		default: {
			throw new UnsupportedOperationException("不支持的注册类型");
		}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SnowFlakeIdFactory.applicationContext = applicationContext;
	}

	/**
	 * @return the defaultModuleName
	 */
	public String getDefaultModuleName() {
		return defaultModuleName;
	}

	/**
	 * @param defaultModuleName
	 *            the defaultModuleName to set
	 */
	public void setDefaultModuleName(String defaultModuleName) {
		this.defaultModuleName = defaultModuleName;
	}

	/**
	 * @return the dataCenter
	 */
	public int getDataCenter() {
		return dataCenter;
	}

	/**
	 * @param dataCenter
	 *            the dataCenter to set
	 */
	public void setDataCenter(int dataCenter) {
		this.dataCenter = dataCenter;
	}

	/**
	 * @return the registryType
	 */
	public RegistryType getRegistryType() {
		return registryType;
	}

	/**
	 * @param registryType
	 *            the registryType to set
	 */
	public void setRegistryType(RegistryType registryType) {
		this.registryType = registryType;
	}

	/**
	 * @return the registryHosts
	 */
	public String getRegistryHosts() {
		return registryHosts;
	}

	/**
	 * @param registryHosts
	 *            the registryHosts to set
	 */
	public void setRegistryHosts(String registryHosts) {
		this.registryHosts = registryHosts;
	}

}
