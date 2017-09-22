package de.symeda.sormas.api.utils;

import java.io.Serializable;

public class EpiWeek implements Serializable {
	
	private final int year;
	private final int week;
	
	public EpiWeek(int year, int week) {
		this.year = year;
		this.week = week;
	}
	
	public int getYear() {
		return year;
	}
	
	public int getWeek() {
		return week;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EpiWeek other = (EpiWeek) obj;
		if (week != other.week)
			return false;
		if (year != other.year)
			return false;
		return true;
	}
	
	

}
