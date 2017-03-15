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

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.ShipmentStatus;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class SampleGrid extends Grid {
	
	private static final String TEST_RESULT_GEN = "testResultGen";
	
	private CaseReferenceDto caseRef;
	
	public SampleGrid() {
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);
		
		BeanItemContainer<SampleIndexDto> container = new BeanItemContainer<SampleIndexDto>(SampleIndexDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);
		
		generatedContainer.addGeneratedProperty(TEST_RESULT_GEN, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				SampleIndexDto sampleIndexDto = (SampleIndexDto)itemId;
				if(sampleIndexDto.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE) {
					return "Specimen condition not adequate";
				} else if(sampleIndexDto.getTestResult() != null) {
					return sampleIndexDto.getTestResult().toString();
				} else {
					return "";
				}
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
		
		setColumns(SampleIndexDto.UUID, SampleIndexDto.SAMPLE_CODE, SampleIndexDto.SHIPMENT_STATUS, SampleIndexDto.ASSOCIATED_CASE,
				SampleIndexDto.LGA, SampleIndexDto.SHIPMENT_DATE, SampleIndexDto.RECEIVED_DATE, SampleIndexDto.LAB, SampleIndexDto.SAMPLE_MATERIAL,
				SampleIndexDto.LAB_USER, SampleIndexDto.TEST_TYPE, TEST_RESULT_GEN);
		
		getColumn(SampleIndexDto.UUID).setRenderer(new UuidRenderer());
		getColumn(SampleIndexDto.SHIPMENT_DATE).setRenderer(new DateRenderer(DateHelper.getDateFormat()));
		getColumn(SampleIndexDto.RECEIVED_DATE).setRenderer(new DateRenderer(DateHelper.getDateFormat()));
		
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
		
		reload();
	}
	
	public SampleGrid(CaseReferenceDto caseRef) {
		this();
		removeColumn(SampleIndexDto.ASSOCIATED_CASE);
		setShipmentStatusFilter(null);
		this.caseRef = caseRef;
		reload();
	}
	
	public void setShipmentStatusFilter(ShipmentStatus shipmentStatus) {
		getContainer().removeContainerFilters(SampleIndexDto.SHIPMENT_STATUS);
		if(shipmentStatus != null) {
			Equal filter = new Equal(SampleIndexDto.SHIPMENT_STATUS, shipmentStatus);
			getContainer().addContainerFilter(filter);
		}
	}
	
	public void setDistrictFilter(DistrictReferenceDto district) {
		getContainer().removeContainerFilters(SampleIndexDto.LGA);
		if(district != null) {
			Equal filter = new Equal(SampleIndexDto.LGA, district);
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
			Or textFilter = new Or(new SimpleStringFilter(SampleIndexDto.SAMPLE_CODE, text, true, false), new SimpleStringFilter(SampleIndexDto.UUID, text, true, false), new SimpleStringFilter(SampleIndexDto.ASSOCIATED_CASE, text, true, false));
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
	
}
