package de.symeda.sormas.api;

public interface PermanentlyDeletableFacade<F extends PermanentlyDeletableFacade> {

	void delete(String uuid);
}
