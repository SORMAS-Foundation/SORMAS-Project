package de.symeda.sormas.backend.visit;

import de.symeda.sormas.api.visit.VisitResult;
import de.symeda.sormas.api.visit.VisitStatus;

public class VisitLogic {
  private VisitLogic() {
    // hidden constructor
  }

  public static VisitResult getVisitResult(VisitStatus status, boolean symptomatic) {

    if (VisitStatus.UNCOOPERATIVE.equals(status)) {
      return VisitResult.UNCOOPERATIVE;
    }
    if (VisitStatus.UNAVAILABLE.equals(status)) {
      return VisitResult.UNAVAILABLE;
    }
    if (symptomatic) {
      return VisitResult.SYMPTOMATIC;
    }
    return VisitResult.NOT_SYMPTOMATIC;
  }

}
