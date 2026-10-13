package de.symeda.sormas.backend.news;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

class CustomZonedDateTimeDeserializerTest {

	private final ObjectMapper mapper =
		new ObjectMapper().registerModule(new SimpleModule().addDeserializer(ZonedDateTime.class, new CustomZonedDateTimeDeserializer()));

	@Test
	void parsesEiosDateTimeVariants() throws Exception {
		Assertions.assertEquals(ZonedDateTime.of(2024, 5, 9, 1, 30, 14, 614455900, ZoneOffset.UTC), parse("\"2024-05-09T01:30:14.6144559Z\""));
		Assertions.assertEquals(ZonedDateTime.of(2024, 5, 9, 1, 30, 14, 0, ZoneOffset.ofHours(2)), parse("\"2024-05-09T01:30:14+02:00\""));
		Assertions.assertEquals(ZonedDateTime.of(2024, 5, 9, 1, 30, 0, 0, ZoneOffset.UTC), parse("\"2024-05-09T01:30\""));
		Assertions.assertEquals(ZonedDateTime.of(2024, 5, 9, 0, 0, 0, 0, ZoneOffset.UTC), parse("\"2024-05-09\""));
		Assertions.assertEquals(ZonedDateTime.of(2024, 5, 9, 1, 30, 14, 0, ZoneOffset.UTC), parse("1715218214000"));
	}

	@Test
	void mapsUnusableValuesToNull() throws Exception {
		Assertions.assertNull(parse("\"\""));
		Assertions.assertNull(parse("\"   \""));
		Assertions.assertNull(parse("\"not a date\""));
		Assertions.assertNull(parse("null"));
	}

	private ZonedDateTime parse(String json) throws Exception {
		return mapper.readValue(json, ZonedDateTime.class);
	}
}
