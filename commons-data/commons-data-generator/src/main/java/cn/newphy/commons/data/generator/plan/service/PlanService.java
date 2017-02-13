package cn.newphy.commons.data.generator.plan.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.newphy.commons.data.generator.model.plan.Plan;
import cn.newphy.commons.data.generator.plan.PlanConst;
import cn.newphy.commons.data.generator.plan.dao.PlanDao;
import cn.newphy.commons.data.generator.utils.FileUtils;

@Service
public class PlanService {
	private Logger log = LoggerFactory.getLogger(PlanService.class);
	
	@Autowired
	private PlanDao planDao;
	
	public List<Plan> queryPlanByGroup(String group) {
		List<Plan> plans = planDao.query();
		List<Plan> groupPlans = new ArrayList<Plan>();
		if(plans != null && plans.size() > 0) {
			for (Plan plan : plans) {
				if(plan.getGroup().equals(group)) {
					groupPlans.add(plan);
				}
			}
		}
		return groupPlans;
	}
	
	public void addPlan(Plan plan) {
		planDao.save(plan);
		String templateFileName = plan.getId() + ".ftl";
		File templateFile = getTemplateFile(templateFileName);
		String content = plan.getContent();
		try {
			FileUtils.writeContent(templateFile, content);
		} catch (IOException e) {
			planDao.delete(plan.getId());
			String msg = "生成模板文件出错";
			log.error(msg, e);
			throw new IllegalStateException(msg);
		}
		plan.setFile(templateFileName);
		planDao.update(plan);
	}
	
	
	public void deletePlan(String planId) {
		Plan plan = getPlan(planId);
		if(plan != null) {
			File templateFile = getTemplateFile(plan.getFile());
			if(templateFile != null && templateFile.exists()) {
				templateFile.delete();
			}
		}
		planDao.delete(planId);
	}

	
	public Plan getPlan(String planId) {
		Plan plan = planDao.get(planId);
		File file = getTemplateFile(plan.getFile());
		if(file != null) {
			try {
				String content = FileUtils.readContent(file);
				plan.setContent(content);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return plan;
	}
	
	public void updatePlan(Plan plan) {
		String templateFileName = plan.getId() + ".ftl";
		File templateFile = getTemplateFile(templateFileName);
		String content = plan.getContent();
		try {
			if(content == null) {
				content = "";
			}
			FileUtils.writeContent(templateFile, content);
		} catch (IOException e) {
			planDao.delete(plan.getId());
			String msg = "生成模板文件出错";
			log.error(msg, e);
			throw new IllegalStateException(msg);
		}
		plan.setFile(templateFileName);
		planDao.update(plan);
		
	}
	
	
	private File getTemplateFile(String templateFileName) {
		if(templateFileName == null || templateFileName.length() == 0) {
			return  null;
		}
		File templateDir = new File(FileUtils.getWorkPath(), PlanConst.TEMPLATE_PATH);
		File template = new File(templateDir, templateFileName);
		return template;
	}
}
