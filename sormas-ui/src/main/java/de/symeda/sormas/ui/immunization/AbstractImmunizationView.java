/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.ui.immunization;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.caze.CaseDataView;
import de.symeda.sormas.ui.contact.ContactDataView;
import de.symeda.sormas.ui.events.EventParticipantDataView;
import de.symeda.sormas.ui.utils.AbstractEditAllowedDetailView;
import de.symeda.sormas.ui.utils.DirtyStateComponent;

public abstract class AbstractImmunizationView extends AbstractEditAllowedDetailView<ImmunizationReferenceDto> {

	public static final String ROOT_VIEW_NAME = ImmunizationsView.VIEW_NAME;

	/**
	 * URL parameter holding the UUID of the case an immunization was opened from, e.g.
	 * {@code immunizations/data/XYZ789-...?caseUuid=ABC123-...}.
	 */
	public static final String ORIGIN_CASE_PARAM = "caseUuid";
	/**
	 * URL parameter holding the UUID of the contact an immunization was opened from, e.g.
	 * {@code immunizations/data/XYZ789-...?contactUuid=ABC123-...}.
	 */
	public static final String ORIGIN_CONTACT_PARAM = "contactUuid";
	/**
	 * URL parameter holding the UUID of the event participant an immunization was opened from, e.g.
	 * {@code immunizations/data/XYZ789-...?eventParticipantUuid=ABC123-...}.
	 */
	public static final String ORIGIN_EVENT_PARTICIPANT_PARAM = "eventParticipantUuid";

	protected AbstractImmunizationView(String viewName) {
		super(viewName);
	}

	@Override
	protected CoreFacade getEditPermissionFacade() {
		return FacadeProvider.getImmunizationFacade();
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {

		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	protected ImmunizationReferenceDto getReferenceByUuid(String uuid) {
		final ImmunizationReferenceDto reference;
		if (FacadeProvider.getImmunizationFacade().exists(uuid)) {
			reference = FacadeProvider.getImmunizationFacade().getReferenceByUuid(uuid);
		} else {
			reference = null;
		}
		return reference;
	}

	@Override
	protected String getRootViewName() {
		return ROOT_VIEW_NAME;
	}

	@Override
	protected void initView(String params) {

	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		if (!findReferenceByParams(params)) {
			return;
		}

		menu.removeAllViews();
		menu.addView(ImmunizationsView.VIEW_NAME, I18nProperties.getCaption(Captions.immunizationImmunizationsList));

		final String originCaseUuid = getUrlParam(params, ORIGIN_CASE_PARAM);
		if (originCaseUuid != null && UiUtil.permitted(UserRight.CASE_VIEW) && FacadeProvider.getCaseFacade().exists(originCaseUuid)) {
			menu.addView(CaseDataView.VIEW_NAME, I18nProperties.getString(Strings.entityCase), originCaseUuid, true);
		}

		final String originContactUuid = getUrlParam(params, ORIGIN_CONTACT_PARAM);
		if (originContactUuid != null
			&& UiUtil.permitted(UserRight.CONTACT_VIEW)
			&& FacadeProvider.getContactFacade().exists(originContactUuid)) {
			menu.addView(ContactDataView.VIEW_NAME, I18nProperties.getString(Strings.entityContact), originContactUuid, true);
		}

		final String originEventParticipantUuid = getUrlParam(params, ORIGIN_EVENT_PARTICIPANT_PARAM);
		if (originEventParticipantUuid != null
			&& UiUtil.permitted(UserRight.EVENT_VIEW)
			&& FacadeProvider.getEventParticipantFacade().exists(originEventParticipantUuid)) {
			menu.addView(
				EventParticipantDataView.VIEW_NAME,
				I18nProperties.getString(Strings.entityEventParticipant),
				originEventParticipantUuid,
				true);
		}

		menu.addView(ImmunizationDataView.VIEW_NAME, I18nProperties.getCaption(ImmunizationDto.I18N_PREFIX), params);
		menu.addView(ImmunizationPersonView.VIEW_NAME, I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.PERSON), params);

		setMainHeaderComponent(ControllerProvider.getImmunizationController().getImmunizationViewTitleLayout(getReference().getUuid()));
	}

	/**
	 * Reads the value of the URL parameter {@code name} from the parameters of this view.
	 * <p>
	 * Example: {@code getUrlParam("XYZ789-...?caseUuid=ABC123-...", "caseUuid")} returns {@code "ABC123-..."};
	 * {@code getUrlParam("XYZ789-...", "caseUuid")} (opened from the person or the immunization directory) returns {@code null}.
	 *
	 * @return The trimmed value, or {@code null} if the parameter is missing or blank.
	 */
	static String getUrlParam(String params, String name) {

		if (StringUtils.isBlank(params)) {
			return null;
		}

		String[] passedParams = params.split("\\?", 2);
		if (passedParams.length < 2) {
			return null;
		}

		for (String param : passedParams[1].split("&")) {
			String[] keyValue = param.split("=", 2);
			if (keyValue.length == 2 && keyValue[0].trim().equals(name) && StringUtils.isNotBlank(keyValue[1])) {
				return keyValue[1].trim();
			}
		}

		return null;
	}

	@Override
	protected void setSubComponent(DirtyStateComponent newComponent) {
		super.setSubComponent(newComponent);

		ImmunizationDto dto = FacadeProvider.getImmunizationFacade().getByUuid(getReference().getUuid());
		if (dto.isDeleted()) {
			newComponent.setEnabled(false);
		}
	}
}
