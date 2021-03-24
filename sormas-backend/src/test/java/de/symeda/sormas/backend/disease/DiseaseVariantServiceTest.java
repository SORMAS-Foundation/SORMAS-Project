/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.backend.disease;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.backend.AbstractBeanTest;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author Alex Vidrean
 * @since 18-Mar-21
 */
public class DiseaseVariantServiceTest extends AbstractBeanTest {

    @Test
    public void testGetByName() {
        creator.createDiseaseVariant("B.1.1.7", Disease.CORONAVIRUS);
        creator.createDiseaseVariant("B.1.1.28.1-P.1", Disease.CORONAVIRUS);

        assertThat(getDiseaseVariantService().getByName("B.1.1.7", Disease.CORONAVIRUS), hasSize(1));
        assertThat(getDiseaseVariantService().getByName("B.1.1.7", Disease.ANTHRAX), hasSize(0));
        assertThat(getDiseaseVariantService().getByName(" b.1.1.28.1-P.1 ", Disease.CORONAVIRUS), hasSize(1));

    }

}
