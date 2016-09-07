package cn.newphy.commons.consistency.invoker;

/**
 * 一致性触发接口
 * 
 * @author xn044401
 *
 */
public interface ConsistencyInvoker {

	void invoke(ConsistencyModel	 model) throws Exception;
}
