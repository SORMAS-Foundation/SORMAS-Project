package de.symeda.sormas.ui.dashboard.surveillance.components.epicurve.builders;

import de.symeda.sormas.api.dashboard.EpiCurveGrouping;
import de.symeda.sormas.ui.dashboard.surveillance.components.epicurve.SurveillanceEpiCurveMode;

public class SurveillanceEpiCurveBuilders {

	public static SurveillanceEpiCurveBuilder getEpiCurveBuilder(
		SurveillanceEpiCurveMode epiCurveSurveillanceMode,
		EpiCurveGrouping epiCurveGrouping) {
		if (epiCurveSurveillanceMode == SurveillanceEpiCurveMode.CASE_STATUS) {
			return new CaseStatusCurveBuilder(epiCurveGrouping);
		} else if (epiCurveSurveillanceMode == SurveillanceEpiCurveMode.ALIVE_OR_DEAD) {
			return new AliveOrDeadCurveBuilder(epiCurveGrouping);
		}
		return null;
	}
}
