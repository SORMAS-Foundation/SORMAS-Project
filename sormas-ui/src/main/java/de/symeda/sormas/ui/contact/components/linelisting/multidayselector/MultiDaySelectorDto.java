package de.symeda.sormas.ui.contact.components.linelisting.multidayselector;

import java.io.Serializable;
import java.time.LocalDate;

public class MultiDaySelectorDto implements Serializable {

	public static final String MULTI_DAY = "multiDay";
	public static final String FIRST_DATE = "firstDate";
	public static final String LAST_DATE = "lastDate";

	private boolean multiDay;
	private LocalDate firstDate;
	private LocalDate lastDate;

	public boolean isMultiDay() {
		return multiDay;
	}

	public void setMultiDay(boolean multiDay) {
		this.multiDay = multiDay;
	}

	public LocalDate getFirstDate() {
		return firstDate;
	}

	public void setFirstDate(LocalDate firstDate) {
		this.firstDate = firstDate;
	}

	public LocalDate getLastDate() {
		return lastDate;
	}

	public void setLastDate(LocalDate lastDate) {
		this.lastDate = lastDate;
	}
}
