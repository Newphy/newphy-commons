package cn.newphy.commons.data.generator;

import java.util.Map;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cn.newphy.commons.data.generator.menu.Menu;
import cn.newphy.commons.data.generator.menu.MenuFactory;
import cn.newphy.commons.data.generator.menu.MenuList;

public class BaseController {

	private static final String ALERT_KEY = "alert";
	
	protected void menu(String code, Map<String, Object> modelMap) {
		MenuList menuList = MenuFactory.getMenuList();
		modelMap.put("menuList", menuList);
		Menu menu = MenuFactory.getMenu(code);
		modelMap.put("activeMenu", menu);
	}
	
	
	protected void info(String msg, Map<String, Object> modelMap) {
		addAlert("info", msg, modelMap);
	}
	
	protected void success(String msg, Map<String, Object> modelMap) {
		addAlert("success", msg, modelMap);
	}
	
	protected void warn(String msg, Map<String, Object> modelMap) {
		addAlert("warn", msg, modelMap);
	}
	
	protected void error(String msg, Map<String, Object> modelMap) {
		addAlert("error", msg, modelMap);
	}
	
	private void addAlert(String type, String msg, Map<String, Object> modelMap) {
		Alert alert = new Alert();
		alert.setMsg(msg);
		alert.setType(type);
		modelMap.put(ALERT_KEY, alert);
	}
	
	
	protected void redirectInfo(String msg, RedirectAttributes redirectAttributes) {
		addRedirectAlert("info", msg, redirectAttributes);
	}
	
	protected void redirectSuccess(String msg, RedirectAttributes redirectAttributes) {
		addRedirectAlert("success", msg, redirectAttributes);
	}
	
	protected void redirectWarn(String msg, RedirectAttributes redirectAttributes) {
		addRedirectAlert("warn", msg, redirectAttributes);
	}
	
	protected void redirectError(String msg, RedirectAttributes redirectAttributes) {
		addRedirectAlert("error", msg, redirectAttributes);
	}
	
	private void addRedirectAlert(String type, String msg, RedirectAttributes redirectAttributes) {
		Alert alert = new Alert();
		alert.setMsg(msg);
		alert.setType(type);
		redirectAttributes.addFlashAttribute(ALERT_KEY, alert);
	}

}
