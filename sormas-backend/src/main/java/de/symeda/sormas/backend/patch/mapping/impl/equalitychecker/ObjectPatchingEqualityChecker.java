package de.symeda.sormas.backend.patch.mapping.impl.equalitychecker;

import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.backend.patch.mapping.PatchingEqualityChecker;

@ApplicationScoped
public class ObjectPatchingEqualityChecker implements PatchingEqualityChecker {

	public static final Set<Class<?>> SUPPORTED_TYPES = Set.of(Object.class);

	@Override
	public boolean areEqual(Object a, Object b) {
		return Objects.equals(a, b);
	}

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}
}
