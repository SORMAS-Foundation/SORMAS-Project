package de.symeda.sormas.api;

import java.util.List;

import de.symeda.sormas.api.common.progress.ProcessedEntity;

public interface PermanentlyDeletableFacade {

	void delete(String uuid);

	List<ProcessedEntity> delete(List<String> uuids);
}
