package cn.newphy.data.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

public class PageByPage<T> extends ArrayList<T> implements Page<T> {
	private static final long serialVersionUID = 867755909294344406L;

	private final Pageable pageable;
	private final boolean hasNext;


	public PageByPage(List<T> content, Pageable pageable, boolean hasNext) {
		Assert.notNull(pageable, "分页信息不能为空");
		if(content != null && pageable != null) {
			addAll(content.subList(0, pageable.getPageSize()));
		}
		this.pageable = pageable;
		this.hasNext = hasNext;
	}

	

	@Override
	public PageMode getPageMode() {
		return PageMode.PAGE_BY_PAGE;
	}


	@Override
	public int getOffset() {
		return getPageNumber()*getPageSize();
	}

	@Override
	public int getTotalPages() {
		throw new UnsupportedOperationException();
	}


	@Override
	public long getTotalElements() {
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean isHasNext() {
		return hasNext;
	}

	
	@Override
	public int getPageNumber() {
		return pageable == null ? 0 : pageable.getPageNumber();
	}

	@Override
	public int getPageSize() {
		return pageable == null ? 0 : pageable.getPageSize();
	}

	@Override
	public int getNumberOfElements() {
		return size();
	}


	@Override
	public boolean isHasPrevious() {
		return getPageNumber() > 0;
	}

	@Override
	public boolean isFirstPage() {
		return !isHasPrevious();
	}

	@Override
	public boolean isLastPage() {
		return !isHasNext();
	}


	public Pageable getNextPageable() {
		return isHasNext() ? pageable.getNext() : null;
	}

	@Override
	public Pageable getPreviousPageable() {
		if (isHasPrevious()) {
			return pageable.getPreviousOrFirst();
		}
		return null;
	}


	@Override
	public Sort getSort() {
		return pageable == null ? null : pageable.getSort();
	}
	

	@Override
	public Map<String, Object> getParamMap() {
		return pageable == null ? null : pageable.getParamMap();
	}


	@Override
	public <S> Page<S> map(Converter<? super T, ? extends S> converter) {
		return new PageByPage<S>(getConvertedContent(converter), pageable, isHasNext());
	}
	
	protected <S> List<S> getConvertedContent(Converter<? super T, ? extends S> converter) {
		Assert.notNull(converter, "Converter must not be null!");
		List<S> result = new ArrayList<S>(this.size());
		for (T element : this) {
			result.add(converter.convert(element));
		}
		return result;
	}


	@Override
	public String toString() {
		String contentType = "UNKNOWN";

		if (this.size() > 0) {
			contentType = get(0).getClass().getName();
		}
		return String.format("Page %s of %d containing %s instances", getPageNumber(), getTotalPages(), contentType);
	}

}
