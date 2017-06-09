package de.symeda.auditlog.api.value.format;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.symeda.auditlog.api.sample.CustomEnum;
import de.symeda.auditlog.api.value.ValueContainer;

/**
 * @see EnumFormatter
 * @author Stefan Kock
 */
public class EnumFormatterTest {

	@Test
	public void testFormat() {

		EnumFormatter formatter = new EnumFormatter();

		assertThat(formatter.format(null), equalTo(ValueContainer.DEFAULT_NULL_STRING));
		assertThat(formatter.format(CustomEnum.VALUE_1), equalTo("VALUE_1"));
	}
}
