package de.symeda.sormas.backend.util;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;

import javax.interceptor.InvocationContext;

import org.junit.Test;

/**
 * @see PerformanceLoggingInterceptor
 *
 */
public class PerformanceLoggingInterceptorTest {

	@Test
	public void testGetInvokedMethod() {

		InvocationContext context = mock(InvocationContext.class);
		Method method = mock(Method.class);
		//when(context.getMethod()).thenReturn(method);
		/*
		 * when(context.getTarget()).thenReturn("de.symeda.sormas.backend.region.CommunityService@15301bba");
		 * when(context.getMethod().getName()).thenReturn("count");
		 * assertThat(PerformanceLoggingInterceptor.getInvokedMethod(context), equalTo("CommunityService.count"));
		 * when(context.getTarget()).thenReturn("de.symeda.sormas.backend.facility.FacilityFacadeEjb$FacilityFacadeEjbLocal@630e5556");
		 * assertThat(PerformanceLoggingInterceptor.getInvokedMethod(context), equalTo("FacilityFacadeEjb.count"));
		 */
	}
}
