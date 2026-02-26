package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;

@ApplicationScoped
public class DatePatchMapper implements ValuePatchMapper {

	private final static Logger logger = LoggerFactory.getLogger(DatePatchMapper.class);

	private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(Date.class);

	private static final String DATE_FORMAT = "yyyy-MM-dd";

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> ValueMappingResult<T> map(Object value, Class<T> targetType, Set<String> inputLanguageCodes) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			sdf.setLenient(false);
			return ValueMappingResult.withData((T) sdf.parse(value.toString()));
		} catch (ParseException e) {
			logger.info("DateMapper: cannot parse date value [{}], expected format: [{}]", value, DATE_FORMAT, e);

			return ValueMappingResult.withCause(DataPatchFailureCause.INVALID_VALUE_TYPE);
		}
	}

}
