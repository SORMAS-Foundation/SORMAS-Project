package de.symeda.sormas.ui.dashboard.surveillance.epicurve;

import de.symeda.sormas.ui.dashboard.diagram.EpiCurveGrouping;
import de.symeda.sormas.ui.dashboard.surveillance.SurveillanceEpiCurveMode;

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
