/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.task;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;
import de.aleri.labcertificategenerator.Generator;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.labcertificate.LabCertificateDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AdvancedFileDownloader;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullToBooleanConverter;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.TaskStatusValidator;

public class TaskEditForm extends AbstractEditForm<TaskDto> {

	private static final long serialVersionUID = 1L;

	private static final String SAVE_INFO = "saveInfo";
	private static final String ASSIGNEE_MISSING_INFO = "assigneeMissingInfo";

	private static final String GENERATE_BTN = "generateLabornachweis";
	private HorizontalLayout generateLayout;
	private TextField labornachweisField;
	private Button generateButton;
	private CheckBox testV;

	private ComboBox healthDepartments;
	private static final String HEALTH_DEPARTMENTS = "healthDepartments";


	//@formatter:off
	private static final String HTML_LAYOUT = 
		fluidRow(
			loc(TaskDto.TASK_CONTEXT),
			locs(TaskDto.CAZE, TaskDto.EVENT, TaskDto.CONTACT)) +
			fluidRowLocs(TaskDto.TASK_TYPE) +
			fluidRowLocs(LabCertificateDto.PAYER_NUMBER, LabCertificateDto.OPERATING_FACILITY_NUMBER, LabCertificateDto.DOCTOR_NUMBER, LabCertificateDto.SPECIAL_AGREEMENT_CODE, LabCertificateDto.LAB_NUMBER) +
			fluidRowLocs(LabCertificateDto.TEST_V, LabCertificateDto.SELF_PAYING, LabCertificateDto.SPECIAL_AGREEMENT) +
			fluidRowLocs(LabCertificateDto.FIRST_TEST, LabCertificateDto.NEXT_TEST, "empty") +
			fluidRowLocs(LabCertificateDto.CONTACT_PERSON, LabCertificateDto.OUTBREAK, LabCertificateDto.OUTBREAK_PREVENTION) +
			fluidRowLocs(LabCertificateDto.CORONA_APP) +
			fluidRowLocs(LabCertificateDto.LIVING_IN_FACILITY, LabCertificateDto.MEDICAL_FACILITY, LabCertificateDto.CARE_FACILITY) +
			fluidRowLocs(LabCertificateDto.WORKING_IN_FACILITY, LabCertificateDto.COM_FACILITY, LabCertificateDto.OTHER_FACILITY) +
			fluidRowLocs(LabCertificateDto.AGREED_TO_GDPR) +
			fluidRowLocs(HEALTH_DEPARTMENTS, GENERATE_BTN) +
			fluidRowLocs(TaskDto.SUGGESTED_START, TaskDto.DUE_DATE) +
			fluidRowLocs(TaskDto.ASSIGNEE_USER, TaskDto.PRIORITY) +
			fluidRowLocs(ASSIGNEE_MISSING_INFO) +
			fluidRowLocs(TaskDto.CREATOR_COMMENT) +
			fluidRowLocs(TaskDto.ASSIGNEE_REPLY) +
			fluidRowLocs(TaskDto.TASK_STATUS) +
			fluidRowLocs(LabCertificateDto.LABCERTIFICATEGUID) +
			fluidRowLocs(SAVE_INFO);
	//@formatter:on

	private UserRight editOrCreateUserRight;
	private boolean editedFromTaskGrid;

	public TaskEditForm(boolean create, boolean editedFromTaskGrid) {

		super(TaskDto.class, TaskDto.I18N_PREFIX);

		this.editedFromTaskGrid = editedFromTaskGrid;
		this.editOrCreateUserRight = editOrCreateUserRight;

		addValueChangeListener(e -> {
			updateByTaskContext();
			updateByCreatingAndAssignee();
		});

		setWidth(680, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();

		}
	}

