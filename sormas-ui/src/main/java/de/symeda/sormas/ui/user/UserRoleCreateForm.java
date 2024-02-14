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

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;

public class UserRoleCreateForm extends AbstractEditForm<UserRoleDto> {

	private static final long serialVersionUID = 8099247063020818190L;

	private static final String TEMPLATE_INFO_LOC = "templateInfo";
	private static final String TEMPLATE_USER_ROLE = "templateUserRole";

	private static final String HTML_LAYOUT = fluidRowLocs(TEMPLATE_USER_ROLE, TEMPLATE_INFO_LOC)
		+ fluidRowLocs(UserRoleDto.CAPTION, UserRoleDto.JURISDICTION_LEVEL)
		+ fluidRowLocs(UserRoleDto.DESCRIPTION)
		+ fluidRowLocs(UserRoleDto.HAS_OPTIONAL_HEALTH_FACILITY)
		+ fluidRowLocs(UserRoleDto.HAS_ASSOCIATED_DISTRICT_USER)
		+ fluidRowLocs(UserRoleDto.PORT_HEALTH_USER)
		+ fluidRowLocs(UserRoleDto.RESTRICT_ACCESS_TO_ASSIGNED_ENTITIES);

	protected UserRoleCreateForm() {
		super(UserRoleDto.class, UserRoleDto.I18N_PREFIX);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		Label infoLabel =
			new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getDescription(Descriptions.userRoleTemplate), ContentMode.HTML);
		infoLabel.setWidthFull();
		infoLabel.setCaption(StringUtils.EMPTY);
		getContent().addComponent(infoLabel, TEMPLATE_INFO_LOC);

		ComboBox templateRoleCombo = addCustomField(TEMPLATE_USER_ROLE, UserRoleDto.class, ComboBox.class);
		setSoftRequired(true, TEMPLATE_USER_ROLE);
		UserRoleFormHelper.setTemplateRoleItems(templateRoleCombo);
		templateRoleCombo.addValueChangeListener(e -> applyTemplateData((UserRoleDto) e.getProperty().getValue()));

		addField(UserRoleDto.CAPTION).setRequired(true);
		addField(UserRoleDto.JURISDICTION_LEVEL).setRequired(true);

		addField(UserRoleDto.DESCRIPTION, TextArea.class);

		addField(UserRoleDto.HAS_OPTIONAL_HEALTH_FACILITY).addStyleName(CssStyles.VSPACE_TOP_3);
		addField(UserRoleDto.HAS_ASSOCIATED_DISTRICT_USER).addStyleName(CssStyles.VSPACE_TOP_3);
		addField(UserRoleDto.PORT_HEALTH_USER).addStyleNames(CssStyles.VSPACE_TOP_3);
		addField(UserRoleDto.RESTRICT_ACCESS_TO_ASSIGNED_ENTITIES).addStyleNames(CssStyles.VSPACE_TOP_3, CssStyles.VSPACE_3);

		UserRoleFormHelper.createFieldDependencies(this);
	}

	private void applyTemplateData(UserRoleDto templateRole) {
		UserRoleDto userRole = getValue();

		if (templateRole == null) {
			userRole.setUserRights(Collections.emptySet());
			userRole.setEmailNotificationTypes(Collections.emptySet());
			userRole.setSmsNotificationTypes(Collections.emptySet());
		} else {
			userRole.setLinkedDefaultUserRole(templateRole.getLinkedDefaultUserRole());
			userRole.setUserRights(templateRole.getUserRights());
			userRole.setEmailNotificationTypes(templateRole.getEmailNotificationTypes());
			userRole.setSmsNotificationTypes(templateRole.getSmsNotificationTypes());

			this.<Field<JurisdictionLevel>> getField(UserRoleDto.JURISDICTION_LEVEL).setValue(templateRole.getJurisdictionLevel());
			this.<Field<Boolean>> getField(UserRoleDto.HAS_OPTIONAL_HEALTH_FACILITY).setValue(templateRole.getHasOptionalHealthFacility());
			this.<Field<Boolean>> getField(UserRoleDto.HAS_ASSOCIATED_DISTRICT_USER).setValue(templateRole.getHasAssociatedDistrictUser());
			this.<Field<Boolean>> getField(UserRoleDto.PORT_HEALTH_USER).setValue(templateRole.isPortHealthUser());
			this.<Field<Boolean>> getField(UserRoleDto.RESTRICT_ACCESS_TO_ASSIGNED_ENTITIES)
				.setValue(templateRole.isRestrictAccessToAssignedEntities());
		}
	}
}
