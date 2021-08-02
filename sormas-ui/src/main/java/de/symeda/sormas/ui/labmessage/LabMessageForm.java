package de.symeda.sormas.ui.labmessage;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
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

public class LabMessageForm extends AbstractEditForm<LabMessageDto> {

	private static final long serialVersionUID = -3859401780981133265L;

	//@formatter:off
	private static final String HTML_LAYOUT =
            fluidRowLocs(LabMessageDto.UUID, LabMessageDto.MESSAGE_DATE_TIME) +
			fluidRowLocs(LabMessageDto.LAB_MESSAGE_DETAILS);
	//@formatter:on

	private Panel detailsPanel;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public LabMessageForm() {
		super(LabMessageDto.class, LabMessageDto.I18N_PREFIX);
	}

	@Override
	protected void addFields() {
		addFields(LabMessageDto.UUID, LabMessageDto.MESSAGE_DATE_TIME);

		detailsPanel = new Panel();
		detailsPanel.setHeightFull();
		detailsPanel.addStyleName("lab-message-details");
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
				CustomLayout labMessageDetails = new CustomLayout();
				labMessageDetails.setTemplateContents(htmlConversionResult.getValue());
				detailsPanel.setContent(labMessageDetails);
			} else {
				detailsPanel.setContent(createXmlDisplay(labMessage.getLabMessageDetails()));
				VaadinUiUtil.showWarningPopup(htmlConversionResult.getError());
			}
		} catch (Exception e) {
			detailsPanel.setContent(createXmlDisplay(labMessage.getLabMessageDetails()));
			logger.error(e.getMessage());
		}
	}

	private Component createXmlDisplay(String xml) {
		Label label = new Label();
		label.setValue(xml);
		label.setContentMode(ContentMode.PREFORMATTED);
		return label;
	}
}
