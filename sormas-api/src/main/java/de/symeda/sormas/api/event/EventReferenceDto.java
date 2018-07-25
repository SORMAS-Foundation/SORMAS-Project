package de.symeda.sormas.api.event;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;

public class EventReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = 2430932452606853497L;

	public EventReferenceDto() {
		
	}
	
	public EventReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public EventReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
	public EventReferenceDto(String uuid, Disease disease, String diseaseDetails, EventType eventType, Date eventDate) {
		setUuid(uuid);
		setCaption(buildCaption(disease, diseaseDetails, eventType, eventDate));
	}
	
	public static String buildCaption(Disease disease, String diseaseDetails, EventType eventType, Date eventDate) {
		
		String diseaseString = disease != Disease.OTHER
				? DataHelper.toStringNullable(disease)
				: DataHelper.toStringNullable(diseaseDetails);
		String eventTypeString = DataHelper.toStringNullable(eventType);
		if (!diseaseString.isEmpty()) {
			eventTypeString = eventTypeString.toLowerCase();
		}
		return diseaseString + " " + eventTypeString + " on " + DateHelper.formatLocalDate(eventDate);
	}
}
