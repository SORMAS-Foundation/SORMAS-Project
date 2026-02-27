package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;

@ApplicationScoped
public class DatePatchMapper implements ValuePatchMapper {

	private static final Logger logger = LoggerFactory.getLogger(DatePatchMapper.class);

	private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(Date.class);

	private static final List<String> DATE_FORMATS = Arrays.asList(
		"yyyy-MM-dd",                // 2025-12-17
		"yyyy/MM/dd",                // 2025/12/17
		"yyyy-MM-dd'T'HH:mm:ssZ",    // 2025-12-17T14:30:00+0100
		"yyyy-MM-dd'T'HH:mm:ssXXX",  // 2025-12-17T14:30:00+01:00
		"yyyy-MM-dd'T'HH:mm:ss",     // 2025-12-17T14:30:00
		"yyyy-MM-dd'T'HH:mm",        // 2025-12-17T14:30
		"yyyy/MM/dd'T'HH:mm:ss",     // 2025/12/17T14:30:00
		"yyyy/MM/dd'T'HH:mm:ssXXX"   // 2025/12/17T14:30:00+01:00
	);

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> ValueMappingResult<T> map(ValuePatchRequest<T> request) {
		Object value = request.getValue();
		if (value == null) {
			return ValueMappingResult.withData(null);
		}

		String str = value.toString().trim();

		for (String format : DATE_FORMATS) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				sdf.setLenient(false);
				Date parsed = sdf.parse(str);
				return ValueMappingResult.withData((T) parsed);
			} catch (ParseException e) {
				// try next format
			}
		}

		logger.info("DatePatchMapper: cannot parse date value [{}], expected one of formats: [{}]", str, DATE_FORMATS);

		return ValueMappingResult.withCause(DataPatchFailureCause.INVALID_VALUE_TYPE);
	}
}
