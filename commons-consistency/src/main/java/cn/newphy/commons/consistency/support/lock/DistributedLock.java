package cn.newphy.commons.consistency.support.lock;

public interface DistributedLock {

	/**
	 * 加锁
	 * <p>
	 * 如果返回token, 则加锁成功，返回空，则加锁失败
	 * 
	 * @param locker
	 * @return 返回加密token
	 */
	String lock(String locker);

	/**
	 * 延长锁
	 * @param token
	 */
	boolean extend(String token);

	/**
	 * 解锁
	 */
	void unlock(String token);

}
