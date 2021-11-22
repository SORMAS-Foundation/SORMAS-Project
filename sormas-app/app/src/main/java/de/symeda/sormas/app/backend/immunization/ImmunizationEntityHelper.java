package de.symeda.sormas.app.backend.immunization;

import java.util.Date;

public class ImmunizationEntityHelper {

	private ImmunizationEntityHelper() {

	}

	public static Date getDateForComparison(Immunization immunization, boolean withReportDate) {
		return immunization.getEndDate() != null
			? immunization.getEndDate()
			: immunization.getStartDate() != null
				? immunization.getStartDate()
				: (withReportDate ? immunization.getReportDate() : immunization.getCreationDate());
	}
}
