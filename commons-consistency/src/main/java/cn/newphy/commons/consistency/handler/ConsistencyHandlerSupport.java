package cn.newphy.commons.consistency.handler;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import cn.newphy.commons.consistency.dao.ConsistencyDao;
import cn.newphy.commons.consistency.dao.ConsistencyDaoImpl;
import cn.newphy.commons.consistency.support.IDFetcher;

public abstract class ConsistencyHandlerSupport implements ConsistencyHandler, ApplicationContextAware, InitializingBean {

	protected ApplicationContext applicationContext;
	/**
	 * 数据源
	 */
	protected DataSource dataSource;

	/**
	 * 一致性信息DAO
	 */
	protected ConsistencyDao consistencyDao;

	/**
	 * 一致性补偿任务
	 */
	protected ConsistencyCompensateTask compensateTask;

	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.dataSource, "一致性组件未设置数据源");
		this.consistencyDao = new ConsistencyDaoImpl(this.dataSource);
		if(compensateTask == null) {
			compensateTask = new SchedulerCompensateTask(this, applicationContext, dataSource);
		}
		// 开始补偿任务
		compensateTask.start();
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	
	protected class IdConsistencyObject implements ConsistencyObject {
		private Object obj;
		
		public IdConsistencyObject(Object obj) {
			this.obj = obj;
		}

		@Override
		public String getObjectId() {
			return IDFetcher.getId(obj);
		}

		@Override
		public Object getObject() {
			return obj;
		}
		
	}

}
