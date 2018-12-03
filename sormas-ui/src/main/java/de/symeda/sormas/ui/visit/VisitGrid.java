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
package de.symeda.sormas.ui.visit;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class VisitGrid extends Grid {

	private static final String EDIT_BTN_ID = "edit";

	public static final String SYMPTOMS_SYMPTOMATIC = VisitDto.SYMPTOMS + "." + SymptomsDto.SYMPTOMATIC;
	public static final String SYMPTOMS_TEMPERATURE = VisitDto.SYMPTOMS + "." + SymptomsDto.TEMPERATURE;

	private ContactReferenceDto filterContact;
	private PersonReferenceDto filterPerson;

	public VisitGrid() {
		setSizeFull();

		if (CurrentUser.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
        	setSelectionMode(SelectionMode.MULTI);
        } else {
        	setSelectionMode(SelectionMode.NONE);
        }

		BeanItemContainer<VisitDto> container = new BeanItemContainer<VisitDto>(VisitDto.class);
		container.addNestedContainerProperty(SYMPTOMS_SYMPTOMATIC);
		container.addNestedContainerProperty(SYMPTOMS_TEMPERATURE);

		GeneratedPropertyContainer editContainer = new GeneratedPropertyContainer(container);
		VaadinUiUtil.addIconColumn(editContainer, EDIT_BTN_ID, FontAwesome.PENCIL_SQUARE);
		setContainerDataSource(editContainer);

		setColumns(EDIT_BTN_ID, VisitDto.VISIT_DATE_TIME, VisitDto.VISIT_STATUS, VisitDto.VISIT_REMARKS, 
				VisitDto.DISEASE, SYMPTOMS_SYMPTOMATIC, SYMPTOMS_TEMPERATURE);

		getColumn(EDIT_BTN_ID).setRenderer(new HtmlRenderer());
		getColumn(EDIT_BTN_ID).setWidth(60);
		getColumn(SYMPTOMS_SYMPTOMATIC).setRenderer(new BooleanRenderer());

		getColumn(VisitDto.VISIT_DATE_TIME).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat()));

		for (Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
					VisitDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}

		addItemClickListener(e -> {
			if (e.getPropertyId() != null && (e.getPropertyId().equals(EDIT_BTN_ID) || e.isDoubleClick())) {
				VisitDto indexDto = (VisitDto)e.getItemId();
				ControllerProvider.getVisitController().editVisit(indexDto.toReference(), filterContact, r -> reload());
			}
		});
	}

	@SuppressWarnings("unchecked")
	public BeanItemContainer<VisitDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<VisitDto>) container.getWrappedContainer();
	}

	public void reload(ContactReferenceDto contact) {
		this.filterContact = contact;
		filterPerson = null;
		reload(); 	
	}

	public void reload(PersonReferenceDto person) {
		this.filterPerson = person;
		filterContact = null;
		reload(); 	
	}

	protected void reload() {
		List<VisitDto> entries;
		if (filterContact != null) {
			entries = FacadeProvider.getVisitFacade().getAllByContact(filterContact);
		} else if (filterPerson != null) {
			entries = FacadeProvider.getVisitFacade().getAllByPerson(filterPerson);
		} else {
			throw new UnsupportedOperationException("a person or contact filter needs to be set for the visits list");
		}

		getContainer().removeAllItems();
		getContainer().addAll(entries);    	
	}

}


