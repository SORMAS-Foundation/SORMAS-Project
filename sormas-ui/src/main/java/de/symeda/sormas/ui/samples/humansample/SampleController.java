/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.ui.samples.humansample;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_NONE;
import static java.util.Objects.isNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Buffered.SourceException;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.CaseDataView;
import de.symeda.sormas.ui.contact.ContactDataView;
import de.symeda.sormas.ui.events.EventParticipantDataView;
import de.symeda.sormas.ui.samples.AbstractSampleForm;
import de.symeda.sormas.ui.samples.CollapsiblePathogenTestForm;
import de.symeda.sormas.ui.samples.PathogenTestForm;
import de.symeda.sormas.ui.samples.SampleViewType;
import de.symeda.sormas.ui.samples.SamplesView;
import de.symeda.sormas.ui.samples.SamplesViewConfiguration;
import de.symeda.sormas.ui.samples.environmentsample.EnvironmentSampleDataView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.DeleteRestoreHandlers;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;

public class SampleController {

	public SampleController() {
	}

	public void registerViews(Navigator navigator) {
		navigator.addView(SamplesView.VIEW_NAME, SamplesView.class);
		navigator.addView(SampleDataView.VIEW_NAME, SampleDataView.class);
		navigator.addView(EnvironmentSampleDataView.VIEW_NAME, EnvironmentSampleDataView.class);
	}

