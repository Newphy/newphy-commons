package cn.newphy.data.entitydao;

public interface UpdateExpression {

	/**
	 * 获得sql串
	 * 
	 * @return
	 */
	CharSequence toSql(SqlBuilder dialect);
	
	/**
	 * 获得属性名称
	 * @return
	 */
	String getPropertyName();

	/**
	 * 获得参数值
	 * 
	 * @return
	 */
	Object getValue();
}
