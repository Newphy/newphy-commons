package cn.newphy.commons.data.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cn.newphy.commons.data.generator.argument.ArgumentInlializer;
import cn.newphy.commons.data.generator.plan.PlanInitializer;

@Component
public class Initializer implements CommandLineRunner {

	@Autowired
	private ArgumentInlializer argumentInlializer;
	@Autowired
	private PlanInitializer planInitializer;
	
	public void init() {
		// 初始化系统参数
		argumentInlializer.init();
		// 初始化计划
		planInitializer.init();
	}

	@Override
	public void run(String... args) throws Exception {
		Config config = Config.getInstance();
		if(!config.isInitialized()) {
			init();
		}
	}
}
