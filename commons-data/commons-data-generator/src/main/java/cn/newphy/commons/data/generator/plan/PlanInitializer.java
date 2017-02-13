package cn.newphy.commons.data.generator.plan;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.newphy.commons.data.generator.model.plan.Plan;
import cn.newphy.commons.data.generator.plan.service.PlanService;
import cn.newphy.commons.data.generator.utils.FileUtils;

@Component
public class PlanInitializer {
	private Logger log = LoggerFactory.getLogger(PlanInitializer.class);

	@Autowired
	private PlanService planService;

	public void init() {
		Plan mappingPlan = getMappingPlan();
		if(mappingPlan != null) {
			planService.addPlan(mappingPlan);
		}
	}
	
	
	private Plan getMappingPlan() {
		String content = getTemplateContent("/mybatis/template_mappingl.ftl");
		if(content == null || content.length() == 0) {
			return null;
		}
		Plan plan = new Plan();
		plan.setName("Mapping生成计划");
		plan.setGroup("mybatis");
		plan.setContent(content);
		return plan;
	}
	
	
	private String getTemplateContent(String path) {
		String workPath = FileUtils.getWorkPath();
		File templateDir = new File(workPath, PlanConst.TEMPLATE_PATH);
		File template = new File(templateDir, path);
		if(template.exists()) {
			try {
				return FileUtils.readContent(template);
			} catch (IOException e) {
				log.error("读取模板文件出错", e);
				return null;
			}
		}
		return null;
	}
}
