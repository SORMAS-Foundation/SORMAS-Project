package de.symeda.sormas.api;

import de.symeda.sormas.api.common.DeletionDetails;

// TODO: Refactor to DeletableRestorableFacade
public interface DeletableFacade {

	void delete(String uuid, DeletionDetails deletionDetails);

	void restore(String uuid);

	boolean isDeleted(String uuid);
}
