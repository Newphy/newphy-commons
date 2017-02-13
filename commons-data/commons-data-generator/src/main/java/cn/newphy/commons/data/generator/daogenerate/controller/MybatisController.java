package cn.newphy.commons.data.generator.daogenerate.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.newphy.commons.data.generator.BaseController;
import cn.newphy.commons.data.generator.daogenerate.service.MybatisService;
import cn.newphy.commons.data.generator.ds.service.DsService;
import cn.newphy.commons.data.generator.model.ds.Ds;
import cn.newphy.commons.data.generator.model.ds.Entity;
import cn.newphy.commons.data.generator.model.ds.Schema;
import cn.newphy.commons.data.generator.model.ds.Table;
import cn.newphy.commons.data.generator.model.plan.Plan;
import cn.newphy.commons.data.generator.plan.service.PlanService;

@Controller
@RequestMapping("/dao")
public class MybatisController extends BaseController {
	
	private static final String PLAN_GROUP_MYBATIS = "mybatis";
	
	@Autowired
	private MybatisService mybatisService;
	@Autowired
	private DsService dsService;
	@Autowired
	private PlanService planService;

	/**
	 * mapper生成首页
	 * @param param
	 * @return
	 */
	@RequestMapping("/mybatisHome")
	public String mybatisHome(String dsId, Map<String, Object> param) {
		menu("MYBATIS_HOME", param);
		List<Ds> list = dsService.queryDs();
		param.put("dses", list);
		
		if(dsId != null) {
			Ds ds = dsService.getDs(dsId);
			if(ds != null) {
				List<Schema> ss = dsService.getSchemas(ds);
				param.put("schemas", ss);
			}
		}
		return "mybatis/mybatis_home";
	}

	@RequestMapping("/getSchemas")
	@ResponseBody
	public List<Schema> getSchemas(String dsId) {
		if(dsId == null || dsId.equals("")) {
			return new ArrayList<Schema>();
		}
		Ds ds = dsService.getDs(dsId);
		List<Schema> ss = dsService.getSchemas(ds);
		return ss;
	}

	@RequestMapping("/tableListFrame")
	public String tableListFrame(String dsId, String schemaName, Map<String, Object> param) {
		Schema schema = dsService.getSchema(dsId, schemaName);
		if(schema != null) {
			List<Entity> entities = new ArrayList<Entity>();
			for (Table table : schema.getTables()) {
				entities.add(dsService.getEntity(table));
			}
			param.put("entities", entities);
		}
		return "mybatis/table_list_frame";
	}
	
	@RequestMapping("/planListFrame")
	public String planListFrame(Plan plan, String opt, Map<String, Object> param) {
		if(opt != null) {
			if(opt.equals("add")) {
				planService.addPlan(plan);
			}
			else if(opt.equals("delete")) {
				planService.deletePlan(plan.getId());
			}
			else if(opt.equals("update")) {
				planService.updatePlan(plan);
			}
		}
		// 模板列表
		List<Plan> plans = planService.queryPlanByGroup(PLAN_GROUP_MYBATIS);
		param.put("plans", plans);
		return "mybatis/plan_list_frame";
	}
	
	@RequestMapping("/planFrame")
	public String planFrame(String planId, Map<String, Object> param) {
		if(planId != null && planId.length() > 0) {
			Plan plan = planService.getPlan(planId);
			param.put("plan", plan);
		}
		return "mybatis/plan_frame";
	}
	
	@RequestMapping("/planPreviewFrame")
	public String planPreviewFrame(String planId, String dsId, String schemaName, String tableName, 
			String[] columnNames, Map<String, Object> param) {
		Plan plan = planService.getPlan(planId);
		List<String> columnList = new ArrayList<String>();
		if(columnNames != null) {
			columnList = Arrays.asList(columnNames);
		}
		String content = mybatisService.generateMapping(plan, dsId, schemaName, tableName, columnList);
		param.put("content", content);
		return "mybatis/plan_preview_frame";
	}
}
