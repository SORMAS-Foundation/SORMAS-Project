/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.events;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_4;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;

import java.util.Arrays;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class BulkEventDataForm extends AbstractEditForm<EventDto> {
		
		private static final long serialVersionUID = 1L;
		

	private static final String EVENT_STATUS_CHECKBOX = "eventStatusCheckbox";
	
	private static final String HTML_LAYOUT =
			fluidRowLocsCss(VSPACE_4, EVENT_STATUS_CHECKBOX) +
			fluidRowLocs(EventDto.EVENT_STATUS);

	private CheckBox eventStatusCheckBox;
	
	public BulkEventDataForm() {
		super(EventDto.class, EventDto.I18N_PREFIX, null);
		setWidth(680, Unit.PIXELS);
		hideValidationUntilNextCommit();
	}
	
	@Override
	protected void addFields() {
		eventStatusCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkEventStatus));
		getContent().addComponent(eventStatusCheckBox, EVENT_STATUS_CHECKBOX);
		OptionGroup eventStatus = addField(EventDto.EVENT_STATUS, OptionGroup.class);
		eventStatus.setEnabled(false);
		
		FieldHelper.setRequiredWhen(getFieldGroup(), eventStatusCheckBox, Arrays.asList(EventDto.EVENT_STATUS), Arrays.asList(true));
	
		eventStatusCheckBox.addValueChangeListener(e -> {
			eventStatus.setEnabled((boolean) e.getProperty().getValue());
		});
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	public CheckBox getEventStatusCheckBox() {
		return eventStatusCheckBox;
	}
}
