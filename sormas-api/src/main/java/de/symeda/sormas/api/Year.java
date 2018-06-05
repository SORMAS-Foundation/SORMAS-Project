package de.symeda.sormas.api;

import java.io.Serializable;

import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class Year implements Serializable, StatisticsGroupingKey {

	private static final long serialVersionUID = -6317192936320989737L;
	
	private int value;
	
	public Year(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void increaseYearBy(int increase) {
		value += increase;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Year other = (Year) obj;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	public int keyCompareTo(StatisticsGroupingKey o) {
		if (o == null) {
			throw new NullPointerException("Can't compare to null.");
		}
		if (o.getClass() != this.getClass()) {
			throw new UnsupportedOperationException("Can't compare to class " + o.getClass().getName() + " that differs from " + this.getClass().getName());
		}
		
		return Integer.compare(value, ((Year) o).getValue());
	}
	
}
