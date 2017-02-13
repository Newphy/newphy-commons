package cn.newphy.data.entitydao;

public interface ConditionExpression {

	/**
	 * 获得sql串
	 * 
	 * @return
	 */
	CharSequence toSql(SqlBuilder sqlBuilder);
	
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
