package de.symeda.sormas.api.visit;

import java.io.Serializable;

import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.audit.Auditable;

public class VisitResultDto implements Auditable, Serializable {

	public VisitResultDto() {
	}

	public VisitResultDto(VisitOrigin origin, VisitResult result) {
		this.origin = origin;
		this.result = result;
	}

	private VisitOrigin origin;
	private VisitResult result;

	public VisitOrigin getOrigin() {
		return origin;
	}

	public void setOrigin(VisitOrigin origin) {
		this.origin = origin;
	}

	public VisitResult getResult() {
		return result;
	}

	public void setResult(VisitResult result) {
		this.result = result;
	}

	public String toString() {
		return result.toString();
	}

	@Override
	public String getAuditRepresentation() {
		return String.format("%s(origin=%s)", getClass().getSimpleName(), origin);
	}
}
