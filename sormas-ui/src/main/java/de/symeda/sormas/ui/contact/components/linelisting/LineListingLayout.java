package de.symeda.sormas.ui.contact.components.linelisting;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.components.linelisting.PersonField;
import de.symeda.sormas.ui.utils.components.linelisting.PersonFieldDto;
import de.symeda.sormas.ui.utils.components.multidayselector.MultiDaySelectorDto;

public class LineListingLayout extends VerticalLayout {

	public static final float DEFAULT_WIDTH = 1696;

	private final CaseSelector caseSelector;
	private final ComboBox<Disease> disease;
	private final ComboBox<RegionReferenceDto> region;
	private final ComboBox<DistrictReferenceDto> district;

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

		caseSelector = new CaseSelector();
		caseSelector.setId("lineListingCase");
		sharedInformationComponent.addComponent(caseSelector);

		HorizontalLayout sharedInformationBar = new HorizontalLayout();
		sharedInformationBar.addStyleName(CssStyles.SPACING_SMALL);

		disease = new ComboBox<>(I18nProperties.getCaption(Captions.disease));
		disease.setId("lineListingDisease");
		disease.setItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		sharedInformationBar.addComponent(disease);

		region = new ComboBox<>(I18nProperties.getCaption(Captions.region));
		region.setId("lineListingRegion");
		sharedInformationBar.addComponent(region);

