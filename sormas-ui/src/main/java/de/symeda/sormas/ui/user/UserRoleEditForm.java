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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.CheckboxSet;

public class UserRoleEditForm extends AbstractUserRoleForm {

	private static final long serialVersionUID = 8099247063020818190L;

	private static final String USER_RIGHTS_LABEL_LOC = "userRightsLabel";
	private static final String VERSION_UPDATE_INFO_LOC = "versionUpdateInfoLoc";

	private final static List<String> defaultRightsOrder = Arrays.asList("_VIEW", "_EDIT", "_CREATE");

	private static final String HTML_LAYOUT = fluidRowLocs(UserRoleDto.CAPTION, UserRoleDto.JURISDICTION_LEVEL)
		+ fluidRowLocs(UserRoleDto.LINKED_DEFAULT_USER_ROLE, VERSION_UPDATE_INFO_LOC)
		+ fluidRowLocs(UserRoleDto.DESCRIPTION)
		+ fluidRowLocs(UserRoleDto.HAS_OPTIONAL_HEALTH_FACILITY)
		+ fluidRowLocs(UserRoleDto.HAS_ASSOCIATED_DISTRICT_USER)
		+ fluidRowLocs(UserRoleDto.PORT_HEALTH_USER)
		+ fluidRowLocs(USER_RIGHTS_LABEL_LOC)
		+ fluidRowLocs(UserRoleDto.USER_RIGHTS);

	private CheckboxSet<UserRight> userRightCbSet;

	protected UserRoleEditForm() {
		super(UserRoleDto.class, UserRoleDto.I18N_PREFIX);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		addField(UserRoleDto.CAPTION).setRequired(true);
		addField(UserRoleDto.JURISDICTION_LEVEL).setRequired(true);

		addField(UserRoleDto.LINKED_DEFAULT_USER_ROLE);

		addField(UserRoleDto.DESCRIPTION, TextArea.class);

		addField(UserRoleDto.HAS_OPTIONAL_HEALTH_FACILITY).addStyleName(CssStyles.VSPACE_TOP_3);
		addField(UserRoleDto.HAS_ASSOCIATED_DISTRICT_USER).addStyleName(CssStyles.VSPACE_TOP_3);
		addField(UserRoleDto.PORT_HEALTH_USER).addStyleNames(CssStyles.VSPACE_TOP_3, CssStyles.VSPACE_3);

		Label userRightsLabel = new Label(I18nProperties.getCaption(Captions.UserRole_userRights), ContentMode.HTML);
		userRightsLabel.addStyleNames(CssStyles.H2);
		getContent().addComponent(userRightsLabel, USER_RIGHTS_LABEL_LOC);

		Label versionUpdateInfoLabel =
			new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getDescription(Descriptions.userRoleVersionUpdate), ContentMode.HTML);
		versionUpdateInfoLabel.setWidthFull();
		getContent().addComponent(versionUpdateInfoLabel, VERSION_UPDATE_INFO_LOC);

		userRightCbSet = addField(UserRoleDto.USER_RIGHTS, CheckboxSet.class);
		userRightCbSet.setCaption(null);
		userRightCbSet.setItems(getSortedUserRights(), r -> r.getUserRightGroup().toString(), (r) -> {
			String description = r.getDescription();
			Set<UserRight> requiredUserRights = UserRight.getRequiredUserRights(Collections.singleton(r));
			if (CollectionUtils.isEmpty(requiredUserRights)) {
				return description;
			}

			return description + "\n" + I18nProperties.getCaption(Captions.requiredUserRights) + ": "
				+ requiredUserRights.stream().map(UserRight::toString).collect(Collectors.joining(", "));
		});
		userRightCbSet.addCheckboxValueChangeListener(e -> {
			CheckBox checkbox = e.getCheckbox();
			if (Boolean.TRUE.equals(checkbox.getValue())) {
				Set<UserRight> requiredUserRights = UserRight.getRequiredUserRights(Collections.singleton((UserRight) checkbox.getData()));
				requiredUserRights.forEach(r -> userRightCbSet.getCheckboxByData(r).ifPresent(cb -> cb.setValue(true)));
			}
		});

		UserRoleFormHelper.createFieldDependencies(this);
	}

	private List<UserRight> getSortedUserRights() {
		return Stream.of(UserRight.values()).sorted((r1, r2) -> {
			int groupOrder1 = r1.getUserRightGroup().ordinal();
			int groupOrder2 = r2.getUserRightGroup().ordinal();

			int rightOrder1 = IntStream.range(0, defaultRightsOrder.size())
				.filter(i -> r1.name().endsWith(defaultRightsOrder.get(i)))
				.findFirst()
				.orElse(defaultRightsOrder.size());
			int rightOrder2 = IntStream.range(0, defaultRightsOrder.size())
				.filter(i -> r2.name().endsWith(defaultRightsOrder.get(i)))
				.findFirst()
				.orElse(defaultRightsOrder.size());

			return groupOrder1 != groupOrder2 ? groupOrder1 - groupOrder2 : rightOrder1 - rightOrder2;
		}).collect(Collectors.toList());
	}

	void applyTemplateData(UserRoleDto templateRole) {
		if (templateRole != null) {
			userRightCbSet.setValue(templateRole.getUserRights());
			getValue().setEmailNotificationTypes(templateRole.getEmailNotificationTypes());
			getValue().setSmsNotificationTypes(templateRole.getSmsNotificationTypes());
			this.<Field<JurisdictionLevel>> getField(UserRoleDto.JURISDICTION_LEVEL).setValue(templateRole.getJurisdictionLevel());
			this.<Field<Boolean>> getField(UserRoleDto.HAS_OPTIONAL_HEALTH_FACILITY).setValue(templateRole.getHasOptionalHealthFacility());
			this.<Field<Boolean>> getField(UserRoleDto.HAS_ASSOCIATED_DISTRICT_USER).setValue(templateRole.getHasAssociatedDistrictUser());
			this.<Field<Boolean>> getField(UserRoleDto.PORT_HEALTH_USER).setValue(templateRole.isPortHealthUser());
		}
	}

	@Override
	DefaultUserRole getDefaultUserRole() {
		return (DefaultUserRole) getField(UserRoleDto.LINKED_DEFAULT_USER_ROLE).getValue();
	}
}
