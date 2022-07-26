package de.symeda.sormas.backend.person;

import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumConverter;

public class OccupationTypeConverter extends CustomizableEnumConverter<OccupationType> {

	public OccupationTypeConverter() {
		super(OccupationType.class);
	}
}
