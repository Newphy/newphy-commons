package cn.newphy.data.entitydao;


public abstract class EntityDaoFactory {
	
	/**
	 * 创建EntityDao
	 * 
	 * @param clazz
	 * @return
	 */
	public abstract <T> EntityDao<T> createEntityDao(Class<T> entityClass);
}
