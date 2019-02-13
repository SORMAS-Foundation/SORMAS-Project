package de.symeda.sormas.api.clinicalcourse;

import de.symeda.sormas.api.ReferenceDto;

public class ClinicalVisitReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -8220449896773019721L;
	
	public ClinicalVisitReferenceDto() {
		
	}
	
	public ClinicalVisitReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public ClinicalVisitReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}

}
