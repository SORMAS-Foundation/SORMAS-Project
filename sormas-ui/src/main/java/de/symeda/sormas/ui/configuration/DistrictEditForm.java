package de.symeda.sormas.ui.configuration;

import com.vaadin.data.util.converter.StringToFloatConverter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class DistrictEditForm extends AbstractEditForm<DistrictDto> {

	private static final long serialVersionUID = 7573666294384000190L;

	private static final String HTML_LAYOUT =
			LayoutUtil.fluidRowLocs(DistrictDto.NAME, DistrictDto.EPID_CODE)
			+ LayoutUtil.loc(DistrictDto.REGION)
			+ LayoutUtil.fluidRowLocs(DistrictDto.POPULATION, DistrictDto.GROWTH_RATE);
	
	public DistrictEditForm(UserRight editOrCreateUserRight, boolean create) {
		super(DistrictDto.class, DistrictDto.I18N_PREFIX, editOrCreateUserRight);
		
		setWidth(540, Unit.PIXELS);
		
		if (create) {
			hideValidationUntilNextCommit();
		}
	}
	
	@Override
	protected void addFields() {
		addField(DistrictDto.NAME, TextField.class);
		addField(DistrictDto.EPID_CODE, TextField.class);
		ComboBox region = addField(DistrictDto.REGION, ComboBox.class);
		TextField population = addField(DistrictDto.POPULATION, TextField.class);
		population.setConverter(new StringToIntegerConverter());
		population.setConversionError("Only numbers are allowed for " + population.getCaption());
		TextField growthRate = addField(DistrictDto.GROWTH_RATE, TextField.class);
		growthRate.setConverter(new StringToFloatConverter());
		growthRate.setConversionError("Only numbers (with decimal places) are allowed for " + growthRate.getCaption());

		setRequired(true, DistrictDto.NAME, DistrictDto.EPID_CODE, DistrictDto.REGION);
		
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
