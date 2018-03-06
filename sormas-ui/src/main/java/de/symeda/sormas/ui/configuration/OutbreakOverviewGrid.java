package de.symeda.sormas.ui.configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class OutbreakOverviewGrid extends Grid implements ItemClickListener {

	private static final String REGION = "State";

	private UserDto user;

	public OutbreakOverviewGrid() {
		super();
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);		

		user = LoginHelper.getCurrentUser();

		addColumn(REGION, RegionReferenceDto.class).setMaximumWidth(200);
		
		for (Disease disease : Disease.values()) {
			
			if (!disease.isSupportingOutbreakMode()) {
				continue;
			}
			
			addColumn(disease, OutbreakRegionConfiguration.class)
				.setMaximumWidth(200)
				.setHeaderCaption(disease.toShortString())			
				.setConverter(new Converter<String,OutbreakRegionConfiguration>() {
					@Override
					public OutbreakRegionConfiguration convertToModel(String value,
							Class<? extends OutbreakRegionConfiguration> targetType, Locale locale)
							throws ConversionException {
				        throw new UnsupportedOperationException("Can only convert from OutbreakRegionConfiguration to String");
					}
	
					@Override
					public String convertToPresentation(OutbreakRegionConfiguration value,
							Class<? extends String> targetType, Locale locale) throws ConversionException {
		        		
		        		boolean styleAsButton = LoginHelper.hasUserRight(UserRight.OUTBREAK_CONFIGURE_ALL) || 
		        				(LoginHelper.hasUserRight(UserRight.OUTBREAK_CONFIGURE_RESTRICTED) && LoginHelper.getCurrentUser().getRegion().equals(value.getRegion()));
		        		boolean moreThanHalfOfDistricts = value.getAffectedDistricts().size( )>= value.getTotalDistricts() / 2.0f;
	
	        			String styles;
		        		if (styleAsButton) {
		        			if (moreThanHalfOfDistricts) {
		        				styles = CssStyles.buildVaadinStyle(CssStyles.VAADIN_BUTTON, CssStyles.BUTTON_CRITICAL);
		        			} else if (!value.getAffectedDistricts().isEmpty()) {
		        				styles = CssStyles.buildVaadinStyle(CssStyles.VAADIN_BUTTON, CssStyles.BUTTON_WARNING);
		        			} else {
		        				styles = CssStyles.buildVaadinStyle(CssStyles.VAADIN_BUTTON);
		        			}
		        			
		        		} else {
		        			if (moreThanHalfOfDistricts) {
		        				styles = CssStyles.buildVaadinStyle(CssStyles.VAADIN_LABEL, CssStyles.LABEL_CRITICAL);
		        			} else if (!value.getAffectedDistricts().isEmpty()) {
		        				styles = CssStyles.buildVaadinStyle(CssStyles.VAADIN_LABEL, CssStyles.LABEL_WARNING);
		        			} else {
		        				styles = CssStyles.buildVaadinStyle(CssStyles.VAADIN_LABEL);
		        			}
		        		}
	        			return LayoutUtil.divCss(styles, value.toString());
					}
	
					@Override
					public Class<OutbreakRegionConfiguration> getModelType() {
						return OutbreakRegionConfiguration.class;
					}
	
					@Override
					public Class<String> getPresentationType() {
						return String.class;
					}
					
				})
				.setRenderer(new HtmlRenderer());

		}

		setCellDescriptionGenerator(cell -> getCellDescription(cell));

		setCellStyleGenerator(new CellStyleGenerator() {
			@Override
			public String getStyle(CellReference cell) {
				if (cell.getProperty().getValue() instanceof OutbreakRegionConfiguration) {
					return CssStyles.ALIGN_CENTER;
				} 
				return null;
			}
		});
		
		addItemClickListener(this);
	}
	
	private String getCellDescription(CellReference cell) {
		Item item = cell.getItem();
		
		if (cell.getPropertyId() == REGION) {
			return "";
		}
		
		Set<DistrictReferenceDto> affectedDistricts = ((OutbreakRegionConfiguration) item.getItemProperty((Disease) cell.getPropertyId()).getValue()).getAffectedDistricts();
		
		if (affectedDistricts.isEmpty()) {
			return "No outbreak";
		}

		StringBuilder affectedDistrictsStringBuilder = new StringBuilder();
		affectedDistrictsStringBuilder.append("Affected districts: ");
		
		int index = 0;
		for (DistrictReferenceDto affectedDistrict : affectedDistricts) {
			affectedDistrictsStringBuilder.append(affectedDistrict.toString());
			if (index < affectedDistricts.size() - 1) {
				affectedDistrictsStringBuilder.append(", ");
			}
			index++;
		}
		
		return affectedDistrictsStringBuilder.toString();
	}

	public void reload() {
		Container.Indexed container = getContainerDataSource();
		container.removeAllItems();
		
		// Initially set all columns to their default value
		for (RegionReferenceDto region : FacadeProvider.getRegionFacade().getAllAsReference()) {
			addItem(region);
		}

		// Alter cells with regions and diseases that actually have an outbreak
		List<OutbreakDto> activeOutbreaks = FacadeProvider.getOutbreakFacade().getAllAfter(null);

		for (OutbreakDto outbreak : activeOutbreaks) {
			
			DistrictReferenceDto outbreakDistrict = outbreak.getDistrict();
			RegionReferenceDto outbreakRegion = FacadeProvider.getDistrictFacade().getDistrictByUuid(outbreakDistrict.getUuid()).getRegion();
			Disease outbreakDisease = outbreak.getDisease();
			
			((OutbreakRegionConfiguration) container.getItem(outbreakRegion).getItemProperty(outbreakDisease).getValue()).getAffectedDistricts().add(outbreakDistrict);
		}
	}

	@SuppressWarnings("unchecked")
	private void addItem(RegionReferenceDto region) {
		int totalDistricts = FacadeProvider.getDistrictFacade().getCountByRegion(region.getUuid());
		Item item = getContainerDataSource().addItem(region);
		item.getItemProperty(REGION).setValue(region);
		for (Disease disease : Disease.values()) {

			if (!disease.isSupportingOutbreakMode()) {
				continue;
			}
			
			item.getItemProperty(disease).setValue(new OutbreakRegionConfiguration(disease, region, totalDistricts, new HashSet<>()));
		}
	}

	@Override
	public void itemClick(ItemClickEvent event) {
		Item clickedItem = event.getItem();
		
		if (event.getPropertyId() == REGION) {
			return;
		}

		// Open the outbreak configuration window for the clicked row when
		// a) the user is allowed to configure all existing outbreaks or
		// b) the user is allowed to configure outbreaks in his assigned region and has clicked the respective row
		if (LoginHelper.hasUserRight(UserRight.OUTBREAK_CONFIGURE_ALL)) {
			ControllerProvider.getOutbreakController().openOutbreakConfigurationWindow((Disease) event.getPropertyId(), (OutbreakRegionConfiguration) clickedItem.getItemProperty((Disease) event.getPropertyId()).getValue());
		} else if (LoginHelper.hasUserRight(UserRight.OUTBREAK_CONFIGURE_RESTRICTED)) {
			if (user.getRegion().equals(clickedItem.getItemProperty(REGION).getValue())) {
				ControllerProvider.getOutbreakController().openOutbreakConfigurationWindow((Disease) event.getPropertyId(), (OutbreakRegionConfiguration) clickedItem.getItemProperty((Disease) event.getPropertyId()).getValue());
			}
		} else {
			return;
		}
	}

	

}
