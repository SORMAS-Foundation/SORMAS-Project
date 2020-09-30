package de.symeda.sormas.backend.visit;

import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.visit.VisitResult;
import de.symeda.sormas.api.visit.VisitResultDto;
import de.symeda.sormas.api.visit.VisitStatus;

public class VisitLogic {
  private VisitLogic() {
    // hidden constructor
  }

  public static VisitResultDto getVisitResult(VisitStatus status, VisitOrigin origin, boolean symptomatic) {
    boolean external = origin.equals(VisitOrigin.EXTERNAL_JOURNAL);
    if (VisitStatus.UNCOOPERATIVE.equals(status)) {
      return new VisitResultDto(external, VisitResult.UNCOOPERATIVE);
    }
    if (VisitStatus.UNAVAILABLE.equals(status)) {
      return new VisitResultDto(external, VisitResult.UNAVAILABLE);
    }
    if (symptomatic) {
      return new VisitResultDto(external, VisitResult.SYMPTOMATIC);
    }
    return new VisitResultDto(external, VisitResult.NOT_SYMPTOMATIC);
  }

}
