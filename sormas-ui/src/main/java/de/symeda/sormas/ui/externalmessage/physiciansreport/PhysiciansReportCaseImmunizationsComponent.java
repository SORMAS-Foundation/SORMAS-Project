/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.externalmessage.physiciansreport;

import static de.symeda.sormas.ui.utils.CssStyles.H3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.vaccination.VaccinationAssociationType;
import de.symeda.sormas.api.vaccination.VaccinationCriteria;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DeletableUtils;
import de.symeda.sormas.ui.utils.DirtyCheckPopup;
import de.symeda.sormas.ui.vaccination.VaccinationEditForm;

public class PhysiciansReportCaseImmunizationsComponent extends CommitDiscardWrapperComponent<VerticalLayout> {

	private static final long serialVersionUID = -5128676869217088760L;

	private final VaccinationList vaccinationList;
	private final Button addVaccinationButton;
	private final CaseDataDto caze;
	private final List<VaccinationDto> vaccinationsToDisplay;
	private final List<VaccinationDto> vaccinationsToCreate = new ArrayList<>();
	private final List<VaccinationDto> vaccinationsToUpdate = new ArrayList<>();
	private final Map<VaccinationDto, DeletionDetails> vaccinationsToDelete = new HashMap<>();
	private CommitDiscardWrapperComponent<VaccinationEditForm> currentVaccinationEditComponent;
	private CommitDiscardWrapperComponent<VaccinationEditForm> createVaccinationComponent;

	public PhysiciansReportCaseImmunizationsComponent(CaseDataDto caze) {
		super(createLayout());
		this.caze = caze;

		Label immunizationsLabel = new Label(I18nProperties.getString(Strings.entityVaccinations));
		immunizationsLabel.addStyleName(H3);
		getWrappedComponent().addComponent(immunizationsLabel);

		VaccinationCriteria criteria = new VaccinationCriteria.Builder(caze.getPerson()).withDisease(caze.getDisease())
			.build()
			.vaccinationAssociationType(VaccinationAssociationType.CASE)
			.caseReference(caze.toReference())
			.region(getRegion())
			.district(getDistrict());

		vaccinationsToDisplay = FacadeProvider.getVaccinationFacade().getVaccinationsByCriteria(criteria, 0, null, null);

		vaccinationList = new VaccinationList(this::handleDeleteVaccination, (v) -> handleEditVaccination(v, caze));
		vaccinationList.setVaccinations(vaccinationsToDisplay);

		getWrappedComponent().addComponent(vaccinationList);

		addVaccinationButton = ButtonHelper.createButton(Captions.physiciansReportCaseAddVaccination, e -> {
			handleAddVaccination(caze);
		});

		HorizontalLayout vaccinationListToolbar = new HorizontalLayout(addVaccinationButton);
		vaccinationListToolbar.setWidthFull();
		vaccinationListToolbar.setComponentAlignment(addVaccinationButton, Alignment.MIDDLE_RIGHT);
		getWrappedComponent().addComponent(vaccinationListToolbar);

		setPreCommitListener((callback) -> {
			if (!handleDirty(callback)) {
				callback.run();
			}
		});
	}

	private static VerticalLayout createLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(new MarginInfo(false, true));
		layout.setWidth(900, Unit.PIXELS);

