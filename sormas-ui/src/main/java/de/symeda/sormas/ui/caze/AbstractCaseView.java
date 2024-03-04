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

package de.symeda.sormas.ui.caze;

import java.util.EnumSet;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.maternalhistory.MaternalHistoryView;
import de.symeda.sormas.ui.caze.porthealthinfo.PortHealthInfoView;
import de.symeda.sormas.ui.clinicalcourse.ClinicalCourseView;
import de.symeda.sormas.ui.epidata.CaseEpiDataView;
import de.symeda.sormas.ui.externalmessage.ExternalMessagesView;
import de.symeda.sormas.ui.hospitalization.HospitalizationView;
import de.symeda.sormas.ui.therapy.TherapyView;
import de.symeda.sormas.ui.utils.AbstractEditAllowedDetailView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DirtyStateComponent;
import de.symeda.sormas.ui.utils.ExternalJournalUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;
import de.symeda.sormas.ui.utils.ViewMode;

public abstract class AbstractCaseView extends AbstractEditAllowedDetailView<CaseReferenceDto> {

	public static final String VIEW_MODE_URL_PREFIX = "v";

	public static final String ROOT_VIEW_NAME = CasesView.VIEW_NAME;

	private Boolean hasOutbreak;
	private boolean caseFollowupEnabled;

	private final ViewConfiguration viewConfiguration;
	private final boolean redirectSimpleModeToCaseDataView;
	private final OptionGroup viewModeToggle;
	private final Property.ValueChangeListener viewModeToggleListener;

	protected AbstractCaseView(String viewName, boolean redirectSimpleModeToCaseDataView) {
		super(viewName);
		caseFollowupEnabled = UiUtil.enabled(FeatureType.CASE_FOLLOWUP);

		if (!ViewModelProviders.of(AbstractCaseView.class).has(ViewConfiguration.class)) {
			// init default view mode
			ViewConfiguration initViewConfiguration = UiUtil.permitted(UserRight.CLINICAL_COURSE_VIEW) || UiUtil.permitted(UserRight.THERAPY_VIEW)
				? new ViewConfiguration(ViewMode.NORMAL)
				: new ViewConfiguration(ViewMode.SIMPLE);
			ViewModelProviders.of(AbstractCaseView.class).get(ViewConfiguration.class, initViewConfiguration);
		}

		this.viewConfiguration = ViewModelProviders.of(AbstractCaseView.class).get(ViewConfiguration.class);
		this.redirectSimpleModeToCaseDataView = redirectSimpleModeToCaseDataView;

		viewModeToggle = new OptionGroup();
		CssStyles.style(viewModeToggle, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY, CssStyles.VSPACE_TOP_3);
		viewModeToggle.addItems((Object[]) ViewMode.values());
		viewModeToggle.setItemCaption(ViewMode.SIMPLE, I18nProperties.getEnumCaption(ViewMode.SIMPLE));
		viewModeToggle.setItemCaption(ViewMode.NORMAL, I18nProperties.getEnumCaption(ViewMode.NORMAL));
		// View mode toggle is hidden by default
		viewModeToggle.setVisible(false);
		addHeaderComponent(viewModeToggle);

		viewModeToggleListener = event -> {
			viewConfiguration.setViewMode((ViewMode) event.getProperty().getValue());
			// refresh
			ControllerProvider.getCaseController().navigateToCase(getReference().getUuid());
		};
		viewModeToggle.addValueChangeListener(viewModeToggleListener);
	}

	@Override
	protected CoreFacade getEditPermissionFacade() {
		return FacadeProvider.getCaseFacade();
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {

		if (!findReferenceByParams(params)) {
			return;
		}

		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getReference().getUuid());

		// Handle outbreaks for the disease and district of the case
		if (UiUtil.enabled(FeatureType.OUTBREAKS)
			&& (FacadeProvider.getOutbreakFacade().hasOutbreak(caze.getResponsibleDistrict(), caze.getDisease())
				|| FacadeProvider.getOutbreakFacade().hasOutbreak(caze.getDistrict(), caze.getDisease()))
			&& caze.getDisease().usesSimpleViewForOutbreaks()) {
			hasOutbreak = true;

			//			viewConfiguration.setViewMode(ViewMode.SIMPLE);
			//			// param might change this
			//			if (passedParams.length > 1 && passedParams[1].startsWith(VIEW_MODE_URL_PREFIX + "=")) {
			//				String viewModeString = passedParams[1].substring(2);
			//				try {
			//					viewConfiguration.setViewMode(ViewMode.valueOf(viewModeString.toUpperCase()));
			//				} catch (IllegalArgumentException ex) { } // just ignore
			//			}
			//
			viewModeToggle.removeValueChangeListener(viewModeToggleListener);
			viewModeToggle.setValue(viewConfiguration.getViewMode());
			viewModeToggle.addValueChangeListener(viewModeToggleListener);
			viewModeToggle.setVisible(true);

		} else {
			hasOutbreak = false;
			viewModeToggle.setVisible(false);
		}

		menu.removeAllViews();
		menu.addView(CasesView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, Captions.caseCasesList));

		if (UiUtil.permitted(FeatureType.EXTERNAL_MESSAGES, UserRight.EXTERNAL_MESSAGE_VIEW)
			&& FacadeProvider.getExternalMessageFacade().existsExternalMessageForEntity(getReference())) {
			menu.addView(ExternalMessagesView.VIEW_NAME, I18nProperties.getCaption(Captions.externalMessagesList));
		}

		menu.addView(CaseDataView.VIEW_NAME, I18nProperties.getCaption(CaseDataDto.I18N_PREFIX), params);

