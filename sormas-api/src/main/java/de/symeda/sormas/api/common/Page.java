package de.symeda.sormas.api.common;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {

	List<T> elements;
	int offset = 0;
	int size = 50;
	long totalElementCount = 0;
	boolean hasNext = false;

	public static Page emptyResults() {
		return new Page();
	}

	private Page() {
	}

	public Page(List<T> elements, int offset, int size, Long totalElementCount) {
		this.elements = elements;
		this.totalElementCount = totalElementCount;
		this.offset = offset;
		this.size = size;
		hasNext = totalElementCount > offset + size;
	}

	public List<T> getElements() {
		return elements;
	}

	public void setElements(List<T> elements) {
		this.elements = elements;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Long getTotalElementCount() {
		return totalElementCount;
	}

	public void setTotalElementCount(int totalElementCount) {
		this.totalElementCount = totalElementCount;
	}

	public boolean isHasNext() {
		return hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}
}
