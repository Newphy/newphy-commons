package cn.newphy.data.entitydao.mybatis.util;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.newphy.data.entitydao.mybatis.EntityMapping;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkUtils {
	private static final Logger log = LoggerFactory.getLogger(FreemarkUtils.class);

	private static Configuration configuration;
	private static Template template;

	static {
		try {
			configuration = new Configuration(Configuration.VERSION_2_3_22);
			ClassTemplateLoader loader = new ClassTemplateLoader(new FreemarkUtils().getClass(),
					"/cn/newphy/data/entitydao/mybatis/template");
			configuration.setTemplateLoader(loader);
			configuration.setDefaultEncoding("UTF-8");
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		} catch (Exception e) {
			log.error("", e);
		}
	}

	public static String parse(String name, EntityMapping entityMapping) {
		try {
			StringWriter writer = new StringWriter();
			template = configuration.getTemplate(name + ".ftl");
			template.process(entityMapping, writer);
			writer.flush();
			writer.close();
			return writer.toString();
		} catch (Exception e) {
			log.error("解析模板文件出错", e);
			throw new IllegalStateException("解析模板文件出错");
		}
	}

}