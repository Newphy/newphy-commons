package cn.newphy.commons.hibernate.jdbc;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import cn.newphy.commons.hibernate.Page;

public class JdbcHelper {

	private NamedParameterJdbcTemplate namedParamJdbcTemplate;
	private JdbcTemplate jdbcTemplate;

	public JdbcHelper() {
		super();
	}

	public JdbcHelper(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		namedParamJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * sql更新,使用？占位符
	 * 
	 * @param sql
	 * @param values
	 * @return 影响的行数
	 */
	public int update(CharSequence sql, Object... values) {
		return jdbcTemplate.update(sql.toString(), values);
	}

	/**
	 * sql更新，使用名称占位符
	 * 
	 * @param sql
	 * @param paramMap
	 * @return
	 */
	public int updateNP(CharSequence sql, Map<String, ?> paramMap) {
		return namedParamJdbcTemplate.update(sql.toString(), paramMap);
	}

	/**
	 * 查询列表
	 * 
	 * @param sql
	 * @param rowMapper
	 * @param values
	 * @return
	 */
	public <T> List<T> query(CharSequence sql, RowMapper<T> rowMapper, Object... values) {
		List<T> list = jdbcTemplate.query(sql.toString(), values, rowMapper);
		return list;
	}

	/**
	 * 查询列表
	 * 
	 * @param sql
	 * @param rowMapper
	 * @param paramMap
	 * @return
	 */
	public <T> List<T> queryNP(CharSequence sql, RowMapper<T> rowMapper, Map<String, ?> paramMap) {
		List<T> list = namedParamJdbcTemplate.query(sql.toString(), paramMap, rowMapper);
		return list;
	}

	/**
	 * 查询单个对象
	 * 
	 * @param sql
	 * @param rowMapper
	 * @param values
	 * @return
	 */
	public <T> T queryForObject(CharSequence sql, RowMapper<T> rowMapper, Object... values) {
		return (T) jdbcTemplate.queryForObject(sql.toString(), values, rowMapper);
	}

	/**
	 * 查询单个对象
	 * 
	 * @param sql
	 * @param rowMapper
	 * @param values
	 * @return
	 */
	public <T> T queryForObject(CharSequence sql, Class<T> requiredType, Object... values) {
		T t = (T) jdbcTemplate.queryForObject(sql.toString(), values, requiredType);
		return t;
	}

	/**
	 * 查询单个对象
	 * 
	 * @param sql
	 * @param rowMapper
	 * @param paramMap
	 * @return
	 */
	public <T> T queryNPForObject(CharSequence sql, RowMapper<T> rowMapper, Map<String, ?> paramMap) {
		return (T) namedParamJdbcTemplate.queryForObject(sql.toString(), paramMap, rowMapper);
	}

	/**
	 * 查询单个对象
	 * 
	 * @param sql
	 * @param requiredType
	 * @param paramMap
	 * @return
	 */
	public <T> T queryNPForObject(CharSequence sql, Class<T> requiredType, Map<String, ?> paramMap) {
		T t = (T) namedParamJdbcTemplate.queryForObject(sql.toString(), paramMap, requiredType);
		return t;
	}

	/**
	 * 查询分页
	 * 
	 * @param sql
	 * @param page
	 * @param rowMapper
	 * @param values
	 * @return
	 */
	public <T> Page<T> queryForPage(CharSequence sql, Page<T> page, RowMapper<T> rowMapper, Object... values) {
		if (page.getPageNo() <= 0 || page.getPageSize() <= 0) {
			return page;
		}
		int start = page.getStartIndex();
		int size = page.getPageSize();
		if (page.isNeedTotal()) {
			String countSQL = "SELECT count(1) FROM (" + SqlUtils.removeOrders(sql) + ") AA_";
			Long totalCount = jdbcTemplate.queryForLong(countSQL, values);
			if (totalCount == null) {
				return page;
			}
			page.setTotal(totalCount);
		} else {
			// 多查询一个判断是否有下一页
			size += 1;
		}
		StringBuffer selectSql = new StringBuffer(sql);
		selectSql.append(" LIMIT ").append(start).append(", ").append(size);
		List<T> data = jdbcTemplate.query(selectSql.toString(), values, rowMapper);
		page.setData(data.subList(0, Math.min(page.getPageSize(), data.size())));
		if (!page.isNeedTotal() && data.size() > page.getPageSize()) {
			page.setHasNext(true);
		}
		return page;
	}

	/**
	 * 查询分页
	 * 
	 * @param sql
	 * @param page
	 * @param rowMapper
	 * @param paramMap
	 * @return
	 */
	public <T> Page<T> queryNPForPage(CharSequence sql, Page<T> page, RowMapper<T> rowMapper, Map<String, ?> paramMap) {
		if (page.getPageNo() <= 0 || page.getPageSize() <= 0) {
			return page;
		}
		int start = page.getStartIndex();
		int size = page.getPageSize();
		if (page.isNeedTotal()) {
			String countSQL = "SELECT count(1) FROM (" + SqlUtils.removeOrders(sql) + ") AA_";
			Long totalCount = namedParamJdbcTemplate.queryForLong(countSQL, paramMap);
			if (totalCount == null) {
				return page;
			}
			page.setTotal(totalCount);
		} else {
			// 多查询一个判断是否有下一页
			size += 1;
		}
		StringBuffer selectSql = new StringBuffer(sql);
		selectSql.append(" LIMIT ").append(start).append(", ").append(size);
		List<T> data = namedParamJdbcTemplate.query(selectSql.toString(), paramMap, rowMapper);
		if(CollectionUtils.isNotEmpty(data)) {
			data = data.subList(0, Math.min(page.getPageSize(), data.size()));
		}
		page.setData(data);
		if (!page.isNeedTotal() && data.size() > page.getPageSize()) {
			page.setHasNext(true);
		}
		return page;
	}

	/**
	 * 设置数据源
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		if (dataSource != null) {
			jdbcTemplate = new JdbcTemplate(dataSource);
		}
	}

	/**
	 * 获得数据源
	 * 
	 * @return
	 */
	public DataSource getDataSource() {
		if (jdbcTemplate != null) {
			return jdbcTemplate.getDataSource();
		}
		return null;
	}
}
