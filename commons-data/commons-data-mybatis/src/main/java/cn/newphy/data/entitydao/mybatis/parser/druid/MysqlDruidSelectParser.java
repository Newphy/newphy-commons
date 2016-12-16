package cn.newphy.data.entitydao.mybatis.parser.druid;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import cn.newphy.data.domain.Direction;
import cn.newphy.data.domain.Order;
import cn.newphy.data.domain.Pageable;
import cn.newphy.data.entitydao.DialectType;
import cn.newphy.data.entitydao.mybatis.parser.SelectParser;

public class MysqlDruidSelectParser extends SelectParser {
	
	private static final String COUNT = "count";

	public MysqlDruidSelectParser(String sql) {
		super(sql);
	}
	
	@Override
	public DialectType getType() {
		return DialectType.MYSQL;
	}


	@Override
	public String getPageSql(Pageable pageable, String offsetPlaceholder, String limitPlaceholder) {
        MySqlStatementParser sqlStatementParser = new MySqlStatementParser(sql) ;  
        SQLSelectStatement statement = sqlStatementParser.parseSelect();
		
		StringBuffer pageSql = new StringBuffer();
		SQLSelect select = statement.getSelect();
		MySqlSelectQueryBlock selectQuery = (MySqlSelectQueryBlock)select.getQuery();

		// order by
		if(pageable.getSort() != null) {
			selectQuery.setOrderBy(null);
			SQLOrderBy orderBy = new SQLOrderBy();
			for (Order order : pageable.getSort().getOrders()) {
				SQLSelectOrderByItem orderByItem = new SQLSelectOrderByItem();
				orderByItem.setExpr(new SQLIdentifierExpr(order.getColumn()));
				orderByItem.setType(order.getDirection() == Direction.ASC ? SQLOrderingSpecification.ASC : SQLOrderingSpecification.DESC);
				orderBy.addItem(orderByItem);
			}
			orderBy.setParent(select);
			selectQuery.setOrderBy(orderBy);
		}
		
		// limit
		Limit limit = new Limit();
		limit.setOffset(new SQLVariantRefExpr(offsetPlaceholder));
		limit.setRowCount(new SQLVariantRefExpr(limitPlaceholder));
		selectQuery.setLimit(limit);
		
		// 创建sql解析的标准化输出
		SQLASTOutputVisitor sqlastOutputVisitor = new MySqlOutputVisitor(pageSql);
		sqlastOutputVisitor.setPrettyFormat(false);
		statement.accept(sqlastOutputVisitor);
		
		return pageSql.toString();
	}


	@Override
	public String getCountSql() {
		MySqlStatementParser sqlStatementParser = new MySqlStatementParser(sql);
		// 解析select查询
		SQLSelectStatement sqlStatement = sqlStatementParser.parseSelect();
		SQLSelect select = sqlStatement.getSelect() ;
		// //获取sql查询块

		MySqlSelectQueryBlock selectQuery = (MySqlSelectQueryBlock)select.getQuery();
		// 增加count(1)
		SQLAggregateExpr countExpr = new SQLAggregateExpr(COUNT);
		countExpr.getArguments().add(new SQLIntegerExpr(1) );
		List<SQLSelectItem> selectList = selectQuery.getSelectList();
		selectList.clear();
		selectList.add(new SQLSelectItem(countExpr));
		
		// 删除limit
		selectQuery.setLimit(null);
		selectQuery.setOrderBy(null);
		
		StringBuffer countSql = new StringBuffer();
		// 创建sql解析的标准化输出
		SQLASTOutputVisitor sqlastOutputVisitor = new MySqlOutputVisitor(countSql);
		sqlastOutputVisitor.setPrettyFormat(false);
		sqlStatement.accept(sqlastOutputVisitor);

		return countSql.toString();
	}

	
	
}
