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

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;

public class UserRoleCreateForm extends AbstractEditForm<UserRoleDto> {

	private static final long serialVersionUID = 8099247063020818190L;

	private static final String TEMPLATE_INFO_LOC = "templateInfo";
	private static final String TEMPLATE_USER_ROLE = "templateUserRole";

	private static final String HTML_LAYOUT = fluidRowLocs(TEMPLATE_INFO_LOC)
		+ fluidRowLocs(TEMPLATE_USER_ROLE, "")
		+ fluidRowLocs(UserRoleDto.CAPTION, UserRoleDto.JURISDICTION_LEVEL)
		+ fluidRowLocs(UserRoleDto.DESCRIPTION)
		+ fluidRowLocs(UserRoleDto.HAS_OPTIONAL_HEALTH_FACILITY)
		+ fluidRowLocs(UserRoleDto.HAS_ASSOCIATED_DISTRICT_USER)
		+ fluidRowLocs(UserRoleDto.PORT_HEALTH_USER);

	private ComboBox templateRoleCombo;

	protected UserRoleCreateForm() {
		super(UserRoleDto.class, UserRoleDto.I18N_PREFIX);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		getContent().addComponent(
			new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getDescription(Descriptions.userRoleTemplate), ContentMode.HTML),
			TEMPLATE_INFO_LOC);

		templateRoleCombo = addCustomField(TEMPLATE_USER_ROLE, UserRoleReferenceDto.class, ComboBox.class);
		setSoftRequired(true, TEMPLATE_USER_ROLE);
		Set<UserRoleReferenceDto> existingUserRoles = FacadeProvider.getUserRoleFacade().getAllAsReference();
		Set<UserRoleReferenceDto> defaultUserRoles = FacadeProvider.getUserRoleFacade().getDefaultsAsReference();

		ArrayList<UserRoleReferenceDto> templateItems = new ArrayList<>(existingUserRoles);
		templateItems.addAll(
			defaultUserRoles.stream()
				.map(
					r -> new UserRoleReferenceDto(
						r.getUuid(),
						r.getCaption() + " (" + I18nProperties.getCaption(Captions.captionDefault) + ")",
						r.isDefault()))
				.collect(Collectors.toSet()));

		FieldHelper.updateItems(templateRoleCombo, templateItems);

		addField(UserRoleDto.CAPTION).setRequired(true);
		addField(UserRoleDto.JURISDICTION_LEVEL).setRequired(true);

		addField(UserRoleDto.DESCRIPTION, TextArea.class);

		addField(UserRoleDto.HAS_OPTIONAL_HEALTH_FACILITY).addStyleName(CssStyles.VSPACE_TOP_3);
		addField(UserRoleDto.HAS_ASSOCIATED_DISTRICT_USER).addStyleName(CssStyles.VSPACE_TOP_3);
		addField(UserRoleDto.PORT_HEALTH_USER).addStyleName(CssStyles.VSPACE_TOP_3);
	}

	public UserRoleReferenceDto getSelectedTemplateRole() {
		return (UserRoleReferenceDto) templateRoleCombo.getValue();
	}
}
