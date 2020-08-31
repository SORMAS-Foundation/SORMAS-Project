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

package de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers;

import de.symeda.sormas.api.utils.pseudonymization.ValuePseudonymizer;

public class LatitudePseudonymizer extends ValuePseudonymizer<Double> {

	public static double RANDOMIZATION_DISTANCE_RAD = 5000 / 111300D; //5000 meters / about 111300 meters in one degree

	@Override
	protected Double pseudonymizeValue(Double value) {
		return randomizeLatitude(value, RANDOMIZATION_DISTANCE_RAD);
	}

	private Double randomizeLatitude(double latitude, double distanceRad) {
		double v = Math.random();
		double u = Math.random();

		double t = 2 * Math.PI * v;
		double w = distanceRad * Math.sqrt(u);
		double y = w * Math.sin(t);

		return latitude + y;
	}
}