	@Override
	protected void addFields() {

		addField(TaskDto.CAZE, ComboBox.class);
		addField(TaskDto.EVENT, ComboBox.class);
		addField(TaskDto.CONTACT, ComboBox.class);
		DateTimeField startDate = addDateField(TaskDto.SUGGESTED_START, DateTimeField.class, -1);
		DateTimeField dueDate = addDateField(TaskDto.DUE_DATE, DateTimeField.class, -1);
		dueDate.setImmediate(true);
		addField(TaskDto.PRIORITY, ComboBox.class);
		NullableOptionGroup taskStatus = addField(TaskDto.TASK_STATUS, NullableOptionGroup.class);
		NullableOptionGroup taskContext = addField(TaskDto.TASK_CONTEXT, NullableOptionGroup.class);
		taskContext.setImmediate(true);
		taskContext.addValueChangeListener(event -> updateByTaskContext());

		ComboBox taskTypeField = addField(TaskDto.TASK_TYPE, ComboBox.class);
		taskTypeField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		taskTypeField.setImmediate(true);
		taskTypeField.addValueChangeListener(e -> {
			TaskType taskType = (TaskType) e.getProperty().getValue();
			if (taskType != null) {
				setRequired(taskType.isCreatorCommentRequired(), TaskDto.CREATOR_COMMENT);
			}
			this.updateOnTaskTypeChange(taskType);
		});

		//labornachweisField = addField(LabCertificateDto.LABCERTIFICATEGUID, TextField.class);

		//labornachweisField.setCaption("Labornachweis GUID");
		//labornachweisField.setWidth(100, Unit.PERCENTAGE);

		//setVisible(false, LabCertificateDto.LABCERTIFICATEGUID);

		generateLayout = new HorizontalLayout();
		getContent().addComponent(generateLayout, GENERATE_BTN);

		generateButton = new Button("Generieren");
		generateButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		generateButton.setIcon(VaadinIcons.PLUS_CIRCLE);
		//generateLayout.addComponent(labornachweisField);

		this.healthDepartments = new ComboBox();
		getContent().addComponent(this.healthDepartments, HEALTH_DEPARTMENTS);
		List<FacilityReferenceDto> facilities = FacadeProvider.getFacilityFacade().getAllHealthDepartments();

		this.healthDepartments.addItems(facilities);
		this.healthDepartments.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		this.healthDepartments.setImmediate(true);
		this.healthDepartments.setVisible(false);
		this.healthDepartments.setCaption(I18nProperties.getCaption(Captions.Task_healthDepartment));

		this.healthDepartments.addValueChangeListener(e -> {
			if(getValue() != null) {
				FacilityReferenceDto facilityReferenceDto = (FacilityReferenceDto) e.getProperty().getValue();
				getValue().getLabCertificate().setHealthDepartment(facilityReferenceDto);
			}
		});


		if(getValue() == null && facilities.size() == 1){
			this.healthDepartments.setValue(facilities.get(0));
		}



		final AdvancedFileDownloader downloader = new AdvancedFileDownloader();
		downloader
			.addAdvancedDownloaderListener(new AdvancedFileDownloader.AdvancedDownloaderListener() {
				/**
				 * This method will be invoked just before the download
				 * starts. Thus, a new file path can be set.
				 *
				 * @param downloadEvent
				 */
				@Override
				public void beforeDownload(AdvancedFileDownloader.DownloaderEvent downloadEvent) {

					String pdfPath = ControllerProvider.getTaskController().generateCovTestPdf(getValue());
					downloader.setFilePath(pdfPath);
				}
			});
		downloader.extend(generateButton);

		generateLayout.addComponent(generateButton);
		generateLayout.setComponentAlignment(generateButton, Alignment.MIDDLE_LEFT);
		generateLayout.setVisible(false);

		generateButton.setEnabled(UserProvider.getCurrent().hasUserRight(UserRight.TASK_PRINT_LAB_CERTIFICATE));

		createLabDocFields();

		ComboBox assigneeUser = addField(TaskDto.ASSIGNEE_USER, ComboBox.class);
		assigneeUser.addValueChangeListener(e -> {
			updateByCreatingAndAssignee();
			checkIfAssigneeEmailOrPhoneIsProvided((UserReferenceDto) e.getProperty().getValue());
		});
		assigneeUser.setImmediate(true);

		TextArea creatorComment = addField(TaskDto.CREATOR_COMMENT, TextArea.class);
		creatorComment.setRows(2);
		creatorComment.setImmediate(true);
		addField(TaskDto.ASSIGNEE_REPLY, TextArea.class).setRows(4);

		setRequired(true, TaskDto.TASK_CONTEXT, TaskDto.TASK_TYPE, TaskDto.ASSIGNEE_USER, TaskDto.DUE_DATE, TaskDto.TASK_STATUS);
		setReadOnly(true, TaskDto.TASK_CONTEXT, TaskDto.CAZE, TaskDto.CONTACT, TaskDto.EVENT);

		addValueChangeListener(e -> {
			TaskDto taskDto = getValue();

			if (taskDto.getTaskType() == TaskType.CASE_INVESTIGATION && taskDto.getCaze() != null) {
				taskStatus.addValidator(
					new TaskStatusValidator(
						taskDto.getCaze().getUuid(),
						I18nProperties.getValidationError(Validations.investigationStatusUnclassifiedCase)));

				if (!editedFromTaskGrid) {
					final HorizontalLayout saveInfoLayout = new HorizontalLayout(
						new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoSaveOfTask), ContentMode.HTML));
					saveInfoLayout.setSpacing(true);
					saveInfoLayout.setMargin(new MarginInfo(true, false, true, false));
					getContent().addComponent(saveInfoLayout, SAVE_INFO);
				}
			}

			UserDto userDto = UserProvider.getCurrent().getUser();
			List<DistrictReferenceDto> districts;
			List<RegionReferenceDto> regions;
			if (taskDto.getCaze() != null) {
				CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(taskDto.getCaze().getUuid());

				districts = getCaseDistricts(caseDto);
				regions = getCaseRegions(caseDto);
			} else if (taskDto.getContact() != null) {
				ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(taskDto.getContact().getUuid());
				if (contactDto.getRegion() != null && contactDto.getDistrict() != null) {
					districts = DataHelper.asListNullable(contactDto.getDistrict());
					regions = DataHelper.asListNullable(contactDto.getRegion());
				} else {
					CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(contactDto.getCaze().getUuid());
					districts = getCaseDistricts(caseDto);
					regions = getCaseRegions(caseDto);
				}
			} else if (taskDto.getEvent() != null) {
				EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(taskDto.getEvent().getUuid());

				districts = DataHelper.asListNullable(eventDto.getEventLocation().getDistrict());
				regions = DataHelper.asListNullable(eventDto.getEventLocation().getRegion());
			} else {
				districts = DataHelper.asListNullable(userDto.getDistrict());
				regions = DataHelper.asListNullable(userDto.getRegion());
			}

			final List<UserReferenceDto> users = new ArrayList<>();
			if (districts != null) {
				users.addAll(FacadeProvider.getUserFacade().getUserRefsByDistricts(districts, true));
			} else if (regions != null) {
				users.addAll(FacadeProvider.getUserFacade().getUsersByRegionsAndRoles(regions));
			} else {
				// fallback - just show all users
				users.addAll(FacadeProvider.getUserFacade().getAllUserRefs(false));
			}

			// Allow users to assign tasks to users of the next higher jurisdiction level, when the higher jurisdiction contains the users jurisdiction
			// For facility users, this checks where the facility is located and considers the district & community of the faciliy the "higher level"
			// For national users, there is no higher level
			if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.ASSIGN_TASKS_TO_HIGHER_LEVEL)
				&& UserRole.getJurisdictionLevel(userDto.getUserRoles()) != JurisdictionLevel.NATION) {

				List<UserReferenceDto> superordinateUsers = FacadeProvider.getUserFacade().getUsersWithSuperiorJurisdiction(userDto);
				if (superordinateUsers != null) {
					users.addAll(superordinateUsers);
				}
			}

			// Validation
			startDate.addValidator(
				new DateComparisonValidator(
					startDate,
					dueDate,
					true,
					false,
					I18nProperties.getValidationError(Validations.beforeDate, startDate.getCaption(), dueDate.getCaption())));
			dueDate.addValidator(
				new DateComparisonValidator(
					dueDate,
					startDate,
					false,
					false,
					I18nProperties.getValidationError(Validations.afterDate, dueDate.getCaption(), startDate.getCaption())));

			Map<String, Long> userTaskCounts =
				FacadeProvider.getTaskFacade().getPendingTaskCountPerUser(users.stream().map(ReferenceDto::getUuid).collect(Collectors.toList()));
			for (UserReferenceDto user : users) {
				assigneeUser.addItem(user);
				Long userTaskCount = userTaskCounts.get(user.getUuid());
				assigneeUser.setItemCaption(user, user.getCaption() + " (" + (userTaskCount != null ? userTaskCount.toString() : "0") + ")");
			}
		});
	}

	private List<DistrictReferenceDto> getCaseDistricts(CaseDataDto caseDto) {
		List<DistrictReferenceDto> districts = new ArrayList<>(2);

		if (caseDto.getResponsibleDistrict() != null) {
			districts.add(caseDto.getResponsibleDistrict());
		}

		districts.add(caseDto.getDistrict());

		return districts;
	}

	private List<RegionReferenceDto> getCaseRegions(CaseDataDto caseDto) {
		List<RegionReferenceDto> regions = new ArrayList<>(2);

		if (caseDto.getResponsibleDistrict() != null) {
			regions.add(caseDto.getResponsibleRegion());
		}

		regions.add(caseDto.getRegion());

		return regions;
	}

	private <F extends Field> F createAndBindField(String propertyId, Class<F> fieldType, String caption){
		F field = addField(propertyId, fieldType);
		field.setCaption(caption);
		return field;
	}

	private TextField createTextField(String caption, String location){

		TextField textField = getFieldGroup().buildAndBind(caption, (Object) TaskDto.LABCERTIFICATE + "." + location, TextField.class);
		getContent().addComponent(textField, location);
		return textField;
	}

	private CheckBox createCheckBox(String caption, String location){
		CheckBox checkBox = this.getFieldGroup().buildAndBind(caption, TaskDto.LABCERTIFICATE + "." + location, CheckBox.class);
		getContent().addComponent(checkBox, location);
		checkBox.setValue(false);
		checkBox.setConverter(new NullToBooleanConverter());
		return checkBox;
	}

	private void createLabDocFields(){
		TextField payerNumber = createTextField(I18nProperties.getCaption(Captions.Task_payerId), LabCertificateDto.PAYER_NUMBER);

		TextField betriebsstaettenNumber = createTextField(I18nProperties.getCaption(Captions.Task_establishmentId), LabCertificateDto.OPERATING_FACILITY_NUMBER);

		TextField doctorNumber = createTextField(I18nProperties.getCaption(Captions.Task_drId), LabCertificateDto.DOCTOR_NUMBER);

		TextField specialAgreementCode = createTextField(I18nProperties.getCaption(Captions.Task_kvSpecialNumber), LabCertificateDto.SPECIAL_AGREEMENT_CODE);

		TextField labNr = createTextField(I18nProperties.getCaption(Captions.Task_labId), LabCertificateDto.LAB_NUMBER);

		testV = createCheckBox( I18nProperties.getCaption(Captions.Task_testV), LabCertificateDto.TEST_V);

		CheckBox selfPaying = createCheckBox(I18nProperties.getCaption(Captions.Task_selfPaying), LabCertificateDto.SELF_PAYING);

		CheckBox specialAgreement = createCheckBox(I18nProperties.getCaption(Captions.Task_specialAgreement), LabCertificateDto.SPECIAL_AGREEMENT);

		CheckBox firstTest = createCheckBox(I18nProperties.getCaption(Captions.Task_firstTest), LabCertificateDto.FIRST_TEST);

		CheckBox nextTest = createCheckBox(I18nProperties.getCaption(Captions.Task_nextTest), LabCertificateDto.NEXT_TEST);

		CheckBox contactPerson = createCheckBox(I18nProperties.getCaption(Captions.Task_contactPerson), LabCertificateDto.CONTACT_PERSON);

		CheckBox outbreak = createCheckBox(I18nProperties.getCaption(Captions.Task_outbreak), LabCertificateDto.OUTBREAK);

		CheckBox outbreakPrevention = createCheckBox(I18nProperties.getCaption(Captions.Task_outbreakPrevention), LabCertificateDto.OUTBREAK_PREVENTION);

		CheckBox coronaApp = createCheckBox(I18nProperties.getCaption(Captions.Task_coronaApp), LabCertificateDto.CORONA_APP);

		CheckBox livingInFacility = createCheckBox(I18nProperties.getCaption(Captions.Task_livingInFacility), LabCertificateDto.LIVING_IN_FACILITY);

		CheckBox workingInFacility = createCheckBox(I18nProperties.getCaption(Captions.Task_workingInFacility), LabCertificateDto.WORKING_IN_FACILITY);

		CheckBox medicalFacility = createCheckBox(I18nProperties.getCaption(Captions.Task_medicalFacility), LabCertificateDto.MEDICAL_FACILITY);

		CheckBox comFacility = createCheckBox(I18nProperties.getCaption(Captions.Task_comFacility), LabCertificateDto.COM_FACILITY);

		CheckBox careFacility = createCheckBox(I18nProperties.getCaption(Captions.Task_careFacility), LabCertificateDto.CARE_FACILITY);

		CheckBox otherFacility = createCheckBox(I18nProperties.getCaption(Captions.Task_otherFacility), LabCertificateDto.OTHER_FACILITY);

		CheckBox agreedToGdpr = createCheckBox(I18nProperties.getCaption(Captions.Task_agreedToGdpr), LabCertificateDto.AGREED_TO_GDPR);

		payerNumber.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setPayerNumber(payerNumber.getValue());
		});

		betriebsstaettenNumber.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setOperatingFacilityNumber(betriebsstaettenNumber.getValue());
		});


		doctorNumber.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setDoctorNumber(doctorNumber.getValue());
		});

		specialAgreementCode.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setSpecialAgreementCode(specialAgreementCode.getValue());
		});

		labNr.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setLabNumber(labNr.getValue());
		});

		testV.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setTestV(testV.getValue());

			if((boolean) e.getProperty().getValue()){
				selfPaying.setValue(false);
				specialAgreement.setValue(false);
			}
		});

		selfPaying.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setSelfPaying(selfPaying.getValue());

			if((boolean) e.getProperty().getValue()){
				testV.setValue(false);
				specialAgreement.setValue(false);
			}
		});

		specialAgreement.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setSpecialAgreement(specialAgreement.getValue());

			if((boolean) e.getProperty().getValue()){
				selfPaying.setValue(false);
				testV.setValue(false);
				specialAgreementCode.setEnabled(true);
				specialAgreementCode.setRequired(true);
			}
			else{
				specialAgreementCode.setEnabled(false);
				specialAgreementCode.setRequired(false);
			}
		});

		firstTest.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setFirstTest(firstTest.getValue());

			if((boolean) e.getProperty().getValue()){
				nextTest.setValue(false);
			}
		});

		nextTest.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setNextTest(nextTest.getValue());

			if((boolean) e.getProperty().getValue()){
				firstTest.setValue(false);
			}
		});

		contactPerson.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setContactPerson(contactPerson.getValue());

			if((boolean) e.getProperty().getValue()){
				outbreak.setValue(false);
				outbreakPrevention.setValue(false);
				coronaApp.setValue(false);
			}
		});

		outbreak.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setOutbreak(outbreak.getValue());

			if((boolean) e.getProperty().getValue()){
				contactPerson.setValue(false);
				outbreakPrevention.setValue(false);
				coronaApp.setValue(false);
			}
		});

		outbreakPrevention.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setOutbreakPrevention(outbreakPrevention.getValue());

			if((boolean) e.getProperty().getValue()){
				outbreak.setValue(false);
				contactPerson.setValue(false);
				coronaApp.setValue(false);
			}
		});

		coronaApp.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setCoronaApp(coronaApp.getValue());

			if((boolean) e.getProperty().getValue()){
				outbreak.setValue(false);
				outbreakPrevention.setValue(false);
				contactPerson.setValue(false);
			}
		});

		livingInFacility.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setLivingInFacility(livingInFacility.getValue());

			if((boolean) e.getProperty().getValue()){
				workingInFacility.setValue(false);
			}
		});

		workingInFacility.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setWorkingInFacility(workingInFacility.getValue());
			if((boolean) e.getProperty().getValue()){
				livingInFacility.setValue(false);
			}
		});

		medicalFacility.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setMedicalFacility(medicalFacility.getValue());

			if((boolean) e.getProperty().getValue()){
			 	comFacility.setValue(false);
				careFacility.setValue(false);
				otherFacility.setValue(false);
			}
		});

		comFacility.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setCommunityFacility(comFacility.getValue());

			if((boolean) e.getProperty().getValue()){
				medicalFacility.setValue(false);
				careFacility.setValue(false);
				otherFacility.setValue(false);
			}
		});

		careFacility.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setCareFacility(careFacility.getValue());

			if((boolean) e.getProperty().getValue()){
				comFacility.setValue(false);
				medicalFacility.setValue(false);
				otherFacility.setValue(false);
			}
		});

		otherFacility.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setOtherFacility(otherFacility.getValue());

			if((boolean) e.getProperty().getValue()){
				comFacility.setValue(false);
				careFacility.setValue(false);
				medicalFacility.setValue(false);
			}
		});

		agreedToGdpr.addValueChangeListener(e -> {
			if(getValue().getLabCertificate() == null){
				return;
			}
			getValue().getLabCertificate().setAgreedToGdpr(agreedToGdpr.getValue());
		});

		setVisible(false,
				LabCertificateDto.AGREED_TO_GDPR, LabCertificateDto.OPERATING_FACILITY_NUMBER, LabCertificateDto.CARE_FACILITY, LabCertificateDto.COM_FACILITY,
				LabCertificateDto.CONTACT_PERSON, LabCertificateDto.CORONA_APP, LabCertificateDto.DOCTOR_NUMBER, LabCertificateDto.FIRST_TEST, LabCertificateDto.LAB_NUMBER,
				LabCertificateDto.LIVING_IN_FACILITY, LabCertificateDto.MEDICAL_FACILITY, LabCertificateDto.NEXT_TEST, LabCertificateDto.OTHER_FACILITY,
				LabCertificateDto.OUTBREAK, LabCertificateDto.OUTBREAK_PREVENTION, LabCertificateDto.PAYER_NUMBER, LabCertificateDto.SELF_PAYING, LabCertificateDto.SPECIAL_AGREEMENT,
				LabCertificateDto.SPECIAL_AGREEMENT_CODE, LabCertificateDto.TEST_V, LabCertificateDto.WORKING_IN_FACILITY);

	}

	private void checkIfAssigneeEmailOrPhoneIsProvided(UserReferenceDto assigneeRef) {

		if (assigneeRef == null || FacadeProvider.getFeatureConfigurationFacade().isFeatureDisabled(FeatureType.TASK_NOTIFICATIONS)) {
			return;
		}

		UserDto user = FacadeProvider.getUserFacade().getByUuid(assigneeRef.getUuid());
		boolean hasEmail = !StringUtils.isEmpty(user.getUserEmail());
		boolean hasPhoneNumber = !StringUtils.isEmpty(user.getPhone());

		boolean isSmsServiceSetUp = FacadeProvider.getConfigFacade().isSmsServiceSetUp();

		if (isSmsServiceSetUp && !hasEmail && !hasPhoneNumber) {
			getContent().addComponent(
				getMissingInfoComponent(I18nProperties.getString(Strings.infoAssigneeMissingEmailOrPhoneNumber)),
				ASSIGNEE_MISSING_INFO);
		} else if (!isSmsServiceSetUp && !hasEmail) {
			getContent().addComponent(getMissingInfoComponent(I18nProperties.getString(Strings.infoAssigneeMissingEmail)), ASSIGNEE_MISSING_INFO);
		} else {
			getContent().removeComponent(ASSIGNEE_MISSING_INFO);
		}
	}

	private HorizontalLayout getMissingInfoComponent(String caption) {
		Label assigneeMissingInfoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + caption, ContentMode.HTML);
		assigneeMissingInfoLabel.setWidthFull();

		final HorizontalLayout assigneeMissingInfo = new HorizontalLayout(assigneeMissingInfoLabel);
		assigneeMissingInfo.setSpacing(true);
		assigneeMissingInfo.setMargin(new MarginInfo(false, false, true, false));
		assigneeMissingInfo.setWidthFull();

		return assigneeMissingInfo;
	}

	private void updateOnTaskTypeChange(TaskType taskType){
		if(taskType == TaskType.SAMPLE_COLLECTION) {
			if(getValue().getLabCertificate() == null){
				getValue().setLabCertificate(LabCertificateDto.build(getValue()));
			}
			if(getValue().getLabCertificate().getLabCertificateGuid() == null) {
				String guid = Generator.generateGuid();
				getValue().getLabCertificate().setLabCertificateGuid(guid);
				//labornachweisField.setValue(guid);
			}

			if(getValue().getLabCertificate().getHealthDepartment() != null){
				this.healthDepartments.setValue(getValue().getLabCertificate().getHealthDepartment());
			}

			setEnabled( getValue().getLabCertificate().isSpecialAgreement(), LabCertificateDto.SPECIAL_AGREEMENT_CODE);
		}
		setVisible(taskType == TaskType.SAMPLE_COLLECTION,
				LabCertificateDto.AGREED_TO_GDPR, LabCertificateDto.OPERATING_FACILITY_NUMBER, LabCertificateDto.CARE_FACILITY, LabCertificateDto.COM_FACILITY,
				LabCertificateDto.CONTACT_PERSON, LabCertificateDto.CORONA_APP, LabCertificateDto.DOCTOR_NUMBER, LabCertificateDto.FIRST_TEST, LabCertificateDto.LAB_NUMBER,
				LabCertificateDto.LIVING_IN_FACILITY, LabCertificateDto.MEDICAL_FACILITY, LabCertificateDto.NEXT_TEST, LabCertificateDto.OTHER_FACILITY,
				LabCertificateDto.OUTBREAK, LabCertificateDto.OUTBREAK_PREVENTION, LabCertificateDto.PAYER_NUMBER, LabCertificateDto.SELF_PAYING, LabCertificateDto.SPECIAL_AGREEMENT,
				LabCertificateDto.SPECIAL_AGREEMENT_CODE, LabCertificateDto.TEST_V, LabCertificateDto.WORKING_IN_FACILITY, HEALTH_DEPARTMENTS);
		generateLayout.setVisible(taskType == TaskType.SAMPLE_COLLECTION);

		setRequired(taskType == TaskType.SAMPLE_COLLECTION, HEALTH_DEPARTMENTS);


	}

	private void updateByCreatingAndAssignee() {

		TaskDto value = getValue();
		if (value != null) {
			boolean creating = value.getCreationDate() == null;

			UserDto user = UserProvider.getCurrent().getUser();
			boolean creator = user.equals(value.getCreatorUser());
			boolean supervisor = UserRole.isSupervisor(user.getUserRoles());
			boolean assignee = user.equals(getFieldGroup().getField(TaskDto.ASSIGNEE_USER).getValue());

			setVisible(!creating || assignee, TaskDto.ASSIGNEE_REPLY, TaskDto.TASK_STATUS);
			if (creating && !assignee) {
				discard(TaskDto.ASSIGNEE_REPLY, TaskDto.TASK_STATUS);
			}

			if (UserProvider.getCurrent().hasUserRight(editOrCreateUserRight)) {
				setReadOnly(!(assignee || creator), TaskDto.TASK_STATUS);
				setReadOnly(!assignee, TaskDto.ASSIGNEE_REPLY);
				setReadOnly(
					!creator,
					TaskDto.TASK_TYPE,
					TaskDto.PRIORITY,
					TaskDto.SUGGESTED_START,
					TaskDto.DUE_DATE,
					TaskDto.ASSIGNEE_USER,
					TaskDto.CREATOR_COMMENT);
				setReadOnly(
					!(creator || supervisor),
					TaskDto.PRIORITY,
					TaskDto.SUGGESTED_START,
					TaskDto.DUE_DATE,
					TaskDto.ASSIGNEE_USER,
					TaskDto.CREATOR_COMMENT);
			}
		}
	}

	private void updateByTaskContext() {
		TaskContext taskContext = (TaskContext) getFieldGroup().getField(TaskDto.TASK_CONTEXT).getValue();

		// Task types depending on task context
		ComboBox taskType = (ComboBox) getFieldGroup().getField(TaskDto.TASK_TYPE);
		FieldHelper.updateItems(taskType, TaskType.getTaskTypes(taskContext));

		// context reference depending on task context
		ComboBox caseField = (ComboBox) getFieldGroup().getField(TaskDto.CAZE);
		ComboBox eventField = (ComboBox) getFieldGroup().getField(TaskDto.EVENT);
		ComboBox contactField = (ComboBox) getFieldGroup().getField(TaskDto.CONTACT);
		if (taskContext != null) {
			switch (taskContext) {
			case CASE:
				FieldHelper.setFirstVisibleClearOthers(caseField, eventField, contactField);
				FieldHelper.setFirstRequired(caseField, eventField, contactField);
				break;
			case EVENT:
				FieldHelper.setFirstVisibleClearOthers(eventField, caseField, contactField);
				FieldHelper.setFirstRequired(eventField, caseField, contactField);
				break;
			case CONTACT:
				FieldHelper.setFirstVisibleClearOthers(contactField, caseField, eventField);
				FieldHelper.setFirstRequired(contactField, caseField, eventField);
				break;
			case GENERAL:
				FieldHelper.setFirstVisibleClearOthers(null, caseField, contactField, eventField);
				FieldHelper.setFirstRequired(null, caseField, contactField, eventField);
				break;
			}
		} else {
			FieldHelper.setFirstVisibleClearOthers(null, caseField, eventField, contactField);
			FieldHelper.setFirstRequired(null, caseField, eventField, contactField);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
