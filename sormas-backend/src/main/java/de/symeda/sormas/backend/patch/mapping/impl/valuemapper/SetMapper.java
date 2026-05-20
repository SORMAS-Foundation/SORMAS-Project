package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;

@ApplicationScoped
public class SetMapper implements ValuePatchMapper {

	@Override
	public <T> ValueMappingResult<T> map(ValuePatchRequest<T> request) {
		return null;
	}

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return Set.of(Set.class);
	}

	public static void main(String[] args) {
		Set<String> fewoij = Set.of("fewoij");

	}
}
