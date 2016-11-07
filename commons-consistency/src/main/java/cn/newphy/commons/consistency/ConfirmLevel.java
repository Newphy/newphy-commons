package cn.newphy.commons.consistency;

/**
 * 确认级别
 * 
 * @author liuhui18
 * @time 2016年10月31日
 * @Copyright (c) 深圳市牛鼎丰科技有限公司-版权所有.
 */
public enum ConfirmLevel  {
	/**
	 * 缺省状态，默认为SENT级别
	 */
	DEFAULT,
	
	/**
	 * 保证发送成功
	 */
	SENT,
	
	/**
	 * 保证执行成功
	 */
	EXECUTED;
	
}
