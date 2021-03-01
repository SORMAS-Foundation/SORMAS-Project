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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;

public class SormasToSormasOptionsForm extends AbstractEditForm<SormasToSormasOptionsDto> {

	private static final String HTML_LAYOUT = fluidRowLocs(SormasToSormasOptionsDto.ORGANIZATION)
		+ fluidRowLocs(SormasToSormasOptionsDto.WITH_ASSOCIATED_CONTACTS)
		+ fluidRowLocs(SormasToSormasOptionsDto.WITH_SAMPLES)
		+ fluidRowLocs(SormasToSormasOptionsDto.HAND_OVER_OWNERSHIP)
		+ fluidRowLocs(SormasToSormasOptionsDto.PSEUDONYMIZE_PERSONAL_DATA)
		+ fluidRowLocs(SormasToSormasOptionsDto.PSEUDONYMIZE_SENSITIVE_DATA)
		+ fluidRowLocs(SormasToSormasOptionsDto.COMMENT);

	private final boolean forCase;

	private List<String> excludedOrganizationIds;

	private final boolean hasOptions;

	public SormasToSormasOptionsForm(boolean isForCase, List<String> excludedOrganizationIds) {
		this(isForCase, excludedOrganizationIds, true);
	}

	public SormasToSormasOptionsForm(boolean hasOptions) {
		this(false, null, hasOptions);
	}

	public SormasToSormasOptionsForm(boolean isForCase, List<String> excludedOrganizationIds, boolean hasOptions) {
		super(SormasToSormasOptionsDto.class, SormasToSormasOptionsDto.I18N_PREFIX, false);

		this.forCase = isForCase;
		this.excludedOrganizationIds = excludedOrganizationIds == null ? Collections.emptyList() : excludedOrganizationIds;
		this.hasOptions = hasOptions;

		addFields();

		setWidthUndefined();
		hideValidationUntilNextCommit();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		ComboBox organizationField = addField(SormasToSormasOptionsDto.ORGANIZATION, ComboBox.class);
		organizationField.setRequired(true);
		List<ServerAccessDataReferenceDto> organizations = FacadeProvider.getSormasToSormasFacade().getAvailableOrganizations();
		organizationField.addItems(organizations.stream().filter(o -> !excludedOrganizationIds.contains(o.getUuid())).collect(Collectors.toList()));

		if (hasOptions) {
			if (forCase) {
				addField(SormasToSormasOptionsDto.WITH_ASSOCIATED_CONTACTS);
			}

			addField(SormasToSormasOptionsDto.WITH_SAMPLES);

			addField(SormasToSormasOptionsDto.HAND_OVER_OWNERSHIP);

			addField(SormasToSormasOptionsDto.PSEUDONYMIZE_PERSONAL_DATA);

			CheckBox pseudonymizeSensitiveData = addField(SormasToSormasOptionsDto.PSEUDONYMIZE_SENSITIVE_DATA);
			pseudonymizeSensitiveData.addStyleNames(CssStyles.VSPACE_3);
			TextArea comment = addField(SormasToSormasOptionsDto.COMMENT, TextArea.class);
			comment.setRows(3);
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