		district = new ComboBox<>(I18nProperties.getCaption(Captions.district));
		district.setId("lineListingDistrict");
		sharedInformationBar.addComponent(district);

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = e.getValue();
			updateDistricts(regionDto);
		});

		sharedInformationComponent.addComponent(sharedInformationBar);

		addComponent(sharedInformationComponent);

		contactLines = new ArrayList<>();
		VerticalLayout lineComponent = new VerticalLayout();
		lineComponent.setMargin(false);

		Label lineComponentLabel = new Label();
		lineComponentLabel.setValue(I18nProperties.getCaption(Captions.lineListingNewCasesList));
		lineComponentLabel.addStyleName(CssStyles.H3);
		lineComponent.addComponent(lineComponentLabel);

		ContactLineLayout line = new ContactLineLayout(lineComponent, 0);
		line.setBean(new ContactLineDto());
		contactLines.add(line);
		lineComponent.addComponent(line);
		lineComponent.setSpacing(false);
		addComponent(lineComponent);

		UserProvider currentUserProvider = UserProvider.getCurrent();
		if (currentUserProvider != null && UserRole.isSupervisor(currentUserProvider.getUserRoles())) {
			RegionReferenceDto userRegion = currentUserProvider.getUser().getRegion();
			region.setValue(userRegion);
			region.setVisible(false);
			updateDistricts(userRegion);
		} else {
			region.setItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		}

		HorizontalLayout actionBar = new HorizontalLayout();
		Button addLine = ButtonHelper.createIconButton(Captions.lineListingAddLine, VaadinIcons.PLUS, e -> {
			ContactLineLayout newLine = new ContactLineLayout(lineComponent, contactLines.size() + 1);
			ContactLineDto lastLineDto = contactLines.get(contactLines.size() - 1).getBean();
			ContactLineDto newLineDto = new ContactLineDto();
			newLineDto.setCaze(lastLineDto.getCaze());
			newLineDto.setDisease(lastLineDto.getDisease());
			newLineDto.setRegion(lastLineDto.getRegion());
			newLineDto.setDistrict(lastLineDto.getDistrict());
			newLineDto.setDateOfReport(lastLineDto.getDateOfReport());
			newLineDto.setMultiDaySelector(lastLineDto.getMultiDaySelector());
			newLineDto.setTypeOfContact(lastLineDto.getTypeOfContact());
			newLineDto.setRelationToCase(lastLineDto.getRelationToCase());
			newLine.setBean(newLineDto);
			newLine.addDeleteLineListener(evt -> {
				ContactLineLayout selectedLine = (ContactLineLayout) evt.getSource();
				lineComponent.removeComponent(selectedLine);
				contactLines.remove(selectedLine);
				contactLines.get(0).formatLine(0);
				if (contactLines.size() > 1) {
					contactLines.get(0).getDelete().setEnabled(true);
				}
			});
			contactLines.add(newLine);
			lineComponent.addComponent(newLine);

			if (contactLines.size() > 1) {
				contactLines.get(0).getDelete().setEnabled(true);
			}
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

	private void updateDistricts(RegionReferenceDto regionDto) {
		FieldHelper.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
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

	class ContactLineLayout extends HorizontalLayout {

		private final Binder<ContactLineDto> binder = new Binder<>(ContactLineDto.class);

		private final DateField dateOfReport;
		private final MultiDayContactField multiDay;

		private final ComboBox<ContactProximity> typeOfContact;
		private final ComboBox<ContactRelation> relationToCase;
		private final PersonField person;

		private final Button delete;

		public ContactLineLayout(VerticalLayout lineComponent, int lineIndex) {

			addStyleName(CssStyles.SPACING_SMALL);
			setMargin(false);

			binder.forField(caseSelector).bind(ContactLineDto.CAZE);
			binder.forField(disease).asRequired().bind(ContactLineDto.DISEASE);
			binder.forField(region).asRequired().bind(ContactLineDto.REGION);
			binder.forField(district).asRequired().bind(ContactLineDto.DISTRICT);

			dateOfReport = new DateField();
			dateOfReport.setId("lineListingDateOfReport_" + lineIndex);
			dateOfReport.setWidth(150, Unit.PIXELS);
			binder.forField(dateOfReport).asRequired().bind(ContactLineDto.DATE_OF_REPORT);
			dateOfReport.setRangeEnd(LocalDate.now());

			multiDay = new MultiDayContactField();
			multiDay.setId("lineListingMultiDay_" + lineIndex);
			binder.forField(multiDay).bind(ContactLineDto.MULTI_DAY_SELECTOR);

			typeOfContact = new ComboBox<>();
			typeOfContact.setId("lineListingContactProximity_" + lineIndex);
			typeOfContact.setItems(ContactProximity.values());
			typeOfContact.setWidth(150, Unit.PIXELS);
			typeOfContact.addStyleName(CssStyles.CAPTION_OVERFLOW);
			binder.forField(typeOfContact).bind(ContactLineDto.TYPE_OF_CONTACT);

			relationToCase = new ComboBox<>();
			relationToCase.setId("lineListingRelationToCase_" + lineIndex);
			relationToCase.setItems(ContactRelation.values());
			relationToCase.setWidth(150, Unit.PIXELS);
			relationToCase.addStyleName(CssStyles.CAPTION_OVERFLOW);
			binder.forField(relationToCase).bind(ContactLineDto.RELATION_TO_CASE);

			person = new PersonField();
			person.setId("lineListingPerson_" + lineIndex);
			binder.forField(person).bind(ContactLineDto.PERSON);

			delete = ButtonHelper.createIconButtonWithCaption("delete_" + lineIndex, null, VaadinIcons.TRASH, event -> {
				fireEvent(new DeleteLineEvent(this));
			});

			addComponents(dateOfReport, multiDay, typeOfContact, relationToCase, person, delete);
			formatLine(lineIndex);
		}

		public Registration addDeleteLineListener(DeleteLineListener deleteLineListener) {
			return addListener(DeleteLineEvent.class, deleteLineListener, DeleteLineListener.DELETE_LINE_METHOD);
		}

		public void setBean(ContactLineDto bean) {
			binder.setBean(bean);
		}

		public ContactLineDto getBean() {
			return binder.getBean();
		}

		public boolean hasErrors() {
			BinderValidationStatus<PersonFieldDto> personValidationStatus = person.validate();
			BinderValidationStatus<ContactLineDto> lineValidationStatus = binder.validate();
			return personValidationStatus.hasErrors() || lineValidationStatus.hasErrors();
		}

		private void formatLine(int lineIndex) {

			dateOfReport.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REPORT_DATE));
			dateOfReport.removeStyleName(CssStyles.CAPTION_HIDDEN);
			setComponentAlignment(dateOfReport, Alignment.BOTTOM_LEFT);
			multiDay.showCaptions();
			typeOfContact.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.CONTACT_PROXIMITY));
			typeOfContact.removeStyleName(CssStyles.CAPTION_HIDDEN);
			setComponentAlignment(typeOfContact, Alignment.BOTTOM_LEFT);
			relationToCase.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.RELATION_TO_CASE));
			relationToCase.removeStyleName(CssStyles.CAPTION_HIDDEN);
			setComponentAlignment(relationToCase, Alignment.BOTTOM_LEFT);
			person.showCaptions();
			setComponentAlignment(person, Alignment.BOTTOM_LEFT);
			delete.setEnabled(lineIndex > 0);
			setComponentAlignment(delete, Alignment.MIDDLE_LEFT);
		}

		public Button getDelete() {
			return delete;
		}
	}

	public static class ContactLineDto implements Serializable {

		public static final String CAZE = "caze";
		public static final String DISEASE = "disease";
		public static final String REGION = "region";
		public static final String DISTRICT = "district";
		public static final String DATE_OF_REPORT = "dateOfReport";
		public static final String MULTI_DAY_SELECTOR = "multiDaySelector";
		public static final String TYPE_OF_CONTACT = "typeOfContact";
		public static final String RELATION_TO_CASE = "relationToCase";
		public static final String PERSON = "person";

		private CaseReferenceDto caze;
		private Disease disease;
		private RegionReferenceDto region;
		private DistrictReferenceDto district;
		private LocalDate dateOfReport;
		private MultiDaySelectorDto multiDaySelector;
		private ContactProximity typeOfContact;
		private ContactRelation relationToCase;
		private PersonFieldDto person;

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

		public MultiDaySelectorDto getMultiDaySelector() {
			return multiDaySelector;
		}

		public void setMultiDaySelector(MultiDaySelectorDto multiDaySelector) {
			this.multiDaySelector = multiDaySelector;
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

		public PersonFieldDto getPerson() {
			return person;
		}

		public void setPerson(PersonFieldDto person) {
			this.person = person;
		}
	}
}
