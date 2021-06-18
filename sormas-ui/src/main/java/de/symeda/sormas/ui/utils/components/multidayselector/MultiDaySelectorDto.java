package de.symeda.sormas.ui.utils.components.multidayselector;

import java.io.Serializable;
import java.time.LocalDate;

public class MultiDaySelectorDto implements Serializable {

	public static final String MULTI_DAY = "multiDay";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";

	private boolean multiDay;
	private LocalDate startDate;
	private LocalDate endDate;

	public boolean isMultiDay() {
		return multiDay;
	}

	public void setMultiDay(boolean multiDay) {
		this.multiDay = multiDay;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
}
