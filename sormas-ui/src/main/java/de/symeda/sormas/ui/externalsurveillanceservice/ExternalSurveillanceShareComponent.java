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

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.share.ExternalShareInfoCriteria;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DirtyStateComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ExternalSurveillanceShareComponent extends VerticalLayout {

	private static final long serialVersionUID = 4474741971417834271L;

	public ExternalSurveillanceShareComponent(
		String entityString,
		Runnable gatewayCall,
		ExternalShareInfoCriteria shareInfoCriteria,
		DirtyStateComponent editComponent) {
		initLayout(entityString, gatewayCall, shareInfoCriteria, editComponent);
	}

	private void initLayout(
		String entityString,
		Runnable gatewayCall,
		ExternalShareInfoCriteria shareInfoCriteria,
		DirtyStateComponent editComponent) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);
		addStyleNames(CssStyles.SIDE_COMPONENT);

		addComponent(createHeader(entityString, gatewayCall, editComponent));
		addComponent(createShareInfoList(shareInfoCriteria));
	}

	private HorizontalLayout createHeader(String entityString, Runnable gatewayCall, DirtyStateComponent editComponent) {
		Label header = new Label(I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_title));
		header.addStyleName(CssStyles.H3);

		Button button = ButtonHelper.createIconButton(
			Captions.ExternalSurveillanceToolGateway_send,
			VaadinIcons.OUTBOX,
			e -> onSendButtonClick(entityString, gatewayCall, editComponent),
			ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout headerLayout = new HorizontalLayout(header, button);
		headerLayout.setExpandRatio(button, 1);
		headerLayout.setComponentAlignment(header, Alignment.MIDDLE_LEFT);
		headerLayout.setComponentAlignment(button, Alignment.MIDDLE_RIGHT);
		headerLayout.setWidth(100, Unit.PERCENTAGE);
		return headerLayout;
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
