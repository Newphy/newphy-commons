package cn.newphy.data.id.snowflake;

/**
 * SnowFlakeId
 * <p>
 * |-- 40bit timespan --|-- 9bit seq
 * --|
 * <ul>
 * <li>timespan: 间隔2016-1-1 00:00:00的毫秒数，40bit可用32年</li>
 * <li>seq: 一毫秒最多并发512，秒级并发512000</li>
 * </ul>
 * 
 * @author liuhui18
 * @createTime 2016年8月25日
 * @Copyright (c) 深圳市牛鼎丰科技有限公司-版权所有.
 */
public class UniqueID {

	private static final long BACK_TOLERANT = 2000L;
	
	// 2016-01-01 00:00:00的起点
	private long twepoch = 1454256000597L;


	private long sequence = 0L;

	private long sequenceBits = 9L;

	private long timestampLeftShift = sequenceBits;

	private long sequenceMask = -1L ^ (-1L << sequenceBits);

	private long lastTimestamp = -1L;
	

	public UniqueID() {
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
		long l = ((timestamp - twepoch) << timestampLeftShift)  | sequence;
		return l;
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
	
	
	public static void main(String[] args) {

		UniqueID uid = new UniqueID();
		for (int i = 0; i < 10000; i++) {
			System.out.println(uid.nextId());
		}
	}
	

}
