package de.symeda.sormas.ui.contact.components.linelisting.layout;

import java.io.Serializable;
import java.time.LocalDate;
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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.contact.components.linelisting.contactfield.ContactLineField;
import de.symeda.sormas.ui.contact.components.linelisting.contactfield.ContactLineFieldDto;
import de.symeda.sormas.ui.contact.components.linelisting.section.LineListingSection;
import de.symeda.sormas.ui.contact.components.linelisting.sharedinfo.SharedInfoField;
import de.symeda.sormas.ui.contact.components.linelisting.sharedinfo.SharedInfoFieldDto;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.linelisting.line.DeleteLineEvent;
import de.symeda.sormas.ui.utils.components.linelisting.line.LineLayout;

public class LineListingLayout extends VerticalLayout {

	public static final float DEFAULT_WIDTH = 1696;

	private final SharedInfoField sharedInfoField;
	private final List<ContactLineLayout> lines;

	private final Window window;
	private Consumer<List<ContactLineDto>> saveCallback;

	public LineListingLayout(Window window) {

		this.window = window;

		setSpacing(false);

		LineListingSection sharedInformationComponent = new LineListingSection(Captions.lineListingSharedInformation);

		sharedInfoField = new SharedInfoField();
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

			result.setCaze(layoutBean.getSharedInfoField().getCaze());
			result.setDisease(layoutBean.getSharedInfoField().getDisease());
			result.setRegion(layoutBean.getSharedInfoField().getRegion());
			result.setDistrict(layoutBean.getSharedInfoField().getDistrict());
			result.setDateOfReport(layoutBean.getLineField().getDateOfReport());
			result.setFirstContactDate(layoutBean.getLineField().getMultiDaySelector().getStartDate());
			result.setLastContactDate(layoutBean.getLineField().getMultiDaySelector().getEndDate());
			result.setTypeOfContact(layoutBean.getLineField().getTypeOfContact());
			result.setRelationToCase(layoutBean.getLineField().getRelationToCase());

			result.setFirstName(layoutBean.getLineField().getPerson().getFirstName());
			result.setLastName(layoutBean.getLineField().getPerson().getLastName());
			result.setDateOfBirthYYYY(layoutBean.getLineField().getPerson().getBirthDate().getDateOfBirthYYYY());
			result.setDateOfBirthMM(layoutBean.getLineField().getPerson().getBirthDate().getDateOfBirthMM());
			result.setDateOfBirthDD(layoutBean.getLineField().getPerson().getBirthDate().getDateOfBirthDD());
			result.setSex(layoutBean.getLineField().getPerson().getSex());

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

		private CaseReferenceDto caze;
		private Disease disease;
		private RegionReferenceDto region;
		private DistrictReferenceDto district;
		private LocalDate dateOfReport;
		private LocalDate firstContactDate;
		private LocalDate lastContactDate;
		private ContactProximity typeOfContact;
		private ContactRelation relationToCase;
		private String firstName;
		private String lastName;
		private Integer dateOfBirthDD;
		private Integer dateOfBirthMM;
		private Integer dateOfBirthYYYY;
		private Sex sex;

		public CaseReferenceDto getCaze() {
			return caze;
		}

		public void setCaze(CaseReferenceDto caze) {
			this.caze = caze;
		}

		public Disease getDisease() {
			return disease;
		}

		public void setDisease(Disease disease) {
			this.disease = disease;
		}

		public RegionReferenceDto getRegion() {
			return region;
		}

		public void setRegion(RegionReferenceDto region) {
			this.region = region;
		}

		public DistrictReferenceDto getDistrict() {
			return district;
		}

		public void setDistrict(DistrictReferenceDto district) {
			this.district = district;
		}

		public LocalDate getDateOfReport() {
			return dateOfReport;
		}

		public void setDateOfReport(LocalDate dateOfReport) {
			this.dateOfReport = dateOfReport;
		}

		public LocalDate getFirstContactDate() {
			return firstContactDate;
		}

		public void setFirstContactDate(LocalDate firstContactDate) {
			this.firstContactDate = firstContactDate;
		}

		public LocalDate getLastContactDate() {
			return lastContactDate;
		}

		public void setLastContactDate(LocalDate lastContactDate) {
			this.lastContactDate = lastContactDate;
		}

		public ContactProximity getTypeOfContact() {
			return typeOfContact;
		}

		public void setTypeOfContact(ContactProximity typeOfContact) {
			this.typeOfContact = typeOfContact;
		}

		public ContactRelation getRelationToCase() {
			return relationToCase;
		}

		public void setRelationToCase(ContactRelation relationToCase) {
			this.relationToCase = relationToCase;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public Integer getDateOfBirthDD() {
			return dateOfBirthDD;
		}

		public void setDateOfBirthDD(Integer dateOfBirthDD) {
			this.dateOfBirthDD = dateOfBirthDD;
		}

		public Integer getDateOfBirthMM() {
			return dateOfBirthMM;
		}

		public void setDateOfBirthMM(Integer dateOfBirthMM) {
			this.dateOfBirthMM = dateOfBirthMM;
		}

		public Integer getDateOfBirthYYYY() {
			return dateOfBirthYYYY;
		}

		public void setDateOfBirthYYYY(Integer dateOfBirthYYYY) {
			this.dateOfBirthYYYY = dateOfBirthYYYY;
		}

		public Sex getSex() {
			return sex;
		}

		public void setSex(Sex sex) {
			this.sex = sex;
		}
	}
}
