package de.symeda.sormas.ui.utils;

import com.vaadin.ui.Grid.AbstractRenderer;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import elemental.json.JsonValue;

@SuppressWarnings("serial")
public class ReferenceDtoRenderer extends AbstractRenderer<ReferenceDto> {

    public ReferenceDtoRenderer() {
		super(ReferenceDto.class, "");
	}

	@Override
    public JsonValue encode(ReferenceDto value) {
		String html;
		if (value != null) {
			String uuid = value.getUuid();
			html = "<a title='" + uuid + "'>" + DataHelper.getShortUuid(uuid) + " (" + value.getCaption() + ")</a>";
		} else {
			html = getNullRepresentation();
		}
		return super.encode(html, String.class);
    }
}