package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;

@ApplicationScoped
public class DatePatchMapper implements ValuePatchMapper {

	private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(Date.class);

	private static final String DATE_FORMAT = "yyyy-MM-dd";

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T map(Object value, Class<T> targetType, Set<String> inputLanguageCodes) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			sdf.setLenient(false);
			return (T) sdf.parse(value.toString());
		} catch (ParseException e) {
			throw new IllegalArgumentException("DateMapper: cannot parse date value '" + value + "', expected format: " + DATE_FORMAT, e);
		}
	}

}
