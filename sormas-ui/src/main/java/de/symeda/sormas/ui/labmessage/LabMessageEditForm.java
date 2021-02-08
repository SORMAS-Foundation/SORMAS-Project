package de.symeda.sormas.ui.labmessage;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.converter.Converter;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.labmessage.ExternalMessageResult;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class LabMessageEditForm extends AbstractEditForm<LabMessageDto> {

	//@formatter:off
    private static final String HTML_LAYOUT =
            fluidRowLocs(LabMessageDto.UUID, LabMessageDto.MESSAGE_DATE_TIME) +
			fluidRowLocs(LabMessageDto.LAB_MESSAGE_DETAILS);
	//@formatter:on

	private final boolean readOnly;
	private Label labMessageDetails;

	public LabMessageEditForm(boolean readOnly) {

		super(LabMessageDto.class, LabMessageDto.I18N_PREFIX);

		this.readOnly = readOnly;
	}

	@Override
	protected void addFields() {
		addFields(LabMessageDto.UUID, LabMessageDto.MESSAGE_DATE_TIME);
		labMessageDetails = new Label();
		labMessageDetails.setContentMode(ContentMode.HTML);
		getContent().addComponent(labMessageDetails, LabMessageDto.LAB_MESSAGE_DETAILS);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	public void setValue(LabMessageDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);
		ExternalMessageResult<String> externalMessageResult = FacadeProvider.getExternalLabResultsFacade().convertToHTML(newFieldValue);
		if (externalMessageResult.isSuccess()) {
			labMessageDetails.setValue(externalMessageResult.getValue());
		} else {
			VaadinUiUtil.showWarningPopup(externalMessageResult.getError());
		}
		getFieldGroup().setReadOnly(readOnly);
	}
}
