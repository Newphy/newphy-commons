package cn.newphy.commons.data.generator.demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.newphy.commons.data.generator.BaseController;
import cn.newphy.commons.data.generator.plan.Generator;

@Controller
@RequestMapping("/demo")
public class DemoController extends BaseController {

	@Autowired
	private Generator generator;
	
	@RequestMapping("/demoHome")
	public String demoHome(Map<String, Object> param) {
		menu("DS_HOME", param);
		return "demo/demo_home";
	}
	
	@RequestMapping("/preview")
	public String preview(String content, Map<String, Object> param) {
		System.out.println(content);
		
		Map<String, Object> templateParam = new HashMap<>();
		templateParam.put("owner", "张飞");
		templateParam.put("param_1", "12131312412321321321");
		templateParam.put("user_sign", "张飞");
		templateParam.put("owner_id", "牛飞");
		templateParam.put("year", "2017");
		templateParam.put("month", "01");
		templateParam.put("day", "12");
		String html = generator.generateByString(content, templateParam);
		param.put("content", html);
		return "demo/demo_preview";
	}
}
