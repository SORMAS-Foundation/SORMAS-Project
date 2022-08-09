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

package de.symeda.sormas.ui.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.ui.utils.AbstractForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class UserRoleFormHelper {

	private UserRoleFormHelper() {
	}

	public static void createFieldDependencies(AbstractForm<UserRoleDto> form) {
		BeanFieldGroup<UserRoleDto> fieldGroup = form.getFieldGroup();
		FieldHelper.setVisibleWhen(
			fieldGroup,
			UserRoleDto.HAS_ASSOCIATED_DISTRICT_USER,
			UserRoleDto.JURISDICTION_LEVEL,
			Arrays.asList(JurisdictionLevel.COMMUNITY, JurisdictionLevel.HEALTH_FACILITY),
			true);
		FieldHelper
			.setDisabledWhen(fieldGroup, UserRoleDto.JURISDICTION_LEVEL, JurisdictionLevel.POINT_OF_ENTRY, UserRoleDto.PORT_HEALTH_USER, false);
		fieldGroup.getField(UserRoleDto.JURISDICTION_LEVEL).addValueChangeListener(e -> {
			CheckBox portHealthUserCb = (CheckBox) fieldGroup.getField(UserRoleDto.PORT_HEALTH_USER);
			portHealthUserCb.setValue(e.getProperty().getValue() == JurisdictionLevel.POINT_OF_ENTRY);
		});
	}

	public static void setTemplateRoleItems(ComboBox templateRoleCombo) {
		List<UserRoleDto> existingUserRoles =
			FacadeProvider.getUserRoleFacade().getAll().stream().sorted(Comparator.comparing(UserRoleDto::getCaption)).collect(Collectors.toList());
		List<UserRoleDto> defaultUserRoles = FacadeProvider.getUserRoleFacade()
			.getDefaultUserRolesAsDto()
			.stream()
			.sorted(Comparator.comparing(UserRoleDto::getCaption))
			.collect(Collectors.toList());
		defaultUserRoles.forEach(r -> r.setCaption(r.getCaption() + " (" + I18nProperties.getCaption(Captions.captionDefault) + ")"));

		ArrayList<UserRoleDto> templateItems = new ArrayList<>(existingUserRoles);
		templateItems.addAll(defaultUserRoles);

		FieldHelper.updateItems(templateRoleCombo, templateItems);
		templateItems.forEach(t -> templateRoleCombo.setItemCaption(t, t.getCaption()));
	}
}
