package cn.newphy.data.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

public class TotalPage<T> extends ArrayList<T> implements Page<T> {
	private static final long serialVersionUID = 867755909294344406L;

	private final Pageable pageable;
	private final long total;


	public TotalPage(List<T> content, Pageable pageable, long total) {
		Assert.notNull(pageable, "分页信息不能为空");
		if(content != null && pageable != null) {
			addAll(content.subList(0, Math.min(content.size(), pageable.getPageSize())));
		}
		this.pageable = pageable;
		this.total = total;
	}

	

	@Override
	public PageMode getPageMode() {
		return PageMode.TOTAL;
	}


	@Override
	public int getOffset() {
		return getPageNumber()*getPageSize();
	}

	@Override
	public int getTotalPages() {
		return getPageSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getPageSize());
	}


	@Override
	public long getTotalElements() {
		return total;
	}


	@Override
	public boolean isHasNext() {
		return getPageNumber() + 1 < getTotalPages();
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
		return super.size();
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
		return new TotalPage<S>(getConvertedContent(converter), pageable, total);
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
