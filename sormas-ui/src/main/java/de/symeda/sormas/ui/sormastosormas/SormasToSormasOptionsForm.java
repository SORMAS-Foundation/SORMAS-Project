/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.sormastosormas;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class SormasToSormasOptionsForm extends AbstractEditForm<SormasToSormasOptionsDto> {

	private static final String CUSTOM_OPTIONS_PLACE_HOLDER = "__custom__";

	private static final String HTML_LAYOUT = fluidRowLocs(SormasToSormasOptionsDto.ORGANIZATION)
		+ CUSTOM_OPTIONS_PLACE_HOLDER
		+ fluidRowLocs(SormasToSormasOptionsDto.HAND_OVER_OWNERSHIP)
		+ fluidRowLocs(SormasToSormasOptionsDto.PSEUDONYMIZE_PERSONAL_DATA)
		+ fluidRowLocs(SormasToSormasOptionsDto.PSEUDONYMIZE_SENSITIVE_DATA)
		+ fluidRowLocs(SormasToSormasOptionsDto.COMMENT);

	private final List<String> customOptions;

	private List<String> excludedOrganizationIds;

	private final boolean hasOptions;

	private final Consumer<SormasToSormasOptionsForm> customFieldDependencies;

	public static SormasToSormasOptionsForm forCase(List<String> excludedOrganizationIds) {
		return new SormasToSormasOptionsForm(
			excludedOrganizationIds,
			true,
			Arrays.asList(SormasToSormasOptionsDto.WITH_ASSOCIATED_CONTACTS, SormasToSormasOptionsDto.WITH_SAMPLES),
			null);
	}

	public static SormasToSormasOptionsForm forContact(List<String> excludedOrganizationIds) {
		return new SormasToSormasOptionsForm(excludedOrganizationIds, true, Collections.singletonList(SormasToSormasOptionsDto.WITH_SAMPLES), null);
	}

	public static SormasToSormasOptionsForm forEvent(List<String> excludedOrganizationIds) {
		return new SormasToSormasOptionsForm(
			excludedOrganizationIds,
			true,
			Arrays.asList(SormasToSormasOptionsDto.WITH_EVENT_PARTICIPANTS, SormasToSormasOptionsDto.WITH_SAMPLES),
			(form) -> FieldHelper.setVisibleWhen(
				form.getFieldGroup(),
				SormasToSormasOptionsDto.WITH_SAMPLES,
				SormasToSormasOptionsDto.WITH_EVENT_PARTICIPANTS,
				Boolean.TRUE,
				true));
	}

	public static SormasToSormasOptionsForm withoutOptions() {
		return new SormasToSormasOptionsForm(null, false, null, null);
	}

	private SormasToSormasOptionsForm(
		List<String> excludedOrganizationIds,
		boolean hasOptions,
		List<String> customOptions,
		Consumer<SormasToSormasOptionsForm> customFieldDependencies) {
		super(SormasToSormasOptionsDto.class, SormasToSormasOptionsDto.I18N_PREFIX, false);

		this.customOptions = customOptions == null ? Collections.emptyList() : customOptions;
		this.excludedOrganizationIds = excludedOrganizationIds == null ? Collections.emptyList() : excludedOrganizationIds;
		this.customFieldDependencies = customFieldDependencies;
		this.hasOptions = hasOptions;

		addFields();

		setWidthUndefined();
		hideValidationUntilNextCommit();
	}

	@Override
	protected String createHtmlLayout() {
		String customLocs = customOptions.stream().map(LayoutUtil::fluidRowLocs).collect(Collectors.joining());

		return HTML_LAYOUT.replace(CUSTOM_OPTIONS_PLACE_HOLDER, customLocs);
	}

	@Override
	protected void addFields() {
		ComboBox organizationField = addField(SormasToSormasOptionsDto.ORGANIZATION, ComboBox.class);
		organizationField.setRequired(true);
		List<ServerAccessDataReferenceDto> organizations = FacadeProvider.getSormasToSormasFacade().getAvailableOrganizations();
		organizationField.addItems(organizations.stream().filter(o -> !excludedOrganizationIds.contains(o.getUuid())).collect(Collectors.toList()));

		if (hasOptions) {
			addFields(customOptions);

			addField(SormasToSormasOptionsDto.HAND_OVER_OWNERSHIP);

			addField(SormasToSormasOptionsDto.PSEUDONYMIZE_PERSONAL_DATA);

			CheckBox pseudonymizeSensitiveData = addField(SormasToSormasOptionsDto.PSEUDONYMIZE_SENSITIVE_DATA);
			pseudonymizeSensitiveData.addStyleNames(CssStyles.VSPACE_3);

			TextArea comment = addField(SormasToSormasOptionsDto.COMMENT, TextArea.class);
			comment.setRows(3);

			if (customFieldDependencies != null) {
				customFieldDependencies.accept(this);
			}
		}
	}

	public void disableOrganization() {
		getField(SormasToSormasOptionsDto.ORGANIZATION).setEnabled(false);
	}

	public void disableOrganizationAndOwnership() {
		disableOrganization();
		getField(SormasToSormasOptionsDto.HAND_OVER_OWNERSHIP).setEnabled(false);
	}
}
