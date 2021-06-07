package de.symeda.sormas.ui.contact.components.linelisting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.contact.components.linelisting.contactfield.ContactFieldDto;
import de.symeda.sormas.ui.contact.components.linelisting.contactfield.ContactLineField;
import de.symeda.sormas.ui.contact.components.linelisting.sharedinfo.SharedInfoField;
import de.symeda.sormas.ui.contact.components.linelisting.sharedinfo.SharedInfoFieldDto;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.linelisting.line.DeleteLineEvent;
import de.symeda.sormas.ui.utils.components.linelisting.line.LineLayout;

public class LineListingLayout extends VerticalLayout {

	public static final float DEFAULT_WIDTH = 1696;

	private final SharedInfoField sharedInfoField;

	private final List<ContactLineLayout> contactLines;

	private final Window window;
	private Consumer<List<ContactLineDto>> saveCallback;

	public LineListingLayout(Window window) {

		this.window = window;

		setSpacing(false);

		VerticalLayout sharedInformationComponent = new VerticalLayout();
		sharedInformationComponent.setMargin(false);
		sharedInformationComponent.setSpacing(false);
		Label sharedInformationLabel = new Label();
		sharedInformationLabel.setValue(I18nProperties.getCaption(Captions.lineListingSharedInformation));
		sharedInformationLabel.addStyleName(CssStyles.H3);
		sharedInformationComponent.addComponent(sharedInformationLabel);

		sharedInfoField = new SharedInfoField();
		sharedInfoField.setId("lineListingSharedInfoField");
		sharedInformationComponent.addComponent(sharedInfoField);

		addComponent(sharedInformationComponent);

		contactLines = new ArrayList<>();
		VerticalLayout lineComponent = new VerticalLayout();
		lineComponent.setMargin(false);

		Label lineComponentLabel = new Label();
		lineComponentLabel.setValue(I18nProperties.getCaption(Captions.lineListingNewCasesList));
		lineComponentLabel.addStyleName(CssStyles.H3);
		lineComponent.addComponent(lineComponentLabel);

		ContactLineLayout line = buildNewLine(lineComponent);
		contactLines.add(line);
		lineComponent.addComponent(line);
		lineComponent.setSpacing(false);
		addComponent(lineComponent);

		HorizontalLayout actionBar = new HorizontalLayout();
		Button addLine = ButtonHelper.createIconButton(Captions.lineListingAddLine, VaadinIcons.PLUS, e -> {
			ContactLineLayout newLine = buildNewLine(lineComponent);
			contactLines.add(newLine);
			lineComponent.addComponent(newLine);
			contactLines.get(0).enableDelete(true);
		}, ValoTheme.BUTTON_PRIMARY);

		actionBar.addComponent(addLine);
		actionBar.setComponentAlignment(addLine, Alignment.MIDDLE_LEFT);

		addComponent(actionBar);

		HorizontalLayout buttonsPanel = new HorizontalLayout();
		buttonsPanel.setMargin(false);
		buttonsPanel.setSpacing(true);
		buttonsPanel.setWidth(100, Unit.PERCENTAGE);

		Button cancelButton = ButtonHelper.createButton(Captions.actionDiscard, event -> closeWindow());

		buttonsPanel.addComponent(cancelButton);
		buttonsPanel.setComponentAlignment(cancelButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(cancelButton, 1);

		Button saveButton =
			ButtonHelper.createButton(Captions.actionSave, event -> saveCallback.accept(getContactLineDtos()), ValoTheme.BUTTON_PRIMARY);

		buttonsPanel.addComponent(saveButton);
		buttonsPanel.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(saveButton, 0);

		addComponent(buttonsPanel);
		setComponentAlignment(buttonsPanel, Alignment.BOTTOM_RIGHT);
	}

	public void closeWindow() {
		window.close();
	}

	public void validate() throws ValidationRuntimeException {
		boolean validationFailed = false;
		for (ContactLineLayout line : contactLines) {
			if (line.hasErrors()) {
				validationFailed = true;
			}
		}
		if (validationFailed) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.errorFieldValidationFailed));
		}
	}

	public List<ContactLineDto> getContactLineDtos() {
		return contactLines.stream().map(ContactLineLayout::getBean).collect(Collectors.toList());
	}

	public void setSaveCallback(Consumer<List<ContactLineDto>> saveCallback) {
		this.saveCallback = saveCallback;
	}

	private ContactLineLayout buildNewLine(VerticalLayout lineComponent) {
		ContactLineLayout newLine = new ContactLineLayout(contactLines.size());
		ContactLineDto newLineDto = new ContactLineDto();

		if (!contactLines.isEmpty()) {
			ContactLineDto lastLineDto = contactLines.get(contactLines.size() - 1).getBean();
			newLineDto.setSharedInfoField(lastLineDto.getSharedInfoField());
			newLineDto.setLineField(lastLineDto.getLineField());
		} else {
			newLine.enableDelete(false);
		}

		newLine.setBean(newLineDto);
		newLine.addDeleteLineListener(e -> {
			ContactLineLayout selectedLine = (ContactLineLayout) e.getComponent();
			lineComponent.removeComponent(selectedLine);
			contactLines.remove(selectedLine);
			contactLines.get(0).enableDelete(contactLines.size() > 1);
		});

		return newLine;
	}

	class ContactLineLayout extends LineLayout {

		private final Binder<ContactLineDto> binder = new Binder<>(ContactLineDto.class);

		private final ContactLineField contactLineField;
		private final Button delete;

		public ContactLineLayout(int lineIndex) {

			addStyleName(CssStyles.SPACING_SMALL);
			setMargin(false);

			binder.forField(sharedInfoField).bind(ContactLineDto.SHARED_INFO_FIELD);

			contactLineField = new ContactLineField();
			contactLineField.setId("lineListingContactLineField_" + lineIndex);
			binder.forField(contactLineField).bind(ContactLineDto.LINE_FIELD);

			delete = ButtonHelper
				.createIconButtonWithCaption("delete_" + lineIndex, null, VaadinIcons.TRASH, event -> fireEvent(new DeleteLineEvent(this)));
			delete.setStyleName(CssStyles.VSPACE_3);

			addComponents(contactLineField, delete);

			setComponentAlignment(contactLineField, Alignment.BOTTOM_LEFT);
			setComponentAlignment(delete, Alignment.BOTTOM_LEFT);

			contactLineField.showCaptions();
		}

		public void setBean(ContactLineDto bean) {
			binder.setBean(bean);
		}

		public ContactLineDto getBean() {
			return binder.getBean();
		}

		public boolean hasErrors() {
			return sharedInfoField.hasErrors() | contactLineField.hasErrors();
		}

		public void enableDelete(boolean shouldEnable) {
			delete.setEnabled(shouldEnable);
		}
	}

	public static class ContactLineDto implements Serializable {

		public static final String SHARED_INFO_FIELD = "sharedInfoField";
		public static final String LINE_FIELD = "lineField";

		private SharedInfoFieldDto sharedInfoField;
		private ContactFieldDto lineField;

		public SharedInfoFieldDto getSharedInfoField() {
			return sharedInfoField;
		}

		public void setSharedInfoField(SharedInfoFieldDto sharedInfoField) {
			this.sharedInfoField = sharedInfoField;
		}

		public ContactFieldDto getLineField() {
			return lineField;
		}

		public void setLineField(ContactFieldDto lineField) {
			this.lineField = lineField;
		}
	}
}
