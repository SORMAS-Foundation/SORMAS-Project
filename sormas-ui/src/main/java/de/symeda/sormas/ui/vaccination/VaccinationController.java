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

import java.util.function.Consumer;

import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
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

		VaccinationEditForm form = new VaccinationEditForm(true, disease, fieldAccessCheckers);
		VaccinationDto vaccination = VaccinationDto.build(UserProvider.getCurrent().getUserReference());
		if (immunization != null) {
			vaccination.setImmunization(immunization);
		}
		form.setValue(vaccination);

		final CommitDiscardWrapperComponent<VaccinationEditForm> cdwComponent =
			new CommitDiscardWrapperComponent<>(form, UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_CREATE), form.getFieldGroup());
		cdwComponent.getCommitButton().setCaption(doSave ? I18nProperties.getCaption(Captions.actionSave) : I18nProperties.getString(Strings.done));

		VaadinUiUtil.showModalPopupWindow(cdwComponent, I18nProperties.getCaption(VaccinationDto.I18N_PREFIX));

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
	}

	public void edit(
		VaccinationDto vaccination,
		Disease disease,
		UiFieldAccessCheckers fieldAccessCheckers,
		boolean doSave,
		Consumer<VaccinationDto> commitCallback,
		Runnable deleteCallback) {

		VaccinationEditForm form = new VaccinationEditForm(true, disease, fieldAccessCheckers);
		form.setValue(vaccination);

		final CommitDiscardWrapperComponent<VaccinationEditForm> cdwComponent =
			new CommitDiscardWrapperComponent<>(form, UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_EDIT), form.getFieldGroup());
		cdwComponent.getCommitButton().setCaption(doSave ? I18nProperties.getCaption(Captions.actionSave) : I18nProperties.getString(Strings.done));

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(cdwComponent, I18nProperties.getCaption(VaccinationDto.I18N_PREFIX));

		cdwComponent.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				if (doSave) {
					FacadeProvider.getVaccinationFacade().save(form.getValue());
				}
				if (commitCallback != null) {
					commitCallback.accept(form.getValue());
				}
			}
		});

		if (UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_DELETE)) {
			cdwComponent.addDeleteWithReasonListener((deleteDetails) -> {
				popupWindow.close();
				if (doSave) {
					FacadeProvider.getVaccinationFacade().deleteWithImmunization(vaccination.getUuid(), deleteDetails);
				}
				if (deleteCallback != null) {
					deleteCallback.run();
				}
			}, I18nProperties.getCaption(VaccinationDto.I18N_PREFIX));
		}
	}

}
