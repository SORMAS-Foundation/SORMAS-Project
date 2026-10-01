package de.symeda.sormas.api.audit;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class ExternalSystemCallAuditRequest {

	private String systemName;
	private String actionType;

	private String additionalMessage;

	private Map<String, String> details;

	private LocalDateTime dateTime = LocalDateTime.now();

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getAdditionalMessage() {
		return additionalMessage;
	}

	public void setAdditionalMessage(String additionalMessage) {
		this.additionalMessage = additionalMessage;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public Map<String, String> getDetails() {
		return details;
	}

	public void setDetails(Map<String, String> details) {
		this.details = details;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ExternalSystemCallAuditRequest that = (ExternalSystemCallAuditRequest) o;
		return Objects.equals(systemName, that.systemName)
			&& Objects.equals(actionType, that.actionType)
			&& Objects.equals(additionalMessage, that.additionalMessage)
			&& Objects.equals(details, that.details)
			&& Objects.equals(dateTime, that.dateTime);
	}

	@Override
	public int hashCode() {
		return Objects.hash(systemName, actionType, additionalMessage, details, dateTime);
	}
}
