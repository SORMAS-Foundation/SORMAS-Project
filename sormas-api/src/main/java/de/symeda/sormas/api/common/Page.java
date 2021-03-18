package de.symeda.sormas.api.common;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {
    List<T> elements;
    int pageNumber = 0;
    int size = 50;
    long totalElementCount =0;
    boolean hasNext=false;


    public static Page emptyResults(){
       return new Page();
    }

    private Page() {
    }

    public Page(List<T> elements, int pageNumber, int size, Long totalElementCount) {
        this.elements = elements;
        this.totalElementCount = totalElementCount;
        this.pageNumber = pageNumber;
        this.size = size;
        hasNext = totalElementCount >(pageNumber+1)*size;
    }

    public List<T> getElements() {
        return elements;
    }

    public void setElements(List<T> elements) {
        this.elements = elements;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
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
