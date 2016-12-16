package cn.newphy.data.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PageRequest implements Pageable, Serializable {

	private static final long serialVersionUID = -4541509938956089562L;

	private final int page;
	private final int size;
	private Sort sort;
	private final PageMode pageMode;
	private final Map<String, Object> paramMap = new LinkedHashMap<>();


	public PageRequest(int page, int size) {
		this(page, size, PageMode.TOTAL);
	}


	public PageRequest(int page, int size, PageMode pageMode) {
		this(page, size, null, pageMode);
	}

	public PageRequest(int page, int size, Sort sort, PageMode pageMode) {
		if (page < 0) {
			throw new IllegalArgumentException("Page index must not be less than zero!");
		}
		if (size < 1) {
			throw new IllegalArgumentException("Page size must not be less than one!");
		}
		this.sort = sort;
		this.page = page;
		this.size = size;
		this.pageMode = pageMode;
	}
	

	@Override
	public PageMode getPageMode() {
		return this.pageMode;
	}


	@Override
	public void setSort(Sort sort) {
		this.sort = sort;
	}


	@Override
	public Sort getSort() {
		return sort;
	}


	@Override
	public Pageable getNext() {
		PageRequest next = new PageRequest(getPageNumber() + 1, getPageSize(), getSort(), getPageMode());
		next.paramMap.putAll(this.paramMap);
		return next;
	}


	public Pageable getPrevious() {
		if(getPageNumber() == 0) {
			return this;
		} else {
			PageRequest previous = new PageRequest(getPageNumber() - 1, getPageSize(), getSort(), getPageMode());
			previous.paramMap.putAll(this.paramMap);
			return previous;
		}
	}


	@Override
	public Pageable getFirst() {
		PageRequest first = new PageRequest(0, getPageSize(), getSort(), getPageMode());
		first.paramMap.putAll(this.paramMap);
		return first;
	}

	@Override
	public int getPageSize() {
		return size;
	}

	@Override
	public int getPageNumber() {
		return page;
	}

	@Override
	public int getOffset() {
		return Math.max(0, (page-1) * size);
	}

	@Override
	public boolean isHasPrevious() {
		return page > 0;
	}


	public Pageable getPreviousOrFirst() {
		return isHasPrevious() ? getPrevious() : getFirst();
	}
	

	@Override
	public void addParameter(String key, Object value) {
		this.paramMap.put(key, value);
	}

	@Override
	public void addParameters(Map<String, ?> paramMap) {
		this.paramMap.putAll(paramMap);
	}

	@Override
	public Map<String, Object> getParamMap() {
		return Collections.unmodifiableMap(paramMap);
	}
	
	
	@Override
	public void orderBy(Direction direction, String property) {
		this.sort = new Sort(direction, property).and(this.sort);
	}

	@Override
	public void orderAsc(String property) {
		orderBy(Direction.ASC, property);
	}

	@Override
	public void orderDesc(String property) {
		orderBy(Direction.DESC, property);
	}

	@Override
	public String toString() {
		return String.format("Page request [number: %d, size %d, sort: %s]", getPageNumber(), getPageSize(),
				sort == null ? null : sort.toString());
	}
}
