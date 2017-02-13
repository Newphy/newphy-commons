package cn.newphy.commons.data.generator.appender;

import cn.newphy.commons.data.generator.config.AppenderConfig;
import cn.newphy.commons.data.generator.model.FileVo;

public interface BaseAppender {
	
	public abstract FileVo doInvoke(AppenderConfig appender);
}
