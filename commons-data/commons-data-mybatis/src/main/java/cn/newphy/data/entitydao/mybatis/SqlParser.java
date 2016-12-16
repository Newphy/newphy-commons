package cn.newphy.data.entitydao.mybatis;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;

public class SqlParser {
	
	private static void countSql() {
		String sql = "select * from demo_user where email = (select email from demo_user where gender = 1 order by id asc LIMIT 1) order by user_name desc, age asc limit #{offset}, #{limit}";
		// 使用mysql解析
		MySqlStatementParser sqlStatementParser = new MySqlStatementParser(sql);
		// 解析select查询
		SQLSelectStatement sqlStatement = sqlStatementParser.parseSelect();
		SQLSelect select = sqlStatement.getSelect() ;
		// //获取sql查询块

		MySqlSelectQueryBlock selectQuery = (MySqlSelectQueryBlock)select.getQuery();
		// 增加count(1)
		SQLAggregateExpr countExpr = new SQLAggregateExpr("COUNT");
		countExpr.getArguments().add(new SQLIntegerExpr(1) );
		List<SQLSelectItem> selectList = selectQuery.getSelectList();
		selectList.clear();
		selectList.add(new SQLSelectItem(countExpr));
		
		// 删除limit
		selectQuery.setLimit(null);
		selectQuery.setOrderBy(null);
		
		StringBuffer out = new StringBuffer();
		// 创建sql解析的标准化输出
		SQLASTOutputVisitor sqlastOutputVisitor = new MySqlOutputVisitor(out);
		sqlastOutputVisitor.setPrettyFormat(false);
		sqlStatement.accept(sqlastOutputVisitor);
		System.out.println("----"  + out);
	}

	private static void replaceOrderBy() {
		String sql = "select count(1) from demo_user where email = (select email from demo_user where gender = 1 order by id asc LIMIT 1) order by user_name desc, age asc limit #{offset}, #{limit}";
		// 使用mysql解析
		MySqlStatementParser sqlStatementParser = new MySqlStatementParser(sql);
		// 解析select查询
		SQLSelectStatement sqlStatement = sqlStatementParser.parseSelect();
		// SQLSelect sqlSelect = sqlStatement.getSelect() ;
		// //获取sql查询块
		// SQLSelectQueryBlock sqlSelectQuery =
		// (SQLSelectQueryBlock)sqlSelect.getQuery() ;

		StringBuffer out = new StringBuffer();
		// 创建sql解析的标准化输出
		SQLASTOutputVisitor sqlastOutputVisitor = new MySqlOutputVisitor(out) {
			@Override
			public boolean visit(SQLOrderBy x) {
				SQLObject selectQuery = x.getParent();
				SQLObject select = selectQuery.getParent();
				if (select != null && select.getParent() instanceof SQLSelectStatement) {
					print("");
				} else {
					super.visit(x);
				}
				return false;
			}
		};
		sqlastOutputVisitor.setPrettyFormat(false);
		sqlStatement.accept(sqlastOutputVisitor);
		System.out.println(out);
	}

	private static void replaceOrderBy1() {
		String sql = "select * from demo_user where email = (select email from demo_user where gender = 1 order by id asc LIMIT 1) order by user_name desc, age asc";
		// 使用mysql解析
		MySqlStatementParser sqlStatementParser = new MySqlStatementParser(sql);
		// 解析select查询
		SQLSelectStatement sqlStatement = sqlStatementParser.parseSelect();
		// SQLSelect sqlSelect = sqlStatement.getSelect() ;
		// //获取sql查询块
		// SQLSelectQueryBlock sqlSelectQuery =
		// (SQLSelectQueryBlock)sqlSelect.getQuery() ;

		StringBuffer out = new StringBuffer();
		// 创建sql解析的标准化输出
		SQLASTOutputVisitor sqlastOutputVisitor = new MySqlOutputVisitor(out) {
			@Override
			public boolean visit(SQLOrderBy x) {
				super.visit(x);
				return false;
			}
		};
		sqlastOutputVisitor.setPrettyFormat(false);
		sqlStatement.accept(sqlastOutputVisitor);
		System.out.println(out);
	}

	public static void main(String[] args) {
		long t = System.currentTimeMillis();
		countSql();
		System.out.println(System.currentTimeMillis() - t);

		long t2 = System.currentTimeMillis();
		countSql();
		System.out.println(System.currentTimeMillis() - t2);
	}

	public static void test(String[] args) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT count(1)  as cc FROM (");
		sql.append("	SELECT DISTINCT k.contact_mobile FROM");
		sql.append("		(");
		sql.append("			(");
		sql.append("				SELECT");
		sql.append("					t.contact_mobile,");
		sql.append("					t.user_id");
		sql.append("				FROM");
		sql.append("					t_call_record t");
		sql.append("				WHERE");
		sql.append("					t.user_id IN ('1', '2', '3') 						");
		sql.append("				AND t.call_begin_time >= sysdate()");
		sql.append("			)");
		sql.append("			UNION");
		sql.append("				(");
		sql.append("					SELECT");
		sql.append("						t.contact_mobile,");
		sql.append("						t.user_id");
		sql.append("					FROM");
		sql.append("						t_contacter t");
		sql.append("					WHERE");
		sql.append("						t.user_id IN('1', '2', '3') ");
		sql.append("				)");
		sql.append("		) k,");
		sql.append("		t_loan_apply_info a");
		sql.append("	WHERE");
		sql.append("		a.mobile = k.contact_mobile");
		sql.append("	AND a.create_time >= sysdate()");
		sql.append(") KK");

		String sql2 = "SELECT count(1) FROM t_loan_apply_info t WHERE t.status = 14 AND t.user_id = 1";

		// 解析SQL
		Statement stmt = null;
		try {
			stmt = CCJSqlParserUtil.parse(sql2);

			stmt = CCJSqlParserUtil.parse(sql.toString());
			long time1 = System.currentTimeMillis();
			stmt = CCJSqlParserUtil.parse(sql.toString());
			System.out.println(System.currentTimeMillis() - time1);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		Select select = (Select) stmt;
		SelectBody selectBody = select.getSelectBody();
		// //处理body-去order by
		// processSelectBody(selectBody);
		// //处理with-去order by
		// processWithItemsList(select.getWithItemsList());
		// //处理为count查询
		// sqlToCount(select);
		// String result = select.toString();
		// return result;
		System.out.println(selectBody);

		MySqlStatementParser parser = new MySqlStatementParser(sql.toString());
		SQLStatement statement = parser.parseStatement();
		long t3 = System.currentTimeMillis();

		parser = new MySqlStatementParser(sql.toString());
		statement = parser.parseStatement();
		System.out.println(System.currentTimeMillis() - t3);
		System.out.println(statement);
	}
}
