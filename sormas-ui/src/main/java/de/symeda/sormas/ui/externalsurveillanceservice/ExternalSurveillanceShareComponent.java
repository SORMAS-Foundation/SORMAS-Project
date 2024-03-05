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

package de.symeda.sormas.ui.externalsurveillanceservice;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.share.ExternalShareInfoCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DirtyStateComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ExternalSurveillanceShareComponent extends VerticalLayout {

	private static final long serialVersionUID = 4474741971417834271L;

	public ExternalSurveillanceShareComponent(
		String entityString,
		Runnable sendHandler,
		Runnable deleteHandler,
		ExternalShareInfoCriteria shareInfoCriteria,
		DirtyStateComponent editComponent) {
		initLayout(entityString, sendHandler, deleteHandler, shareInfoCriteria, editComponent);
	}

	private void initLayout(
		String entityString,
		Runnable sendHandler,
		Runnable deleteHandler,
		ExternalShareInfoCriteria shareInfoCriteria,
		DirtyStateComponent editComponent) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);
		addStyleNames(CssStyles.SIDE_COMPONENT);

		if (shareInfoCriteria.getCaze() != null) {
			String caseUuid = shareInfoCriteria.getCaze().getUuid();
			addComponent(createHeader(entityString, sendHandler, deleteHandler, editComponent, caseUuid, null));
		}

		if (shareInfoCriteria.getEvent() != null) {
			String eventUuid = shareInfoCriteria.getEvent().getUuid();
			addComponent(createHeader(entityString, sendHandler, deleteHandler, editComponent, null, eventUuid));
		}
		addComponent(createShareInfoList(shareInfoCriteria));
	}

	private HorizontalLayout createHeader(
		String entityName,
		Runnable sendHandler,
		Runnable deleteHandler,
		DirtyStateComponent editComponent,
		String caseUuid,
		String eventUuid) {
		Label header = new Label(I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_title));
		header.addStyleName(CssStyles.H3);

		HorizontalLayout headerLayout = new HorizontalLayout(header);
		headerLayout.setComponentAlignment(header, Alignment.MIDDLE_LEFT);

		if (sendHandler != null && UiUtil.permitted(UserRight.EXTERNAL_SURVEILLANCE_SHARE)) {
			Button sendButton = ButtonHelper.createIconButton(
				Captions.ExternalSurveillanceToolGateway_send,
				VaadinIcons.OUTBOX,
				e -> onSendButtonClick(entityName, sendHandler, editComponent),
				ValoTheme.BUTTON_PRIMARY);

			headerLayout.addComponent(sendButton);
			headerLayout.setExpandRatio(sendButton, 1);
			headerLayout.setComponentAlignment(sendButton, Alignment.MIDDLE_RIGHT);
		}

		if (UiUtil.permitted(UserRight.EXTERNAL_SURVEILLANCE_DELETE)) {
			if (caseUuid != null) {
				if (deleteHandler != null && isVisibleDeleteButtonForCase(caseUuid)) {
					addDeleteButtonToLayout(deleteHandler, headerLayout);
				}
			}

			if (eventUuid != null) {
				if (deleteHandler != null && isVisibleDeleteButtonForEvent(eventUuid)) {
					addDeleteButtonToLayout(deleteHandler, headerLayout);
				}
			}
		}

		return headerLayout;
	}

	private void addDeleteButtonToLayout(Runnable deleteHandler, HorizontalLayout headerLayout) {
		Button deleteButton = ButtonHelper.createIconButton("", VaadinIcons.TRASH, e -> deleteHandler.run(), ValoTheme.BUTTON_ICON_ONLY);
		headerLayout.addComponent(deleteButton);
		headerLayout.setComponentAlignment(deleteButton, Alignment.MIDDLE_RIGHT);
		headerLayout.setWidth(100, Unit.PERCENTAGE);
	}

	private boolean isVisibleDeleteButtonForCase(String caseUuid) {
		return FacadeProvider.getExternalShareInfoFacade().isSharedCase(caseUuid);
	}

	private boolean isVisibleDeleteButtonForEvent(String eventUuid) {
		return FacadeProvider.getExternalShareInfoFacade().isSharedEvent(eventUuid);
	}

	private ExternalShareInfoList createShareInfoList(ExternalShareInfoCriteria criteria) {
		ExternalShareInfoList externalShareInfoList =
			new ExternalShareInfoList(criteria, true, Captions.ExternalSurveillanceToolGateway_notTransferred);

		externalShareInfoList.reload();

		return externalShareInfoList;
	}

	private void onSendButtonClick(String entityString, Runnable gatewayCall, DirtyStateComponent editComponent) {

		if (editComponent.isDirty()) {
			VaadinUiUtil.showSimplePopupWindow(
				I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_unableToSend),
				String.format(I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_unableToSend), entityString));
		} else {
			gatewayCall.run();
		}
	}
}
