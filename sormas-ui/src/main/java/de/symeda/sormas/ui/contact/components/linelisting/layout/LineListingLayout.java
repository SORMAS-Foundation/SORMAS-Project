package de.symeda.sormas.ui.contact.components.linelisting.layout;

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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.contact.components.linelisting.contactfield.ContactLineField;
import de.symeda.sormas.ui.contact.components.linelisting.contactfield.ContactLineFieldDto;
import de.symeda.sormas.ui.contact.components.linelisting.sharedinfo.SharedInfoField;
import de.symeda.sormas.ui.contact.components.linelisting.sharedinfo.SharedInfoFieldDto;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateHelper8;
import de.symeda.sormas.ui.utils.components.linelisting.line.DeleteLineEvent;
import de.symeda.sormas.ui.utils.components.linelisting.line.LineLayout;
import de.symeda.sormas.ui.utils.components.linelisting.section.LineListingSection;

public class LineListingLayout extends VerticalLayout {

	public static final float DEFAULT_WIDTH = 1696;

	private final SharedInfoField sharedInfoField;
	private final List<ContactLineLayout> lines;

	private final Window window;
	private Consumer<List<ContactLineDto>> saveCallback;

	public LineListingLayout(Window window, CaseDataDto caseReferenceDto) {

		this.window = window;

		setSpacing(false);

		LineListingSection sharedInformationComponent = new LineListingSection(Captions.lineListingSharedInformation);

		sharedInfoField = new SharedInfoField(caseReferenceDto);
		sharedInfoField.setId("lineListingSharedInfoField");
		sharedInformationComponent.addComponent(sharedInfoField);

		addComponent(sharedInformationComponent);

		LineListingSection lineComponent = new LineListingSection(Captions.lineListingNewContactsList);

		lines = new ArrayList<>();
		ContactLineLayout line = buildNewLine(lineComponent);
		lines.add(line);
		lineComponent.addComponent(line);

		addComponent(lineComponent);

		HorizontalLayout actionBar = new HorizontalLayout();
		Button addLine = ButtonHelper.createIconButton(Captions.lineListingAddLine, VaadinIcons.PLUS, e -> {
			ContactLineLayout newLine = buildNewLine(lineComponent);
			lines.add(newLine);
			lineComponent.addComponent(newLine);
			lines.get(0).enableDelete(true);
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
		for (ContactLineLayout line : lines) {
			if (line.hasErrors()) {
				validationFailed = true;
			}
		}
		if (validationFailed) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.errorFieldValidationFailed));
		}
	}

	public List<ContactLineDto> getContactLineDtos() {
		return lines.stream().map(line -> {
			ContactLineLayoutDto layoutBean = line.getBean();
			ContactLineDto result = new ContactLineDto();

			final ContactDto contact = ContactDto.build();
			contact.setCaze(layoutBean.getSharedInfoField().getCaze());
			contact.setDisease(layoutBean.getSharedInfoField().getDisease());
			contact.setRegion(layoutBean.getSharedInfoField().getRegion());
			contact.setDistrict(layoutBean.getSharedInfoField().getDistrict());
			contact.setReportDateTime(DateHelper8.toDate(layoutBean.getLineField().getDateOfReport()));
			contact.setMultiDayContact(layoutBean.getLineField().getMultiDaySelector().isMultiDay());
			contact.setFirstContactDate(DateHelper8.toDate(layoutBean.getLineField().getMultiDaySelector().getStartDate()));
			contact.setLastContactDate(DateHelper8.toDate(layoutBean.getLineField().getMultiDaySelector().getEndDate()));
			contact.setContactProximity(layoutBean.getLineField().getTypeOfContact());
			contact.setRelationToCase(layoutBean.getLineField().getRelationToCase());

			result.setContact(contact);

			final PersonDto person = PersonDto.build();
			person.setFirstName(layoutBean.getLineField().getPerson().getFirstName());
			person.setLastName(layoutBean.getLineField().getPerson().getLastName());
			person.setBirthdateYYYY(layoutBean.getLineField().getPerson().getBirthDate().getDateOfBirthYYYY());
			person.setBirthdateMM(layoutBean.getLineField().getPerson().getBirthDate().getDateOfBirthMM());
			person.setBirthdateDD(layoutBean.getLineField().getPerson().getBirthDate().getDateOfBirthDD());
			person.setSex(layoutBean.getLineField().getPerson().getSex());

			result.setPerson(person);

			return result;
		}).collect(Collectors.toList());
	}

	public void setSaveCallback(Consumer<List<ContactLineDto>> saveCallback) {
		this.saveCallback = saveCallback;
	}

	private ContactLineLayout buildNewLine(VerticalLayout lineComponent) {
		ContactLineLayout newLine = new ContactLineLayout(lines.size());
		ContactLineLayoutDto newLineDto = new ContactLineLayoutDto();

		if (!lines.isEmpty()) {
			ContactLineLayoutDto lastLineDto = lines.get(lines.size() - 1).getBean();
			newLineDto.setSharedInfoField(lastLineDto.getSharedInfoField());
			newLineDto.setLineField(lastLineDto.getLineField());
		} else {
			newLine.enableDelete(false);
		}

		newLine.setBean(newLineDto);
		newLine.addDeleteLineListener(e -> {
			ContactLineLayout selectedLine = (ContactLineLayout) e.getComponent();
			lineComponent.removeComponent(selectedLine);
			lines.remove(selectedLine);
			lines.get(0).enableDelete(lines.size() > 1);
		});

		return newLine;
	}

	class ContactLineLayout extends LineLayout {

		private final Binder<ContactLineLayoutDto> binder = new Binder<>(ContactLineLayoutDto.class);

		private final ContactLineField contactLineField;
		private final Button delete;

		public ContactLineLayout(int lineIndex) {

			addStyleName(CssStyles.SPACING_SMALL);
			setMargin(false);

			binder.forField(sharedInfoField).bind(ContactLineLayoutDto.SHARED_INFO_FIELD);

			contactLineField = new ContactLineField();
			contactLineField.setId("lineListingContactLineField_" + lineIndex);
			binder.forField(contactLineField).bind(ContactLineLayoutDto.LINE_FIELD);

			delete = ButtonHelper
				.createIconButtonWithCaption("delete_" + lineIndex, null, VaadinIcons.TRASH, event -> fireEvent(new DeleteLineEvent(this)));
			delete.setStyleName(CssStyles.VSPACE_3);

			addComponents(contactLineField, delete);

			setComponentAlignment(contactLineField, Alignment.BOTTOM_LEFT);
			setComponentAlignment(delete, Alignment.BOTTOM_LEFT);

			contactLineField.showCaptions();
		}

		public void setBean(ContactLineLayoutDto bean) {
			binder.setBean(bean);
		}

		public ContactLineLayoutDto getBean() {
			return binder.getBean();
		}

		public boolean hasErrors() {
			return sharedInfoField.hasErrors() | contactLineField.hasErrors();
		}

		public void enableDelete(boolean shouldEnable) {
			delete.setEnabled(shouldEnable);
		}
	}

	public static class ContactLineLayoutDto implements Serializable {

		public static final String SHARED_INFO_FIELD = "sharedInfoField";
		public static final String LINE_FIELD = "lineField";

		private SharedInfoFieldDto sharedInfoField;
		private ContactLineFieldDto lineField;

		public SharedInfoFieldDto getSharedInfoField() {
			return sharedInfoField;
		}

		public void setSharedInfoField(SharedInfoFieldDto sharedInfoField) {
			this.sharedInfoField = sharedInfoField;
		}

		public ContactLineFieldDto getLineField() {
			return lineField;
		}

		public void setLineField(ContactLineFieldDto lineField) {
			this.lineField = lineField;
		}
	}

	public static class ContactLineDto implements Serializable {

		private ContactDto contact;
		private PersonDto person;

		public ContactDto getContact() {
			return contact;
		}

		public void setContact(ContactDto contact) {
			this.contact = contact;
		}

		public PersonDto getPerson() {
			return person;
		}

		public void setPerson(PersonDto person) {
			this.person = person;
		}
	}
}
