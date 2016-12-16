package cn.newphy.data.entitydao.mybatis.plugins.paginator;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Invocation;

import cn.newphy.data.domain.Page;
import cn.newphy.data.domain.PageByPage;
import cn.newphy.data.domain.PageRequest;
import cn.newphy.data.domain.Pageable;
import cn.newphy.data.entitydao.mybatis.EConfiguration;

public class PageByPageProcessor extends PageProcessor {

	public PageByPageProcessor(Invocation invocation, Pageable pageable, EConfiguration configuration) {
		super(invocation, pageable, configuration);
	}

	@Override
	public Page<?> process() throws InvocationTargetException, IllegalAccessException {
		MappedStatement stmt = getMappedStatement();
		Pageable pageablePlus = getPageablePlus(pageable);
		SqlSource sqlSource = stmt.getSqlSource();
		Object parameterObject = getParameterObjectWithPage(stmt.getConfiguration(), pageablePlus);
		BoundSql boundSql = sqlSource.getBoundSql(parameterObject);		

		// 查询分页
		List<?> content = queryPage(boundSql, parameterObject, pageablePlus);
		Page<?> page = new PageByPage<>(content, pageable, content != null && content.size() > pageable.getPageSize());
		return page;
	}
	
	private Pageable getPageablePlus(Pageable pageable) {
		PageRequest pageablePlus = new PageRequest(pageable.getPageNumber(), pageable.getPageSize()+1, pageable.getSort(), pageable.getPageMode());
		return pageablePlus;
	}
}
