package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.importexport.hierarchical.MatchReason;

public final class Match {

	private final String identity;
	private final Map<String, Object> targets;
	private final List<Date> versions;
	private final String errorColumn;
	private final String errorMessage;
	private final MatchReason reason;
	private final boolean createWith;

	private Match(
		String identity,
		Map<String, Object> targets,
		List<Date> versions,
		String errorColumn,
		String errorMessage,
		MatchReason reason,
		boolean createWith) {
		this.identity = identity;
		this.reason = reason;
		this.createWith = createWith;
		this.targets = targets;
		this.versions = versions;
		this.errorColumn = errorColumn;
		this.errorMessage = errorMessage;
	}

	public static Match create() {
		return new Match(null, null, new ArrayList<>(), null, null, MatchReason.NEW, false);
	}

	public static Match existing(String identity, Map<String, Object> targets, List<Date> versions) {
		return existing(identity, targets, versions, MatchReason.BY_ID);
	}

	public static Match existing(String identity, Map<String, Object> targets, List<Date> versions, MatchReason reason) {
		return new Match(identity, new HashMap<>(targets), new ArrayList<>(versions), null, null, reason, false);
	}

	public static Match createWith(Map<String, Object> targets, MatchReason reason) {
		return new Match(null, new HashMap<>(targets), new ArrayList<>(), null, null, reason, true);
	}

	public static Match error(String column, String message) {
		return new Match(null, null, new ArrayList<>(), column, message, null, false);
	}

	public boolean isExisting() {
		return targets != null && !createWith;
	}

	public boolean isCreateWith() {
		return createWith;
	}

	public MatchReason reason() {
		return reason;
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
