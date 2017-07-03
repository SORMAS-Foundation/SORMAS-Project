package de.symeda.auditlog.api.value.format;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.Month;

import org.junit.Test;

import de.symeda.auditlog.api.sample.SimpleBooleanFlagEntity;
import de.symeda.auditlog.api.value.SimpleValueContainer;

public class DefaultValueFormatterTest {

	@Test
	public void shouldFormatEnums() {

		DefaultValueFormatter cut = new DefaultValueFormatter();
		assertThat(cut.format(Month.MAY), is(equalTo("MAY")));

	}

	@Test
	public void shouldFormatHasUuid() {

		DefaultValueFormatter cut = new DefaultValueFormatter();

		final String theUuid = "the-uuid";
		SimpleBooleanFlagEntity e = new SimpleBooleanFlagEntity(theUuid, false);

		assertThat(cut.format(e), is(equalTo(theUuid)));
	}

	@Test
	public void shouldFormatObjects() {

		DefaultValueFormatter cut = new DefaultValueFormatter();
		assertThat(cut.format(Boolean.FALSE), is(equalTo("false")));
	}

	@Test
	public void shouldFormatNull() {

		DefaultValueFormatter cut = new DefaultValueFormatter();
		assertThat(cut.format(null), is(equalTo(SimpleValueContainer.DEFAULT_NULL_STRING)));
	}

}
