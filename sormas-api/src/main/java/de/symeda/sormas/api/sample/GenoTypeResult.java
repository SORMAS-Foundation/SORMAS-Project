/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum GenoTypeResult {

    GENOTYPE_A,
    GENOTYPE_B,
    GENOTYPE_B2,
    GENOTYPE_B3,
    GENOTYPE_C1,
    GENOTYPE_C2,
    GENOTYPE_D1,
    GENOTYPE_D10,
    GENOTYPE_D11,
    GENOTYPE_D2,
    GENOTYPE_D3,
    GENOTYPE_D4,
    GENOTYPE_D5,
    GENOTYPE_D6,
    GENOTYPE_D7,
    GENOTYPE_D8,
    GENOTYPE_D9,
    GENOTYPE_E,
    GENOTYPE_F,
    GENOTYPE_G1,
    GENOTYPE_G2,
    GENOTYPE_G3,
    GENOTYPE_H1,
    GENOTYPE_H2,
    OTHER,
    UNKNOWN;

    @Override
    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
