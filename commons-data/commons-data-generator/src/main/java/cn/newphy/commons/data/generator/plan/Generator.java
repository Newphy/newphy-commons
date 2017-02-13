package cn.newphy.commons.data.generator.plan;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import cn.newphy.commons.data.generator.utils.FileUtils;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;


@Component
public class Generator {

	private static Configuration configuration;
	private static StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();

	static {
		try {
			configuration = new Configuration(Configuration.VERSION_2_3_22);
			ClassTemplateLoader fixedTemplateLoader = new ClassTemplateLoader(Generator.class.getClassLoader(),
					"/cn/newphy/data/template/ftl");
			
			String workPath = FileUtils.getWorkPath();
			File templateDir = new File(workPath, PlanConst.TEMPLATE_PATH);
			if(!templateDir.exists()) {
				templateDir.mkdirs();
			}
			FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(templateDir);
			MultiTemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[]{fixedTemplateLoader, fileTemplateLoader, stringTemplateLoader});
			configuration.setTemplateLoader(mtl);
			configuration.setDefaultEncoding("UTF-8");
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		} catch (Exception e) {
		}
	}
	

	/**
	 * 生成文件内容
	 * @param templateId
	 * @param paramMap
	 */
	public String generate(String templateName, Map<String, Object> paramMap) {
		try {
			StringWriter writer = new StringWriter();
			Template template = configuration.getTemplate(templateName);
			template.process(paramMap, writer);
			writer.flush();
			writer.close();
			return writer.toString();
		} catch (Exception e) {
			throw new IllegalStateException("解析模板文件出错");
		}
	}

	public String generateByString(String templateContent, Map<String, Object> paramMap) {
		String uuid = UUID.randomUUID().toString();
		String name = uuid + ".ftl";
		try {
			stringTemplateLoader.putTemplate(name, templateContent);
			return generate(name, paramMap);
		} finally {
			stringTemplateLoader.removeTemplate(name);
		}
	}
}
