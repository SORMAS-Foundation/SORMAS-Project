package de.symeda.sormas.api.common;

import de.symeda.sormas.api.audit.AuditedClass;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

@AuditedClass
public class Page<T> implements Serializable {

	List<T> elements;
	@Schema(description = "Page offset")
	int offset = 0;
	@Schema(description = "Page size")
	int size = 50;
	@Schema(description = "Total number of entries")
	long totalElementCount = 0;
	@Schema(description = "Indicates whether there is another page to turn to")
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
