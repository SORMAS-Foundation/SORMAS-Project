package de.symeda.sormas.ui.externalmessage;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.v7.data.util.converter.Converter;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageResult;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ExternalMessageForm extends AbstractEditForm<ExternalMessageDto> {

	private static final long serialVersionUID = -3859401780981133265L;

	//@formatter:off
	private static final String HTML_LAYOUT =
            fluidRowLocs(ExternalMessageDto.UUID, ExternalMessageDto.MESSAGE_DATE_TIME) +
			fluidRowLocs(ExternalMessageDto.EXTERNAL_MESSAGE_DETAILS);
	//@formatter:on

	private Panel detailsPanel;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public ExternalMessageForm() {
		super(ExternalMessageDto.class, ExternalMessageDto.I18N_PREFIX);
	}

	@Override
	protected void addFields() {
		addFields(ExternalMessageDto.UUID, ExternalMessageDto.MESSAGE_DATE_TIME);

		detailsPanel = new Panel();
		detailsPanel.setHeightFull();
		detailsPanel.addStyleName("lab-message-details");
		getContent().addComponent(detailsPanel, ExternalMessageDto.EXTERNAL_MESSAGE_DETAILS);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	public void setValue(ExternalMessageDto externalMessage) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(externalMessage);
		getFieldGroup().setReadOnly(true);
		try {
			ExternalMessageResult<String> htmlConversionResult = FacadeProvider.getExternalLabResultsFacade().convertToHTML(externalMessage);
			if (htmlConversionResult.isSuccess()) {
				CustomLayout externalMessageDetails = new CustomLayout();
				externalMessageDetails.setTemplateContents(htmlConversionResult.getValue());
				detailsPanel.setContent(externalMessageDetails);
			} else {
				detailsPanel.setContent(createXmlDisplay(externalMessage.getExternalMessageDetails()));
				VaadinUiUtil.showWarningPopup(htmlConversionResult.getError());
			}
		} catch (Exception e) {
			detailsPanel.setContent(createXmlDisplay(externalMessage.getExternalMessageDetails()));
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
