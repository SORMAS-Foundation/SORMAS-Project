package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;
import de.symeda.sormas.api.referencedata.ReferenceDataValueInstanceProvider;
import de.symeda.sormas.backend.patch.DataPatchingException;

@ApplicationScoped
public class ReferenceDtoPatchMapper implements ValuePatchMapper {

	private final static Logger logger = LoggerFactory.getLogger(ReferenceDtoPatchMapper.class);

	@Inject
	private ReferenceDataValueInstanceProvider referenceDataValueInstanceProvider;

	@Override
	public <T> ValueMappingResult<T> map(ValuePatchRequest<T> request) {
		Object value = request.getValue();
		Class<?> targetType = request.getTargetType();
		String captionCandidate = value.toString();

		if (!ReferenceDto.class.isAssignableFrom(targetType)) {
			throw new IllegalArgumentException(String.format("[%s] is not assignable from [%s].", targetType, targetType.getName()));
		}

		Class<? extends ReferenceDto> referenceType = targetType.asSubclass(ReferenceDto.class);

		return ((Optional<T>) findByCaptionMatch(captionCandidate, referenceType)).map(ValueMappingResult::withData).orElseGet(() -> {
			logger.info("Could not match value: [{}] to referenceType: [{}]", captionCandidate, referenceType);
			return ValueMappingResult.withCause(DataPatchFailureCause.NOT_PRESENT_IN_REFERENCE_DATA_LIST);
		});
	}

	private <T extends ReferenceDto> Optional<T> findByCaptionMatch(String captionCandidate, Class<T> referenceType) {
		try {
			return referenceDataValueInstanceProvider.getOne(captionCandidate, referenceType);
		} catch (IllegalStateException e) {
			logger.error("Configuration issue for referenceType: [{}] for captionCandidate: [{}]", referenceType, captionCandidate);
			throw new DataPatchingException(
				String.format("ReferenceType [%s] not supported", referenceType),
				DataPatchFailureCause.UNSUPPORTED_REFERENCE_DATA);
		}
	}

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return Set.of(ReferenceDto.class);
	}

	@Override
	public int getOrder() {
		return LOW_PRECEDENCE - (ORDER_CHUNK * 2);
	}
}
