package cn.newphy.commons.data.generator.ds.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.newphy.commons.data.generator.BaseController;
import cn.newphy.commons.data.generator.ds.service.DsService;
import cn.newphy.commons.data.generator.model.ds.Ds;

@Controller
@RequestMapping("/ds")
public class DsController extends BaseController {
	
	@Autowired
	private DsService dsService;

	@RequestMapping("/dsHome")
	public String dsHome(String id, String opt, Map<String, Object> param) {
		menu("DS_HOME", param);
		if(id != null && id.length() > 0) {
			if(opt != null && opt.equals("remove")) {
				dsService.deleteDs(id);
			}
			else if(opt != null && opt.equals("edit")) {
				Ds ds = dsService.getDs(id);
				param.put("ds", ds);
			}
		}
		List<Ds> list = dsService.queryDs();
		param.put("dses", list);
		
		return "ds/ds_home";
	}
	
	@RequestMapping("/dsFrame")
	public String dsFrame(String dsId, Map<String, Object> param) {
		if(dsId != null && dsId.length() > 0) {
			Ds ds = dsService.getDs(dsId);
			param.put("ds", ds);
		}
		return "ds/ds_frame";
	}
	
	@RequestMapping("/saveDs")
	public ModelAndView saveDs(Ds ds) {
		if(ds.getId() == null || ds.getId().length() == 0) {
			dsService.addDs(ds);
		} else {
			dsService.editDs(ds);
		}
		return new ModelAndView("redirect:/ds/dsHome");
	}
	
	@RequestMapping("/getDriverClass")
	@ResponseBody
	public String getDriverClass(String url) {
		if(url == null) {
			return "";
		}
		String lurl = url.toLowerCase();
		if(lurl.contains("mysql")) {
			return "com.mysql.jdbc.Driver";
		}
		return "";
	}
	
	
	/**
	 * 测试数据源
	 * @param ds
	 * @return
	 */
	@RequestMapping("/checkDs")
	@ResponseBody
	public String checkDs(Ds ds) {
		boolean flag = dsService.checkDs(ds);
		String result = flag ? "Success" : "Failed";
		return result;
	}
}
