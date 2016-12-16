package cn.newphy.data.domain;

import java.util.Map;

public interface Pageable {

	/**
	 * Returns page mode
	 * 
	 * @return
	 */
	PageMode getPageMode();

	/**
	 * Returns the page to be returned.
	 * 
	 * @return the page to be returned.
	 */
	int getPageNumber();

	/**
	 * Returns the number of items to be returned.
	 * 
	 * @return the number of items of that page
	 */
	int getPageSize();

	/**
	 * Returns the offset to be taken according to the underlying page and page
	 * size.
	 * 
	 * @return the offset to be taken
	 */
	int getOffset();

	/**
	 * Returns the sorting parameters.
	 * 
	 * @return
	 */
	Sort getSort();
	
	/**
	 * Set the sorting parameters
	 * @param sort
	 */
	void setSort(Sort sort);

	/**
	 * Returns the {@link Pageable} requesting the next {@link Page1}.
	 * 
	 * @return
	 */
	Pageable getNext();

	/**
	 * Returns the previous {@link Pageable} or the first {@link Pageable} if
	 * the current one already is the first one.
	 * 
	 * @return
	 */
	Pageable getPreviousOrFirst();

	/**
	 * Returns the {@link Pageable} requesting the first page.
	 * 
	 * @return
	 */
	Pageable getFirst();

	/**
	 * Returns whether there's a previous {@link Pageable} we can access from
	 * the current one. Will return {@literal false} in case the current
	 * {@link Pageable} already refers to the first page.
	 * 
	 * @return
	 */
	boolean isHasPrevious();

	/**
	 * 排序
	 * 
	 * @param property
	 * @param direction
	 */
	void orderBy(Direction direction, String property);

	/**
	 * 正序排列
	 * 
	 * @param property
	 */
	void orderAsc(String property);

	/**
	 * 倒序排列
	 * 
	 * @param property
	 */
	void orderDesc(String property);

	/**
	 * 增加查询参数
	 * 
	 * @param key
	 * @param value
	 */
	void addParameter(String key, Object value);

	/**
	 * 增加查询参数
	 * 
	 * @param map
	 */
	void addParameters(Map<String, ?> map);

	/**
	 * 获得查询参数
	 * 
	 * @return
	 */
	Map<String, Object> getParamMap();
}
