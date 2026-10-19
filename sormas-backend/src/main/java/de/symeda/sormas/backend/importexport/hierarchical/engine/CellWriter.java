package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.Map;

public interface CellWriter {

	void write(Map<String, Object> targets, String target, String[] propertyPath, String header, String value) throws CellWriteException;
}
