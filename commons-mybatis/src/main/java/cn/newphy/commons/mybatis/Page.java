package cn.newphy.commons.mybatis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T> implements Serializable {
	private static final long serialVersionUID = 4858530497985299178L;

	// 页码
	private int pageNo;
	// 每页个数
	private int pageSize;
	// 记录总数
	private long total;
	// 是否需要查询总记录
	private boolean needTotal = true;
	// 是否有下一页
	private boolean hasNext = false;
	// 当页数据
	private List<T> data = new ArrayList<T>();
	
	

	public Page() {
		setPageSize(1);
		setPageNo(1);
	}

	public Page(Page<?> page) {
		this.pageNo = page.getPageNo();
		this.pageSize = page.getPageSize();
		this.needTotal = page.isNeedTotal();
	}

	public Page(int pageNo, int pageSize) {
		setPageSize(pageSize);
		setPageNo(pageNo);
	}

	/**
	 * 计算总页数
	 * 
	 * @return
	 */
	public int getTotalPage() {
		if (pageSize == 0) {
			throw new IllegalArgumentException("pageSize is zero");
		}
		return (int) Math.ceil((double) total / pageSize);
	}

	/**
	 * 计算记录数
	 * 
	 * @return
	 */
	public int getStartIndex() {
		return (pageNo - 1) * pageSize;
	}

	/**
	 * @return the pageNo
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * @param pageNo
	 *            the pageNo to set
	 */
	public void setPageNo(int pageNo) {
		if (pageNo < 1) {
			throw new IllegalArgumentException("pageNo must more than 0");
		}
		this.pageNo = pageNo;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize
	 *            the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		if (pageSize == 0) {
			throw new IllegalArgumentException("pageSize can't set to 0");
		}
		this.pageSize = pageSize;
	}

	/**
	 * @return the total
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * @param total
	 *            the total to set
	 */
	public void setTotal(long total) {
		this.total = total;
	}

	/**
	 * @return the data
	 */
	public List<T> getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(List<T> data) {
		this.data = data;
	}

	/**
	 * @return the needTotal
	 */
	public boolean isNeedTotal() {
		return needTotal;
	}

	/**
	 * @param needTotal
	 *            the needTotal to set
	 */
	public void setNeedTotal(boolean needTotal) {
		this.needTotal = needTotal;
	}

	/**
	 * @return the hasNext
	 */
	public boolean isHasNext() {
		return needTotal ? (pageNo * pageSize < total) : hasNext;
	}

	/**
	 * @param hasNext
	 *            the hasNext to set
	 */
	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	/**
	 * @return the hasPrevious
	 */
	public boolean isHasPrevious() {
		return pageNo == 1 ? false : true;
	}

}
