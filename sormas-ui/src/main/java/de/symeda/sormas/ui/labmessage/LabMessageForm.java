package de.symeda.sormas.ui.labmessage;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.v7.data.util.converter.Converter;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.labmessage.ExternalMessageResult;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;

public class LabMessageForm extends AbstractEditForm<LabMessageDto> {

	private static final long serialVersionUID = -3859401780981133265L;

	//@formatter:off
	private static final String HTML_LAYOUT =
            fluidRowLocs(LabMessageDto.UUID, LabMessageDto.MESSAGE_DATE_TIME) +
			fluidRowLocs(LabMessageDto.LAB_MESSAGE_DETAILS);
	//@formatter:on

	private Label labMessageDetails;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public LabMessageForm() {
		super(LabMessageDto.class, LabMessageDto.I18N_PREFIX);
	}

	@Override
	protected void addFields() {
		addFields(LabMessageDto.UUID, LabMessageDto.MESSAGE_DATE_TIME);
		labMessageDetails = new Label();
		Panel detailsPanel = new Panel(labMessageDetails);
		detailsPanel.setHeightFull();
		getContent().addComponent(detailsPanel, LabMessageDto.LAB_MESSAGE_DETAILS);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	public void setValue(LabMessageDto labMessage) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(labMessage);
		getFieldGroup().setReadOnly(true);
		try {
			ExternalMessageResult<String> htmlConversionResult = FacadeProvider.getExternalLabResultsFacade().convertToHTML(labMessage);
			if (htmlConversionResult.isSuccess()) {
				labMessageDetails.setValue(htmlConversionResult.getValue());
				labMessageDetails.setContentMode(ContentMode.HTML);
			} else {
				String unformattedXml = labMessage.getLabMessageDetails();
				this.labMessageDetails.setValue(unformattedXml);
				this.labMessageDetails.setContentMode(ContentMode.PREFORMATTED);
				VaadinUiUtil.showWarningPopup(htmlConversionResult.getError());
			}
		} catch (NamingException e) {
			String unformattedXml = labMessage.getLabMessageDetails();
			this.labMessageDetails.setValue(unformattedXml);
			this.labMessageDetails.setContentMode(ContentMode.PREFORMATTED);
			logger.error(e.getMessage());
		}
	}
}
