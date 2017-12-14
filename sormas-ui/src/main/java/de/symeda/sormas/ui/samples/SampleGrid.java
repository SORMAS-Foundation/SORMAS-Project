package de.symeda.sormas.ui.samples;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.BooleanRenderer;

@SuppressWarnings("serial")
public class SampleGrid extends Grid {
	
	private static final String TEST_RESULT = "testResult";
	private static final String LAB_USER = "labUser";
	private static final String DISEASE_SHORT = "diseaseShort";
	
	private CaseReferenceDto caseRef;
	
	public SampleGrid() {
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);
		
		BeanItemContainer<SampleIndexDto> container = new BeanItemContainer<SampleIndexDto>(SampleIndexDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);
		
		generatedContainer.addGeneratedProperty(TEST_RESULT, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				SampleIndexDto sampleIndexDto = (SampleIndexDto) itemId;
				SampleTestDto latestSampleTest = FacadeProvider.getSampleTestFacade().getLatestBySample(sampleIndexDto.toReference());
				
				if (sampleIndexDto.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE) {
					return "Specimen not adequate";
				} else if (latestSampleTest != null) {
					return latestSampleTest.getTestResult().toString();
				} else {
					return "Pending";
				}
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
		
		generatedContainer.addGeneratedProperty(LAB_USER, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				SampleIndexDto sampleIndexDto = (SampleIndexDto) itemId;
				SampleTestDto latestSampleTest = FacadeProvider.getSampleTestFacade().getLatestBySample(sampleIndexDto.toReference());
				
				if (latestSampleTest != null) {
					return latestSampleTest.getLabUser().toString();
				} else {
					return "";
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
		
		setColumns(SampleIndexDto.SAMPLE_CODE, SampleIndexDto.LAB_SAMPLE_ID, SampleIndexDto.ASSOCIATED_CASE, DISEASE_SHORT,
				SampleIndexDto.CASE_DISTRICT, SampleIndexDto.SHIPPED, SampleIndexDto.RECEIVED, SampleIndexDto.SHIPMENT_DATE, SampleIndexDto.RECEIVED_DATE, SampleIndexDto.LAB,
				SampleIndexDto.SAMPLE_MATERIAL, SampleIndexDto.LAB_USER, TEST_RESULT);
		
		getColumn(SampleIndexDto.SHIPMENT_DATE).setRenderer(new DateRenderer(DateHelper.getDateFormat()));
		getColumn(SampleIndexDto.RECEIVED_DATE).setRenderer(new DateRenderer(DateHelper.getDateFormat()));
		getColumn(SampleIndexDto.SHIPPED).setRenderer(new BooleanRenderer());
		getColumn(SampleIndexDto.RECEIVED).setRenderer(new BooleanRenderer());
		getColumn(SampleIndexDto.LAB).setMaximumWidth(200);
		
		for(Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
					SampleIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}
		
		addItemClickListener(e -> ControllerProvider.getSampleController().navigateToData(
				((SampleIndexDto)e.getItemId()).getUuid()));
		
		if(LoginHelper.getCurrentUser().getUserRoles().contains(UserRole.LAB_USER)) {
			removeColumn(SampleIndexDto.SHIPMENT_DATE);
		} else {
			removeColumn(SampleIndexDto.RECEIVED_DATE);
		}
	}
	
	public SampleGrid(CaseReferenceDto caseRef) {
		this();
		removeColumn(SampleIndexDto.ASSOCIATED_CASE);
		removeColumn(DISEASE_SHORT);
		removeColumn(SampleIndexDto.CASE_DISTRICT);
		this.caseRef = caseRef;
	}
	
	public void filterForNotShipped() {
		clearShipmentFilters();
		Equal shippedFilter = new Equal(SampleIndexDto.SHIPPED, false);
		getContainer().addContainerFilter(shippedFilter);
	}
	
	public void filterForShipped() {
		clearShipmentFilters();
		Equal shippedFilter = new Equal(SampleIndexDto.SHIPPED, true);
		getContainer().addContainerFilter(shippedFilter);
	}
	
	public void filterForReceived() {
		clearShipmentFilters();
		Equal receivedFilter = new Equal(SampleIndexDto.RECEIVED, true);
		getContainer().addContainerFilter(receivedFilter);
	}
	
	public void filterForReferred() {
		clearShipmentFilters();
		Equal referredFilter = new Equal(SampleIndexDto.REFERRED, true);
		getContainer().addContainerFilter(referredFilter);
	}
	
	public void setRegionFilter(String regionUuid) {
		getContainer().removeContainerFilters(SampleIndexDto.CASE_REGION_UUID);
		if(regionUuid != null) {
			Equal filter = new Equal(SampleIndexDto.CASE_REGION_UUID, regionUuid);
			getContainer().addContainerFilter(filter);
		}
	}
	
	public void setDistrictFilter(DistrictReferenceDto district) {
		getContainer().removeContainerFilters(SampleIndexDto.CASE_DISTRICT);
		if(district != null) {
			Equal filter = new Equal(SampleIndexDto.CASE_DISTRICT, district);
			getContainer().addContainerFilter(filter);
		}
	}
	
	public void setLabFilter(FacilityReferenceDto lab) {
		getContainer().removeContainerFilters(SampleIndexDto.LAB);
		if(lab != null) {
			Equal filter = new Equal(SampleIndexDto.LAB, lab);
			getContainer().addContainerFilter(filter);
		}
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
	private BeanItemContainer<SampleIndexDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<SampleIndexDto>) container.getWrappedContainer();
	}
	
	public void reload() {
		List<SampleIndexDto> samples;
		if(caseRef != null) {
			samples = ControllerProvider.getSampleController().getSamplesByCase(caseRef);
		} else {
			samples = ControllerProvider.getSampleController().getAllSamples();
		}
		
		getContainer().removeAllItems();
		getContainer().addAll(samples);
		
		if(caseRef != null) {
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
	
	public void clearShipmentFilters() {
		getContainer().removeContainerFilters(SampleIndexDto.SHIPPED);
		getContainer().removeContainerFilters(SampleIndexDto.RECEIVED);
		getContainer().removeContainerFilters(SampleIndexDto.REFERRED);
	}
	
}
