package de.symeda.sormas.api.epidata;

import java.util.Date;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;

public class EpiDataTravelHelper {

	public static String buildTravelString(TravelType travelType,  String travelDestination, Date travelDateFrom, Date travelDateTo) {
		StringBuilder resultString = new StringBuilder();
		
		if (!DataHelper.isNullOrEmpty(travelDestination)) {
			resultString.append(travelDestination);
		}
		
		if (travelType != null) {
			if (resultString.length() > 0) {
				resultString.append(" ");
			}
			resultString.append(travelType);
		}
		
		if (travelDateFrom != null) {
			if (resultString.length() > 0) {
				resultString.append(" ");
			}
			resultString.append(DateHelper.formatLocalShortDate(travelDateFrom));
		}

		if (travelDateTo != null) {
			if (travelDateFrom != null) {
				resultString.append(" - ");
			} else if (resultString.length() > 0) {
				resultString.append(" ");
			}
			resultString.append(DateHelper.formatLocalShortDate(travelDateTo));
		}
		
		return resultString.toString();
	}
	
}
