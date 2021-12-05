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

package de.symeda.sormas.ui.vaccination.list;

import java.util.List;
import java.util.function.Function;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.vaccination.VaccinationListCriteria;
import de.symeda.sormas.api.vaccination.VaccinationListEntryDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;
import de.symeda.sormas.ui.utils.components.sidecomponent.event.EditSideComponentFieldEventListener;

public class VaccinationListComponent extends SideComponent {

	private final AbstractDetailView<? extends ReferenceDto> view;

	public VaccinationListComponent(VaccinationListCriteria criteria, AbstractDetailView<? extends ReferenceDto> view) {
		super(I18nProperties.getString(Strings.entityVaccinations));
		this.view = view;

		VaccinationList vaccinationList = new VaccinationList(
			criteria.getDisease(),
			maxDisplayedEntries -> FacadeProvider.getVaccinationFacade().getEntriesList(criteria, 0, maxDisplayedEntries));
		vaccinationList.addSideComponentFieldEditEventListener(editSideComponentFieldEventListener(vaccinationList));
		addComponent(vaccinationList);
		vaccinationList.reload();
	}

	public VaccinationListComponent(
		CaseReferenceDto caseReferenceDto,
		VaccinationListCriteria criteria,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		AbstractDetailView<? extends ReferenceDto> view) {

		this(
			criteria,
			region,
			district,
			maxDisplayedEntries -> FacadeProvider.getVaccinationFacade()
				.getEntriesListWithRelevance(caseReferenceDto, criteria, 0, maxDisplayedEntries),
			view);
	}

	public VaccinationListComponent(
		ContactReferenceDto contactReferenceDto,
		VaccinationListCriteria criteria,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		AbstractDetailView<? extends ReferenceDto> view) {

		this(
			criteria,
			region,
			district,
			maxDisplayedEntries -> FacadeProvider.getVaccinationFacade()
				.getEntriesListWithRelevance(contactReferenceDto, criteria, 0, maxDisplayedEntries),
			view);
	}

	public VaccinationListComponent(
		EventParticipantReferenceDto eventParticipantReferenceDto,
		VaccinationListCriteria criteria,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		AbstractDetailView<? extends ReferenceDto> view) {

		this(
			criteria,
			region,
			district,
			maxDisplayedEntries -> FacadeProvider.getVaccinationFacade()
				.getEntriesListWithRelevance(eventParticipantReferenceDto, criteria, 0, maxDisplayedEntries),
			view);
	}

	private VaccinationListComponent(
		VaccinationListCriteria criteria,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		Function<Integer, List<VaccinationListEntryDto>> entriesListSupplier,
		AbstractDetailView<? extends ReferenceDto> view) {
		super(I18nProperties.getString(Strings.entityVaccinations));

		this.view = view;

		VaccinationList vaccinationList = new VaccinationList(criteria.getDisease(), entriesListSupplier);
		vaccinationList.addSideComponentFieldEditEventListener(editSideComponentFieldEventListener(vaccinationList));

		createNewVaccinationButton(criteria, region, district, SormasUI::refreshView);
		addComponent(vaccinationList);
		vaccinationList.reload();
	}

	private void createNewVaccinationButton(
		VaccinationListCriteria criteria,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		Runnable refreshCallback) {
		UserProvider currentUser = UserProvider.getCurrent();
		if (currentUser != null && currentUser.hasUserRight(UserRight.IMMUNIZATION_CREATE)) {
			Button createButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.vaccinationNewVaccination));
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(
				e -> view.showNavigationConfirmPopupIfDirty(
					() -> ControllerProvider.getVaccinationController()
						.create(
							region,
							district,
							criteria.getPerson(),
							criteria.getDisease(),
							UiFieldAccessCheckers.getNoop(),
							v -> refreshCallback.run())));
			addCreateButton(createButton);
		}
	}

	private EditSideComponentFieldEventListener editSideComponentFieldEventListener(VaccinationList vaccinationList) {
		return e -> {
			VaccinationListEntry listEntry = (VaccinationListEntry) e.getComponent();
			view.showNavigationConfirmPopupIfDirty(
				() -> ControllerProvider.getVaccinationController()
					.edit(
						FacadeProvider.getVaccinationFacade().getByUuid(listEntry.getVaccination().getUuid()),
						listEntry.getVaccination().getDisease(),
						UiFieldAccessCheckers.getDefault(listEntry.getVaccination().isPseudonymized()),
						true,
						v -> SormasUI.refreshView(),
						vaccinationList.deleteCallback()));
		};
	}
}
