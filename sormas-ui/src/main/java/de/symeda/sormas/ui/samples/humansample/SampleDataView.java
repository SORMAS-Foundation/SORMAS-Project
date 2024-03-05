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

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.EntityDto;
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
import de.symeda.sormas.ui.AbstractInfoLayout;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseInfoLayout;
import de.symeda.sormas.ui.contact.ContactInfoLayout;
import de.symeda.sormas.ui.events.EventParticipantInfoLayout;
import de.symeda.sormas.ui.samples.AbstractSampleView;
import de.symeda.sormas.ui.samples.AdditionalTestListComponent;
import de.symeda.sormas.ui.samples.HasName;
import de.symeda.sormas.ui.samples.pathogentestlink.PathogenTestListComponent;
import de.symeda.sormas.ui.sormastosormas.SormasToSormasListComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;

public class SampleDataView extends AbstractSampleView implements HasName {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String EDIT_LOC = "edit";
	public static final String CASE_LOC = "case";
	public static final String CONTACT_LOC = "contact";
	public static final String EVENT_PARTICIPANT_LOC = "eventParticipant";
	public static final String PATHOGEN_TESTS_LOC = "pathogenTests";
	public static final String ADDITIONAL_TESTS_LOC = "additionalTests";
	public static final String SORMAS_TO_SORMAS_LOC = "sormsToSormas";

	String oldViewName = null;

	private CommitDiscardWrapperComponent<SampleEditForm> editComponent;
	private Disease disease;

	public SampleDataView() {
		super(VIEW_NAME);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		View oldView = event.getOldView();
		if (oldView != null) {
			List<Class<?>> interfaces = Arrays.asList(oldView.getClass().getInterfaces());
			if (interfaces.contains(HasName.class)) {
				oldViewName = ((HasName) oldView).getName();
			}
		}
		initOrRedirect(event);
	}

	@Override
	protected void initView(String params) {

		setHeightUndefined();

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);

		SampleDto sampleDto = FacadeProvider.getSampleFacade().getSampleByUuid(getSampleRef().getUuid());

		disease = null;
		AbstractInfoLayout<EntityDto> dependentComponent = getDependentSideComponent(sampleDto);

		SampleController sampleController = ControllerProvider.getSampleController();
		editComponent = sampleController.getSampleEditComponent(
			getSampleRef().getUuid(),
			sampleDto.isPseudonymized(),
			sampleDto.isInJurisdiction(),
			disease,
			true,
			getOldViewName());

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

		LayoutWithSidePanel layout = new LayoutWithSidePanel(
			editComponent,
			CASE_LOC,
			CONTACT_LOC,
			EVENT_PARTICIPANT_LOC,
			PATHOGEN_TESTS_LOC,
			ADDITIONAL_TESTS_LOC,
			SORMAS_TO_SORMAS_LOC);

		container.addComponent(layout);

		if (dependentComponent != null) {
			if (dependentComponent.getClass().equals(CaseInfoLayout.class)) {
				layout.addSidePanelComponent(dependentComponent, CASE_LOC);
			} else if (dependentComponent.getClass().equals(ContactInfoLayout.class)) {
				layout.addSidePanelComponent(dependentComponent, CONTACT_LOC);
			} else if (dependentComponent.getClass().equals(EventParticipantInfoLayout.class)) {
				layout.addSidePanelComponent(dependentComponent, EVENT_PARTICIPANT_LOC);
			}
		}

		SampleReferenceDto sampleReferenceDto = getSampleRef();
		PathogenTestListComponent pathogenTestListComponent =
			new PathogenTestListComponent(sampleReferenceDto, this::showUnsavedChangesPopup, isEditAllowed());
		layout.addSidePanelComponent(new SideComponentLayout(pathogenTestListComponent), PATHOGEN_TESTS_LOC);

		if (UiUtil.permitted(FeatureType.ADDITIONAL_TESTS, UserRight.ADDITIONAL_TEST_VIEW)) {

			AdditionalTestListComponent additionalTestList =
				new AdditionalTestListComponent(sampleReferenceDto.getUuid(), this::showUnsavedChangesPopup, isEditAllowed());
			additionalTestList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(additionalTestList, ADDITIONAL_TESTS_LOC);
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

			layout.addSidePanelComponent(sormasToSormasLocLayout, SORMAS_TO_SORMAS_LOC);
		}

		final String uuid = sampleDto.getUuid();
		final boolean deleted = FacadeProvider.getSampleFacade().isDeleted(uuid);
		layout.disableIfNecessary(deleted, isEditAllowed() ? EditPermissionType.ALLOWED : EditPermissionType.REFUSED);
	}

	private AbstractInfoLayout<EntityDto> getDependentSideComponent(SampleDto sampleDto) {

		final CaseReferenceDto associatedCase = sampleDto.getAssociatedCase();
		if (associatedCase != null && UserProvider.getCurrent().hasAllUserRights(UserRight.CASE_VIEW)) {
			final CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid());
			disease = caseDto.getDisease();

			final CaseInfoLayout caseInfoLayout = new CaseInfoLayout(caseDto);
			caseInfoLayout.addStyleName(CssStyles.SIDE_COMPONENT);

			return (AbstractInfoLayout) caseInfoLayout;
		}

		final ContactReferenceDto associatedContact = sampleDto.getAssociatedContact();
		if (associatedContact != null && UserProvider.getCurrent().hasAllUserRights(UserRight.CONTACT_VIEW)) {
			final ContactDto contactDto = FacadeProvider.getContactFacade().getByUuid(associatedContact.getUuid());

			disease = contactDto.getDisease();

			final ContactInfoLayout contactInfoLayout =
				new ContactInfoLayout(contactDto, UiFieldAccessCheckers.getDefault(contactDto.isPseudonymized()));
			contactInfoLayout.addStyleName(CssStyles.SIDE_COMPONENT);

			return (AbstractInfoLayout) contactInfoLayout;
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
				UiFieldAccessCheckers.getDefault(eventParticipantDto.isPseudonymized()));

			eventParticipantInfoLayout.addStyleName(CssStyles.SIDE_COMPONENT);

			return (AbstractInfoLayout) eventParticipantInfoLayout;
		}
		return null;
	}

	public String getOldViewName() {
		return oldViewName;
	}

	@Override
	public String getName() {
		return VIEW_NAME;
	}
}
