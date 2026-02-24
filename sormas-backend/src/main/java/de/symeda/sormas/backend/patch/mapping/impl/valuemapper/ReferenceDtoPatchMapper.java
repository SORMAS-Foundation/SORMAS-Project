package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;
import de.symeda.sormas.api.referencedata.ReferenceDataValueInstanceProvider;

@ApplicationScoped
public class ReferenceDtoPatchMapper implements ValuePatchMapper {

	@Inject
	private ReferenceDataValueInstanceProvider referenceDataValueInstanceProvider;

	@Override
	public <T> T map(Object value, Class<T> targetType, Set<String> inputLanguageCodes) {
		String captionCandidate = value.toString();

		if (!targetType.isAssignableFrom(ReferenceDto.class)) {
			throw new IllegalArgumentException(String.format("[%s] is not assignable from [%s].", value, targetType.getName()));
		}

		Class<? extends ReferenceDto> referenceType = targetType.asSubclass(ReferenceDto.class);

		// TODO: could be nice to produce the error directly here
		return (T) referenceDataValueInstanceProvider.getOne(captionCandidate, referenceType)
			.orElseThrow(
				() -> new IllegalStateException(
					String.format("Could not match value: [%s] to referenceType: [%s]", captionCandidate, referenceType)));
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
