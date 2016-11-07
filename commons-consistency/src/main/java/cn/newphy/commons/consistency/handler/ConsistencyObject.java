package cn.newphy.commons.consistency.handler;

/**
 * 一致性对象
 * 
 * @author xn044401
 *
 */
public interface ConsistencyObject {

	/**
	 * 获得业务对象编号
	 * @return
	 */
	String getObjectId();
	
	
	/**
	 * 获得业务对象
	 * 
	 * @return
	 */
	Object getObject();
	
}
