package de.symeda.sormas.api;

import java.io.Serializable;

import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class Quarter implements Serializable, StatisticsGroupingKey {

	private static final long serialVersionUID = -5708479972788056298L;
	
	private int value;
	
	public Quarter(int value) {
		if (value < 1 || value > 4) {
			throw new IllegalArgumentException("Quarters may only have a value between 1 and 4");
		}
		
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	/**
	 * Increases the quarter by 1 or sets it back to 1 if its current value is 4.
	 * 
	 * @return true if the quarter has been set back to 1, indicating a concomitant increase of the year.
	 */
	public boolean increaseQuarter() {
		if (value == 4) {
			value = 1;
			return true;
		} else {
			value++;
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "Q" + value;
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
		Quarter other = (Quarter) obj;
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
		
		return Integer.compare(value, ((Quarter) o).getValue());
	}
	
}
