package de.symeda.sormas.backend.visit;

import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.visit.VisitResult;
import de.symeda.sormas.api.visit.VisitResultDto;
import de.symeda.sormas.api.visit.VisitStatus;

public class VisitLogic {

	private VisitLogic() {
		// hidden constructor
	}

	public static VisitResultDto getVisitResult(VisitStatus status, VisitOrigin origin, Boolean symptomatic) {
		if (VisitStatus.UNCOOPERATIVE.equals(status)) {
			return new VisitResultDto(origin, VisitResult.UNCOOPERATIVE);
		}
		if (VisitStatus.UNAVAILABLE.equals(status)) {
			return new VisitResultDto(origin, VisitResult.UNAVAILABLE);
		}
		if (Boolean.TRUE.equals(symptomatic)) {
			return new VisitResultDto(origin, VisitResult.SYMPTOMATIC);
		}
		return new VisitResultDto(origin, VisitResult.NOT_SYMPTOMATIC);
	}

}
