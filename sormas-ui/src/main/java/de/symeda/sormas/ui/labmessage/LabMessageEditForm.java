package de.symeda.sormas.ui.labmessage;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.converter.Converter;

import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class LabMessageEditForm extends AbstractEditForm<LabMessageDto> {

	//@formatter:off
    private static final String HTML_LAYOUT =
            fluidRowLocs(LabMessageDto.UUID, LabMessageDto.MESSAGE_DATE_TIME) +
			fluidRowLocs(LabMessageDto.LAB_MESSAGE_DETAILS);
	//@formatter:on

	private final String HTML = "<html>\n" + "<body>\n" + "\n" + "<h2>An Unordered HTML List</h2>\n" + "\n" + "<ul>\n" + "  <li>Coffee</li>\n"
		+ "  <li>Tea</li>\n" + "  <li>Milk</li>\n" + "</ul>  \n" + "\n" + "<h2>An Ordered HTML List</h2>\n" + "\n" + "<ol>\n" + "  <li>Coffee</li>\n"
		+ "  <li>Tea</li>\n" + "  <li>Milk</li>\n" + "</ol> \n" + "\n" + "</body>\n" + "</html>";

	private final boolean readOnly;

	public LabMessageEditForm() {
		this(false);
	}

	public LabMessageEditForm(boolean readOnly) {

		super(LabMessageDto.class, LabMessageDto.I18N_PREFIX);

		setWidth(800, Unit.PIXELS);

		this.readOnly = readOnly;
	}

	@Override
	protected void addFields() {
		addFields(LabMessageDto.UUID, LabMessageDto.MESSAGE_DATE_TIME);
		Label labMessageDetails = new Label(HTML);
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
		getFieldGroup().setReadOnly(readOnly);
	}
}
