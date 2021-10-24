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

package de.symeda.sormas.ui.sormastosormas;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasPersonPreview;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.api.utils.LocationHelper;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.ui.utils.CssStyles;

public class PreviewGridHelper {

	private static final String PERSON_NAME = "personName";
	private static final String BIRTH_DATE = "birthdDate";

	private PreviewGridHelper() {
	}

	public static <T> List<String> createPersonColumns(Grid<T> grid, Function<T, SormasToSormasPersonPreview> getPerson) {
		((Grid.Column<PseudonymizableDto, ?>) grid.addComponentColumn(previewData -> {
			SormasToSormasPersonPreview person = getPerson.apply(previewData);
			if (person.isPseudonymized()) {
				return new Label(I18nProperties.getCaption(Captions.inaccessibleValue));
			}
			return new Label(person.getFirstName() + " " + person.getLastName());
		})).setId(PERSON_NAME).setStyleGenerator(item -> {
			if (item.isPseudonymized()) {
				return CssStyles.INACCESSIBLE_COLUMN;
			}

			return "";
		});
		grid.addComponentColumn(
			previewData -> new Label(
				DateFormatHelper.formatDate(
					getPerson.apply(previewData).getBirthdateDD(),
					getPerson.apply(previewData).getBirthdateMM(),
					getPerson.apply(previewData).getBirthdateYYYY())))
			.setId(BIRTH_DATE);
		grid.addComponentColumn(previewData -> new Label(getPerson.apply(previewData).getSex().toString())).setId(SormasToSormasPersonPreview.SEX);
		grid.addComponentColumn(previewData -> new Label(LocationHelper.buildLocationString(getPerson.apply(previewData).getAddress())))
			.setId(SormasToSormasPersonPreview.ADDRESS);

		return Arrays.asList(PERSON_NAME, BIRTH_DATE, SormasToSormasPersonPreview.SEX, SormasToSormasPersonPreview.ADDRESS);
	}
}
