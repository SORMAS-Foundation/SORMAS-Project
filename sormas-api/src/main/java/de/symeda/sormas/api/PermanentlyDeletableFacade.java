package de.symeda.sormas.api;

import java.util.List;

public interface PermanentlyDeletableFacade {

	void delete(String uuid);

	void delete(List<String> uuids);
}
