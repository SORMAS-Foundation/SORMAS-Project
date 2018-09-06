package de.symeda.sormas.ui.configuration;

import com.vaadin.data.util.converter.StringToFloatConverter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class RegionEditForm extends AbstractEditForm<RegionDto> {

	private static final long serialVersionUID = 7858602578903198825L;
	
	private static final String HTML_LAYOUT = 
			LayoutUtil.fluidRowLocs(RegionDto.NAME, RegionDto.EPID_CODE)
			+ LayoutUtil.fluidRowLocs(RegionDto.POPULATION, RegionDto.GROWTH_RATE);

	public RegionEditForm(UserRight editOrCreateUserRight, boolean create) {
		super(RegionDto.class, RegionDto.I18N_PREFIX, editOrCreateUserRight);
		
		setWidth(540, Unit.PIXELS);
		
		if (create) {
			hideValidationUntilNextCommit();
		}
	}
	
	@Override
	protected void addFields() {
		addField(RegionDto.NAME, TextField.class);
		addField(RegionDto.EPID_CODE, TextField.class);
		TextField population = addField(RegionDto.POPULATION, TextField.class);
		population.setConverter(new StringToIntegerConverter());
		population.setConversionError("Only numbers are allowed for " + population.getCaption());
		TextField growthRate = addField(RegionDto.GROWTH_RATE, TextField.class);
		growthRate.setConverter(new StringToFloatConverter());
		growthRate.setConversionError("Only numbers (with decimal places) are allowed for " + growthRate.getCaption());
		
		setRequired(true, RegionDto.NAME, RegionDto.EPID_CODE);
	}
	

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
