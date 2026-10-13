package de.symeda.sormas.backend.news;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Lenient deserializer for the date/time strings EIOS returns. EIOS is not consistent: seconds, the fractional part and
 * the zone offset are all optional in practice, and some articles carry an empty string or a date without a time at all.
 * A single unparsable value must never abort the deserialization of a whole page of articles, so anything we cannot make
 * sense of is logged and mapped to {@code null}.
 */
public class CustomZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomZonedDateTimeDeserializer.class);

	/**
	 * yyyy-MM-ddTHH:mm[:ss][.fraction][offset] - the offset defaults to UTC when EIOS omits it.
	 */
	private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm")
		.optionalStart()
		.appendPattern(":ss")
		.optionalEnd()
		.optionalStart()
		.appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
		.optionalEnd()
		.optionalStart()
		.appendOffsetId()
		.optionalEnd()
		.toFormatter();

	@Override
	public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
			return Instant.ofEpochMilli(p.getLongValue()).atZone(ZoneOffset.UTC);
		}

		String text = p.getText();
		if (text == null || text.trim().isEmpty()) {
			return null;
		}
		return parse(text.trim());
	}

	private static ZonedDateTime parse(String value) {
		try {
			return FORMATTER.parse(value, CustomZonedDateTimeDeserializer::fromTemporal);
		} catch (Exception e) {
			// fall through to the date-only attempt
		}
		try {
			return LocalDate.parse(value).atStartOfDay(ZoneOffset.UTC);
		} catch (Exception e) {
			LOGGER.warn("Unable to parse EIOS date/time value '{}'; treating it as not set", value);
			return null;
		}
	}

	private static ZonedDateTime fromTemporal(java.time.temporal.TemporalAccessor temporal) {
		return temporal.isSupported(ChronoField.OFFSET_SECONDS) ? ZonedDateTime.from(temporal) : LocalDateTime.from(temporal).atZone(ZoneOffset.UTC);
	}
}
