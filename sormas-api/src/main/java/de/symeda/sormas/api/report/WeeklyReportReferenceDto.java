package de.symeda.sormas.api.report;

import de.symeda.sormas.api.ReferenceDto;

public class WeeklyReportReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -2884998571593631851L;

	public WeeklyReportReferenceDto() {
		
	}
	
	public WeeklyReportReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public WeeklyReportReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
}