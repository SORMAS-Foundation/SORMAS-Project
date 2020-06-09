package de.symeda.sormas.ui.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

import java.util.Locale;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class StringToAngularLocationConverterTest {

	@Test
	public void testConvertToModel() {

		StringToAngularLocationConverter c = new StringToAngularLocationConverter();

		assertThat(c.convertToModel(null, Double.class, Locale.GERMANY), is((Double) null));
		assertThat(c.convertToModel(null, Double.class, Locale.ENGLISH), is((Double) null));

		//@formatter:off
		Stream.of(
			Pair.of("1,21", Locale.GERMANY),
			Pair.of("1,21", Locale.ENGLISH),
			Pair.of("1.21", Locale.GERMANY),
			Pair.of("1.21", Locale.ENGLISH)
		)
		.map(p -> c.convertToModel(p.getLeft(), Double.class, p.getRight()))
		.forEach(v -> assertThat(v, closeTo(1.21, 0.00001)));
		//@formatter:on
	}

	@Test
	public void testConvertToPresentation() {

		StringToAngularLocationConverter c = new StringToAngularLocationConverter();

		assertThat(c.convertToPresentation(null, String.class, Locale.GERMANY), is((String) null));
		assertThat(c.convertToPresentation(null, String.class, Locale.ENGLISH), is((String) null));

		assertThat(c.convertToPresentation(1.21D, String.class, Locale.GERMANY), is("1,21"));
		assertThat(c.convertToPresentation(1.21D, String.class, Locale.ENGLISH), is("1.21"));
	}
}
