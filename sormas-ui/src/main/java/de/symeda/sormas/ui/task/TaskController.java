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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.server.Page;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import de.aleri.labcertificategenerator.Generator;
import de.aleri.labcertificategenerator.entity.GesundheitsamtDto;
import de.aleri.labcertificategenerator.entity.StaPersonDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseController;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TaskController {

	public TaskController() {

	}

	public void create(TaskContext context, ReferenceDto entityRef, Runnable callback) {

		TaskEditForm createForm = new TaskEditForm(true, false);
		createForm.setValue(createNewTask(context, entityRef));
		final CommitDiscardWrapperComponent<TaskEditForm> editView = new CommitDiscardWrapperComponent<TaskEditForm>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.TASK_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					TaskDto dto = createForm.getValue();
					FacadeProvider.getTaskFacade().saveTask(dto);
					callback.run();
				}
			}
		});

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingCreateNewTask));
	}

	public void createSampleCollectionTask(TaskContext context, ReferenceDto entityRef, SampleDto sample) {

		TaskEditForm createForm = new TaskEditForm(true, false);
		TaskDto taskDto = createNewTask(context, entityRef);
		taskDto.setTaskType(TaskType.SAMPLE_COLLECTION);
		taskDto.setCreatorComment(sample.getNoTestPossibleReason());
		taskDto.setAssigneeUser(sample.getReportingUser());
		createForm.setValue(taskDto);

		final CommitDiscardWrapperComponent<TaskEditForm> createView = new CommitDiscardWrapperComponent<TaskEditForm>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.TASK_CREATE),
			createForm.getFieldGroup());
		createView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					TaskDto dto = createForm.getValue();
					FacadeProvider.getTaskFacade().saveTask(dto);
				}
			}
		});

		VaadinUiUtil.showModalPopupWindow(createView, I18nProperties.getString(Strings.headingCreateNewTask));
	}

	public void edit(TaskIndexDto dto, Runnable callback, boolean editedFromTaskGrid) {

		// get fresh data
		TaskDto newDto = FacadeProvider.getTaskFacade().getByUuid(dto.getUuid());

		TaskEditForm form = new TaskEditForm(false, editedFromTaskGrid);
		form.setValue(newDto);
		final CommitDiscardWrapperComponent<TaskEditForm> editView =
			new CommitDiscardWrapperComponent<TaskEditForm>(form, UserProvider.getCurrent().hasUserRight(UserRight.TASK_EDIT), form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditTask));

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					TaskDto dto = form.getValue();
					FacadeProvider.getTaskFacade().saveTask(dto);

					if (!editedFromTaskGrid && dto.getCaze() != null) {
						ControllerProvider.getCaseController().navigateToCase(dto.getCaze().getUuid());
					}

					popupWindow.close();
					callback.run();
				}
			}
		});

		editView.addDiscardListener(() -> popupWindow.close());

		if (UserProvider.getCurrent().hasUserRole(UserRole.ADMIN)) {
			editView.addDeleteListener(new DeleteListener() {

				@Override
				public void onDelete() {
					FacadeProvider.getTaskFacade().deleteTask(newDto);
					UI.getCurrent().removeWindow(popupWindow);
					callback.run();
				}
			}, I18nProperties.getString(Strings.entityTask));
		}
	}

	private TaskDto createNewTask(TaskContext context, ReferenceDto entityRef) {
		TaskDto task = TaskDto.build(context, entityRef);
		task.setCreatorUser(UserProvider.getCurrent().getUserReference());
		return task;
	}

	public void deleteAllSelectedItems(Collection<TaskIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoTasksSelected),
				I18nProperties.getString(Strings.messageNoTasksSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(
				String.format(I18nProperties.getString(Strings.confirmationDeleteTasks), selectedRows.size()),
				new Runnable() {

					public void run() {
						for (TaskIndexDto selectedRow : selectedRows) {
							FacadeProvider.getTaskFacade().deleteTask(FacadeProvider.getTaskFacade().getByUuid(selectedRow.getUuid()));
						}
						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingTasksDeleted),
							I18nProperties.getString(Strings.messageTasksDeleted),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}

	public void showBulkTaskDataEditComponent(Collection<? extends TaskIndexDto> selectedTasks, Runnable callback) {
		if (selectedTasks.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoTasksSelected),
				I18nProperties.getString(Strings.messageNoTasksSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		// Check if tasks with multiple districts have been selected
		// In that situation we won't allow editing them
		List<String> taskUuids = selectedTasks.stream().map(TaskIndexDto::getUuid).collect(Collectors.toList());
		List<DistrictReferenceDto> districts = FacadeProvider.getTaskFacade().getDistrictsByTaskUuids(taskUuids, 2L);
		if (districts.size() == 2) {
			new Notification(
				I18nProperties.getString(Strings.headingUnavailableTaskEdition),
				I18nProperties.getString(Strings.messageUnavailableTaskEditionDueToDifferentDistricts),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		// Create a temporary task in order to use the CommitDiscardWrapperComponent
		TaskBulkEditData bulkEditData = new TaskBulkEditData();
		BulkTaskDataForm form = new BulkTaskDataForm(districts.stream().findFirst().orElse(null));
		form.setValue(bulkEditData);
		final CommitDiscardWrapperComponent<BulkTaskDataForm> editView = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditTask));

		editView.addCommitListener(() -> {
			TaskBulkEditData updatedBulkEditData = form.getValue();
			for (TaskIndexDto indexDto : selectedTasks) {
				TaskDto dto = FacadeProvider.getTaskFacade().getByUuid(indexDto.getUuid());
				if (form.getPriorityCheckbox().getValue()) {
					dto.setPriority(updatedBulkEditData.getTaskPriority());
				}
				if (form.getAssigneeCheckbox().getValue()) {
					dto.setAssigneeUser(updatedBulkEditData.getTaskAssignee());
				}
				if (form.getTaskStatusCheckbox().getValue()) {
					dto.setTaskStatus(updatedBulkEditData.getTaskStatus());
				}

				FacadeProvider.getTaskFacade().saveTask(dto);
			}
			popupWindow.close();
			Notification.show(I18nProperties.getString(Strings.messageTasksEdited), Type.HUMANIZED_MESSAGE);
			callback.run();
		});

		editView.addDiscardListener(popupWindow::close);
	}

	public void archiveAllSelectedItems(Collection<? extends TaskIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoTasksSelected),
				I18nProperties.getString(Strings.messageNoTasksSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmArchiving),
				new Label(String.format(I18nProperties.getString(Strings.confirmationArchiveTasks), selectedRows.size())),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				e -> {
					if (e.booleanValue()) {
						List<String> caseUuids = selectedRows.stream().map(TaskIndexDto::getUuid).collect(Collectors.toList());
						FacadeProvider.getTaskFacade().updateArchived(caseUuids, true);
						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingTasksArchived),
							I18nProperties.getString(Strings.messageTasksArchived),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}

	public void dearchiveAllSelectedItems(Collection<? extends TaskIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoTasksSelected),
				I18nProperties.getString(Strings.messageNoTasksSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmDearchiving),
				new Label(String.format(I18nProperties.getString(Strings.confirmationDearchiveTasks), selectedRows.size())),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				e -> {
					if (e.booleanValue() == true) {
						List<String> caseUuids = selectedRows.stream().map(TaskIndexDto::getUuid).collect(Collectors.toList());
						FacadeProvider.getTaskFacade().updateArchived(caseUuids, false);
						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingTasksDearchived),
							I18nProperties.getString(Strings.messageTasksDearchived),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}

	public void markAsDone(Collection<? extends TaskIndexDto> selectedTasks){
		if (selectedTasks.size() == 0) {
			new Notification(
					I18nProperties.getString(Strings.headingNoTasksSelected),
					I18nProperties.getString(Strings.messageNoTasksSelected),
					Type.WARNING_MESSAGE,
					false).show(Page.getCurrent());
			return;
		}
		for (TaskIndexDto task: selectedTasks) {
			TaskDto taskDto = FacadeProvider.getTaskFacade().getByUuid(task.getUuid());
			taskDto.setTaskStatus(TaskStatus.DONE);
			FacadeProvider.getTaskFacade().saveTask(taskDto);
		}
	}

	public String printLabornachweise(Collection<? extends TaskIndexDto> selectedTasks) {

		if (selectedTasks.size() == 0) {
			new Notification(
					I18nProperties.getString(Strings.headingNoTasksSelected),
					I18nProperties.getString(Strings.messageNoTasksSelected),
					Type.WARNING_MESSAGE,
					false).show(Page.getCurrent());
			return null;
		}

		List<TaskDto> tasks = new ArrayList<>();

		for (TaskIndexDto currentTask: selectedTasks) {
			if(currentTask.getTaskType() == TaskType.SAMPLE_COLLECTION){
				TaskDto task = FacadeProvider.getTaskFacade().getByUuid(currentTask.getUuid());
				tasks.add(task);
			}
		}

		List<String> paths = new ArrayList<>();
		for (TaskDto currentTask: tasks) {
			String path = this.generateCovTestPdf(currentTask);
			paths.add(path);
		}

		try {
			String path = FacadeProvider.getConfigFacade().getTempFilesPath() + Generator.generateGuid() + "_merged" + File.separator;
			Generator.createMergedPdf(paths, path);

			return path + "merged.pdf";
		}catch (Exception e){
			LoggerFactory.getLogger(CaseController.class).error(e.getMessage());
		}

		return null;
	}

	public String generateCovTestPdf(TaskDto task){
		try {
			PersonDto person = null;

			if(task.getTaskContext().equals(TaskContext.CASE)){
				CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(task.getCaze().getUuid());
				person = FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid());
			}
			else if(task.getTaskContext().equals(TaskContext.CONTACT)){
				ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(task.getContact().getUuid());
				person = FacadeProvider.getPersonFacade().getPersonByUuid(contact.getPerson().getUuid());
			}

			if(person == null){
				throw new RuntimeException("Person is null but required for generation");
			}

			if(person.getBirthdateYYYY() == null || person.getBirthdateMM() == null || person.getBirthdateDD() == null){
				new Notification(
						I18nProperties.getString(Strings.headingBirthDateNotPresent),
						I18nProperties.getString(Strings.errorBirthDateNotPresent),
						Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				return null;
			}

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
			staPerson.setKostentraeger(task.getLabCertificate().getPayerNumber());
			staPerson.setBetriebsSt(task.getLabCertificate().getOperatingFacilityNumber());
			staPerson.setArztNr(task.getLabCertificate().getDoctorNumber());
			staPerson.setAusstellungsDatum(new Date());
			staPerson.setErsttest(task.getLabCertificate().isFirstTest());
			staPerson.setWeitererTest(task.getLabCertificate().isNextTest());
			staPerson.setTaetigkeit(task.getLabCertificate().isWorkingInFacility());
			staPerson.setGemeinschaft(task.getLabCertificate().isCommunityFacility());
			staPerson.setBetraut(task.getLabCertificate().isLivingInFacility());
			staPerson.setVerbreitung(task.getLabCertificate().isOutbreakPrevention());
			staPerson.setWohneinrichtung(task.getLabCertificate().isCareFacility());
			staPerson.setTel(person.getPhone());
			staPerson.setRvo(task.getLabCertificate().isTestV());
			staPerson.setRisikoApp(task.getLabCertificate().isCoronaApp());
			staPerson.setZustimmung(task.getLabCertificate().isAgreedToGdpr());
			staPerson.setLabNr(task.getLabCertificate().getLabNumber());
			staPerson.setKontaktPerson(task.getLabCertificate().isContactPerson());
			staPerson.setSelbstzahler(task.getLabCertificate().isSelfPaying());
			staPerson.setRegionalziffer(task.getLabCertificate().getSpecialAgreementCode());
			staPerson.setRegional(task.getLabCertificate().isSpecialAgreement());
			staPerson.setAusbruch(task.getLabCertificate().isOutbreak());
			staPerson.setMedeinrichtung(task.getLabCertificate().isMedicalFacility());
			staPerson.setSonstige(task.getLabCertificate().isOtherFacility());

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

			FacilityDto healthDepartment = FacadeProvider.getFacilityFacade().getByUuid(task.getLabCertificate().getHealthDepartment().getUuid());
			GesundheitsamtDto ga = new GesundheitsamtDto();
			ga.setOrt(healthDepartment.getPostalCode() + " " + healthDepartment.getCity());
			ga.setAmt(healthDepartment.getDepartment());
			ga.setBereich(healthDepartment.getSector());
			ga.setArzt(healthDepartment.getDrName());
			ga.setStr(healthDepartment.getStreet() + " " + healthDepartment.getHouseNumber());
			ga.setPlz(healthDepartment.getPostalCode());
			ga.setTel(healthDepartment.getTelNo());
			ga.setFax(healthDepartment.getFaxNo());

			String guid = task.getLabCertificate().getLabCertificateGuid();

			String path = FacadeProvider.getConfigFacade().getTempFilesPath() + guid + File.separator;
			Generator generator = new Generator(staPerson, ga, guid);
			generator.savePdf(path + "nachweis.pdf", path);
			return path + "nachweis.pdf";
		}
		catch (Exception e){
			LoggerFactory.getLogger(CaseController.class).error(e.getMessage());
		}

		return null;
	}
}
