package cn.newphy.commons.data.generator;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

public class SiteMeshFilter extends ConfigurableSiteMeshFilter {

	@Override
	protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
		builder.addDecoratorPath("/ds/dsHome", "/WEB-INF/jsp/sitemesh.jsp")
				.addDecoratorPath("/arg/argHome", "/WEB-INF/jsp/sitemesh.jsp")
				.addDecoratorPath("/dao/mybatisHome", "/WEB-INF/jsp/sitemesh.jsp")
				.addDecoratorPath("/demo/demoHome", "/WEB-INF/jsp/sitemesh.jsp")				
				.addExcludedPath("/css/*")
				.addExcludedPath("/fonts/*")
				.addExcludedPath("/images/*")
				.addExcludedPath("/js/*");
	}
}
