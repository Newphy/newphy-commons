package cn.newphy.commons.data.generator.daogenerate.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.newphy.commons.data.generator.argument.service.ArgService;
import cn.newphy.commons.data.generator.ds.service.DsService;
import cn.newphy.commons.data.generator.model.arg.Argument;
import cn.newphy.commons.data.generator.model.ds.Column;
import cn.newphy.commons.data.generator.model.ds.Table;
import cn.newphy.commons.data.generator.model.plan.Plan;
import cn.newphy.commons.data.generator.plan.Generator;
import cn.newphy.commons.data.generator.plan.service.PlanService;

@Service
public class MybatisService {
	
	@Autowired
	private DsService dsService;
	@Autowired
	private PlanService planService;
	@Autowired
	private ArgService argService;
	@Autowired
	private Generator generator;

	/**
	 * 生成Mapping参数
	 * @param templateId
	 * @param dsId
	 * @param schemaName
	 * @param tableName
	 * @param columnNames
	 * @return
	 */
	public String generateMapping(Plan plan, String dsId, String schemaName, String tableName, List<String> columnNames) {
		Table table = dsService.getTable(dsId, schemaName, tableName);
		if(table == null) {
			throw new IllegalStateException("找不到数据表[" + schemaName + "." + tableName + "]");
		}
		// 过滤字段
		if(columnNames != null && columnNames.size() > 0 && columnNames.size() < table.getColumns().size()) {
			List<Column> columns = new ArrayList<>();
			for (Column column : table.getColumns()) {
				if(columnNames.contains(column.getName())) {
					columns.add(column);
				}
			}
			table.setColumns(columns);
		}
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("table", table);
		List<Argument> args = argService.queryArgument();
		for (Argument argument : args) {
			param.put(argument.getName(), argument.getValue());
		}
		String content = generator.generateByString(plan.getContent(), param);
		return content;
	}

}
