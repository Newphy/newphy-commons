package cn.newphy.commons.consistency.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import cn.newphy.commons.consistency.support.lock.DBDistributedLock;
import cn.newphy.commons.consistency.support.lock.DistributedLock;

/**
 * 一致性补偿任务
 * @author xn044401
 *
 */
public class SchedulerCompensateTask implements ConsistencyCompensateTask {
	private Logger logger = LoggerFactory.getLogger(ConsistencyCompensateTask.class);
	
	private static final int MAX_RETRY = 500;
	
	private static final String LOCK_MODULE = "CONSISTENCY";

	private ApplicationContext applicationContext;
	// 分布式锁
	private DistributedLock distributedLock;

	private ConsistencyHandler consistencyHandler;
	
	private String cronExpression = "*/10 * * * * ? ";
	
	
	public SchedulerCompensateTask(ConsistencyHandler consistencyHandler, ApplicationContext applicationContext, DataSource dataSource) {
		this.applicationContext = applicationContext;
		this.consistencyHandler = consistencyHandler;
		if(dataSource == null) {
			throw new IllegalArgumentException("dataSource为空");
		}
		distributedLock = new DBDistributedLock(dataSource);
	}


	@Override
	public void start() {
		logger.info("~~~ start compenstate task ~~~");
		ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();
		registrar.addCronTask(new CronTask(new Runnable() {
			@Override
			public void run() {
				execute();
			}
		}, cronExpression));

		Map<String, SchedulingConfigurer> configurers = applicationContext.getBeansOfType(SchedulingConfigurer.class);
		for (SchedulingConfigurer configurer : configurers.values()) {
			configurer.configureTasks(registrar);
		}
		Map<String, ? super Object> schedulers = new HashMap<String, Object>();
		schedulers.putAll(applicationContext.getBeansOfType(TaskScheduler.class));
		schedulers.putAll(applicationContext.getBeansOfType(ScheduledExecutorService.class));
		if(schedulers.size() > 0) {
			registrar.setScheduler(schedulers.values().iterator().next());			
		}
		registrar.afterPropertiesSet();
	}

	private void execute() {
		logger.debug("~~~ start compensate ~~~");
		String token = distributedLock.lock(LOCK_MODULE);
		int total = 0;
		if (token != null) {
			logger.info("~~~ consistency compensate, get distributed lock, token=[{}] ~~~", token);
			// 获取锁成功
			try {
				int count = 0;
				do {
					total += consistencyHandler.compensate(MAX_RETRY);
				} while (count > 0 && distributedLock.extend(token));
			} finally {
				distributedLock.unlock(token);
			}
			logger.info("~~~ consistency compensate, compensate count=[{}] ~~~", total);
		}
		logger.debug("~~~ finish compensate, compensate count=[{}] ~~~", total);
	}

}
