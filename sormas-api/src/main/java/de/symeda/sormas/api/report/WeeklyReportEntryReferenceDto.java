package de.symeda.sormas.api.report;

import de.symeda.sormas.api.ReferenceDto;

public class WeeklyReportEntryReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = 7863410150359837423L;

	public WeeklyReportEntryReferenceDto() {
		
	}
	
	public WeeklyReportEntryReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public WeeklyReportEntryReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
}
