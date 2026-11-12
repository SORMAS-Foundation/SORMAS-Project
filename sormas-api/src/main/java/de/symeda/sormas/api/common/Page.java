package de.symeda.sormas.api.common;

import java.io.Serializable;
import java.util.List;

import de.symeda.sormas.api.audit.AuditedClass;

@AuditedClass
public class Page<T> implements Serializable {

	private List<T> elements;
	private int offset = 0;
	private int size = 50;
	private long totalElementCount = 0;
	private boolean hasNext = false;

	public static <T> Page<T> emptyResults() {
		return new Page<>();
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