	public void navigateToData(String sampleUuid) {
		String navigationState = SampleDataView.VIEW_NAME + "/" + sampleUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateTo(SampleCriteria sampleCriteria) {
		ViewModelProviders.of(SamplesView.class).remove(SampleCriteria.class);
		ViewModelProviders.of(SamplesView.class).get(SampleCriteria.class, sampleCriteria);
		ViewModelProviders.of(SamplesView.class).get(SamplesViewConfiguration.class).setViewType(SampleViewType.HUMAN);
		SormasUI.get().getNavigator().navigateTo(SamplesView.VIEW_NAME);
	}

	public void create(CaseReferenceDto caseRef, Disease disease, Runnable callback) {
		createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), caseRef), disease, callback);
	}

	public void create(ContactReferenceDto contactRef, Disease disease, Runnable callback) {
		createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), contactRef), disease, callback);
	}

	public void create(EventParticipantReferenceDto eventParticipantRef, Disease disease, Runnable callback) {
		createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), eventParticipantRef), disease, callback);
	}

	private void createSample(SampleDto sampleDto, Disease disease, Runnable callback) {
		final CommitDiscardWrapperComponent<SampleCreateForm> editView = getSampleCreateComponent(sampleDto, disease, callback);
		// add option to create additional pathogen tests
		SampleEditPathogenTestListHandler pathogenTestHandler = new SampleEditPathogenTestListHandler();
		addPathogenTestButton(editView, false, null, null, pathogenTestHandler::addPathogenTest);

		editView.setPostCommitListener(() -> {
			pathogenTestHandler.saveAll(sampleDto.toReference());
			SormasUI.refreshView();
		});

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingCreateNewSample));
	}

	/**
	 *
	 * Works just like the addPathogenTestComponent(..., int caseSampleCount, ...), but additionally determines the caseSampleCount.
	 * For performance reasons, this method shall just be called when the caseSampleCount is not known already and just one pathogen test
	 * create component shall be added.
	 *
	 * @param sampleComponent
	 *            to add the pathogen test create component to.
	 */
	public CollapsiblePathogenTestForm addPathogenTestComponent(
		CommitDiscardWrapperComponent<? extends AbstractSampleForm> sampleComponent,
		boolean viaLims,
		boolean deleteOnCancel,
		boolean addSeparator,
		Consumer<PathogenTestDto> saveHandler) {

		int caseSampleCount = caseSampleCountOf(sampleComponent.getWrappedComponent().getValue());
		return addPathogenTestComponent(sampleComponent, null, caseSampleCount, saveHandler, true, viaLims, deleteOnCancel, addSeparator);
	}

	public CollapsiblePathogenTestForm addPathogenTestComponent(
		CommitDiscardWrapperComponent<? extends AbstractSampleForm> sampleComponent,
		PathogenTestDto pathogenTest,
		Consumer<PathogenTestDto> saveHandler,
		int caseSampleCount,
		boolean isNew,
		boolean viaLims,
		boolean addSeparator) {
		return addPathogenTestComponent(sampleComponent, pathogenTest, caseSampleCount, saveHandler, isNew, viaLims, false, addSeparator);
	}

	/**
	 *
	 * @param sampleComponent
	 *            to add the pathogen test create component to.
	 * @param pathogenTest
	 *            the preset values to insert. May be null.
	 * @param caseSampleCount
	 *            describes how many samples already exist for a case related to the pathogen test's sample (if a case exists, otherwise 0
	 *            is valid).
	 * @param saveHandler
	 *            used to do the save individual pathogen tests when the user clicks save/commit on the parent form
	 * @param isNew
	 *            for existing pathogen tests, the 'remove this pathogen test' button is hidden for users without
	 *            UserRight.PATHOGEN_TEST_DELETE permission.
	 * @return the pathogen test create component added.
	 */
	private CollapsiblePathogenTestForm addPathogenTestComponent(
		CommitDiscardWrapperComponent<? extends AbstractSampleForm> sampleComponent,
		PathogenTestDto pathogenTest,
		int caseSampleCount,
		Consumer<PathogenTestDto> saveHandler,
		boolean isNew,
		boolean viaLims,
		boolean deleteOnCancel,
		boolean addSeparator) {

		Label separator = new Label("<br/><hr/><br/>", ContentMode.HTML);
		separator.setWidth(100f, Unit.PERCENTAGE);
		separator.setVisible(addSeparator);
		sampleComponent.addComponent(separator, sampleComponent.getComponentCount() - 1);

		PathogenTestForm pathogenTestForm = new PathogenTestForm(sampleComponent.getWrappedComponent(), true, caseSampleCount, false, true);  // Valid because jurisdiction doesn't matter for entities that are about to be created
		// prefill fields
		if (pathogenTest != null) {
			pathogenTestForm.setValue(pathogenTest);
			// show typingId field when it has a preset value
			if (StringUtils.isNotBlank(pathogenTest.getTypingId())) {
				pathogenTestForm.getField(PathogenTestDto.TYPING_ID).setVisible(true);
			}
		} else {
			pathogenTestForm.setValue(PathogenTestDto.build(sampleComponent.getWrappedComponent().getValue(), UserProvider.getCurrent().getUser()));
			// remove value invalid for newly created pathogen tests
			ComboBox pathogenTestResultField = pathogenTestForm.getField(PathogenTestDto.TEST_RESULT);
			pathogenTestResultField.removeItem(PathogenTestResultType.NOT_DONE);
			pathogenTestResultField.setValue(PathogenTestResultType.PENDING);
			ComboBox testDiseaseField = pathogenTestForm.getField(PathogenTestDto.TESTED_DISEASE);
			testDiseaseField.setValue(FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease());

		}
		// setup field updates
		Field testLabField = pathogenTestForm.getField(PathogenTestDto.LAB);
		NullableOptionGroup samplePurposeField = sampleComponent.getWrappedComponent().getField(SampleDto.SAMPLE_PURPOSE);
		Runnable updateTestLabFieldRequired = () -> testLabField.setRequired(!SamplePurpose.INTERNAL.equals(samplePurposeField.getValue()));
		updateTestLabFieldRequired.run();
		samplePurposeField.addValueChangeListener(e -> updateTestLabFieldRequired.run());

		// validate pathogen test create component before saving the sample
		sampleComponent.addFieldGroups(pathogenTestForm.getFieldGroup());

		// Sample creation specific configuration
		final DateTimeField sampleDateField = sampleComponent.getWrappedComponent().getField(SampleDto.SAMPLE_DATE_TIME);
		final DateTimeField testDateField = pathogenTestForm.getField(PathogenTestDto.TEST_DATE_TIME);
		testDateField.addValidator(
			new DateComparisonValidator(
				testDateField,
				sampleDateField,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, testDateField.getCaption(), sampleDateField.getCaption())));

		if (viaLims) {
			setViaLimsFieldChecked(pathogenTestForm);
		}

		CollapsiblePathogenTestForm collapsibleForm =
			new CollapsiblePathogenTestForm(pathogenTestForm, isNew || isNull(pathogenTest), deleteOnCancel);

		// save pathogen test after saving sample
		CommitDiscardWrapperComponent.CommitListener savePathogenTest = () -> {
			saveHandler.accept(pathogenTestForm.getValue());
		};
		sampleComponent.addCommitListener(savePathogenTest);

		// add delete if allowed
		if (isNew || isNull(pathogenTest) || UserProvider.getCurrent().hasUserRight(UserRight.PATHOGEN_TEST_DELETE)) {
			collapsibleForm.setDeleteHandler(() -> {
				sampleComponent.removeComponent(separator);
				sampleComponent.removeComponent(collapsibleForm);
				sampleComponent.removeFieldGroups(pathogenTestForm.getFieldGroup());
				sampleComponent.removeCommitListener(savePathogenTest);
				pathogenTestForm.discard();
			});
		}

		// add the pathogenTestForm above the overall discard and commit buttons
		sampleComponent.addComponent(collapsibleForm, sampleComponent.getComponentCount() - 1);
		// add space above the buttons bar
		sampleComponent.getButtonsPanel().addStyleName(CssStyles.VSPACE_TOP_3);

		return collapsibleForm;
	}

	public CommitDiscardWrapperComponent<SampleCreateForm> getSampleCreateComponent(SampleDto sampleDto, Disease disease, Runnable callback) {

		return getSampleCreateComponent(sampleDto, disease, UserRight.SAMPLE_CREATE, callback);
	}

	private CommitDiscardWrapperComponent<SampleCreateForm> getSampleCreateComponent(
		SampleDto sampleDto,
		Disease disease,
		UserRight userRight,
		Runnable callback) {

		final SampleCreateForm createForm = new SampleCreateForm(disease);
		createForm.setValue(sampleDto);
		final CommitDiscardWrapperComponent<SampleCreateForm> editView =
			new CommitDiscardWrapperComponent<>(createForm, UserProvider.getCurrent().hasUserRight(userRight), createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				FacadeProvider.getSampleFacade().saveSample(sampleDto);
				if (callback != null) {
					callback.run();
				}
			}
		});

		return editView;
	}

	public void addPathogenTestButton(
		CommitDiscardWrapperComponent<? extends AbstractSampleForm> editView,
		boolean viaLims,
		Runnable addCallback,
		Runnable deleteCallback,
		Consumer<PathogenTestDto> saveHandler) {

		if (!UserProvider.getCurrent().hasUserRight(UserRight.PATHOGEN_TEST_CREATE)) {
			return;
		}

		Button addPathogenTestButton = new Button(I18nProperties.getCaption(Captions.pathogenTestAdd));
		addPathogenTestButton.addClickListener((e) -> {
			if (addCallback != null) {
				addCallback.run();
			}
			CollapsiblePathogenTestForm pathogenTestForm = addPathogenTestComponent(editView, viaLims, true, true, saveHandler);

			if (deleteCallback != null) {
				pathogenTestForm.addDetachListener((de) -> deleteCallback.run());
			}
		});
		editView.getButtonsPanel().addComponent(addPathogenTestButton, 0);
	}

	public void setViaLimsFieldChecked(PathogenTestForm pathogenTestForm) {
		CheckBox viaLimsCheckbox = pathogenTestForm.getField(PathogenTestDto.VIA_LIMS);
		viaLimsCheckbox.setValue(Boolean.TRUE);
	}

	public void createReferral(SampleDto existingSample, Disease disease) {

		CommitDiscardWrapperComponent<SampleCreateForm> createView = getSampleReferralCreateComponent(existingSample, disease);
		createView.addDoneListener(() -> navigateToData(existingSample.getUuid()));
		createView.getWrappedComponent().getValue().setPathogenTestResult(PathogenTestResultType.PENDING);
		VaadinUiUtil.showModalPopupWindow(createView, I18nProperties.getString(Strings.headingReferSample));
	}

	public CommitDiscardWrapperComponent<SampleCreateForm> getSampleReferralCreateComponent(SampleDto existingSample, Disease disease) {
		final SampleDto referralSample = SampleDto.buildReferralDto(UserProvider.getCurrent().getUserReference(), existingSample);

		final CommitDiscardWrapperComponent<SampleCreateForm> createView =
			getSampleCreateComponent(referralSample, disease, UserRight.SAMPLE_TRANSFER, null);

		createView.addCommitListener(() -> {
			if (!createView.getWrappedComponent().getFieldGroup().isModified()) {

				SampleDto updatedSample = FacadeProvider.getSampleFacade().getSampleByUuid(existingSample.getUuid());
				updatedSample.setReferredTo(referralSample.toReference());
				FacadeProvider.getSampleFacade().saveSample(updatedSample);
			}
		});
		return createView;
	}

	public CommitDiscardWrapperComponent<SampleEditForm> getSampleEditComponent(
		final String sampleUuid,
		boolean isPseudonymized,
		boolean inJurisdiction,
		Disease disease,
		boolean showDeleteButton) {

		SampleEditForm form = new SampleEditForm(isPseudonymized, inJurisdiction, disease);
		form.setWidth(form.getWidth() * 10 / 12, Unit.PIXELS);
		SampleDto dto = FacadeProvider.getSampleFacade().getSampleByUuid(sampleUuid);
		form.setValue(dto);
		final CommitDiscardWrapperComponent<SampleEditForm> editView =
			new CommitDiscardWrapperComponent<SampleEditForm>(form, true, form.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				SampleDto changedDto = form.getValue();
				SampleDto originalDto = FacadeProvider.getSampleFacade().getSampleByUuid(changedDto.getUuid());
				FacadeProvider.getSampleFacade().saveSample(changedDto);
				SormasUI.refreshView();

				updateAssociationsForSample(changedDto);

				if (changedDto.getSpecimenCondition() != originalDto.getSpecimenCondition()
					&& changedDto.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE
					&& UserProvider.getCurrent().hasUserRight(UserRight.TASK_CREATE)) {
					requestSampleCollectionTaskCreation(changedDto, form);
				} else {
					Notification.show(I18nProperties.getString(Strings.messageSampleSaved), Type.TRAY_NOTIFICATION);
				}
			}
		});

		if (showDeleteButton && UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_DELETE)) {
			editView.addDeleteWithReasonOrRestoreListener((deleteDetails) -> {
				FacadeProvider.getSampleFacade().delete(dto.getUuid(), deleteDetails);
				updateAssociationsForSample(dto);

				final CaseReferenceDto associatedCase = dto.getAssociatedCase();
				final ContactReferenceDto associatedContact = dto.getAssociatedContact();
				final EventParticipantReferenceDto associatedEventParticipant = dto.getAssociatedEventParticipant();

				if (associatedCase != null) {
					UI.getCurrent().getNavigator().navigateTo(CaseDataView.VIEW_NAME + "/" + associatedCase.getUuid());
				} else if (associatedContact != null) {
					UI.getCurrent().getNavigator().navigateTo(ContactDataView.VIEW_NAME + "/" + associatedContact.getUuid());
				} else {
					UI.getCurrent().getNavigator().navigateTo(EventParticipantDataView.VIEW_NAME + "/" + associatedEventParticipant.getUuid());
				}

			}, (deletionDetails) -> {
				FacadeProvider.getSampleFacade().restore(dto.getUuid());
				updateAssociationsForSample(dto);
				UI.getCurrent().getNavigator().navigateTo(SamplesView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entitySample), FacadeProvider.getSampleFacade().isDeleted(dto.getUuid()));
		}

		if (dto.getReferredTo() != null || dto.getSamplePurpose() == SamplePurpose.EXTERNAL) {
			editView.getWrappedComponent().getField(SampleDto.SAMPLE_PURPOSE).setEnabled(false);
		}

		if (dto.isDeleted()) {
			editView.getWrappedComponent().getField(SampleDto.DELETION_REASON).setVisible(true);
			if (editView.getWrappedComponent().getField(SampleDto.DELETION_REASON).getValue() == DeletionReason.OTHER_REASON) {
				editView.getWrappedComponent().getField(SampleDto.OTHER_DELETION_REASON).setVisible(true);
			}
		}

		editView.restrictEditableComponentsOnEditView(UserRight.SAMPLE_EDIT, null, UserRight.SAMPLE_DELETE, null, dto.isInJurisdiction());

		return editView;
	}

	private void updateAssociationsForSample(SampleDto sampleDto) {
		final CaseReferenceDto associatedCase = sampleDto.getAssociatedCase();
		if (associatedCase != null && UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT)) {
			final CaseDataDto caseDataByUuid = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid());
			FacadeProvider.getCaseFacade().save(caseDataByUuid);
		}

		final ContactReferenceDto associatedContact = sampleDto.getAssociatedContact();
		if (associatedContact != null && UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EDIT)) {
			final ContactDto contactDataByUuid = FacadeProvider.getContactFacade().getByUuid(associatedContact.getUuid());
			FacadeProvider.getContactFacade().save(contactDataByUuid);
		}
	}

	/**
	 * Initialize 'Refer to another laboratory' button or link to referred sample
	 *
	 * @param editForm
	 *            the edit form to attach the 'Refer to another laboratory' button to.
	 * @param disease
	 *            required for field visibility checks in the sample create form opened when a sample reference shall be created.
	 * @param createReferral
	 *            instructions for what shall happen when the user chooses to create a referral
	 * @param openReferredSample
	 *            instructions for what shall happen when the user chooses to open the referred sample
	 */
	public void addReferOrLinkToOtherLabButton(
		CommitDiscardWrapperComponent<SampleEditForm> editForm,
		Disease disease,
		Consumer<Disease> createReferral,
		Consumer<SampleDto> openReferredSample) {

		Button referOrLinkToOtherLabButton = null;
		SampleDto sample = editForm.getWrappedComponent().getValue();
		if (sample.getReferredTo() == null) {
			if (sample.getSamplePurpose() == SamplePurpose.EXTERNAL && UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_TRANSFER)) {
				referOrLinkToOtherLabButton =
					ButtonHelper.createButton("referOrLinkToOtherLab", I18nProperties.getCaption(Captions.sampleRefer), new ClickListener() {

						private static final long serialVersionUID = 1L;

						@Override
						public void buttonClick(ClickEvent event) {
							try {
								createReferral.accept(disease);
							} catch (SourceException | InvalidValueException e) {
								Notification.show(I18nProperties.getString(Strings.messageSampleErrors), Type.ERROR_MESSAGE);
							}
						}
					}, ValoTheme.BUTTON_LINK);
			}
		} else {
			SampleDto referredDto = FacadeProvider.getSampleFacade().getSampleByUuid(sample.getReferredTo().getUuid());
			FacilityReferenceDto referredDtoLab = referredDto.getLab();
			String referOrLinkToOtherLabButtonCaption = referredDtoLab == null
				? I18nProperties.getCaption(Captions.sampleReferredToInternal) + " ("
					+ DateFormatHelper.formatLocalDateTime(referredDto.getSampleDateTime()) + ")"
				: I18nProperties.getCaption(Captions.sampleReferredTo) + " " + referredDtoLab.buildCaption();

			referOrLinkToOtherLabButton = ButtonHelper.createButton("referOrLinkToOtherLab", referOrLinkToOtherLabButtonCaption, new ClickListener() {

				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					openReferredSample.accept(referredDto);
				}

			});
		}

		if (referOrLinkToOtherLabButton != null) {
			editForm.getButtonsPanel().addComponentAsFirst(referOrLinkToOtherLabButton);
			editForm.getButtonsPanel().setComponentAlignment(referOrLinkToOtherLabButton, Alignment.BOTTOM_LEFT);
		}
	}

	/**
	 *
	 * @param editForm
	 *            the edit form to attach the 'Referred from' button to.
	 * @param navigation
	 *            instructions for what shall happen when the user chooses to open the related sample
	 */
	public void addReferredFromButton(CommitDiscardWrapperComponent<SampleEditForm> editForm, Consumer<SampleDto> navigation) {
		SampleReferenceDto referredFromRef = FacadeProvider.getSampleFacade().getReferredFrom(editForm.getWrappedComponent().getValue().getUuid());
		if (referredFromRef != null) {
			SampleDto referredFrom = FacadeProvider.getSampleFacade().getSampleByUuid(referredFromRef.getUuid());
			FacilityReferenceDto referredFromLab = referredFrom.getLab();
			String referredButtonCaption = referredFromLab == null
				? I18nProperties.getCaption(Captions.sampleReferredFromInternal) + " ("
					+ DateFormatHelper.formatLocalDateTime(referredFrom.getSampleDateTime()) + ")"
				: I18nProperties.getCaption(Captions.sampleReferredFrom) + " " + referredFromLab.buildCaption();
			Button referredButton = ButtonHelper
				.createButton("referredFrom", referredButtonCaption, event -> navigation.accept(referredFrom), ValoTheme.BUTTON_LINK, VSPACE_NONE);
			editForm.getWrappedComponent().addReferredFromButton(referredButton);
		}
	}

	private void requestSampleCollectionTaskCreation(SampleDto dto, SampleEditForm form) {

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);

		ConfirmationComponent requestTaskComponent = VaadinUiUtil.buildYesNoConfirmationComponent();

		Label description = new Label(I18nProperties.getString(Strings.messageCreateCollectionTask), ContentMode.HTML);
		description.setWidth(100, Unit.PERCENTAGE);
		layout.addComponent(description);
		layout.addComponent(requestTaskComponent);
		layout.setComponentAlignment(requestTaskComponent, Alignment.BOTTOM_RIGHT);
		layout.setSizeUndefined();
		layout.setSpacing(true);

		Window popupWindow = VaadinUiUtil.showPopupWindow(layout);
		popupWindow.setSizeUndefined();
		popupWindow.setCaption(I18nProperties.getString(Strings.headingCreateNewTaskQuestion));
		requestTaskComponent.getConfirmButton().addClickListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				popupWindow.close();
				final CaseReferenceDto associatedCase = dto.getAssociatedCase();
				final ContactReferenceDto associatedContact = dto.getAssociatedContact();
				final EventParticipantReferenceDto associatedEventParticipant = dto.getAssociatedEventParticipant();
				if (associatedCase != null) {
					final CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid());
					ControllerProvider.getTaskController().createSampleCollectionTask(TaskContext.CASE, associatedCase, dto, caseDto.getDisease());
				} else if (associatedContact != null) {
					final ContactDto contactDto = FacadeProvider.getContactFacade().getByUuid(associatedContact.getUuid());
					ControllerProvider.getTaskController()
						.createSampleCollectionTask(TaskContext.CONTACT, associatedContact, dto, contactDto.getDisease());
				} else if (associatedEventParticipant != null) {
					final EventParticipantDto eventParticipantDto =
						FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(associatedEventParticipant.getUuid());
					final EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(eventParticipantDto.getEvent().getUuid(), false);
					ControllerProvider.getTaskController()
						.createSampleCollectionTask(TaskContext.EVENT, eventParticipantDto.getEvent(), dto, eventDto.getDisease());
				}
			}
		});
		requestTaskComponent.getCancelButton().addClickListener(event -> popupWindow.close());
	}

	public void showChangePathogenTestResultWindow(
		CommitDiscardWrapperComponent<? extends AbstractSampleForm> editComponent,
		String sampleUuid,
		PathogenTestResultType newResult,
		Consumer<Boolean> callback) {

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);

		ConfirmationComponent confirmationComponent = VaadinUiUtil.buildYesNoConfirmationComponent();

		Label description = new Label(String.format(I18nProperties.getString(Strings.messageChangePathogenTestResult), newResult.toString()));
		description.setWidth(100, Unit.PERCENTAGE);
		layout.addComponent(description);
		layout.addComponent(confirmationComponent);
		layout.setComponentAlignment(confirmationComponent, Alignment.BOTTOM_RIGHT);
		layout.setSizeUndefined();
		layout.setSpacing(true);

		Window popupWindow = VaadinUiUtil.showPopupWindow(layout);
		popupWindow.setSizeUndefined();
		popupWindow.setCaption(I18nProperties.getString(Strings.headingChangePathogenTestResult));
		confirmationComponent.getConfirmButton().addClickListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				if (editComponent != null && !SampleCreateForm.class.equals(editComponent.getWrappedComponent().getClass())) {
					editComponent.commit();
				}
				SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(sampleUuid);
				sample.setPathogenTestResult(newResult);
				FacadeProvider.getSampleFacade().saveSample(sample);
				popupWindow.close();
				SormasUI.refreshView();
				if (callback != null) {
					callback.accept(true);
				}
			}
		});
		confirmationComponent.getCancelButton().addClickListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				popupWindow.close();
				if (callback != null) {
					callback.accept(false);
				}
			}
		});
	}

	public void deleteAllSelectedItems(Collection<SampleIndexDto> selectedRows, HumanSampleGrid sampleGrid, Runnable noEntriesRemainingCallback) {

		ControllerProvider.getDeleteRestoreController()
			.deleteAllSelectedItems(
				selectedRows,
				DeleteRestoreHandlers.forSample(),
				bulkOperationCallback(sampleGrid, noEntriesRemainingCallback, null));

	}

	public void restoreSelectedSamples(Collection<SampleIndexDto> selectedRows, HumanSampleGrid sampleGrid, Runnable noEntriesRemainingCallback) {

		ControllerProvider.getDeleteRestoreController()
			.restoreSelectedItems(
				selectedRows,
				DeleteRestoreHandlers.forSample(),
				bulkOperationCallback(sampleGrid, noEntriesRemainingCallback, null));
	}

	public TitleLayout getSampleViewTitleLayout(SampleDto sample) {

		TitleLayout titleLayout = new TitleLayout();

		titleLayout.addRow(DataHelper.getShortUuid(sample.getUuid()));
		titleLayout.addRow(DateFormatHelper.formatDate(sample.getSampleDateTime()));

		String mainRowText = SampleReferenceDto.buildCaption(
			sample.getSampleMaterial(),
			sample.getAssociatedCase() != null ? sample.getAssociatedCase().getUuid() : null,
			sample.getAssociatedContact() != null ? sample.getAssociatedContact().getUuid() : null,
			sample.getAssociatedEventParticipant() != null ? sample.getAssociatedEventParticipant().getUuid() : null);
		titleLayout.addMainRow(mainRowText);

		return titleLayout;
	}

	public int caseSampleCountOf(SampleDto sample) {
		CaseReferenceDto cazeRef = sample.getAssociatedCase();
		if (cazeRef == null) {
			return 0;
		} else {
			SampleCriteria sampleCriteria = new SampleCriteria();
			sampleCriteria.caze(cazeRef);
			return (int) FacadeProvider.getSampleFacade().count(sampleCriteria);
		}
	}

	public Disease getDiseaseOf(SampleDto sample) {
		CaseReferenceDto cazeRef = sample.getAssociatedCase();
		if (cazeRef != null) {
			return FacadeProvider.getCaseFacade().getByUuid(cazeRef.getUuid()).getDisease();
		}
		ContactReferenceDto contactRef = sample.getAssociatedContact();
		if (contactRef != null) {
			return FacadeProvider.getContactFacade().getByUuid(contactRef.getUuid()).getDisease();
		}
		EventParticipantReferenceDto eventPartRef = sample.getAssociatedEventParticipant();
		if (eventPartRef != null) {
			EventReferenceDto eventRef = FacadeProvider.getEventParticipantFacade().getByUuid(eventPartRef.getUuid()).getEvent();
			if (eventRef != null) {
				return FacadeProvider.getEventFacade().getEventByUuid(eventRef.getUuid(), false).getDisease();
			}
		}
		return null;
	}

	private Consumer<List<SampleIndexDto>> bulkOperationCallback(
		HumanSampleGrid sampleGrid,
		Runnable noEntriesRemainingCallback,
		Window popupWindow) {
		return remainingSamples -> {
			if (popupWindow != null) {
				popupWindow.close();
			}

			sampleGrid.reload();
			if (CollectionUtils.isNotEmpty(remainingSamples)) {
				sampleGrid.asMultiSelect().selectItems(remainingSamples.toArray(new SampleIndexDto[0]));
			} else {
				noEntriesRemainingCallback.run();
			}
		};
	}
}
