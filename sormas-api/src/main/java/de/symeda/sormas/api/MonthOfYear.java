package de.symeda.sormas.api;

import java.io.Serializable;

public class MonthOfYear implements Serializable {
	
	private static final long serialVersionUID = -5776682012649885759L;
	
	private Month month;
	private int year;
	
	public MonthOfYear(Month month, int year) {
		this.month = month;
		this.year = year;
	}
	
	public Month getMonth() {
		return month;
	}
	
	public int getYear() {
		return year;
	}

	@Override
	public String toString() {
		return month.toString() + " " + year;
	}
	
}