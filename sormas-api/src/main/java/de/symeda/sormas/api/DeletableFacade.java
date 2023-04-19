package de.symeda.sormas.api;

import de.symeda.sormas.api.common.DeletionDetails;

public interface DeletableFacade {

	void delete(String uuid, DeletionDetails deletionDetails);

	void undelete(String uuid);

	boolean isDeleted(String uuid);
}
