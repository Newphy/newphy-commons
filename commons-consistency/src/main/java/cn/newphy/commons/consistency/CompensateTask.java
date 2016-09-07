package cn.newphy.commons.consistency;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

public class CompensateTask {
	private Logger logger = LoggerFactory.getLogger(CompensateTask.class);
	
	private static final int MAX_RETRY = 500;

	private ApplicationContext applicationContext;
	// 分布式锁
	private DistributedLock distributedLock;

	private ConsistencyHandler consistencyHandler;

	public CompensateTask(ApplicationContext applicationContext, ConsistencyHandler consistencyHandler, DistributedLock distributedLock) {
		this.applicationContext = applicationContext;
		this.consistencyHandler = consistencyHandler;
		this.distributedLock = distributedLock;
	}

	public void start(String cronExpression) {
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
		logger.info("~~~ start compensate ~~~");
		String token = distributedLock.lock(null);
		int total = 0;
		if (token != null) {
			logger.info("~~~ get distributed lock, token=[{}] ~~~", token);
			// 获取锁成功
			try {
				int count = 0;
				do {
					count = consistencyHandler.compensate(MAX_RETRY);
					total += count;
				} while (count > 0 && distributedLock.extend(token));
			} finally {
				distributedLock.unlock(token);
			}
		}
		logger.info("~~~ finish compensate, compensate count=[{}] ~~~", total);
	}
}
