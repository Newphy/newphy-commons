package cn.newphy.commons.consistency.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.newphy.commons.consistency.ConfirmLevel;

@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Consistency {
	/**
	 * 分发queue名称
	 * 
	 * @return
	 */
	String value();

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
	int retryInterval() default 60;

}
