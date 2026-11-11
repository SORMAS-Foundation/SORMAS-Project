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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.climate.ClimateComparison;
import de.symeda.sormas.api.climate.ClimateDailySeriesDto;
import de.symeda.sormas.api.climate.ClimateSummaryCardDto;
import de.symeda.sormas.api.climate.ClimateVariable;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.ui.utils.CssStyles;

class EnvironmentClimateCardsTest {

	@Test
	void testWithoutDataTheCardsShowADash() {

		EnvironmentClimateCards cards = new EnvironmentClimateCards();
		cards.update(series());

		assertEquals(3, cards.getCards().getComponentCount());
		for (Component card : cards.getCards()) {
			List<Label> labels = labels(card);
			assertEquals(EnvironmentClimateCards.NO_VALUE, labels.get(1).getValue());
			assertEquals("No data yet", labels.get(2).getValue());
		}
	}

	@Test
	void testOnlyTheNoonReadingLineIsSecondary() {

		LocalDate day = LocalDate.of(2026, 11, 6);
		ClimateSummaryCardDto rainfall = new ClimateSummaryCardDto(ClimateVariable.RAINFALL, day.minusDays(6), day);
		rainfall.setComparison(ClimateComparison.NOT_ENOUGH_DATA);
		rainfall.setDays(2);
		ClimateSummaryCardDto humidity = new ClimateSummaryCardDto(ClimateVariable.HUMIDITY, day.minusDays(6), day);
		humidity.setComparison(ClimateComparison.VALUES_ONLY);
		humidity.setDays(5);
		humidity.setValue(68d);
		humidity.setHumidityMeasure(HumidityMeasure.NOON_READING);
		ClimateDailySeriesDto series = series();
		series.getCards().add(rainfall);
		series.getCards().add(humidity);

		EnvironmentClimateCards cards = new EnvironmentClimateCards();
		cards.update(series);

		List<Label> notEnough = labels(cards.getCards().getComponent(0));
		assertEquals(EnvironmentClimateCards.NO_VALUE, notEnough.get(1).getValue());
		notEnough.forEach(label -> assertFalse(label.getStyleName().contains(CssStyles.LABEL_SECONDARY), label.getValue()));

		for (Label label : labels(cards.getCards().getComponent(1))) {
			assertEquals("Noon reading".equals(label.getValue()), label.getStyleName().contains(CssStyles.LABEL_SECONDARY), label.getValue());
		}
		assertTrue(labels(cards.getCards().getComponent(1)).stream().anyMatch(l -> "5 of 7 days with data".equals(l.getValue())));
	}

	private static ClimateDailySeriesDto series() {
		return new ClimateDailySeriesDto(null, null, false, false, null, 3, 2023, 2025);
	}

	private static List<Label> labels(Component card) {
		List<Label> labels = new ArrayList<>();
		((VerticalLayout) card).forEach(c -> labels.add((Label) c));
		return labels;
	}
}
