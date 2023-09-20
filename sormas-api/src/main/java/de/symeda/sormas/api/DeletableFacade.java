package de.symeda.sormas.api;

import java.util.List;

import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;

public interface DeletableFacade {

	void delete(String uuid, DeletionDetails deletionDetails);

	List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails);

	void restore(String uuid);

	List<ProcessedEntity> restore(List<String> uuids);

	boolean isDeleted(String uuid);
}
