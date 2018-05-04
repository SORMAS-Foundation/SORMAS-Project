package de.symeda.sormas.api;

import java.io.Serializable;

public class QuarterOfYear implements Serializable {
	
	private static final long serialVersionUID = -7158625755180563434L;
	
	private int quarter;
	private int year;
	
	public QuarterOfYear(int quarter, int year) {
		this.quarter = quarter;
		this.year = year;
	}
	
	public void increaseQuarter() {
		if (quarter == 4) {
			quarter = 1;
			year++;
		} else {
			quarter++;
		}
	}
	
	public int getQuarter() {
		return quarter;
	}
	
	public int getYear() {
		return year;
	}

	@Override
	public String toString() {
		return "Q" + quarter + "/" + year;
	}
	
}