		return layout;
	}

	private DistrictReferenceDto getDistrict() {
		return caze.getResponsibleDistrict();
	}

	private RegionReferenceDto getRegion() {
		return caze.getResponsibleRegion();
	}

	@Override
	protected void onCommit() {
		for (VaccinationDto vaccination : vaccinationsToUpdate) {
			FacadeProvider.getVaccinationFacade().save(vaccination);
		}

		for (VaccinationDto vaccination : vaccinationsToCreate) {
			FacadeProvider.getVaccinationFacade()
				.createWithImmunization(vaccination, getRegion(), getDistrict(), caze.getPerson(), caze.getDisease());
		}

		for (Map.Entry<VaccinationDto, DeletionDetails> deleteEntry : vaccinationsToDelete.entrySet()) {
			FacadeProvider.getVaccinationFacade().deleteWithImmunization(deleteEntry.getKey().getUuid(), deleteEntry.getValue());
		}

		vaccinationsToUpdate.clear();
		vaccinationsToCreate.clear();
		vaccinationsToDelete.clear();

		super.onCommit();
	}

	@Override
	public boolean isDirty() {
		return !vaccinationsToUpdate.isEmpty() || !vaccinationsToCreate.isEmpty() || !vaccinationsToDelete.isEmpty();
	}

	private void handleAddVaccination(CaseDataDto caze) {
		if (handleDirty(() -> handleAddVaccination(caze))) {
			return;
		}

		createVaccinationComponent = ControllerProvider.getVaccinationController()
			.getVaccinationCreateComponent(
				null,
				getRegion(),
				getDistrict(),
				caze.getPerson(),
				caze.getDisease(),
				UiFieldAccessCheckers.getNoop(),
				false,
				(v -> {
					vaccinationsToCreate.add(v);
					vaccinationsToDisplay.add(v);
					vaccinationList.setVaccinations(vaccinationsToDisplay);
				}));
		createVaccinationComponent.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));
		createVaccinationComponent.getButtonsPanel().setComponentAlignment(createVaccinationComponent.getDiscardButton(), Alignment.BOTTOM_LEFT);

		createVaccinationComponent.addDoneListener(() -> {
			getWrappedComponent().removeComponent(createVaccinationComponent);
			createVaccinationComponent = null;
			addVaccinationButton.setVisible(true);
		});

		addVaccinationButton.setVisible(false);
		getWrappedComponent().addComponent(createVaccinationComponent);
	}

	private void handleDeleteVaccination(VaccinationDto vaccination) {
		if (vaccinationsToCreate.contains(vaccination)) {
			vaccinationsToCreate.remove(vaccination);
		} else {
			DeletableUtils.showDeleteWithReasonPopup(
				String.format(I18nProperties.getString(Strings.confirmationDeleteEntity), I18nProperties.getCaption(Captions.Vaccination)),
				(details) -> {
					vaccinationsToUpdate.remove(vaccination);
					vaccinationsToDelete.put(vaccination, details);
				});
		}

		vaccinationsToDisplay.remove(vaccination);
		vaccinationList.setVaccinations(vaccinationsToDisplay);
	}

	private void handleEditVaccination(VaccinationDto vaccination, CaseDataDto caze) {
		if (handleDirty(() -> handleEditVaccination(vaccination, caze))) {
			return;
		}

		VaccinationListItem collapsedComponent = vaccinationList.getItemByVaccination(vaccination);

		currentVaccinationEditComponent = ControllerProvider.getVaccinationController()
			.getVaccinationEditComponent(
				vaccination,
				caze.getDisease(),
				UiFieldAccessCheckers.forDataAccessLevel(UiUtil.getPseudonymizableDataAccessLevel(caze.isInJurisdiction()), caze.isPseudonymized()),
				false,
				(v) -> {
					if (!vaccinationsToCreate.contains(v)) {
						vaccinationsToUpdate.add(v);
					}
				},
				true,
				UiUtil.permitted(UserRight.IMMUNIZATION_DELETE));

		currentVaccinationEditComponent.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));
		currentVaccinationEditComponent.getButtonsPanel()
			.setComponentAlignment(currentVaccinationEditComponent.getDiscardButton(), Alignment.BOTTOM_LEFT);

		currentVaccinationEditComponent.addDoneListener(() -> {
			vaccinationList.setVaccinations(vaccinationsToDisplay);
			currentVaccinationEditComponent = null;
			addVaccinationButton.setVisible(true);
		});

		addVaccinationButton.setVisible(false);
		vaccinationList.replaceComponent(collapsedComponent, currentVaccinationEditComponent);
	}

	private boolean handleDirty(Runnable callback) {
		if (currentVaccinationEditComponent != null) {
			if (currentVaccinationEditComponent.isDirty()) {
				DirtyCheckPopup.show(currentVaccinationEditComponent, callback);

				return true;
			}

			currentVaccinationEditComponent.discard();
		}

		if (createVaccinationComponent != null) {
			if (createVaccinationComponent.isDirty()) {
				DirtyCheckPopup.show(createVaccinationComponent, callback);
				return true;
			}

			createVaccinationComponent.discard();
		}

		return false;
	}

	private static class VaccinationList extends VerticalLayout {

		private final Consumer<VaccinationDto> deleteHandler;
		private final Consumer<VaccinationDto> editHandler;

		private List<VaccinationDto> vaccinations;

		public VaccinationList(Consumer<VaccinationDto> deleteHandler, Consumer<VaccinationDto> editHandler) {
			this.deleteHandler = deleteHandler;
			this.editHandler = editHandler;

			setMargin(false);
			setSpacing(false);
		}

		public void setVaccinations(List<VaccinationDto> vaccinations) {
			this.vaccinations = vaccinations;

			removeAllComponents();

			vaccinations.forEach(v -> {
				addComponent(new VaccinationListItem(v, () -> deleteHandler.accept(v), () -> editHandler.accept(v)));
			});
		}

		public VaccinationListItem getItemByVaccination(VaccinationDto vaccination) {
			return (VaccinationListItem) getComponent(vaccinations.indexOf(vaccination));
		}
	}

	private static class VaccinationListItem extends VerticalLayout {

		private static final long serialVersionUID = -8775209997959611902L;

		private final VaccinationDto vaccination;

		public VaccinationListItem(VaccinationDto vaccination, Runnable deleteHandler, Runnable editHandler) {
			this.vaccination = vaccination;

			setWidthFull();
			setMargin(false);
			setSpacing(false);

			buildLayout(deleteHandler, editHandler);
		}

		private void buildLayout(Runnable deleteHandler, Runnable editHandler) {

			HorizontalLayout vaccineLayout = new HorizontalLayout();
			vaccineLayout.setMargin(false);
			vaccineLayout.setSpacing(false);
			vaccineLayout.setWidthFull();

			String vaccine = vaccination.getVaccineName() != null
				? (vaccination.getVaccineName() == Vaccine.OTHER ? vaccination.getOtherVaccineName() : vaccination.getVaccineName().toString())
				: null;
			Label vaccineLabel = new Label(StringUtils.isNotBlank(vaccine) ? vaccine : I18nProperties.getString(Strings.labelNoVaccineName));
			CssStyles.style(vaccineLabel, CssStyles.LABEL_BOLD);

			Label dateLabel = new Label(
				vaccination.getVaccinationDate() != null
					? DateFormatHelper.formatDate(vaccination.getVaccinationDate())
					: I18nProperties.getString(Strings.labelNoVaccinationDate));

			VerticalLayout vaccineInfoLayout = new VerticalLayout(vaccineLabel, dateLabel);
			vaccineInfoLayout.setMargin(false);
			vaccineInfoLayout.setSpacing(false);
			vaccineLayout.addComponent(vaccineInfoLayout);
			vaccineLayout.setExpandRatio(vaccineInfoLayout, 1);

			Button deleteButton = ButtonHelper.createIconButtonWithCaption("delete-vaccination", null, VaadinIcons.TRASH, (e) -> {
				deleteHandler.run();
			}, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);

			Button editButton = ButtonHelper.createIconButtonWithCaption("edit-vaccination", null, VaadinIcons.PENCIL, (e) -> {
				editHandler.run();
			}, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);

			HorizontalLayout buttonsLayout = new HorizontalLayout(deleteButton, editButton);
			vaccineLayout.addComponent(buttonsLayout);
			vaccineLayout.setComponentAlignment(buttonsLayout, Alignment.TOP_RIGHT);
			vaccineLayout.setExpandRatio(buttonsLayout, 0);

			addComponent(vaccineLayout);

			Label horizontalRule = new Label("<br><hr /><br>", ContentMode.HTML);
			horizontalRule.setWidthFull();
			addComponent(horizontalRule);
		}

		public VaccinationDto getVaccination() {
			return vaccination;
		}

	}
}
