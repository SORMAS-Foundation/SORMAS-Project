package de.symeda.sormas.ui.configuration.infrastructure;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.StringToAngularLocationConverter;

public class PointOfEntryForm extends AbstractEditForm<PointOfEntryDto> {

	private static final String HTML_LAYOUT = 
			LayoutUtil.fluidRowLocs(PointOfEntryDto.NAME, PointOfEntryDto.POINT_OF_ENTRY_TYPE)
			+ LayoutUtil.fluidRowLocs(PointOfEntryDto.REGION, PointOfEntryDto.DISTRICT)
			+ LayoutUtil.fluidRowLocs(PointOfEntryDto.LATITUDE, PointOfEntryDto.LONGITUDE)
			+ LayoutUtil.fluidRowLocs(PointOfEntryDto.ACTIVE, "");
	
	private boolean create;
	
	public PointOfEntryForm(UserRight editOrCreateUserRight, boolean create) {
		super(PointOfEntryDto.class, PointOfEntryDto.I18N_PREFIX, editOrCreateUserRight, false);
		this.create = create;
		
		setWidth(540, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}
		addFields();
	}
	
	@Override
	protected void addFields() {
		addField(PointOfEntryDto.NAME, TextField.class);
		addField(PointOfEntryDto.POINT_OF_ENTRY_TYPE, ComboBox.class);
		addField(PointOfEntryDto.ACTIVE, CheckBox.class);
		TextField tfLatitude = addField(PointOfEntryDto.LATITUDE, TextField.class);
		TextField tfLongitude = addField(PointOfEntryDto.LONGITUDE, TextField.class);
		ComboBox cbRegion = addInfrastructureField(PointOfEntryDto.REGION);
		ComboBox cbDistrict = addInfrastructureField(PointOfEntryDto.DISTRICT);

		tfLatitude.setConverter(new StringToAngularLocationConverter());
		tfLatitude.setConversionError(I18nProperties.getValidationError(Validations.onlyGeoCoordinatesAllowed, tfLatitude.getCaption()));
		tfLongitude.setConverter(new StringToAngularLocationConverter());
		tfLongitude.setConversionError(I18nProperties.getValidationError(Validations.onlyGeoCoordinatesAllowed, tfLongitude.getCaption()));

		cbRegion.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(cbDistrict,
					regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		cbRegion.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		
		setRequired(true, PointOfEntryDto.NAME, PointOfEntryDto.POINT_OF_ENTRY_TYPE);
		if (!create) {
			cbRegion.setEnabled(false);
			cbDistrict.setEnabled(false);
		} else {
			setRequired(true, PointOfEntryDto.REGION, PointOfEntryDto.DISTRICT);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
