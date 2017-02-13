package cn.newphy.data.entitydao.mybatis;

import java.io.Serializable;

public interface IdGenerator {

	/**
	 * 生成主键
	 * 
	 * @param globalConfig
	 * @param entity
	 * @return
	 */
	Serializable generate(GlobalConfig globalConfig, Object entity);
}
