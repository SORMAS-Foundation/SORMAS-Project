/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.externalmessage;

import java.util.Locale;
import java.util.Map;

import de.symeda.sormas.api.sample.GenoType;

public class RubeCodeMapper {

	private static final Map<String, GenoType> GENOTYPE_CODES = Map.ofEntries(
		Map.entry("1A", GenoType.GENOTYPE_1A),
		Map.entry("1B", GenoType.GENOTYPE_1B),
		Map.entry("1C", GenoType.GENOTYPE_1C),
		Map.entry("1D", GenoType.GENOTYPE_1D),
		Map.entry("1E", GenoType.GENOTYPE_1E),
		Map.entry("1F", GenoType.GENOTYPE_1F),
		Map.entry("1G", GenoType.GENOTYPE_1G),
		Map.entry("1H", GenoType.GENOTYPE_1H),
		Map.entry("1I", GenoType.GENOTYPE_1I),
		Map.entry("1J", GenoType.GENOTYPE_1J),
		Map.entry("2A", GenoType.GENOTYPE_2A),
		Map.entry("2B", GenoType.GENOTYPE_2B),
		Map.entry("2C", GenoType.GENOTYPE_2C),
		Map.entry("NA", GenoType.GENOTYPE_NA),
		Map.entry("UNK", GenoType.GENOTYPE_UNK));

	public static GenoType mapGenotype(String rawCode) {
		if (rawCode == null) {
			return null;
		}
		return GENOTYPE_CODES.get(rawCode.trim().toUpperCase(Locale.ROOT));
	}
}
