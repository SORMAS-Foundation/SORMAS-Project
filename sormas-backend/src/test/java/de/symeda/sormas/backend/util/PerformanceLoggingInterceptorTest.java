package de.symeda.sormas.backend.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

/**
 * @see PerformanceLoggingInterceptor
 *
 */
public class PerformanceLoggingInterceptorTest {

	/**
	 * Only tests the String inspection but avoids mocking due to massive performance impacts on other unit tests in the same test run.
	 */
	@Test
	public void testGetInvokedMethod() {

		assertThat(
			PerformanceLoggingInterceptor.getInvokedMethod("de.symeda.sormas.backend.region.CommunityService@15301bba", "count"),
			equalTo("CommunityService.count"));

		assertThat(
			PerformanceLoggingInterceptor
				.getInvokedMethod("de.symeda.sormas.backend.facility.FacilityFacadeEjb$FacilityFacadeEjbLocal@630e5556", "count"),
			equalTo("FacilityFacadeEjb.count"));
	}
}