		boolean showExtraMenuEntries = UiUtil.disabled(FeatureType.OUTBREAKS)
			|| !hasOutbreak
			|| !caze.getDisease().usesSimpleViewForOutbreaks()
			|| viewConfiguration.getViewMode() != ViewMode.SIMPLE;
		if (showExtraMenuEntries) {
			menu.addView(CasePersonView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PERSON), params);
			if (caze.getDisease() == Disease.CONGENITAL_RUBELLA) {
				menu.addView(
					MaternalHistoryView.VIEW_NAME,
					I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.MATERNAL_HISTORY),
					params);
			}
			if (UiUtil.enabled(FeatureType.VIEW_TAB_CASES_HOSPITALIZATION)
				&& !caze.checkIsUnreferredPortHealthCase()
				&& !UserProvider.getCurrent().isPortHealthUser()) {
				menu.addView(
					HospitalizationView.VIEW_NAME,
					I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HOSPITALIZATION),
					params);
			}
			if (caze.getCaseOrigin() == CaseOrigin.POINT_OF_ENTRY
				&& ControllerProvider.getCaseController().hasPointOfEntry(caze)
				&& UiUtil.permitted(UserRight.PORT_HEALTH_INFO_VIEW)) {
				menu.addView(
					PortHealthInfoView.VIEW_NAME,
					I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PORT_HEALTH_INFO),
					params);
			}
			if (UiUtil.enabled(FeatureType.VIEW_TAB_CASES_SYMPTOMS)) {
				menu.addView(CaseSymptomsView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.SYMPTOMS), params);
			}
			if (UiUtil.enabled(FeatureType.VIEW_TAB_CASES_EPIDEMIOLOGICAL_DATA) && caze.getDisease() != Disease.CONGENITAL_RUBELLA) {
				menu.addView(CaseEpiDataView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.EPI_DATA), params);
			}
			if (UiUtil.permitted(FeatureType.VIEW_TAB_CASES_THERAPY, UserRight.THERAPY_VIEW)
				&& !caze.checkIsUnreferredPortHealthCase()
				&& UiUtil.enabled(FeatureType.CLINICAL_MANAGEMENT)) {
				menu.addView(TherapyView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.THERAPY), params);
			}
		}

		if (caseFollowupEnabled && UiUtil.enabled(FeatureType.VIEW_TAB_CASES_FOLLOW_UP) && caze.getFollowUpStatus() != FollowUpStatus.NO_FOLLOW_UP) {
			menu.addView(CaseVisitsView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.VISITS), params);
		}

		if (showExtraMenuEntries) {
			if (UiUtil.permitted(
				EnumSet.of(FeatureType.VIEW_TAB_CASES_FOLLOW_UP, FeatureType.VIEW_TAB_CASES_CLINICAL_COURSE, FeatureType.CLINICAL_MANAGEMENT),
				UserRight.CLINICAL_COURSE_VIEW) && !caze.checkIsUnreferredPortHealthCase()) {
				menu.addView(
					ClinicalCourseView.VIEW_NAME,
					I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CLINICAL_COURSE),
					params);
			}
		}
		if (FacadeProvider.getDiseaseConfigurationFacade().hasFollowUp(caze.getDisease())
			&& UiUtil.permitted(UserRight.CONTACT_VIEW)
			&& !caze.checkIsUnreferredPortHealthCase()) {
			menu.addView(CaseContactsView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, Captions.caseContacts), params);
		}

		if (caze.getExternalData() != null && !caze.getExternalData().isEmpty()) {
			menu.addView(CaseExternalDataView.VIEW_NAME, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.EXTERNAL_DATA), params);
		}

		setMainHeaderComponent(ControllerProvider.getCaseController().getCaseViewTitleLayout(caze));

		if (caseFollowupEnabled && UiUtil.permitted(UserRight.MANAGE_EXTERNAL_SYMPTOM_JOURNAL)) {
			PersonDto casePerson = FacadeProvider.getPersonFacade().getByUuid(caze.getPerson().getUuid());
			ExternalJournalUtil.getExternalJournalUiButton(casePerson, caze).ifPresent(getButtonsLayout()::addComponent);
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {

		super.enter(event);

		if (getReference() == null) {
			UI.getCurrent().getNavigator().navigateTo(getRootViewName());
		} else if (redirectSimpleModeToCaseDataView && getViewMode() == ViewMode.SIMPLE) {
			ControllerProvider.getCaseController().navigateToCase(getReference().getUuid());
		} else {
			initView(event.getParameters().trim());
		}
	}

	@Override
	protected String getRootViewName() {
		return ROOT_VIEW_NAME;
	}

	@Override
	protected CaseReferenceDto getReferenceByUuid(String uuid) {

		final CaseReferenceDto reference;
		if (FacadeProvider.getCaseFacade().exists(uuid)) {
			reference = FacadeProvider.getCaseFacade().getReferenceByUuid(uuid);
		} else {
			reference = null;
		}
		return reference;
	}

	@Override
	protected void setSubComponent(DirtyStateComponent newComponent) {

		super.setSubComponent(newComponent);

		if (getReference() != null && FacadeProvider.getCaseFacade().isDeleted(getReference().getUuid())) {
			newComponent.setEnabled(false);
		}
	}

	public CaseReferenceDto getCaseRef() {
		return (CaseReferenceDto) getReference();
	}

	public boolean isHasOutbreak() {
		return hasOutbreak;
	}

	public ViewMode getViewMode() {

		if (Boolean.FALSE.equals(hasOutbreak)) {
			return ViewMode.NORMAL;
		}

		return viewConfiguration.getViewMode();
	}
}
