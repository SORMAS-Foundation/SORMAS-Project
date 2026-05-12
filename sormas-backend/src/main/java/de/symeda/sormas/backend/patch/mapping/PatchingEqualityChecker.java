package de.symeda.sormas.backend.patch.mapping;

import de.symeda.sormas.api.utils.OrderedRegisterable;

/**
 * Contract to specify how two values of a supported type should be compared for equality.
 */
public interface PatchingEqualityChecker extends OrderedRegisterable<PatchingEqualityChecker> {

	/**
	 * @param a
	 *            first value — guaranteed non-null by the registry
	 * @param b
	 *            second value — guaranteed non-null by the registry
	 * @return true if the two values are considered equal for patch purposes
	 */
	boolean areEqual(Object a, Object b);
}
