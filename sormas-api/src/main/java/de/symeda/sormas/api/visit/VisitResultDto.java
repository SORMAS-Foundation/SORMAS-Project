package de.symeda.sormas.api.visit;

import java.io.Serializable;

import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;

@AuditedClass
public class VisitResultDto implements Serializable {

	public VisitResultDto() {
	}

	public VisitResultDto(VisitOrigin origin, VisitResult result) {
		this.origin = origin;
		this.result = result;
	}

	private VisitOrigin origin;
	private VisitResult result;

	@AuditInclude
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

}
