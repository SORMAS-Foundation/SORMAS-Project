package de.symeda.sormas.patch.mapping.impl.valuemapper;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.patch.mapping.ValueMapper;
import de.symeda.sormas.api.referencedata.ReferenceDataValueInstanceProvider;

@ApplicationScoped
public class ReferenceDtoMapper implements ValueMapper {

	@Inject
	private ReferenceDataValueInstanceProvider referenceDataValueInstanceProvider;

	@Override
	public <T> T map(Object value, Class<T> targetType) {
		String captionCandidate = value.toString();

		return null;
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
