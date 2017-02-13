package cn.newphy.data.entitydao.mybatis;

import cn.newphy.data.entitydao.mybatis.util.CamelCaseUtils;

public interface TableNameStrategy {

	/**
	 * 获得表名
	 * @param globalConfig
	 * @param entityClass
	 * @return
	 */
	String getTableName(GlobalConfig globalConfig, Class<?> entityClass);
	
	
	public static class CamelCaseTableNameStrategy implements TableNameStrategy {

		@Override
		public String getTableName(GlobalConfig globalConfig, Class<?> entityClass) {
			String className = entityClass.getSimpleName();
			String tableName = CamelCaseUtils.camelCase2Underline(className);
			return tableName;
		}

	}
	
	public static class PrefixCamelCaseTableNameStrategy extends CamelCaseTableNameStrategy {
		
		private String prefix = "";
		
		@Override
		public String getTableName(GlobalConfig globalConfig, Class<?> entityClass) {
			return getPrefix() + super.getTableName(globalConfig, entityClass);
		}

		/**
		 * @return the prefix
		 */
		public String getPrefix() {
			return prefix;
		}

		/**
		 * @param prefix the prefix to set
		 */
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

	}
}
