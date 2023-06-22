package de.symeda.sormas.api;

import java.util.List;

import de.symeda.sormas.api.common.DeletionDetails;

// TODO: Refactor to DeletableRestorableFacade
public interface DeletableFacade {

	void delete(String uuid, DeletionDetails deletionDetails);

	void delete(List<String> uuids, DeletionDetails deletionDetails);

	void restore(String uuid);

	boolean isDeleted(String uuid);
}
