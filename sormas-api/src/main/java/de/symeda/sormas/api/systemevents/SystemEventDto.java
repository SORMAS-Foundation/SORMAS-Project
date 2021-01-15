package de.symeda.sormas.api.systemevents;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.utils.DataHelper;

import java.util.Date;

public class SystemEventDto extends EntityDto {

	private SystemEventType type;
	private Date startDate;
	private Date endDate;
	private SystemEventStatus status;
	private String additionalInfo;

	public SystemEventType getType() {
		return type;
	}

	public void setType(SystemEventType type) {
		this.type = type;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public SystemEventStatus getStatus() {
		return status;
	}

	public void setStatus(SystemEventStatus status) {
		this.status = status;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public static SystemEventDto build() {

		SystemEventDto systemEvent = new SystemEventDto();
		systemEvent.setUuid(DataHelper.createUuid());
		return systemEvent;
	}

}
