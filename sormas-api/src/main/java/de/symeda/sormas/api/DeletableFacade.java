package de.symeda.sormas.api;

import java.util.List;

import de.symeda.sormas.api.common.DeletionDetails;

public interface DeletableFacade {

	void delete(String uuid, DeletionDetails deletionDetails);

	void delete(List<String> uuids, DeletionDetails deletionDetails);

	void restore(String uuid);

	void restore(List<String> uuids);

	boolean isDeleted(String uuid);
}
