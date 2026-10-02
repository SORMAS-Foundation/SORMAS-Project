/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.api.epipulse.referencevalue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectionSite;

public class EpipulseSiteOfInfectionRefTest {

	/** Not a site: EpiPulse has no value for "we did not establish where the infection was". */
	private static final Set<SyphilisInfectionSite> NOT_A_SITE = EnumSet.of(SyphilisInfectionSite.UNKNOWN);

	@Test
	@DisplayName("every constant is accounted for: a site maps, anything else does not")
	public void everySiteIsAccountedFor() {

		for (SyphilisInfectionSite site : SyphilisInfectionSite.values()) {
			if (NOT_A_SITE.contains(site)) {
				assertNull(EpipulseSiteOfInfectionRef.codeFor(site), site.name() + " must not be reported as a site of infection");
				continue;
			}
			assertNotNull(
				EpipulseSiteOfInfectionRef.codeFor(site),
				site.name() + " is not accounted for. Map it in EpipulseSiteOfInfectionRef if EpiPulse names it, "
					+ "or add it to NOT_A_SITE here if it is not a site.");
		}
	}

	@Test
	@DisplayName("each site carries the code EpiPulse defines for it")
	public void eachSiteCarriesItsCode() {

		assertEquals("AR", EpipulseSiteOfInfectionRef.codeFor(SyphilisInfectionSite.ANORECTAL));
		assertEquals("GEN", EpipulseSiteOfInfectionRef.codeFor(SyphilisInfectionSite.GENITAL));
		assertEquals("PH", EpipulseSiteOfInfectionRef.codeFor(SyphilisInfectionSite.PHARYNGEAL));
		assertEquals("OTH", EpipulseSiteOfInfectionRef.codeFor(SyphilisInfectionSite.OTHER));
	}

	@Test
	@DisplayName("nothing recorded reports nothing")
	public void nothingRecordedReportsNothing() {
		assertNull(EpipulseSiteOfInfectionRef.codeFor(null));
	}
}
