package de.symeda.sormas.api;

public class IntegerRange {

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
			return from + "-" + to;
		} else if (from == null && to != null) {
			return "< " + to;
		} else if (from != null && to == null) {
			return from + "+";
		} else {
			return "Unknown";
		}
	}
	
}
