package de.symeda.sormas.ui.epidata;

import com.vaadin.ui.DateField;
import com.vaadin.ui.TextArea;

import de.symeda.sormas.api.epidata.EpiDataGatheringDto;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class EpiDataGatheringEditForm extends AbstractEditForm<EpiDataGatheringDto> {

	private static final String HTML_LAYOUT =
			LayoutUtil.fluidRowLocs(EpiDataGatheringDto.GATHERING_DATE, "") +
			LayoutUtil.fluidRowLocs(EpiDataGatheringDto.DESCRIPTION) +
			LayoutUtil.fluidRowLocs(EpiDataGatheringDto.GATHERING_ADDRESS)
	;
	
	public EpiDataGatheringEditForm() {
		super(EpiDataGatheringDto.class, EpiDataGatheringDto.I18N_PREFIX);
		
		setWidth(540, Unit.PIXELS);
	}
	
	@Override
	protected void addFields() {
		DateField gatheringDate = addField(EpiDataGatheringDto.GATHERING_DATE, DateField.class);
		addField(EpiDataGatheringDto.DESCRIPTION, TextArea.class).setRows(2);
		addField(EpiDataGatheringDto.GATHERING_ADDRESS, LocationEditForm.class).setCaption(null);
		
		FieldHelper.makeFieldSoftRequired(gatheringDate);
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
