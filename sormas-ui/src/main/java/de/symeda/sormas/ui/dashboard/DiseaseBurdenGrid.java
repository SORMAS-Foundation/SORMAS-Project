/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.dashboard;

import java.util.List;
import java.util.Locale;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.converter.StringToLongConverter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.PercentageRenderer;

@SuppressWarnings("serial")
public class DiseaseBurdenGrid extends Grid {

	private static final String VIEW_DETAILS_BTN_ID = "viewDetails";

	public DiseaseBurdenGrid() {
		setSizeFull();

		BeanItemContainer<DiseaseBurdenDto> container = new BeanItemContainer<DiseaseBurdenDto>(DiseaseBurdenDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);

		setColumns(/* VIEW_DETAILS_BTN_ID, */
				DiseaseBurdenDto.DISEASE, 
				DiseaseBurdenDto.CASE_COUNT, 
				DiseaseBurdenDto.PREVIOUS_CASE_COUNT,
				DiseaseBurdenDto.CASES_DIFFERENCE, 
				DiseaseBurdenDto.EVENT_COUNT,
				DiseaseBurdenDto.OUTBREAK_DISTRICT_COUNT,
				DiseaseBurdenDto.CASE_DEATH_COUNT,
				DiseaseBurdenDto.CASE_FATALITY_RATE);

		for (Column column : getColumns()) {
			if (column.getPropertyId().equals(VIEW_DETAILS_BTN_ID)) {
				column.setHeaderCaption("");
			} else {
				column.setHeaderCaption(I18nProperties.getPrefixCaption(DiseaseBurdenDto.I18N_PREFIX,
						column.getPropertyId().toString(), column.getHeaderCaption()));
			}
		}

		// format columns
		getColumn(DiseaseBurdenDto.CASE_FATALITY_RATE).setRenderer(new PercentageRenderer());

		// format casesGrowth column with chevrons
		getColumn(DiseaseBurdenDto.CASES_DIFFERENCE).setConverter(new StringToLongConverter() {
			@Override
			public String convertToPresentation(Long value, Class<? extends String> targetType, Locale locale)
					throws ConversionException {

				String stringRepresentation = super.convertToPresentation(value, targetType, locale);
				String chevronType = "";
				String criticalLevel = "";

				if (value > 0) {
					chevronType = FontAwesome.CHEVRON_UP.getHtml();
					criticalLevel = CssStyles.LABEL_CRITICAL;
				} else if (value < 0) {
					chevronType = FontAwesome.CHEVRON_DOWN.getHtml();
					criticalLevel = CssStyles.LABEL_POSITIVE;
				} else {
					chevronType = FontAwesome.CHEVRON_RIGHT.getHtml();
					criticalLevel = CssStyles.LABEL_IMPORTANT;
				}

				stringRepresentation = "<div class=\"v-label v-widget " + criticalLevel + " v-label-" + criticalLevel
						+ " align-center v-label-align-center bold v-label-bold large v-label-large v-has-width\" "
						+ "	  style=\"width: 15px;\">"
						+ "		<span class=\"v-icon\" style=\"font-family: FontAwesome;\">" + chevronType
						+ "		</span>" + "</div>";

				return stringRepresentation;
			}
		}).setRenderer(new HtmlRenderer());

		setSelectionMode(SelectionMode.NONE);
	}

	@SuppressWarnings("unchecked")
	private BeanItemContainer<DiseaseBurdenDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<DiseaseBurdenDto>) container.getWrappedContainer();
	}

	public void reload(List<DiseaseBurdenDto> items) {
		getContainer().removeAllItems();
		getContainer().addAll(items);
	}
}
