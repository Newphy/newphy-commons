package cn.newphy.data.entitydao.mybatis;

import java.util.Collection;

import org.apache.ibatis.mapping.ResultMapping;

import cn.newphy.data.entitydao.ConditionExpression;
import cn.newphy.data.entitydao.mybatis.expression.BetweenConditionExpression;
import cn.newphy.data.entitydao.mybatis.expression.InConditionExpression;
import cn.newphy.data.entitydao.mybatis.expression.OperatorConditionExpression;
import cn.newphy.data.entitydao.mybatis.expression.OrConditionExpression;
import cn.newphy.data.entitydao.mybatis.expression.SimpleConditionExpression;

public abstract class ConditionExpressions {
	

	/**
	 * 等于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public static ConditionExpression eq(EntityMapping entityMapping, String propertyName, Object value) {
		return new OperatorConditionExpression(getResultMapping(entityMapping, propertyName), value, "=");
	}

	/**
	 * 不等于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public static ConditionExpression ne(EntityMapping entityMapping, String propertyName, Object value) {
		return new OperatorConditionExpression(getResultMapping(entityMapping, propertyName), value, "<>");
	}

	/**
	 * like
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public static ConditionExpression like(EntityMapping entityMapping, String propertyName, String value) {
		return new OperatorConditionExpression(getResultMapping(entityMapping, propertyName), value, "LIKE");
	}

	/**
	 * 大于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public static ConditionExpression gt(EntityMapping entityMapping, String propertyName, Object value) {
		return new OperatorConditionExpression(getResultMapping(entityMapping, propertyName), value, ">");
	}

	/**
	 * 大于等于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public static ConditionExpression ge(EntityMapping entityMapping, String propertyName, Object value) {
		return new OperatorConditionExpression(getResultMapping(entityMapping, propertyName), value, ">=");
	}

	/**
	 * 小于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public static ConditionExpression lt(EntityMapping entityMapping, String propertyName, Object value) {
		return new OperatorConditionExpression(getResultMapping(entityMapping, propertyName), value, "<");
	}

	/**
	 * 小于等于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public static ConditionExpression le(EntityMapping entityMapping, String propertyName, Object value) {
		return new OperatorConditionExpression(getResultMapping(entityMapping, propertyName), value, "<=");
	}

	/**
	 * between
	 * 
	 * @param propertyName
	 * @param lo
	 *            低值
	 * @param hi
	 *            高值
	 * @return
	 */
	public static ConditionExpression between(EntityMapping entityMapping, String propertyName, Object lo, Object hi) {
		return new BetweenConditionExpression(getResultMapping(entityMapping, propertyName), lo, hi);
	}

	/**
	 * in
	 * 
	 * @param propertyName
	 * @param values
	 * @return
	 */
	public static ConditionExpression in(EntityMapping entityMapping, String propertyName, Object[] values) {
		return new InConditionExpression(getResultMapping(entityMapping, propertyName), values);
	}

	/**
	 * in 操作
	 * 
	 * @param propertyName
	 * @param values
	 * @return
	 */
	public static ConditionExpression in(EntityMapping entityMapping, String propertyName, Collection<?> values) {
		return new InConditionExpression(getResultMapping(entityMapping, propertyName), values == null ? null : values.toArray());
	}

	/**
	 * 为空
	 * 
	 * @param propertyName
	 * @return
	 */
	public static ConditionExpression isNull(EntityMapping entityMapping, String propertyName) {
		return new SimpleConditionExpression(getResultMapping(entityMapping, propertyName), " IS NULL");
	}

	/**
	 * 不为空
	 * 
	 * @param propertyName
	 * @return
	 */
	public static ConditionExpression notNull(EntityMapping entityMapping, String propertyName) {
		return new SimpleConditionExpression(getResultMapping(entityMapping, propertyName), " IS NOT NULL");
	}
	
	/**
	 * 或操作
	 * @param exp1
	 * @param exp2
	 * @param expN
	 * @return
	 */
	public static ConditionExpression or(ConditionExpression exp1, ConditionExpression exp2, ConditionExpression...expN) {
		int length = 2 + (expN == null ? 0 : expN.length);
		ConditionExpression[] exps = new ConditionExpression[length];
		exps[0] = exp1;
		exps[1] = exp2;
		for(int i = 0; i < expN.length; i++) {
			exps[i+2] = expN[i];
		}
		return new OrConditionExpression(exps);
	}


	private static ResultMapping getResultMapping(EntityMapping entityMapping, String propertyName) {
		if(entityMapping == null) {
			throw new IllegalStateException("entitiyMapping为空");
		}
		ResultMapping resultMapping = entityMapping.getResultMappingByProperty(propertyName);
		if(resultMapping == null) {
			throw new IllegalArgumentException("ResultMap[" + entityMapping.getSourceId() + "]没有属性名为[" + propertyName + "]的映射");
		}
		return resultMapping;
	}
}
