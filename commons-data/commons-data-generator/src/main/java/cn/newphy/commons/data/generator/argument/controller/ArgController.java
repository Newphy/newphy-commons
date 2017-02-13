package cn.newphy.commons.data.generator.argument.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cn.newphy.commons.data.generator.BaseController;
import cn.newphy.commons.data.generator.CRUDDao.ExistException;
import cn.newphy.commons.data.generator.argument.service.ArgService;
import cn.newphy.commons.data.generator.model.arg.Argument;

@Controller
@RequestMapping("/arg")
public class ArgController extends BaseController {
	
	@Autowired
	private ArgService argService;

	@RequestMapping("/argHome")
	public String argHome(String id, String opt, Map<String, Object> param) {
		menu("ARG_HOME", param);
		if(id != null && id.length() > 0) {
			if(opt != null && opt.equals("remove")) {
				argService.deleteArgument(id);
			}
		}
		List<Argument> list = argService.queryArgument();
		param.put("args", list);
		return "arg/arg_home";
	}
	
	@RequestMapping("/saveArg")
	public ModelAndView saveArg(Argument arg, RedirectAttributes redirectAttributes) {
		if(arg.getId() == null || arg.getId().length() == 0) {
			try {
				argService.addArgument(arg);
			} catch (ExistException e) {
				redirectError("参数名不能重复.", redirectAttributes);
			}
		} else {
			argService.editArgument(arg);
		}
		return new ModelAndView("redirect:/arg/argHome");
	}
	
	@RequestMapping("/argFrame")
	public String argFrame(String id, Map<String, Object> param) {
		if(id != null && id.length() > 0) {
			Argument arg = argService.getArgument(id);
			param.put("arg", arg);
		}
		return "arg/arg_frame";
	}
	

}
