package cn.newphy.commons.data.generator.appender;

import cn.newphy.commons.data.generator.config.AppenderConfig;
import cn.newphy.commons.data.generator.model.FileVo;

public class FileAppender implements BaseAppender{

	@Override
	public FileVo doInvoke(AppenderConfig appender) {
		FileVo fileVo = new FileVo();
		fileVo.setFileName(appender.getFileName());
		fileVo.setFilePath(appender.getPath());
		fileVo.setTemplateFile(appender.getTemplate());
		return fileVo;
	}
	
}
