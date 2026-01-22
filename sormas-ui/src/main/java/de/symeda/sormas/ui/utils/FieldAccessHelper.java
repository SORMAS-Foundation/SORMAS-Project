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

package de.symeda.sormas.ui.utils;

import java.util.stream.Stream;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.ui.UiUtil;

public class FieldAccessHelper {

	public static boolean isAllInaccessible(String... values) {
		return Stream.of(values).allMatch(v -> I18nProperties.getCaption(Captions.inaccessibleValue).equals(v));
	}

	public static <T> UiFieldAccessCheckers<T> getFieldAccessCheckers(boolean inJurisdiction, boolean isPseudonymized) {
		return UiFieldAccessCheckers.forDataAccessLevel(
			UiUtil.getPseudonymizableDataAccessLevel(inJurisdiction),
			isPseudonymized,
			FacadeProvider.getConfigFacade().getCountryLocale());
	}

	public static <T extends PseudonymizableDto> UiFieldAccessCheckers<T> getFieldAccessCheckers(T dto) {
		return UiFieldAccessCheckers.forDataAccessLevel(
			UiUtil.getPseudonymizableDataAccessLevel(dto.isInJurisdiction()),
			dto.isPseudonymized(),
			FacadeProvider.getConfigFacade().getCountryLocale());
	}

}
