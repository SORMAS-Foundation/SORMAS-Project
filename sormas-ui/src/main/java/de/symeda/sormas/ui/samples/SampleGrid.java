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
package de.symeda.sormas.ui.samples;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class SampleGrid extends Grid {

	public static final String EDIT_BTN_ID = "edit";
	
	private static final String TEST_RESULT_AND_SPECIMEN = "testResultAndSpecimen";
	private static final String DISEASE_SHORT = "diseaseShort";
	
	private SampleCriteria sampleCriteria = new SampleCriteria();
	
	public SampleGrid() {
		setSizeFull();
		
		sampleCriteria.archived(false);
		
		if (CurrentUser.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
        	setSelectionMode(SelectionMode.MULTI);
        } else {
        	setSelectionMode(SelectionMode.NONE);
        }
		
		BeanItemContainer<SampleIndexDto> container = new BeanItemContainer<SampleIndexDto>(SampleIndexDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
        VaadinUiUtil.addIconColumn(generatedContainer, EDIT_BTN_ID, FontAwesome.PENCIL_SQUARE);
		setContainerDataSource(generatedContainer);
		
		generatedContainer.addGeneratedProperty(TEST_RESULT_AND_SPECIMEN, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				SampleIndexDto sampleIndexDto = (SampleIndexDto) itemId;
				
				if (sampleIndexDto.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE) {
					return "Specimen not adequate";
				} else if (sampleIndexDto.getSampleTestResult() != null) {
					return sampleIndexDto.getSampleTestResult().toString();
				} else {
					return "Pending";
				}
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
		
		generatedContainer.addGeneratedProperty(DISEASE_SHORT, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				SampleIndexDto indexDto = (SampleIndexDto) itemId;
				return indexDto.getDisease() != Disease.OTHER 
						? (indexDto.getDisease() != null ? indexDto.getDisease().toShortString() : "")
						: DataHelper.toStringNullable(indexDto.getDiseaseDetails());
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
        });
		
		setColumns(EDIT_BTN_ID, SampleIndexDto.SAMPLE_CODE, SampleIndexDto.LAB_SAMPLE_ID, SampleIndexDto.ASSOCIATED_CASE, DISEASE_SHORT,
				SampleIndexDto.CASE_DISTRICT, SampleIndexDto.SHIPPED, SampleIndexDto.RECEIVED, SampleIndexDto.SHIPMENT_DATE, SampleIndexDto.RECEIVED_DATE, SampleIndexDto.LAB,
				SampleIndexDto.SAMPLE_MATERIAL, SampleIndexDto.SAMPLE_TEST_LAB_USER_NAME, TEST_RESULT_AND_SPECIMEN);
		
		getColumn(EDIT_BTN_ID).setRenderer(new HtmlRenderer());
        getColumn(EDIT_BTN_ID).setWidth(60);
		getColumn(SampleIndexDto.SHIPMENT_DATE).setRenderer(new DateRenderer(DateHelper.getLocalDateFormat()));
		getColumn(SampleIndexDto.RECEIVED_DATE).setRenderer(new DateRenderer(DateHelper.getLocalDateFormat()));
		getColumn(SampleIndexDto.SHIPPED).setRenderer(new BooleanRenderer());
		getColumn(SampleIndexDto.RECEIVED).setRenderer(new BooleanRenderer());
		getColumn(SampleIndexDto.LAB).setMaximumWidth(200);
		
		for(Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
					SampleIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}
		
		addItemClickListener(e -> {
	       	if (e.getPropertyId() != null && (e.getPropertyId().equals(EDIT_BTN_ID) || e.isDoubleClick())) {
	       		ControllerProvider.getSampleController().navigateToData(((SampleIndexDto)e.getItemId()).getUuid());
	       	}
		});
		
		if (CurrentUser.getCurrent().hasUserRole(UserRole.LAB_USER) || CurrentUser.getCurrent().hasUserRole(UserRole.EXTERNAL_LAB_USER)) {
			removeColumn(SampleIndexDto.SHIPMENT_DATE);
		} else {
			removeColumn(SampleIndexDto.RECEIVED_DATE);
		}
		
		if (CurrentUser.getCurrent().hasUserRole(UserRole.EXTERNAL_LAB_USER)) {
			removeColumn(SampleIndexDto.ASSOCIATED_CASE);
		}
	}
	
	public SampleGrid(CaseReferenceDto caseRef) {
		this();
		removeColumn(SampleIndexDto.ASSOCIATED_CASE);
		removeColumn(DISEASE_SHORT);
		removeColumn(SampleIndexDto.CASE_DISTRICT);
		sampleCriteria.caze(caseRef);
	}
	
	public void clearShipmentFilters(boolean reload) {
		sampleCriteria.shipped(null);
		sampleCriteria.received(null);
		sampleCriteria.referred(null);
		if (reload) {
			reload();
		}
	}

	public void filterForNotShipped() {
		clearShipmentFilters(false);
		sampleCriteria.shipped(false);
		reload();
	}
	
	public void filterForShipped() {
		clearShipmentFilters(false);
		sampleCriteria.shipped(true);
		reload();
	}
	
	public void filterForReceived() {
		clearShipmentFilters(false);
		sampleCriteria.received(true);
		reload();
	}
	
	public void filterForReferred() {
		clearShipmentFilters(false);
		sampleCriteria.referred(true);
		reload();
	}
	
	public void setRegionFilter(RegionReferenceDto region) {
		sampleCriteria.region(region);
		reload();
	}
	
	public void setDistrictFilter(DistrictReferenceDto district) {
		sampleCriteria.district(district);
		reload();
	}
	
	public void setLabFilter(FacilityReferenceDto lab) {
		sampleCriteria.laboratory(lab);
		reload();
	}	
	
	public void setTestResultFilter(SampleTestResultType testResult) {
		sampleCriteria.testResult(testResult);
		reload();
	}
	
	public void setSpecimenConditionFilter(SpecimenCondition specimenCondition) {
		sampleCriteria.specimenCondition(specimenCondition);
		reload();
	}
	
	public void setCaseClassificationFilter(CaseClassification caseClassification) {
		sampleCriteria.caseClassification(caseClassification);
		reload();
	}

	public void setDiseaseFilter(Disease disease) {
		sampleCriteria.disease(disease);
		reload();
	}
	
	public void filterByText(String text) {
		getContainer().removeContainerFilters(SampleIndexDto.SAMPLE_CODE);
		getContainer().removeContainerFilters(SampleIndexDto.UUID);
		getContainer().removeContainerFilters(SampleIndexDto.ASSOCIATED_CASE);
		if(text != null && !text.isEmpty()) {
			Or textFilter = new Or(new SimpleStringFilter(SampleIndexDto.SAMPLE_CODE, text, true, false), 
					new SimpleStringFilter(SampleIndexDto.UUID, text, true, false), 
					new SimpleStringFilter(SampleIndexDto.ASSOCIATED_CASE, text, true, false));
			getContainer().addContainerFilter(textFilter);
		}
	}
	
	@SuppressWarnings("unchecked")
	public BeanItemContainer<SampleIndexDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<SampleIndexDto>) container.getWrappedContainer();
	}
	
	public void reload() {
    	List<SampleIndexDto> samples = FacadeProvider.getSampleFacade().getIndexList(
    			CurrentUser.getCurrent().getUuid(), 
    			sampleCriteria);
    	
		getContainer().removeAllItems();
		getContainer().addAll(samples);
		
		if(sampleCriteria.getCaze() != null) {
			this.setHeightByRows(getContainer().size() < 10 ? (getContainer().size() > 0 ? getContainer().size() : 1) : 10);
		}
	}
	
	public void refresh(SampleIndexDto sample) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
		BeanItem<SampleIndexDto> item = getContainer().getItem(sample);
		if(item != null) {
            // Updated product
			@SuppressWarnings("rawtypes")
			MethodProperty p = (MethodProperty) item.getItemProperty(SampleIndexDto.UUID);
			p.fireValueChange();
		} else {
            // New product
			getContainer().addBean(sample);
		}
	}

	public void remove(SampleIndexDto sample) {
		getContainer().removeItem(sample);
	}

	public SampleCriteria getSampleCriteria() {
		return sampleCriteria;
	}
	
}
