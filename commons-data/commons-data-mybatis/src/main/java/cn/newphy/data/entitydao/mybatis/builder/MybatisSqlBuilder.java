package cn.newphy.data.entitydao.mybatis.builder;

import java.util.List;

import cn.newphy.data.domain.Order;
import cn.newphy.data.entitydao.DialectType;
import cn.newphy.data.entitydao.SqlBuilder;

public abstract class MybatisSqlBuilder implements SqlBuilder {
	public final static String SELECT = "SELECT ";
	public final static String SELECT_COUNT = "SELECT COUNT(1) ";
	public final static String FROM = " FROM ";
	public final static String WHERE = " WHERE ";
	public final static String AND = " AND ";
	public final static String OR = " OR ";
	public final static String ASC = " ASC";
	public final static String DESC = " DESC";
	public final static String ORDER_BY = " ORDER BY ";
	public final static String UPDATE = "UPDATE ";
	public final static String SET = " SET ";
	public final static String COMMA = ", ";
	public final static String BRACKET_L = " (";
	public final static String BRACKET_R = ") ";

	private final DialectType dialectType;

	public MybatisSqlBuilder(DialectType dialectType) {
		this.dialectType = dialectType;
	}

	public DialectType getType() {
		return dialectType;
	}

	public CharSequence getSelectString( String tableName, List<String> columns) {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT);
		if (columns.size() > 0) {
			for (int i = 0; i < columns.size(); i++) {
				sql.append(columns.get(i)).append((columns.size() - 1 == i) ? " " : COMMA);
			}
		} else {
			sql.append("*");
		}
		sql.append(FROM).append(tableName);
		return sql;
	}
	
	public CharSequence getCountString(String tableName) {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT_COUNT);
		sql.append(FROM).append(tableName);
		return sql;
	}
	
	public CharSequence getUpdateString(String tableName, List<CharSequence> updateFragments) {
		StringBuilder sql = new StringBuilder();
		sql.append(UPDATE).append(tableName);
		if (updateFragments.size() > 0) {
			for (int i = 0; i < updateFragments.size(); i++) {
				if(i == 0) {
					sql.append(SET);
				}
				sql.append(updateFragments.get(i));
				if(i != updateFragments.size()-1) {
					sql.append(COMMA);
				}
			}
		} 
		return sql;
	}

	public CharSequence getWhereString(List<CharSequence> whereConditions) {
		if (whereConditions.size() > 0) {
			StringBuilder sql = new StringBuilder();
			for (int i = 0; i < whereConditions.size(); i++) {
				sql.append(i == 0 ? WHERE : AND).append(whereConditions.get(i));
			}
			return sql;
		}
		return "";
	}

	public CharSequence getOrderString(List<Order> orders) {
		if (orders.size() > 0) {
			StringBuffer sql = new StringBuffer();
			for (int i = 0; i < orders.size(); i++) {
				if(i == 0) {
					sql.append(ORDER_BY);
				}
				sql.append(orders.get(i).getColumn())
						.append(orders.get(i).getDirection());
				if (i != orders.size() - 1) {
					sql.append(COMMA);
				}
			}
			return sql;
		}
		return "";
	}

	/**
	 * 获取Limit字符串
	 * 
	 * @param sql
	 * @param offset
	 * @param limit
	 * @return
	 */
	public CharSequence getLimitString(int offset, int limit) {
		throw new UnsupportedOperationException("paged queries not supported");
	}

	/**
	 * 组成占位符形式sql
	 * 
	 * @param sql
	 * @param offset
	 * @param offsetPlaceholder
	 * @param limit
	 * @param limitPlaceholder
	 * @return
	 */
	public CharSequence getLimitString(String offsetPlaceholder, String limitPlaceholder) {
		throw new UnsupportedOperationException("paged queries not supported");
	}

	public CharSequence getOrString(List<CharSequence> fragments) {
		StringBuffer sql = new StringBuffer();
		sql.append(BRACKET_L);
		for (int i = 0; i < fragments.size(); i++) {
			sql.append(fragments.get(i));
			if (i != fragments.size() - 1) {
				sql.append(OR);
			}
		}
		sql.append(BRACKET_R);
		return sql;
	}

	public boolean supportsLimit() {
		return false;
	}

	public boolean supportsLimitOffset() {
		return supportsLimit();
	}

}
