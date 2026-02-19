package de.symeda.sormas.patch.mapping;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import de.symeda.sormas.api.patch.mapping.FieldCustomMapper;

@ApplicationScoped
public class FieldCustomMapperRegistry {

	@Inject
	private Instance<FieldCustomMapper> instances;

	// TODO: change signature.
	public <T> T map(Object value, Class<T> targetType) {
		throw new UnsupportedOperationException();
	}
}
