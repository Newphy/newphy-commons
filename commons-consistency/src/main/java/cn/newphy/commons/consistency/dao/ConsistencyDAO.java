package cn.newphy.commons.consistency.dao;

import java.util.List;

import cn.newphy.commons.consistency.ConsistencyInfo;

public interface ConsistencyDAO {

	/**
	 * 获得一致性信息
	 * 
	 * @param id
	 * @return
	 */
	ConsistencyInfo getConsistencyByTxId(String txId);

	/**
	 * 获得一致性详情信息
	 * 
	 * @param id
	 * @return
	 */
	ConsistencyInfo getDetail(long id);

	/**
	 * 保存一致性消息
	 * 
	 * @param message
	 */
	void addConsistency(ConsistencyInfo cinfo);

	/**
	 * 更新一致性信息.
	 * 
	 * @param id
	 */
	void updateConsistency(ConsistencyInfo cinfo);

	/**
	 * 获取重试列表
	 * 
	 * @return
	 */
	List<ConsistencyInfo> queryRetryList(int maxRetry);
}
