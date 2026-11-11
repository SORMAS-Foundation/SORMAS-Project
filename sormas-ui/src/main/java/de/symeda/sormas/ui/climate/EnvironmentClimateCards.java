/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.climate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.climate.ClimateComparison;
import de.symeda.sormas.api.climate.ClimateDailySeriesDto;
import de.symeda.sormas.api.climate.ClimateRangePosition;
import de.symeda.sormas.api.climate.ClimateSummaryCardDto;
import de.symeda.sormas.api.climate.ClimateVariable;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * The three summary cards of the environment Climate data tab: rainfall, temperature and humidity over the last 7 days
 * with data, compared with the daily normals of those days, or with the same days last year before normals exist.
 * Warning colour only for a value outside the normal range.
 */
public class EnvironmentClimateCards extends VerticalLayout {

	private static final long serialVersionUID = 6915416713390367183L;

	/** The value of a card without data. */
	static final String NO_VALUE = "–";

	private final Label heading = new Label();
	private final Label normalsFrom = new Label();
	private final CssLayout cards = new CssLayout();

	public EnvironmentClimateCards() {

		setMargin(false);
		setSpacing(false);
		setWidthFull();
		heading.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE);
		normalsFrom.setWidthFull();
		// a flex row (climate.scss): the cards share the width, grow to the height of the tallest and wrap on narrow screens
		cards.addStyleName(CssStyles.CLIMATE_CARDS);
		cards.setWidthFull();
		addComponents(heading, normalsFrom, cards);
	}

	public void update(ClimateDailySeriesDto series) {

		cards.removeAllComponents();
		List<ClimateSummaryCardDto> cardData = series.getCards();
		ClimateSummaryCardDto first = cardData.isEmpty() ? null : cardData.get(0);
		if (first == null || first.getFirstDay() == null) {
			heading.setValue(I18nProperties.getString(Strings.climateCardsHeadingNoData));
			normalsFrom.setVisible(false);
			// the three cards still show, with a dash for the value, so the section is not blank (decision 114)
			for (ClimateVariable variable : ClimateVariable.values()) {
				VerticalLayout layout = cardLayout(variable);
				layout.addComponent(valueLabel(NO_VALUE));
				layout.addComponent(wrapping(I18nProperties.getString(Strings.climateCardNoData), null));
				cards.addComponent(layout);
			}
			return;
		}

		heading.setValue(
			String.format(
				I18nProperties.getString(Strings.climateCardsHeading),
				ClimateCaptions.date(first.getFirstDay()),
				ClimateCaptions.date(first.getLastDay())));

		boolean againstNormal = cardData.stream().anyMatch(c -> c.getComparison() == ClimateComparison.NORMAL);
		normalsFrom.setVisible(againstNormal);
		if (againstNormal) {
			normalsFrom.setValue(
				series.isDistrictNormals() && series.getDistrict() != null
					? String.format(
						I18nProperties.getString(Strings.climateCardsNormalFromDistrict),
						series.getFirstReferenceYear(),
						series.getLastReferenceYear(),
						series.getDistrict().getCaption())
					: String.format(
						I18nProperties.getString(Strings.climateCardsNormalFrom),
						series.getFirstReferenceYear(),
						series.getLastReferenceYear()));
		}

		for (ClimateSummaryCardDto card : cardData) {
			cards.addComponent(card(card));
		}
	}

	private static VerticalLayout card(ClimateSummaryCardDto card) {

		VerticalLayout layout = cardLayout(card.getVariable());

		// lines are in the normal text colour, except the noon reading note and the warning colour outside the normal range (decision 114)
		if (card.getComparison() == ClimateComparison.NOT_ENOUGH_DATA) {
			layout.addComponent(valueLabel(NO_VALUE));
			layout.addComponent(wrapping(I18nProperties.getString(Strings.climateCardNotEnoughData), null));
			layout.addComponent(wrapping(String.format(I18nProperties.getString(Strings.climateCardDays), card.getDays()), null));
			return layout;
		}

		layout.addComponent(valueLabel(value(card)));

		for (String line : comparisonLines(card)) {
			layout.addComponent(wrapping(line, null));
		}
		if (card.getComparison() == ClimateComparison.NORMAL && card.getPosition() != null) {
			boolean outside = card.getPosition() != ClimateRangePosition.WITHIN;
			layout.addComponent(wrapping(position(card.getPosition()), outside ? CssStyles.LABEL_WARNING : null));
		}
		if (card.getVariable() == ClimateVariable.HUMIDITY && card.getHumidityMeasure() == HumidityMeasure.NOON_READING) {
			layout.addComponent(wrapping(I18nProperties.getString(Strings.climateCardNoonReading), CssStyles.LABEL_SECONDARY));
		}
		if (card.getDays() < 7) {
			layout.addComponent(wrapping(String.format(I18nProperties.getString(Strings.climateCardDays), card.getDays()), null));
		}
		return layout;
	}

	/**
	 * @return a card with the variable's icon and name
	 */
	private static VerticalLayout cardLayout(ClimateVariable variable) {

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setSpacing(false);
		layout.setWidthFull();
		layout.addStyleName(CssStyles.CLIMATE_CARD);

		Label title = new Label(ClimateIcon.of(variable).html(ClimateIcon.LARGE) + " " + ClimateChartScript.html(title(variable)), ContentMode.HTML);
		title.addStyleName(CssStyles.LABEL_BOLD);
		layout.addComponent(title);
		return layout;
	}

	private static Label valueLabel(String text) {
		Label value = new Label(text);
		value.addStyleNames(CssStyles.LABEL_LARGE, CssStyles.LABEL_BOLD);
		return value;
	}

	CssLayout getCards() {
		return cards;
	}

	static String title(ClimateVariable variable) {
		switch (variable) {
		case RAINFALL:
			return I18nProperties.getCaption(Captions.climateRainfallTotal);
		case TEMPERATURE:
			return I18nProperties.getCaption(Captions.climateTemperatureMean);
		default:
			return I18nProperties.getCaption(Captions.climateHumidityMean);
		}
	}

	/**
	 * @return "64 mm", "27.4 °C", "78 %"
	 */
	static String value(ClimateSummaryCardDto card) {
		switch (card.getVariable()) {
		case RAINFALL:
			return ClimateCaptions.oneDecimal(card.getValue()) + " mm";
		case TEMPERATURE:
			return ClimateCaptions.oneDecimal(card.getValue()) + " °C";
		default:
			return Math.round(card.getValue()) + " %";
		}
	}

	/**
	 * @return the comparison in words: "38% more than normal", "+0.6 °C on normal", "Normally dry at this time of year",
	 *         "38% more rain than last year", "No earlier year to compare with yet"
	 */
	static List<String> comparisonLines(ClimateSummaryCardDto card) {

		List<String> lines = new ArrayList<>();
		boolean normal = card.getComparison() == ClimateComparison.NORMAL;
		if (card.getComparison() == ClimateComparison.VALUES_ONLY) {
			lines.add(I18nProperties.getString(Strings.climateCardValuesOnly));
			return lines;
		}

		if (card.getVariable() == ClimateVariable.RAINFALL) {
			if (normal && card.isNormallyDry()) {
				lines.add(I18nProperties.getString(Strings.climateCardNormallyDry));
			} else if (card.getPercentDifference() == null) {
				// last year's same days were dry: no percentage of 0 mm
				lines.add(I18nProperties.getString(Strings.climateCardDryLastYear));
			} else {
				double percent = card.getPercentDifference();
				String amount = String.valueOf(Math.round(Math.abs(percent)));
				String key = normal
					? (percent >= 0 ? Strings.climateCardMoreRain : Strings.climateCardLessRain)
					: (percent >= 0 ? Strings.climateCardMoreRainLastYear : Strings.climateCardLessRainLastYear);
				lines.add(String.format(I18nProperties.getString(key), amount));
			}
		} else if (card.getVariable() == ClimateVariable.TEMPERATURE || !normal) {
			if (card.getDifference() != null) {
				String unit =
					card.getVariable() == ClimateVariable.TEMPERATURE ? " °C" : " " + I18nProperties.getString(Strings.climatePercentagePoints);
				String difference = String.format(Locale.ROOT, "%+.1f", card.getDifference()) + unit;
				lines.add(
					String.format(
						I18nProperties.getString(normal ? Strings.climateCardDifferenceNormal : Strings.climateCardDifferenceLastYear),
						difference));
			}
		}
		return lines;
	}

	static String position(ClimateRangePosition position) {
		switch (position) {
		case ABOVE:
			return I18nProperties.getString(Strings.climateCardAboveRange);
		case BELOW:
			return I18nProperties.getString(Strings.climateCardBelowRange);
		default:
			return I18nProperties.getString(Strings.climateCardWithinRange);
		}
	}

	private static Label wrapping(String text, String style) {
		Label label = new Label(text);
		label.setWidthFull();
		if (style != null) {
			label.addStyleName(style);
		}
		return label;
	}
}
