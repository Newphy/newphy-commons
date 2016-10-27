package cn.newphy.data.id.snowflake;

/**
 * SnowFlakeId
 * <p>
 * |-- 40bit timespan --|-- 3bit datacenter --|-- 11bit workId --|-- 9bit seq
 * --|
 * <ul>
 * <li>timespan: 间隔2016-1-1 00:00:00的毫秒数，40bit可用32年</li>
 * <li>datacenter: 机房个数，最多8个</li>
 * <li>workId: 单应用节点数，最多2048个</li>
 * <li>seq: 一毫秒最多并发512，秒级并发512000</li>
 * </ul>
 * 
 * @author liuhui18
 * @createTime 2016年8月25日
 * @Copyright (c) 深圳市牛鼎丰科技有限公司-版权所有.
 */
public class SnowFlakeId {

	private static final long workerIdBits = 11L;

	public static final long maxWorkerId = -1L ^ (-1L << workerIdBits);

	private static final long BACK_TOLERANT = 2000L;
	
	// 2016-01-01 00:00:00的起点
	private long twepoch = 1454256000597L;

	// workerId
	private long workerId;
	// 数据中心编号
	private long datacenterId;

	private long sequence = 0L;

	private long datacenterIdBits = 3L;

	private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

	private long sequenceBits = 9L;

	private long workerIdShift = sequenceBits;

	private long datacenterIdShift = sequenceBits + workerIdBits;

	private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

	private long sequenceMask = -1L ^ (-1L << sequenceBits);

	private long lastTimestamp = -1L;
	

	public SnowFlakeId(int datacenterId, int workerId) {
		set(datacenterId, workerId);
	}

	/**
	 * 生成编号
	 * 
	 * @return
	 */
	public synchronized long nextId() {
		long timestamp = timeGen();

		// 增加2s的兼容时间，避免时间同步时导致异常
		if (timestamp + BACK_TOLERANT < lastTimestamp) {
			System.out.println("clock is moving backwards. Rejecting requests until " + lastTimestamp);
			throw new RuntimeException(String.format(
					"Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
		}

		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & sequenceMask;
			if (sequence == 0) {
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else {
			sequence = 0L;
		}
		lastTimestamp = timestamp;
		long l = ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift)
				| (workerId << workerIdShift) | sequence;
		System.out.println(l + " (dataCenter=" + datacenterId + "; workerId=" + workerId + ")");
		return l;
	}

	/**
	 * 设置datacenterId和workerId
	 * 
	 * @param datacenterId
	 * @param workerId
	 */
	public synchronized void set(int datacenterId, int workerId) {
		// sanity check for workerId
		if (workerId > maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(
					String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
		}
		if (datacenterId > maxDatacenterId || datacenterId < 0) {
			throw new IllegalArgumentException(
					String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
		}
		this.workerId = workerId;
		this.datacenterId = datacenterId;
	}

	protected long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	protected long timeGen() {
		return System.currentTimeMillis();
	}
}
