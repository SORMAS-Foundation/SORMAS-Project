package de.symeda.auditlog.api.value.reflection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EntityInspectorTest {

	@Test
	public void testBuildFieldName() throws Exception {

		String fieldName = EntityInspector.buildFieldName("getTheAttribute");
		assertThat(fieldName, is(equalTo("theAttribute")));

		fieldName = EntityInspector.buildFieldName("isValidBla");
		assertThat(fieldName, is(equalTo("validBla")));

		fieldName = EntityInspector.buildFieldName("someStrangeNameRemainsTheSame");
		assertThat(fieldName, is(equalTo("someStrangeNameRemainsTheSame")));
	}

}
