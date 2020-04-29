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
package de.symeda.sormas.ui.dashboard.map;

import java.util.List;

import com.vaadin.ui.Window;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.renderers.DateRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.V7UuidRenderer;

@SuppressWarnings("serial")
public class CasePopupGrid extends Grid {

	public static final String DISEASE_SHORT = Captions.columnDiseaseShort;
	public static final String FIRST_NAME = PersonDto.FIRST_NAME;
	public static final String LAST_NAME = PersonDto.LAST_NAME;
	
	private final FacilityReferenceDto facility;
	private final DashboardMapComponent dashboardMapComponent;
	
	public CasePopupGrid(Window window, FacilityReferenceDto facility, DashboardMapComponent dashboardMapComponent) {
		this.facility = facility;
		this.dashboardMapComponent = dashboardMapComponent;
		setWidth(960, Unit.PIXELS);
		setHeightUndefined();
		
		setSelectionMode(SelectionMode.NONE);
		
		BeanItemContainer<CaseDataDto> container = new BeanItemContainer<CaseDataDto>(CaseDataDto.class);
        GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
        setContainerDataSource(generatedContainer);
        
        generatedContainer.addGeneratedProperty(DISEASE_SHORT, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				CaseDataDto caseDataDto = (CaseDataDto) itemId;
				String diseaseName = caseDataDto.getDisease().getName();
				return Disease.valueOf(diseaseName).toShortString();
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
        });
        
        generatedContainer.addGeneratedProperty(FIRST_NAME, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				CaseDataDto caseDataDto = (CaseDataDto) itemId;
				PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(caseDataDto.getPerson().getUuid());
				return personDto.getFirstName();
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
        });
        
        generatedContainer.addGeneratedProperty(LAST_NAME, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				CaseDataDto caseDataDto = (CaseDataDto) itemId;
				PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(caseDataDto.getPerson().getUuid());
				return personDto.getLastName();
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
        });
        
        setColumns(CaseDataDto.UUID, DISEASE_SHORT, CaseDataDto.CASE_CLASSIFICATION, FIRST_NAME,
        		LAST_NAME, CaseDataDto.REPORT_DATE, CaseDataDto.HEALTH_FACILITY_DETAILS);
        
        getColumn(CaseDataDto.UUID).setRenderer(new V7UuidRenderer());
		Language userLanguage = I18nProperties.getUserLanguage();
		getColumn(CaseDataDto.REPORT_DATE).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
        
        if (facility == null || !FacilityHelper.isOtherOrNoneHealthFacility(facility.getUuid())) {
        	getColumn(CaseDataDto.HEALTH_FACILITY_DETAILS).setHidden(true);
        }
        
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixCaption(
        			CaseDataDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        addItemClickListener(e -> {
        	window.close();
        	ControllerProvider.getCaseController().navigateToCase(
        		((CaseDataDto)e.getItemId()).getUuid(), true);
        });
        
        reload();
	}
	
	@SuppressWarnings("unchecked")
	private BeanItemContainer<CaseDataDto> getContainer() {
    	GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
        return (BeanItemContainer<CaseDataDto>) container.getWrappedContainer();
    }
	
	public void reload() {
		getContainer().removeAllItems();
		
		List<CaseDataDto> cases = dashboardMapComponent.getCasesForFacility(facility);
		
        getContainer().addAll(cases);
        this.setHeightByRows(cases.size());
    }
	
}
