package de.symeda.sormas.ui.utils;

import com.vaadin.v7.ui.Grid;
import de.symeda.sormas.api.therapy.PeriodDto;
import elemental.json.JsonValue;

public class PeriodRenderer extends Grid.AbstractRenderer<PeriodDto> {

	public PeriodRenderer() {
		super(PeriodDto.class, "");
	}

	@Override
	public JsonValue encode(PeriodDto value) {
		if(value == null){
			return encode(getNullRepresentation(), String.class);
		}

		String periodString = DateFormatHelper.buildPeriodString(value.getStart(), value.getEnd());

		return encode(periodString, String.class);
	}
}
