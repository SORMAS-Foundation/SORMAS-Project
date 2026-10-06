package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Match {

	private final String identity;
	private final Map<String, Object> targets;
	private final List<Date> versions;
	private final String errorColumn;
	private final String errorMessage;

	private Match(String identity, Map<String, Object> targets, List<Date> versions, String errorColumn, String errorMessage) {
		this.identity = identity;
		this.targets = targets;
		this.versions = versions;
		this.errorColumn = errorColumn;
		this.errorMessage = errorMessage;
	}

	public static Match create() {
		return new Match(null, null, new ArrayList<>(), null, null);
	}

	public static Match existing(String identity, Map<String, Object> targets, List<Date> versions) {
		return new Match(identity, new HashMap<>(targets), new ArrayList<>(versions), null, null);
	}

	public static Match error(String column, String message) {
		return new Match(null, null, new ArrayList<>(), column, message);
	}

	public boolean isExisting() {
		return targets != null;
	}

	public boolean isError() {
		return errorMessage != null;
	}

	public String identity() {
		return identity;
	}

	public Map<String, Object> targets() {
		return targets;
	}

	public List<Date> versions() {
		return versions;
	}

	public String errorColumn() {
		return errorColumn;
	}

	public String errorMessage() {
		return errorMessage;
	}
}
