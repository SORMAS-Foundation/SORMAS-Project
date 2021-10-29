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
package de.symeda.sormas.ui.samples;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.v7.ui.ComboBox;
import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
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

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
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
import de.symeda.sormas.ui.labmessage.LabMessagesView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SampleController {

	public SampleController() {
	}

	public void registerViews(Navigator navigator) {
		navigator.addView(SamplesView.VIEW_NAME, SamplesView.class);
		navigator.addView(SampleDataView.VIEW_NAME, SampleDataView.class);
		if (UserProvider.getCurrent().hasUserRight(UserRight.LAB_MESSAGES)) {
			navigator.addView(LabMessagesView.VIEW_NAME, LabMessagesView.class);
		}
	}

	public void navigateToData(String sampleUuid) {
		String navigationState = SampleDataView.VIEW_NAME + "/" + sampleUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void create(CaseReferenceDto caseRef, Runnable callback) {
		createSample(callback, SampleDto.build(UserProvider.getCurrent().getUserReference(), caseRef));
	}

	public void create(ContactReferenceDto contactRef, Runnable callback) {
		createSample(callback, SampleDto.build(UserProvider.getCurrent().getUserReference(), contactRef));
	}

	public void create(EventParticipantReferenceDto eventParticipantRef, Runnable callback) {
		createSample(callback, SampleDto.build(UserProvider.getCurrent().getUserReference(), eventParticipantRef));
	}

	private void createSample(Runnable callback, SampleDto sampleDto) {
		final CommitDiscardWrapperComponent<SampleCreateForm> editView = getSampleCreateComponent(sampleDto, callback);
		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingCreateNewSample));
	}

	public CommitDiscardWrapperComponent<SampleCreateForm> getSampleCreateComponent(
		SampleDto sampleDto,
		BiConsumer<SampleDto, PathogenTestDto> consumer) {
		return getSampleCreateComponent(sampleDto, consumer, () -> {
		});
	}

	/**
	 * 
	 * Works just like the addPathogenTestCreateComponent(..., int caseSampleCount, ...), but additionally determines the caseSampleCount.
	 * For performance reasons, this method shall just be called when the caseSampleCount is not known already and just one pathogen test
	 * create component shall be added.
	 * 
	 * @param sampleComponent
	 *            to add the pathogen test create component to.
	 * @param pathogenTest
	 *            the preset values to insert. May be null.
	 * @param removable
	 *            if true, a button is shown to remove the pathogen test create component again from the sample create component.
	 * @return the pathogen test create component added.
	 */
	public PathogenTestForm addPathogenTestCreateComponent(
		CommitDiscardWrapperComponent<? extends AbstractSampleForm> sampleComponent,
		PathogenTestDto pathogenTest,
		boolean removable) {

		CaseReferenceDto cazeRef = sampleComponent.getWrappedComponent().getValue().getAssociatedCase();
		if (cazeRef == null) {
			return addPathogenTestCreateComponent(sampleComponent, pathogenTest, 0, removable);
		} else {
			List<SampleDto> samples = FacadeProvider.getSampleFacade().getByCaseUuids(Arrays.asList(cazeRef.getUuid()));
			return addPathogenTestCreateComponent(sampleComponent, pathogenTest, samples.size(), removable);
		}

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
	 * @param removable
	 *            if true, a button is shown to remove the pathogen test create component again from the sample create component.
	 * @return the pathogen test create component added.
	 */
	public PathogenTestForm addPathogenTestCreateComponent(
		CommitDiscardWrapperComponent<? extends AbstractSampleForm> sampleComponent,
		PathogenTestDto pathogenTest,
		int caseSampleCount,
		boolean removable) {
		// add horizontal rule to clearly distinguish the component
		Label horizontalRule = new Label("<br><hr /><br>", ContentMode.HTML);
		horizontalRule.setWidth(100f, Unit.PERCENTAGE);
		sampleComponent.addComponent(horizontalRule, sampleComponent.getComponentCount() - 1);

		PathogenTestForm pathogenTestForm = new PathogenTestForm(sampleComponent.getWrappedComponent().getValue(), true, caseSampleCount, false);
		// prefill fields
		if (pathogenTest != null) {
			pathogenTestForm.setValue(pathogenTest);
			// show typingId field when it has a preset value
			if (pathogenTest.getTypingId() != null && !"".equals(pathogenTest.getTypingId())) {
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
		// validate pathogen test create component before saving the sample
		sampleComponent.addFieldGroups(pathogenTestForm.getFieldGroup());
		CommitDiscardWrapperComponent.CommitListener savePathogenTest =
			() -> FacadeProvider.getPathogenTestFacade().savePathogenTest(pathogenTestForm.getValue());
		sampleComponent.addCommitListener(savePathogenTest);
		// Discard button configuration
		if (removable) {
			Button discardButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.pathogenTestRemove));
			VerticalLayout buttonLayout = new VerticalLayout(discardButton);
			buttonLayout.setComponentAlignment(discardButton, Alignment.TOP_RIGHT);
			// add the discard button above the overall discard and commit buttons
			sampleComponent.addComponent(buttonLayout, sampleComponent.getComponentCount() - 1);
			discardButton.addClickListener(o -> {
				sampleComponent.removeComponent(horizontalRule);
				sampleComponent.removeComponent(buttonLayout);
				sampleComponent.removeComponent(pathogenTestForm);
				sampleComponent.removeFieldGroups(pathogenTestForm.getFieldGroup());
				sampleComponent.removeCommitListener(savePathogenTest);
				pathogenTestForm.discard();
			});
		}
		// Country specific configuration
		boolean germanInstance = FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY);
		pathogenTestForm.getField(PathogenTestDto.REPORT_DATE).setVisible(germanInstance);
		pathogenTestForm.getField(PathogenTestDto.EXTERNAL_ID).setVisible(germanInstance);
		pathogenTestForm.getField(PathogenTestDto.EXTERNAL_ORDER_ID).setVisible(germanInstance);
		pathogenTestForm.getField(PathogenTestDto.VIA_LIMS).setVisible(germanInstance);
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

		// add the pathogenTestForm above the overall discard and commit buttons
		sampleComponent.addComponent(pathogenTestForm, sampleComponent.getComponentCount() - 1);
		return pathogenTestForm;
	}

	public CommitDiscardWrapperComponent<SampleCreateForm> getSampleCreateComponent(SampleDto sampleDto, Runnable callback) {
		return getSampleCreateComponent(sampleDto, (savedSampleDto, savedPathogenTestDto) -> {
		}, callback);
	}

	public CommitDiscardWrapperComponent<SampleCreateForm> getSampleCreateComponent(
		SampleDto sampleDto,
		BiConsumer<SampleDto, PathogenTestDto> consumer,
		Runnable callback) {
		final SampleCreateForm createForm = new SampleCreateForm();
		createForm.setValue(sampleDto);
		final CommitDiscardWrapperComponent<SampleCreateForm> editView = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				saveSample(createForm, consumer, editView);
				callback.run();
			}
		});
		Button addPathogenTestButton = new Button(I18nProperties.getCaption(Captions.pathogenTestAdd));
		addPathogenTestButton.addClickListener((e) -> addPathogenTestCreateComponent(editView, null, true));
		editView.getButtonsPanel().addComponent(addPathogenTestButton, 0);

		return editView;
	}

	public void createReferral(SampleDto sample) {

		final SampleCreateForm createForm = new SampleCreateForm();
		final SampleDto referralSample = SampleDto.buildReferral(UserProvider.getCurrent().getUserReference(), sample);
		createForm.setValue(referralSample);
		final CommitDiscardWrapperComponent<SampleCreateForm> createView = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_CREATE),
			createForm.getFieldGroup());

		createView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				saveSample(createForm, createView);

				SampleDto updatedSample = FacadeProvider.getSampleFacade().getSampleByUuid(sample.getUuid());
				updatedSample.setReferredTo(referralSample.toReference());
				FacadeProvider.getSampleFacade().saveSample(updatedSample);

				navigateToData(sample.getUuid());
			}
		});

		// Reload the page when the form is discarded because the sample has been saved before
		createView.addDiscardListener(new DiscardListener() {

			@Override
			public void onDiscard() {
				navigateToData(sample.getUuid());
			}
		});

		VaadinUiUtil.showModalPopupWindow(createView, I18nProperties.getString(Strings.headingReferSample));
	}

	private void saveSample(SampleCreateForm createForm, CommitDiscardWrapperComponent createView) {
		saveSample(createForm, ((sampleDto, pathogenTestDto) -> {
		}), createView);
	}

	private void saveSample(SampleCreateForm createForm, BiConsumer<SampleDto, PathogenTestDto> consumer, CommitDiscardWrapperComponent createView) {

		final SampleDto newSample = createForm.getValue();
		SampleDto savedSample = FacadeProvider.getSampleFacade().saveSample(newSample);
		consumer.accept(savedSample, null);
	}

	public CommitDiscardWrapperComponent<SampleEditForm> getSampleEditComponent(final String sampleUuid, boolean isPseudonymized) {

		SampleEditForm form = new SampleEditForm(isPseudonymized);
		form.setWidth(form.getWidth() * 10 / 12, Unit.PIXELS);
		SampleDto dto = FacadeProvider.getSampleFacade().getSampleByUuid(sampleUuid);
		form.setValue(dto);
		final CommitDiscardWrapperComponent<SampleEditForm> editView = new CommitDiscardWrapperComponent<SampleEditForm>(
			form,
			UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_EDIT),
			form.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				SampleDto changedDto = form.getValue();
				SampleDto originalDto = FacadeProvider.getSampleFacade().getSampleByUuid(changedDto.getUuid());
				FacadeProvider.getSampleFacade().saveSample(changedDto);
				SormasUI.refreshView();

				if (changedDto.getSpecimenCondition() != originalDto.getSpecimenCondition()
					&& changedDto.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE
					&& UserProvider.getCurrent().hasUserRight(UserRight.TASK_CREATE)) {
					requestSampleCollectionTaskCreation(changedDto, form);
				} else {
					Notification.show(I18nProperties.getString(Strings.messageSampleSaved), Type.TRAY_NOTIFICATION);
				}
			}
		});

		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_DELETE)) {
			editView.addDeleteListener(() -> {
				FacadeProvider.getSampleFacade().deleteSample(dto.toReference());
				UI.getCurrent().getNavigator().navigateTo(SamplesView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entitySample));
		}

		// Initialize 'Refer to another laboratory' button or link to referred sample
		Button referOrLinkToOtherLabButton = null;
		if (dto.getReferredTo() == null) {
			if (dto.getSamplePurpose() == SamplePurpose.EXTERNAL && UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_TRANSFER)) {
				referOrLinkToOtherLabButton =
					ButtonHelper.createButton("referOrLinkToOtherLab", I18nProperties.getCaption(Captions.sampleRefer), new ClickListener() {

						private static final long serialVersionUID = 1L;

						@Override
						public void buttonClick(ClickEvent event) {
							try {
								form.commit();
								SampleDto sampleDto = form.getValue();
								sampleDto = FacadeProvider.getSampleFacade().saveSample(sampleDto);
								createReferral(sampleDto);
							} catch (SourceException | InvalidValueException e) {
								Notification.show(I18nProperties.getString(Strings.messageSampleErrors), Type.ERROR_MESSAGE);
							}
						}
					}, ValoTheme.BUTTON_LINK);
			}
		} else {
			SampleDto referredDto = FacadeProvider.getSampleFacade().getSampleByUuid(dto.getReferredTo().getUuid());
			FacilityReferenceDto referredDtoLab = referredDto.getLab();
			String referOrLinkToOtherLabButtonCaption = referredDtoLab == null
				? I18nProperties.getCaption(Captions.sampleReferredToInternal) + " ("
					+ DateFormatHelper.formatLocalDateTime(referredDto.getSampleDateTime()) + ")"
				: I18nProperties.getCaption(Captions.sampleReferredTo) + " " + referredDtoLab.toString();

			referOrLinkToOtherLabButton = ButtonHelper.createButton("referOrLinkToOtherLab", referOrLinkToOtherLabButtonCaption, new ClickListener() {

				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					navigateToData(dto.getReferredTo().getUuid());
				}
			});

		}

		if (referOrLinkToOtherLabButton != null) {
			editView.getButtonsPanel().addComponentAsFirst(referOrLinkToOtherLabButton);
			editView.getButtonsPanel().setComponentAlignment(referOrLinkToOtherLabButton, Alignment.BOTTOM_LEFT);
		}

		if (dto.getReferredTo() != null || dto.getSamplePurpose() == SamplePurpose.EXTERNAL) {
			editView.getWrappedComponent().getField(SampleDto.SAMPLE_PURPOSE).setEnabled(false);
		}

		return editView;
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
					ControllerProvider.getTaskController().createSampleCollectionTask(TaskContext.CASE, associatedCase, dto);
				} else if (associatedContact != null) {
					ControllerProvider.getTaskController().createSampleCollectionTask(TaskContext.CONTACT, associatedContact, dto);
				} else if (associatedEventParticipant != null) {
					final EventParticipantDto eventParticipantDto =
						FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(associatedEventParticipant.getUuid());
					ControllerProvider.getTaskController().createSampleCollectionTask(TaskContext.EVENT, eventParticipantDto.getEvent(), dto);
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
				callback.accept(true);
			}
		});
		confirmationComponent.getCancelButton().addClickListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				popupWindow.close();
				callback.accept(false);
			}
		});
	}

	public void deleteAllSelectedItems(Collection<SampleIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoSamplesSelected),
				I18nProperties.getString(Strings.messageNoSamplesSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil
				.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeleteSamples), selectedRows.size()), () -> {
					List<String> sampleIndexDtoList = selectedRows.stream().map(SampleIndexDto::getUuid).collect(Collectors.toList());
					FacadeProvider.getSampleFacade().deleteAllSamples(sampleIndexDtoList);
					callback.run();
					new Notification(
						I18nProperties.getString(Strings.headingSamplesDeleted),
						I18nProperties.getString(Strings.messageSamplesDeleted),
						Type.HUMANIZED_MESSAGE,
						false).show(Page.getCurrent());
				});
		}
	}

	public VerticalLayout getSampleViewTitleLayout(SampleDto sample) {

		VerticalLayout titleLayout = new VerticalLayout();
		titleLayout.addStyleNames(CssStyles.LAYOUT_MINIMAL, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_4);
		titleLayout.setSpacing(false);

		Label uuidLabel = new Label(DataHelper.getShortUuid(sample.getUuid()));
		uuidLabel.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE);
		titleLayout.addComponent(uuidLabel);

		Label sampleDateLabel = new Label(DateFormatHelper.formatDate(sample.getSampleDateTime()));
		sampleDateLabel.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE);
		titleLayout.addComponent(sampleDateLabel);

		Label sampleCaptionLabel = new Label(
			SampleReferenceDto.buildCaption(
				sample.getSampleMaterial(),
				sample.getAssociatedCase() != null ? sample.getAssociatedCase().getUuid() : null,
				sample.getAssociatedContact() != null ? sample.getAssociatedContact().getUuid() : null,
				sample.getAssociatedEventParticipant() != null ? sample.getAssociatedEventParticipant().getUuid() : null));
		sampleCaptionLabel.addStyleNames(CssStyles.H2, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_PRIMARY);
		titleLayout.addComponents(sampleCaptionLabel);

		return titleLayout;
	}
}
