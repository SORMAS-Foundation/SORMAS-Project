package de.symeda.sormas.api;

public interface HasUuid {

	/**
	 * Returns an identification possibility for this entity type so that objects of this type can be uniquely differentiated from each other.
	 */
	String getUuid();

}
