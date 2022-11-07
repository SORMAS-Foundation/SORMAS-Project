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
package de.symeda.sormas.ui.samples;

import java.util.function.Consumer;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseInfoLayout;
import de.symeda.sormas.ui.contact.ContactInfoLayout;
import de.symeda.sormas.ui.events.EventParticipantInfoLayout;
import de.symeda.sormas.ui.samples.pathogentestlink.PathogenTestListComponent;
import de.symeda.sormas.ui.sormastosormas.SormasToSormasListComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;

public class SampleDataView extends AbstractSampleView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String EDIT_LOC = "edit";
	public static final String CASE_LOC = "case";
	public static final String CONTACT_LOC = "contact";
	public static final String EVENT_PARTICIPANT_LOC = "eventParticipant";
	public static final String PATHOGEN_TESTS_LOC = "pathogenTests";
	public static final String ADDITIONAL_TESTS_LOC = "additionalTests";
	public static final String SORMAS_TO_SORMAS_LOC = "sormsToSormas";

	private CommitDiscardWrapperComponent<SampleEditForm> editComponent;

	public SampleDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {

		setHeightUndefined();

		String htmlLayout = LayoutUtil.fluidRow(
			LayoutUtil.fluidColumnLoc(8, 0, 12, 0, EDIT_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CASE_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CONTACT_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, EVENT_PARTICIPANT_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, PATHOGEN_TESTS_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, ADDITIONAL_TESTS_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SORMAS_TO_SORMAS_LOC));

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		CustomLayout layout = new CustomLayout();
		layout.addStyleName(CssStyles.ROOT_COMPONENT);
		layout.setTemplateContents(htmlLayout);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();
		container.addComponent(layout);

		SampleDto sampleDto = FacadeProvider.getSampleFacade().getSampleByUuid(getSampleRef().getUuid());

		Disease disease = null;
		final CaseReferenceDto associatedCase = sampleDto.getAssociatedCase();
		if (associatedCase != null && UserProvider.getCurrent().hasAllUserRights(UserRight.CASE_VIEW)) {
			final CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid());
			disease = caseDto.getDisease();

			final CaseInfoLayout caseInfoLayout = new CaseInfoLayout(caseDto);
			caseInfoLayout.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addComponent(caseInfoLayout, CASE_LOC);
		}
		final ContactReferenceDto associatedContact = sampleDto.getAssociatedContact();
		if (associatedContact != null && UserProvider.getCurrent().hasAllUserRights(UserRight.CONTACT_VIEW)) {
			final ContactDto contactDto = FacadeProvider.getContactFacade().getByUuid(associatedContact.getUuid());

			disease = contactDto.getDisease();

			final ContactInfoLayout contactInfoLayout = new ContactInfoLayout(
				contactDto,
				UiFieldAccessCheckers.forDataAccessLevel(
					UserProvider.getCurrent().getPseudonymizableDataAccessLevel(contactDto.isInJurisdiction()),
					contactDto.isPseudonymized()));
			contactInfoLayout.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addComponent(contactInfoLayout, CONTACT_LOC);

		}
		final EventParticipantReferenceDto associatedEventParticipant = sampleDto.getAssociatedEventParticipant();
		if (associatedEventParticipant != null && UserProvider.getCurrent().hasAllUserRights(UserRight.EVENTPARTICIPANT_VIEW)) {
			final EventParticipantDto eventParticipantDto =
				FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(associatedEventParticipant.getUuid());
			final EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(eventParticipantDto.getEvent().getUuid(), false);

			disease = eventDto.getDisease();

			final EventParticipantInfoLayout eventParticipantInfoLayout = new EventParticipantInfoLayout(
				eventParticipantDto,
				eventDto,
				UiFieldAccessCheckers.forDataAccessLevel(
					UserProvider.getCurrent().getPseudonymizableDataAccessLevel(eventParticipantDto.isInJurisdiction()),
					eventParticipantDto.isPseudonymized()));

			eventParticipantInfoLayout.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addComponent(eventParticipantInfoLayout, EVENT_PARTICIPANT_LOC);
		}

		SampleController sampleController = ControllerProvider.getSampleController();
		editComponent = sampleController
			.getSampleEditComponent(getSampleRef().getUuid(), sampleDto.isPseudonymized(), sampleDto.isInJurisdiction(), disease, true);

		Consumer<Disease> createReferral = (relatedDisease) -> {
			// save changes before referral creation
			editComponent.commit();
			SampleDto committedSample = editComponent.getWrappedComponent().getValue();
			sampleController.createReferral(committedSample, relatedDisease);
		};
		Consumer<SampleDto> openReferredSample = referredSample -> sampleController.navigateToData(referredSample.getUuid());
		sampleController.addReferOrLinkToOtherLabButton(editComponent, disease, createReferral, openReferredSample);

		Consumer<SampleDto> navigate = targetSampleDto -> sampleController.navigateToData(targetSampleDto.getUuid());
		sampleController.addReferredFromButton(editComponent, navigate);

		editComponent.setMargin(new MarginInfo(false, false, true, false));
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(editComponent, EDIT_LOC);

		SampleReferenceDto sampleReferenceDto = getSampleRef();
		PathogenTestListComponent pathogenTestListComponent = new PathogenTestListComponent(sampleReferenceDto, this::showUnsavedChangesPopup);
		layout.addComponent(new SideComponentLayout(pathogenTestListComponent), PATHOGEN_TESTS_LOC);

		if (UserProvider.getCurrent() != null
			&& UserProvider.getCurrent().hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)
			&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.ADDITIONAL_TESTS)) {

			AdditionalTestListComponent additionalTestList = new AdditionalTestListComponent(sampleReferenceDto.getUuid());
			additionalTestList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addComponent(additionalTestList, ADDITIONAL_TESTS_LOC);
		}

		if (FacadeProvider.getSormasToSormasFacade()
			.isAnyFeatureConfigured(
				FeatureType.SORMAS_TO_SORMAS_SHARE_CASES,
				FeatureType.SORMAS_TO_SORMAS_SHARE_CONTACTS,
				FeatureType.SORMAS_TO_SORMAS_SHARE_EVENTS)) {
			VerticalLayout sormasToSormasLocLayout = new VerticalLayout();
			sormasToSormasLocLayout.setMargin(false);
			sormasToSormasLocLayout.setSpacing(false);

			SormasToSormasListComponent sormasToSormasListComponent = new SormasToSormasListComponent(sampleDto);
			sormasToSormasListComponent.addStyleNames(CssStyles.SIDE_COMPONENT);
			sormasToSormasLocLayout.addComponent(sormasToSormasListComponent);

			layout.addComponent(sormasToSormasLocLayout, SORMAS_TO_SORMAS_LOC);
		}

		final String uuid = sampleDto.getUuid();
		final boolean deleted = FacadeProvider.getSampleFacade().isDeleted(uuid);

		if (deleted) {
			editComponent.setEditable(false, CommitDiscardWrapperComponent.DELETE_UNDELETE);
			disableComponentIfNotNull(layout.getComponent(CASE_LOC));
			disableComponentIfNotNull(layout.getComponent(CONTACT_LOC));
			disableComponentIfNotNull(layout.getComponent(EVENT_PARTICIPANT_LOC));
			disableComponentIfNotNull(layout.getComponent(PATHOGEN_TESTS_LOC));
			disableComponentIfNotNull(layout.getComponent(ADDITIONAL_TESTS_LOC));
			disableComponentIfNotNull(layout.getComponent(SORMAS_TO_SORMAS_LOC));
		}

		if (!isEditAllowed()) {
			container.setEnabled(false);
		}
	}

	private void disableComponentIfNotNull(Component component) {
		if (component != null) {
			component.setEnabled(false);
		}
	}
}
