package de.symeda.sormas.ui.epidata;

import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.epidata.EpiDataBurialDto;
import de.symeda.sormas.ui.location.LocationForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class EpiDataBurialEditForm extends AbstractEditForm<EpiDataBurialDto> {

	private static final String HTML_LAYOUT = 
			LayoutUtil.fluidRowLocs(EpiDataBurialDto.BURIAL_DATE_FROM, EpiDataBurialDto.BURIAL_DATE_TO) +
			LayoutUtil.fluidRowLocs(EpiDataBurialDto.BURIAL_PERSON_NAME, EpiDataBurialDto.BURIAL_RELATION) +
			LayoutUtil.fluidRowLocs(EpiDataBurialDto.BURIAL_ADDRESS) +
			LayoutUtil.fluidRowLocs(EpiDataBurialDto.BURIAL_ILL, EpiDataBurialDto.BURIAL_TOUCHING)
	;
	
	public EpiDataBurialEditForm() {
		super(EpiDataBurialDto.class, EpiDataBurialDto.I18N_PREFIX);
		
		setWidth(540, Unit.PIXELS);
	}
	
	@Override
	protected void addFields() {
		addFields(EpiDataBurialDto.BURIAL_DATE_FROM, EpiDataBurialDto.BURIAL_DATE_TO);
		addField(EpiDataBurialDto.BURIAL_PERSON_NAME, TextField.class);
		addField(EpiDataBurialDto.BURIAL_RELATION, TextField.class);
		addField(EpiDataBurialDto.BURIAL_ILL, OptionGroup.class);
		addField(EpiDataBurialDto.BURIAL_TOUCHING, OptionGroup.class);
		addField(EpiDataBurialDto.BURIAL_ADDRESS, LocationForm.class).setCaption(null);
		
		setRequired(true,
				EpiDataBurialDto.BURIAL_DATE_FROM,
				EpiDataBurialDto.BURIAL_DATE_TO,
				EpiDataBurialDto.BURIAL_ILL,
				EpiDataBurialDto.BURIAL_TOUCHING);
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
