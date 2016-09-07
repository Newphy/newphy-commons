package cn.newphy.commons.consistency;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;

import cn.newphy.commons.consistency.dao.ConsistencyDAO;
import cn.newphy.commons.consistency.dao.ConsistencyDAOImpl;

public abstract class ConsistencyHandlerSupport implements ConsistencyHandler, InitializingBean,
		ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

	protected ApplicationContext applicationContext;
	/**
	 * 数据源
	 */
	protected DataSource dataSource;
	/**
	 * 源地址
	 */
	protected String source;
	
	/**
	 * 补偿Cron表达式
	 */
	protected String cronExpression = "*/10 * * * * ? ";

	/**
	 * 分布式锁
	 */
	protected DistributedLock distributedLock;

	/**
	 * 一致性信息DAO
	 */
	protected ConsistencyDAO consistencyDAO;


	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.dataSource, "一致性组件未设置数据源");
		Assert.hasText(this.source, "一致性组件未设置源地址");
		
		this.source = "consistency[" + this.source + "]";
		this.consistencyDAO = new ConsistencyDAOImpl(dataSource);
		// 分布式锁实现
		if (distributedLock == null) {
			distributedLock = new DBDistributedLock(dataSource);
		}
	}
	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// 开始补偿任务
		CompensateTask compensateTask = new CompensateTask(applicationContext, this, distributedLock);
		compensateTask.start(cronExpression);
	}
	

	protected String getSource() {
		return source;
	}


	@Override
	public void setSource(String source) {
		this.source = source;
	}


	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @param cronExpression
	 *            the cronExpression to set
	 */
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	/**
	 * @param distributedLock
	 *            the distributedLock to set
	 */
	public void setDistributedLock(DistributedLock distributedLock) {
		this.distributedLock = distributedLock;
	}

	
}
