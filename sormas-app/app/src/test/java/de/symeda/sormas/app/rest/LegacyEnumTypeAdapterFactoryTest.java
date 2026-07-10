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

package de.symeda.sormas.app.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Set;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.app.backend.sample.Sample;

public class LegacyEnumTypeAdapterFactoryTest {

	private final Gson gson = new GsonBuilder().registerTypeAdapterFactory(new LegacyEnumTypeAdapterFactory()).create();

	private static class Holder {

		private PathogenTestType testType;
		private Set<PathogenTestType> requestedPathogenTests;
	}

	@Test
	public void translatesRetiredNamesTheServerMayStillSend() {
		assertThat(gson.fromJson("\"RDT\"", PathogenTestType.class), is(PathogenTestType.LATERAL_FLOW_ASSAY));
		assertThat(gson.fromJson("\"ANTIGEN_DETECTION\"", PathogenTestType.class), is(PathogenTestType.LATERAL_FLOW_ASSAY));
		assertThat(gson.fromJson("\"DIRECT_MICROSCOPY\"", PathogenTestType.class), is(PathogenTestType.OTHER));
		assertThat(gson.fromJson("\"PCR_RT_PCR\"", PathogenTestType.class), is(PathogenTestType.PCR_RT_PCR));
	}

	@Test
	public void anUnknownNameDegradesToNullRatherThanAbortingTheSyncBatch() {
		// a server one version ahead sends a constant this app does not have; Gson's built-in adapter yields null
		assertThat(gson.fromJson("\"FROM_THE_FUTURE\"", PathogenTestType.class), is(nullValue()));
		assertThat(gson.fromJson("\"\"", PathogenTestType.class), is(nullValue()));
		assertThat(gson.fromJson("null", PathogenTestType.class), is(nullValue()));

		Holder holder = gson.fromJson("{\"testType\":\"FROM_THE_FUTURE\",\"requestedPathogenTests\":[\"PCR_RT_PCR\"]}", Holder.class);
		assertThat(holder.testType, is(nullValue()));
		assertThat(holder.requestedPathogenTests, contains(PathogenTestType.PCR_RT_PCR));
	}

	@Test
	public void serializationStillWritesTheCurrentConstantName() {
		assertThat(gson.toJson(PathogenTestType.LATERAL_FLOW_ASSAY), is("\"LATERAL_FLOW_ASSAY\""));
	}

	@Test
	public void retiredNamesAreTranslatedInsideACollectionField() {
		Holder holder = gson.fromJson("{\"requestedPathogenTests\":[\"RDT\",\"ANTIGEN_DETECTION\"]}", Holder.class);
		assertThat(holder.requestedPathogenTests, contains(PathogenTestType.LATERAL_FLOW_ASSAY));
	}

	@Test
	public void anUnknownNameInsideACollectionDoesNotReachTheEntity() {
		// Gson adds the degraded null as a Set element; Sample.setRequestedPathogenTests must not call name() on it
		Holder holder = gson.fromJson("{\"requestedPathogenTests\":[\"PCR_RT_PCR\",\"FROM_THE_FUTURE\"]}", Holder.class);
		assertThat(holder.requestedPathogenTests, hasItem(nullValue()));

		Sample sample = new Sample();
		sample.setRequestedPathogenTests(holder.requestedPathogenTests);
		assertThat(sample.getRequestedPathogenTestsString(), is("PCR_RT_PCR,"));
	}
}
