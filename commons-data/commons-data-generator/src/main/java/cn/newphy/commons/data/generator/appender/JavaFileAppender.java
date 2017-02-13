package cn.newphy.commons.data.generator.appender;

import java.io.File;

import cn.newphy.commons.data.generator.config.AppenderConfig;
import cn.newphy.commons.data.generator.model.FileVo;
import cn.newphy.commons.data.generator.model.JavaVo;
import cn.newphy.commons.data.generator.utils.StringUtils;

public class JavaFileAppender implements BaseAppender{

	@Override
	public FileVo doInvoke(AppenderConfig appender) {
		JavaVo javaVo = new JavaVo();
		String fullName = appender.getName();
		String shortName = fullName.substring(fullName.lastIndexOf(".")+1);
		String strPackage = fullName.substring(0,fullName.lastIndexOf("."));
		String aliasName = StringUtils.lowerCaseFirstChar(shortName);
		String packagePath = strPackage.replace(".",File.separator);
		
		javaVo.setFileName(shortName+".java");
		javaVo.setFilePath(appender.getPath()+File.separator+packagePath);
		javaVo.setTemplateFile(appender.getTemplate());
		
		javaVo.setAliasName(aliasName);
		javaVo.setFullName(fullName);
		javaVo.setShortName(shortName);
		javaVo.setStrPackage(strPackage);
		return javaVo;
	}

}
