package cn.newphy.data.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;

/**
 * A page is a sublist of a list of objects. It allows gain information about
 * the position of it in the containing entire list.
 * 
 * @param <T>
 */
public interface Page<T> extends List<T>, Serializable {
	
	/**
	 * 获得分页模式
	 * @return
	 */
	PageMode getPageMode();
	
	/**
	 * Returns the number of total pages.
	 * 
	 * @return the number of total pages
	 */
	int getTotalPages();

	/**
	 * Returns the total amount of elements.
	 * 
	 * @return the total amount of elements
	 */
	long getTotalElements();

	/**
	 * Returns the number of the current page. Is always non-negative.
	 * 
	 * @return the number of the current page.
	 */
	int getPageNumber();

	/**
	 * Returns the size of the page.
	 * 
	 * @return the size of the page.
	 */
	int getPageSize();

	/**
	 * Returns the number of elements currently on this page.
	 * 
	 * @return the number of elements currently on this page.
	 */
	int getNumberOfElements();

	/**
	 * Returns the offset to be taken according to the underlying page and page
	 * size.
	 * 
	 * @return the offset to be taken
	 */
	int getOffset();


	/**
	 * Returns the sorting parameters
	 * 
	 * @return
	 */
	Sort getSort();

	/**
	 * Returns whether the current page is the first one.
	 * 
	 * @return
	 */
	boolean isFirstPage();

	/**
	 * Returns whether the current page is the last one.
	 * 
	 * @return
	 */
	boolean isLastPage();

	/**
	 * Returns if there is a next page.
	 * 
	 * @return if there is a next page.
	 */
	boolean isHasNext();

	/**
	 * Returns if there is a previous page.
	 * 
	 * @return if there is a previous page.
	 */
	boolean isHasPrevious();

	/**
	 * Returns the {@link Pageable} to request the next page. Can be
	 * {@literal null} in case the current page is already the last one. Clients
	 * should check {@link #hasNext()} before calling this method to make sure
	 * they receive a non-{@literal null} value.
	 * 
	 * @return
	 */
	Pageable getNextPageable();

	/**
	 * Returns the {@link Pageable} to request the previous page. Can be
	 * {@literal null} in case the current page is already the first one.
	 * Clients should check {@link #hasPrevious()} before calling this method
	 * make sure receive a non-{@literal null} value.
	 * 
	 * @return
	 */
	Pageable getPreviousPageable();
	
	
	/**
	 * 获得查询参数
	 * @return
	 */
	Map<String, Object> getParamMap();

	/**
	 * Returns a new {@link Page} with the content of the current one mapped by
	 * the given {@link Converter}.
	 * 
	 * @param converter
	 *            must not be {@literal null}.
	 * @return a new {@link Page} with the content of the current one mapped by
	 *         the given {@link Converter}.
	 * @since 1.10
	 */
	<S> Page<S> map(Converter<? super T, ? extends S> converter);
}
