package de.symeda.sormas.ui.samples;

import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class SampleTestGrid extends Grid implements ItemClickListener {

	private static final String EDIT_BTN_ID = "edit";
	private SampleReferenceDto sampleRef;
	
	public SampleTestGrid(SampleReferenceDto sampleRef) {
		setSizeFull();
		
		BeanItemContainer<SampleTestDto> container = new BeanItemContainer<SampleTestDto>(SampleTestDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		VaadinUiUtil.addEditColumn(generatedContainer, EDIT_BTN_ID);
		setContainerDataSource(generatedContainer);
		
		setColumns(EDIT_BTN_ID, SampleTestDto.TEST_TYPE, SampleTestDto.TEST_DATE_TIME, SampleTestDto.LAB,
				SampleTestDto.LAB_USER, SampleTestDto.TEST_RESULT, SampleTestDto.TEST_RESULT_VERIFIED);
		
		getColumn(EDIT_BTN_ID).setRenderer(new HtmlRenderer());
        getColumn(EDIT_BTN_ID).setWidth(60);
        
		getColumn(SampleTestDto.TEST_DATE_TIME).setRenderer(new DateRenderer(DateHelper.getShortDateFormat()));
	
		for(Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
					SampleTestDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}

        setSelectionMode(SelectionMode.NONE);        
		addItemClickListener(this);
		
		this.sampleRef = sampleRef;
		reload();
	}
	
	@SuppressWarnings("unchecked")
	private BeanItemContainer<SampleTestDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<SampleTestDto>) container.getWrappedContainer();
	}
	
	public void reload() {
		List<SampleTestDto> sampleTests = ControllerProvider.getSampleTestController().getSampleTestsBySample(sampleRef);
		getContainer().removeAllItems();
		getContainer().addAll(sampleTests);
		this.setHeightByRows(getContainer().size() < 10 ? (getContainer().size() > 0 ? getContainer().size() : 1) : 10);
	}
	
	public void refresh(SampleTestDto sample) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
		BeanItem<SampleTestDto> item = getContainer().getItem(sample);
		if(item != null) {
            // Updated product
			@SuppressWarnings("rawtypes")
			MethodProperty p = (MethodProperty) item.getItemProperty(SampleTestDto.UUID);
			p.fireValueChange();
		} else {
            // New product
			getContainer().addBean(sample);
		}
	}
	
	public void remove(SampleTestDto sample) {
		getContainer().removeItem(sample);
	}
	
	@Override
	public void itemClick(ItemClickEvent event) {
		SampleTestDto sampleTest = (SampleTestDto)event.getItemId();
		if(EDIT_BTN_ID.equals(event.getPropertyId()) || event.isDoubleClick()) {
			ControllerProvider.getSampleTestController().edit(sampleTest, this);
		}
	}
	
}
