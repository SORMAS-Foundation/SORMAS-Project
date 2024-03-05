/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.vaccination;

import static de.symeda.sormas.api.FacadeProvider.getCaseFacade;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class VaccinationController {

	public void create(
		ImmunizationReferenceDto immunization,
		Disease disease,
		UiFieldAccessCheckers fieldAccessCheckers,
		Consumer<VaccinationDto> commitCallback) {

		create(immunization, null, null, null, disease, fieldAccessCheckers, false, commitCallback);
	}

	public void create(
		RegionReferenceDto region,
		DistrictReferenceDto district,
		PersonReferenceDto person,
		Disease disease,
		UiFieldAccessCheckers fieldAccessCheckers,
		Consumer<VaccinationDto> commitCallback) {

		create(null, region, district, person, disease, fieldAccessCheckers, true, commitCallback);
	}

	private void create(
		ImmunizationReferenceDto immunization,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		PersonReferenceDto person,
		Disease disease,
		UiFieldAccessCheckers fieldAccessCheckers,
		boolean doSave,
		Consumer<VaccinationDto> commitCallback) {

		final CommitDiscardWrapperComponent<VaccinationEditForm> cdwComponent =
			getVaccinationCreateComponent(immunization, region, district, person, disease, fieldAccessCheckers, doSave, commitCallback);

		VaadinUiUtil.showModalPopupWindow(cdwComponent, I18nProperties.getCaption(VaccinationDto.I18N_PREFIX));
	}

	public CommitDiscardWrapperComponent<VaccinationEditForm> getVaccinationCreateComponent(
		ImmunizationReferenceDto immunization,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		PersonReferenceDto person,
		Disease disease,
		UiFieldAccessCheckers fieldAccessCheckers,
		boolean doSave,
		Consumer<VaccinationDto> commitCallback) {
		VaccinationEditForm form = new VaccinationEditForm(true, disease, fieldAccessCheckers);
		VaccinationDto vaccination = VaccinationDto.build(UserProvider.getCurrent().getUserReference());
		if (immunization != null) {
			vaccination.setImmunization(immunization);
		}
		form.setValue(vaccination);

		final CommitDiscardWrapperComponent<VaccinationEditForm> cdwComponent =
			new CommitDiscardWrapperComponent<>(form, UiUtil.permitted(UserRight.IMMUNIZATION_CREATE), form.getFieldGroup());
		cdwComponent.getCommitButton().setCaption(doSave ? I18nProperties.getCaption(Captions.actionSave) : I18nProperties.getString(Strings.done));

		cdwComponent.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				if (doSave && immunization != null) {
					FacadeProvider.getVaccinationFacade().save(form.getValue());
				} else if (doSave) {
					FacadeProvider.getVaccinationFacade().createWithImmunization(form.getValue(), region, district, person, disease);
				}

				if (commitCallback != null) {
					commitCallback.accept(form.getValue());
				}
			}
		});

		return cdwComponent;
	}

	public void edit(
		VaccinationDto vaccination,
		Disease disease,
		UiFieldAccessCheckers fieldAccessCheckers,
		boolean doSave,
		Consumer<VaccinationDto> commitCallback,
		Runnable deleteCallback,
		boolean isEditAllowed,
		boolean isDeleteAllowed) {

		VaccinationEditForm form = new VaccinationEditForm(true, disease, fieldAccessCheckers);
		form.setValue(vaccination);

		boolean isEditOrDeleteAllowed = isEditAllowed || isDeleteAllowed;
		final CommitDiscardWrapperComponent<VaccinationEditForm> editComponent =
			getVaccinationEditComponent(vaccination, disease, fieldAccessCheckers, doSave, commitCallback, isEditAllowed, isDeleteAllowed);

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(
			editComponent,
			I18nProperties.getString(!isEditAllowed ? Strings.headingViewVaccination : Strings.headingEditVaccination));

		if (isEditOrDeleteAllowed) {
			if (isDeleteAllowed) {
				editComponent.addDeleteWithReasonOrRestoreListener((deleteDetails) -> {
					popupWindow.close();
					if (doSave) {
						List<CaseDataDto> cases = getCaseFacade().getRelevantCasesForVaccination(vaccination)
							.stream()
							.filter(c -> !getCaseFacade().hasOtherValidVaccination(c, vaccination.getUuid()))
							.collect(Collectors.toList());
						if (!cases.isEmpty()) {
							showUpdateStatusConfirmationPopup(cases);
						}
						FacadeProvider.getVaccinationFacade().deleteWithImmunization(vaccination.getUuid(), deleteDetails);
					}
					if (deleteCallback != null) {
						deleteCallback.run();
					}
				}, I18nProperties.getCaption(VaccinationDto.I18N_PREFIX));
			}

			editComponent.restrictEditableComponentsOnEditView(
				UserRight.IMMUNIZATION_EDIT,
				null,
				UserRight.IMMUNIZATION_DELETE,
				null,
				vaccination.isInJurisdiction());
		}
		editComponent.getButtonsPanel().setVisible(isEditAllowed || isDeleteAllowed);
	}

	public static void showUpdateStatusConfirmationPopup(List<CaseDataDto> cases) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.CaseData_vaccinationStatusUpdate),
			new Label(I18nProperties.getString(Strings.confirmationVaccinationStatusUpdate)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			600,
			confirmedVaccinationStatusUpdate -> {
				if (confirmedVaccinationStatusUpdate) {
					cases.forEach(VaccinationController::updateVaccinationStatus);
					SormasUI.refreshView();
				} ;
			});
	}

	public static void updateVaccinationStatus(CaseDataDto caseDataDto) {
		getCaseFacade().updateVaccinationStatus(caseDataDto.toReference(), null);
	}

	public CommitDiscardWrapperComponent<VaccinationEditForm> getVaccinationEditComponent(
		VaccinationDto vaccination,
		Disease disease,
		UiFieldAccessCheckers fieldAccessCheckers,
		boolean doSave,
		Consumer<VaccinationDto> commitCallback,
		boolean isEditAllowed,
		boolean isDeleteAllowed) {

		boolean isEditOrDeleteAllowed = isEditAllowed || isDeleteAllowed;
		VaccinationEditForm form = new VaccinationEditForm(true, disease, fieldAccessCheckers);
		form.setValue(vaccination);

		final CommitDiscardWrapperComponent<VaccinationEditForm> editComponent =
			new CommitDiscardWrapperComponent<>(form, isEditOrDeleteAllowed, form.getFieldGroup());
		editComponent.getCommitButton().setCaption(doSave ? I18nProperties.getCaption(Captions.actionSave) : I18nProperties.getString(Strings.done));

		if (isEditAllowed) {
			editComponent.addCommitListener(() -> {
				if (!form.getFieldGroup().isModified()) {
					if (doSave) {
						FacadeProvider.getVaccinationFacade().save(form.getValue());
					}
					if (commitCallback != null) {
						commitCallback.accept(form.getValue());
					}
				}
			});
		}
		return editComponent;
	}
}
