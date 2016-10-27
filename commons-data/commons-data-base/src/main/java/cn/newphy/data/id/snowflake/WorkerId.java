package cn.newphy.data.id.snowflake;

public class WorkerId {
	// 模块名称
	private String moduleName;

	// 数据中心编号
	private int datacenterId;

	// worker编号
	private int workerIndex;
	
	// SnowFlakeId算法
	private final SnowFlakeId snowFlakeId;

	public WorkerId(int datacenterId, String moduleName) {
		this.moduleName = moduleName;
		this.datacenterId = datacenterId;
		this.snowFlakeId = new SnowFlakeId(datacenterId, this.workerIndex);
	}

	/**
	 * @return the moduleName
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * @param moduleName
	 *            the moduleName to set
	 */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * @return the datacenterId
	 */
	public int getDatacenterId() {
		return datacenterId;
	}

	/**
	 * @param datacenterId
	 *            the datacenterId to set
	 */
	public void setDatacenterId(int datacenterId) {
		this.datacenterId = datacenterId;
	}

	/**
	 * @return the workerIndex
	 */
	public int getWorkerIndex() {
		return workerIndex;
	}

	/**
	 * @param workerIndex the workerIndex to set
	 */
	public void setWorkerIndex(int workerIndex) {
		this.workerIndex = workerIndex;
		this.snowFlakeId.set(datacenterId, workerIndex);
	}

	/**
	 * @return the snowFlakeId
	 */
	public SnowFlakeId getSnowFlakeId() {
		return snowFlakeId;
	}
	
	

}
