package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;

public class RegionsGrid extends Grid {

	private static final long serialVersionUID = 6289713952342575369L;

	private static final String EDIT_BTN_ID = "edit";
	
	public RegionsGrid() {
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);
		
		BeanItemContainer<RegionDto> container = new BeanItemContainer<RegionDto>(RegionDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);
		
		if (LoginHelper.hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {
			generatedContainer.addGeneratedProperty(EDIT_BTN_ID, new PropertyValueGenerator<String>() {
				private static final long serialVersionUID = -7255691609662228895L;

				@Override
				public String getValue(Item item, Object itemId, Object propertyId) {
					return FontAwesome.PENCIL_SQUARE.getHtml();
				}

				@Override
				public Class<String> getType() {
					return String.class;
				}
			});
		}
		
		setColumns(RegionDto.NAME, RegionDto.EPID_CODE, RegionDto.POPULATION, RegionDto.GROWTH_RATE);

        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
        			RegionDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
		if (LoginHelper.hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {
			addColumn(EDIT_BTN_ID);
			getColumn(EDIT_BTN_ID).setRenderer(new HtmlRenderer());
			getColumn(EDIT_BTN_ID).setWidth(40);

			addItemClickListener(e -> {
				if (e.getPropertyId() != null && (e.getPropertyId().equals(EDIT_BTN_ID) || e.isDoubleClick())) {
					ControllerProvider.getInfrastructureController().editRegion(((RegionDto) e.getItemId()).getUuid());
				}
			});
		}
	}
	
	@SuppressWarnings("unchecked")
	public BeanItemContainer<RegionDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<RegionDto>) container.getWrappedContainer();
	}
	
	public void reload() {
		List<RegionDto> regions = FacadeProvider.getRegionFacade().getIndexList();
		getContainer().removeAllItems();
		getContainer().addAll(regions);
	}
	
	public void filterByText(String text) {
		getContainer().removeContainerFilters(RegionDto.NAME);
		getContainer().removeContainerFilters(RegionDto.EPID_CODE);

		if (text != null && !text.isEmpty()) {
			List<Filter> orFilters = new ArrayList<Filter>();
			String[] words = text.split("\\s+");
			for (String word : words) {
				orFilters.add(new SimpleStringFilter(RegionDto.NAME, word, true, false));
				orFilters.add(new SimpleStringFilter(RegionDto.EPID_CODE, word, true, false));
			}
			getContainer().addContainerFilter(new Or(orFilters.stream().toArray(Filter[]::new)));
		}
	}
	
}
