package de.symeda.sormas.ui.labmessage;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.util.converter.Converter;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.labmessage.ExternalMessageResult;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class LabMessageEditForm extends AbstractEditForm<LabMessageDto> {

	private static final String SORMAS_TO_SORMAS_BUTTON = "sormasToSormas";

	//@formatter:off
    private static final String HTML_LAYOUT =
            fluidRowLocs(LabMessageDto.UUID, LabMessageDto.MESSAGE_DATE_TIME) +
			fluidRowLocs(LabMessageDto.LAB_MESSAGE_DETAILS) +
    		locCss(CssStyles.ALIGN_RIGHT + " " + CssStyles.VSPACE_TOP_3, SORMAS_TO_SORMAS_BUTTON);
	//@formatter:on

	private final boolean readOnly;
	private Label labMessageDetails;

	public LabMessageEditForm(boolean readOnly, boolean isProcessed, Runnable shareCallback) {

		super(LabMessageDto.class, LabMessageDto.I18N_PREFIX);

		this.readOnly = readOnly;

		boolean shareEnabled = !isProcessed && shareCallback != null && FacadeProvider.getSormasToSormasFacade().isFeatureEnabled();
		if (shareEnabled) {
			addShareButton(shareCallback);
		}
	}

	@Override
	protected void addFields() {
		addFields(LabMessageDto.UUID, LabMessageDto.MESSAGE_DATE_TIME);
		labMessageDetails = new Label();
		Panel detailsPanel = new Panel(labMessageDetails);
		detailsPanel.setWidth(550, Unit.PIXELS);
		getContent().addComponent(detailsPanel, LabMessageDto.LAB_MESSAGE_DETAILS);
	}

	private void addShareButton(Runnable shareCallback) {
		Button shareButton = ButtonHelper.createIconButton(Captions.sormasToSormasSendLabMessage, VaadinIcons.SHARE, (e) -> {
			ControllerProvider.getSormasToSormasController().shareLabMessage(getValue(), shareCallback);
		}, ValoTheme.BUTTON_PRIMARY);
		getContent().addComponent(shareButton, SORMAS_TO_SORMAS_BUTTON);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	public void setValue(LabMessageDto labMessage) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(labMessage);
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
		getFieldGroup().setReadOnly(readOnly);
	}
}
