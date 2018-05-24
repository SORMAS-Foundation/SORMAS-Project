package de.symeda.sormas.api;

import java.io.Serializable;

import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class IntegerRange implements Serializable, StatisticsGroupingKey {

	private static final long serialVersionUID = 4459253785381816968L;

	private Integer from;
	private Integer to;

	public IntegerRange(Integer from, Integer to) {
		this.from = from;
		this.to = to;
	}

	public Integer getFrom() {
		return from;
	}

	public Integer getTo() {
		return to;
	}

	@Override
	public String toString() {
		if (from != null && to != null) {
			if (from == to) {
				return String.valueOf(from);
			} else {
				return String.valueOf(from) + "-" + String.valueOf(to);
			}
		} else if (from == null && to != null) {
			return "< " + String.valueOf(to);
		} else if (from != null && to == null) {
			return String.valueOf(from) + "+";
		} else {
			return "Unknown";
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
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
		IntegerRange other = (IntegerRange) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
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

		if (this.equals(o)) {
			return 0;
		}
		if (this.getFrom() < ((IntegerRange) o).getFrom() || 
				(this.getFrom() == ((IntegerRange) o).getFrom() && this.getTo() < ((IntegerRange) o).getTo())) {
			return -1;
		}
		return 1;
	}

}
