package cn.newphy.commons.consistency.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.newphy.commons.consistency.ConfirmLevel;

@Target({ ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConsistencyApi {

	/**
	 * 分发路径
	 * 
	 * @return
	 */
	String path();

	/**
	 * 一致性级别
	 * 
	 * @return
	 */
	ConfirmLevel confirmLevel() default ConfirmLevel.DEFAULT;

	/**
	 * 重试间隔(秒)
	 * 
	 * @return
	 */
	int retryInterval() default 0;

}
