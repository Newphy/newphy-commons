package cn.newphy.data.hibernate;


public abstract class EntityDaoFactory {
	
	
	public static <T> EntityDao<T> create(Class<T> entityClass) {
		return null;
	}
	

	/**
	 * 创建EntityDao
	 * 
	 * @param clazz
	 * @return
	 */
	public abstract <T> EntityDao<T> createEntityDao(Class<T> entityClass);
}
