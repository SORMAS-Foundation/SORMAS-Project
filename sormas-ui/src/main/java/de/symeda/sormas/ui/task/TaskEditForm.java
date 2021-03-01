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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.person.PersonDto;
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
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
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

	//@formatter:off
	private static final String HTML_LAYOUT = 
		fluidRow(
			loc(TaskDto.TASK_CONTEXT),
			locs(TaskDto.CAZE, TaskDto.EVENT, TaskDto.CONTACT)) +
			fluidRowLocs(TaskDto.TASK_TYPE) +
			fluidRowLocs(TaskDto.PAYER_NUMBER, TaskDto.BETRIEBSSTAETTEN_NUMBER, TaskDto.DOCTOR_NUMBER, TaskDto.SPECIAL_AGREEMENT_CODE, TaskDto.LAB_NUMBER) +
			fluidRowLocs(TaskDto.TEST_V, TaskDto.SELF_PAYING, TaskDto.SPECIAL_AGREEMENT) +
			fluidRowLocs(TaskDto.FIRST_TEST, TaskDto.NEXT_TEST, "empty") +
			fluidRowLocs(TaskDto.CONTACT_PERSON, TaskDto.OUTBREAK, TaskDto.OUTBREAK_PREVENTION) +
			fluidRowLocs(TaskDto.CORONA_APP) +
			fluidRowLocs(TaskDto.LIVING_IN_FACILITY, TaskDto.MEDICAL_FACILITY, TaskDto.CARE_FACILITY) +
			fluidRowLocs(TaskDto.WORKING_IN_FACILITY, TaskDto.COM_FACILITY, TaskDto.OTHER_FACILITY) +
			fluidRowLocs(TaskDto.AGREED_TO_GDPR) +
			fluidRowLocs(GENERATE_BTN) +
			fluidRowLocs(TaskDto.SUGGESTED_START, TaskDto.DUE_DATE) +
			fluidRowLocs(TaskDto.ASSIGNEE_USER, TaskDto.PRIORITY) +
			fluidRowLocs(ASSIGNEE_MISSING_INFO) +
			fluidRowLocs(TaskDto.CREATOR_COMMENT) +
			fluidRowLocs(TaskDto.ASSIGNEE_REPLY) +
			fluidRowLocs(TaskDto.TASK_STATUS) +
			fluidRowLocs(TaskDto.LABORNACHWEIS) +
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

		labornachweisField = addField(TaskDto.LABORNACHWEIS, TextField.class);

		labornachweisField.setCaption("Labornachweis GUID");
		labornachweisField.setWidth(100, Unit.PERCENTAGE);

		setVisible(false, TaskDto.LABORNACHWEIS);

		generateLayout = new HorizontalLayout();
		getContent().addComponent(generateLayout, GENERATE_BTN);

		generateButton = new Button("Generieren");
		generateButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		generateButton.setIcon(VaadinIcons.PLUS_CIRCLE);
		generateLayout.addComponent(labornachweisField);

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
			DistrictReferenceDto district = null;
			RegionReferenceDto region = null;
			if (taskDto.getCaze() != null) {
				CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(taskDto.getCaze().getUuid());
				district = caseDto.getDistrict();
				region = caseDto.getRegion();
			} else if (taskDto.getContact() != null) {
				ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(taskDto.getContact().getUuid());
				if (contactDto.getRegion() != null && contactDto.getDistrict() != null) {
					district = contactDto.getDistrict();
					region = contactDto.getRegion();
				} else {
					CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(contactDto.getCaze().getUuid());
					district = caseDto.getDistrict();
					region = caseDto.getRegion();
				}
			} else if (taskDto.getEvent() != null) {
				EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(taskDto.getEvent().getUuid());
				district = eventDto.getEventLocation().getDistrict();
				region = eventDto.getEventLocation().getRegion();
			} else {
				district = userDto.getDistrict();
				region = userDto.getRegion();
			}

			final List<UserReferenceDto> users = new ArrayList<>();
			if (district != null) {
				users.addAll(FacadeProvider.getUserFacade().getUserRefsByDistrict(district, true));
			} else if (region != null) {
				users.addAll(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(region));
			} else {
				// fallback - just show all users
				users.addAll(FacadeProvider.getUserFacade().getAllUserRefs(false));
			}

			// Allow regional users to assign the task to national ones
			if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.ASSIGN_TASKS_TO_HIGHER_LEVEL)
				&& userDto.getDistrict() == null
				&& userDto.getRegion() != null) {
				users.addAll(
					FacadeProvider.getUserFacade()
						.getUsersByRegionAndRoles(null, UserRole.getWithJurisdictionLevels(JurisdictionLevel.NATION).toArray(new UserRole[0])));
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

	private <F extends Field> F createAndBindField(String propertyId, Class<F> fieldType, String caption){
		F field = addField(propertyId, fieldType);
		field.setCaption(caption);
		return field;
	}

	private void createLabDocFields(){
		TextField payerNumber = createAndBindField(TaskDto.PAYER_NUMBER, TextField.class, I18nProperties.getCaption(Captions.Task_payerId));
		payerNumber.addValueChangeListener(e -> {
			getValue().setPayerNumber(payerNumber.getValue());
		});

		TextField betriebsstaettenNumber = createAndBindField(TaskDto.BETRIEBSSTAETTEN_NUMBER, TextField.class, I18nProperties.getCaption(Captions.Task_establishmentId));
		betriebsstaettenNumber.addValueChangeListener(e -> {
			getValue().setBetriebsstaettenNumber(betriebsstaettenNumber.getValue());
		});

		TextField doctorNumber = createAndBindField(TaskDto.DOCTOR_NUMBER, TextField.class, I18nProperties.getCaption(Captions.Task_drId));
		doctorNumber.addValueChangeListener(e -> {
			getValue().setDoctorNumber(doctorNumber.getValue());
		});

		TextField specialAgreementCode = createAndBindField(TaskDto.SPECIAL_AGREEMENT_CODE, TextField.class, I18nProperties.getCaption(Captions.Task_kvSpecialNumber));
		specialAgreementCode.addValueChangeListener(e -> {
			getValue().setSpecialAgreementCode(specialAgreementCode.getValue());
		});

		TextField labNr = createAndBindField(TaskDto.LAB_NUMBER, TextField.class, I18nProperties.getCaption(Captions.Task_labId));
		labNr.addValueChangeListener(e -> {
			getValue().setLabNumber(labNr.getValue());
		});

		testV = createAndBindField(TaskDto.TEST_V, CheckBox.class, I18nProperties.getCaption(Captions.Task_testV));
		testV.addValueChangeListener(e -> {
			getValue().setTestV(testV.getValue());
		});

		CheckBox selfPaying = addField(TaskDto.SELF_PAYING, CheckBox.class);
		selfPaying.setCaption(I18nProperties.getCaption(Captions.Task_selfPaying));
		selfPaying.addValueChangeListener(e -> {
			getValue().setSelfPaying(selfPaying.getValue());
		});

		CheckBox specialAgreement = addField(TaskDto.SPECIAL_AGREEMENT, CheckBox.class);
		specialAgreement.setCaption(I18nProperties.getCaption(Captions.Task_specialAgreement));
		specialAgreement.addValueChangeListener(e -> {
			getValue().setSpecialAgreement(specialAgreement.getValue());
		});

		CheckBox firstTest = addField(TaskDto.FIRST_TEST, CheckBox.class);
		firstTest.setCaption(I18nProperties.getCaption(Captions.Task_firstTest));
		firstTest.addValueChangeListener(e -> {
			getValue().setFirstTest(firstTest.getValue());
		});

		CheckBox nextTest = addField(TaskDto.NEXT_TEST, CheckBox.class);
		nextTest.setCaption(I18nProperties.getCaption(Captions.Task_nextTest));
		nextTest.addValueChangeListener(e -> {
			getValue().setNextTest(nextTest.getValue());
		});

		CheckBox contactPerson = addField(TaskDto.CONTACT_PERSON, CheckBox.class);
		contactPerson.setCaption(I18nProperties.getCaption(Captions.Task_contactPerson));
		contactPerson.addValueChangeListener(e -> {
			getValue().setContactPerson(contactPerson.getValue());
		});

		CheckBox outbreak = addField(TaskDto.OUTBREAK, CheckBox.class);
		outbreak.setCaption(I18nProperties.getCaption(Captions.Task_outbreak));
		outbreak.addValueChangeListener(e -> {
			getValue().setOutbreak(outbreak.getValue());
		});

		CheckBox outbreakPrevention = addField(TaskDto.OUTBREAK_PREVENTION, CheckBox.class);
		outbreakPrevention.setCaption(I18nProperties.getCaption(Captions.Task_outbreakPrevention));
		outbreakPrevention.addValueChangeListener(e -> {
			getValue().setOutbreakPrevention(outbreakPrevention.getValue());
		});

		CheckBox coronaApp = addField(TaskDto.CORONA_APP, CheckBox.class);
		coronaApp.setCaption(I18nProperties.getCaption(Captions.Task_coronaApp));
		coronaApp.addValueChangeListener(e -> {
			getValue().setCoronaApp(coronaApp.getValue());
		});

		CheckBox livingInFacility = addField(TaskDto.LIVING_IN_FACILITY, CheckBox.class);
		livingInFacility.setCaption(I18nProperties.getCaption(Captions.Task_livingInFacility));
		livingInFacility.addValueChangeListener(e -> {
			getValue().setLivingInFacility(livingInFacility.getValue());
		});

		CheckBox workingInFacility = addField(TaskDto.WORKING_IN_FACILITY, CheckBox.class);
		workingInFacility.setCaption(I18nProperties.getCaption(Captions.Task_workingInFacility));
		workingInFacility.addValueChangeListener(e -> {
			getValue().setWorkingInFacility(workingInFacility.getValue());
		});

		CheckBox medicalFacility = addField(TaskDto.MEDICAL_FACILITY, CheckBox.class);
		medicalFacility.setCaption(I18nProperties.getCaption(Captions.Task_medicalFacility));
		medicalFacility.addValueChangeListener(e -> {
			getValue().setMedicalFacility(medicalFacility.getValue());
		});

		CheckBox comFacility = addField(TaskDto.COM_FACILITY, CheckBox.class);
		comFacility.setCaption(I18nProperties.getCaption(Captions.Task_comFacility));
		comFacility.addValueChangeListener(e -> {
			getValue().setCommunityFacility(comFacility.getValue());
		});

		CheckBox careFacility = addField(TaskDto.CARE_FACILITY, CheckBox.class);
		careFacility.setCaption(I18nProperties.getCaption(Captions.Task_careFacility));
		careFacility.addValueChangeListener(e -> {
			getValue().setCareFacility(careFacility.getValue());
		});

		CheckBox otherFacility = addField(TaskDto.OTHER_FACILITY, CheckBox.class);
		otherFacility.setCaption(I18nProperties.getCaption(Captions.Task_otherFacility));
		otherFacility.addValueChangeListener(e -> {
			getValue().setOtherFacility(otherFacility.getValue());
		});

		CheckBox agreedToGdpr = addField(TaskDto.AGREED_TO_GDPR, CheckBox.class);
		agreedToGdpr.setCaption(I18nProperties.getCaption(Captions.Task_agreedToGdpr));
		agreedToGdpr.addValueChangeListener(e -> {
			getValue().setAgreedToGdpr(agreedToGdpr.getValue());
		});

		setVisible(false, TaskDto.LABORNACHWEIS,
				TaskDto.AGREED_TO_GDPR, TaskDto.BETRIEBSSTAETTEN_NUMBER, TaskDto.CARE_FACILITY, TaskDto.COM_FACILITY,
				TaskDto.CONTACT_PERSON, TaskDto.CORONA_APP, TaskDto.DOCTOR_NUMBER, TaskDto.FIRST_TEST, TaskDto.LAB_NUMBER,
				TaskDto.LIVING_IN_FACILITY, TaskDto.MEDICAL_FACILITY, TaskDto.NEXT_TEST, TaskDto.OTHER_FACILITY,
				TaskDto.OUTBREAK, TaskDto.OUTBREAK_PREVENTION, TaskDto.PAYER_NUMBER, TaskDto.SELF_PAYING, TaskDto.SPECIAL_AGREEMENT,
				TaskDto.SPECIAL_AGREEMENT_CODE, TaskDto.TEST_V, TaskDto.WORKING_IN_FACILITY);
	}

	/*
	private String generateCovTestPdf(){
		try {
			CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getValue().getCaze().getUuid());
			//UserDto user = FacadeProvider.getUserFacade().getByUuid(caze.getClassificationUser().getUuid());
			PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid());

			TaskDto task = getValue();

			StaPersonDto staPerson = new StaPersonDto();
			staPerson.setVorname(person.getFirstName());
			staPerson.setName(person.getLastName());
			staPerson.setAbnahmeDatum(task.getDueDate());
			staPerson.setAbnahmeZeit(task.getDueDate());
			staPerson.setHnr(person.getAddress().getHouseNumber());
			staPerson.setStrasse(person.getAddress().getStreet());
			staPerson.setNatcode("DE");
			staPerson.setPlz(person.getAddress().getPostalCode());
			staPerson.setDatumGeb(new SimpleDateFormat("dd.MM.yyyy").parse(person.getBirthdateDD() + "." + person.getBirthdateMM() + "." + person.getBirthdateYYYY()));
			staPerson.setOrt(person.getAddress().getCity());
			staPerson.setKostentraeger(task.getPayerNumber());
			staPerson.setBetriebsSt(task.getBetriebsstaettenNumber());
			staPerson.setArztNr(task.getDoctorNumber());
			staPerson.setAusstellungsDatum(new Date());
			staPerson.setErsttest(task.isFirstTest());
			staPerson.setWeitererTest(task.isNextTest());
			staPerson.setTaetigkeit(task.isWorkingInFacility());
			staPerson.setGemeinschaft(task.isCommunityFacility());
			staPerson.setBetraut(task.isLivingInFacility());
			staPerson.setVerbreitung(task.isOutbreakPrevention());
			staPerson.setWohneinrichtung(task.isCareFacility());
			staPerson.setTel(person.getPhone());
			staPerson.setRvo(testV.getValue());
			staPerson.setRisikoApp(task.isCoronaApp());
			staPerson.setZustimmung(task.isAgreedToGdpr());
			staPerson.setLabNr(task.getLabNumber());
			//TODO: Woher kommen diese Daten?
			staPerson.setTitel("Dr.");
			staPerson.setZusatz("von");
			staPerson.setKontaktPerson(task.isContactPerson());
			staPerson.setSelbstzahler(task.isSelfPaying());
			staPerson.setRegionalziffer(task.getSpecialAgreementCode());
			staPerson.setRegional(task.isSpecialAgreement());
			staPerson.setAusbruch(task.isOutbreak());
			staPerson.setMedeinrichtung(task.isMedicalFacility());
			staPerson.setSonstige(task.isOtherFacility());

			switch(person.getSex()){
				case MALE:
					staPerson.setGeschlecht("M");
					break;
				case FEMALE:
					staPerson.setGeschlecht("W");
					break;
				case OTHER:
					staPerson.setGeschlecht("D");
					break;
				default:
					staPerson.setGeschlecht("U");
			}

			GaDto ga = new GaDto();
			ga.setGaort("49661 Cloppenburg");
			ga.setGaamt("Cloppenburg");
			ga.setGabereich("Gesundheitsamt");
			ga.setGaarzt("Dr. Hans-Jürgen Stanisla");
			ga.setGastr("Eschstr. 29");
			ga.setGaPlz("49661");
			ga.setGatel("04471/15-0");
			ga.setGafax("04471/15-330");

			String path = FacadeProvider.getConfigFacade().getTempFilesPath() + getValue().getLaborNachweisGuid() + File.separator;
			File dir = new File(path);
			if(!dir.exists()){
				dir.mkdirs();
			}

			Generator generator = new Generator(staPerson, ga, getValue().getLaborNachweisGuid());
			generator.savePdf(path + "nachweis.pdf", path);
			return path + "nachweis.pdf";
		}
		catch (Exception e){

		}

		return null;
	}*/
	

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
		if(taskType == TaskType.SAMPLE_COLLECTION && getValue().getLaborNachweisGuid() == null) {
			String guid = Generator.generateGuid();
			getValue().setLaborNachweisGuid(guid);
			labornachweisField.setValue(guid);
		}
		setVisible(taskType == TaskType.SAMPLE_COLLECTION, TaskDto.LABORNACHWEIS,
				TaskDto.AGREED_TO_GDPR, TaskDto.BETRIEBSSTAETTEN_NUMBER, TaskDto.CARE_FACILITY, TaskDto.COM_FACILITY,
				TaskDto.CONTACT_PERSON, TaskDto.CORONA_APP, TaskDto.DOCTOR_NUMBER, TaskDto.FIRST_TEST, TaskDto.LAB_NUMBER,
				TaskDto.LIVING_IN_FACILITY, TaskDto.MEDICAL_FACILITY, TaskDto.NEXT_TEST, TaskDto.OTHER_FACILITY,
				TaskDto.OUTBREAK, TaskDto.OUTBREAK_PREVENTION, TaskDto.PAYER_NUMBER, TaskDto.SELF_PAYING, TaskDto.SPECIAL_AGREEMENT,
				TaskDto.SPECIAL_AGREEMENT_CODE, TaskDto.TEST_V, TaskDto.WORKING_IN_FACILITY);
		generateLayout.setVisible(taskType == TaskType.SAMPLE_COLLECTION);
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
