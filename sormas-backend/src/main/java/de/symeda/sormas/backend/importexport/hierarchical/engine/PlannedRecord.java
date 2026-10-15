package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;

import de.symeda.sormas.api.importexport.hierarchical.FieldChangeDto;
import de.symeda.sormas.api.importexport.hierarchical.MatchReason;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;

public final class PlannedRecord {

	private final ImportNode node;
	private final PlannedRecord parent;
	private final Map<String, Object> targets;
	private final Map<String, Object> original;
	private final String identity;
	private final List<Date> versions;
	private final List<FieldChangeDto> changes = new ArrayList<>();
	private final List<PlannedRecord> children = new ArrayList<>();
	private MatchReason reason;
	private Map<String, Object> baseline;
	private ImportNode linkedRoot;

	private PlannedRecord(
		ImportNode node,
		PlannedRecord parent,
		Map<String, Object> targets,
		Map<String, Object> original,
		String identity,
		List<Date> versions) {
		this.node = node;
		this.parent = parent;
		this.targets = targets;
		this.original = original;
		this.identity = identity;
		this.versions = versions;
	}

	static PlannedRecord created(ImportNode node, PlannedRecord parent, Map<String, Object> targets) {
		return new PlannedRecord(node, parent, new HashMap<>(targets), null, null, new ArrayList<>());
	}

	static PlannedRecord existing(ImportNode node, PlannedRecord parent, Match match) {
		HashMap<String, Object> original = new HashMap<>(match.targets());
		return new PlannedRecord(node, parent, SerializationUtils.clone(original), original, match.identity(), match.versions())
			.withReason(match.reason());
	}

	static PlannedRecord createdWith(ImportNode node, PlannedRecord parent, Match match) {
		PlannedRecord record =
			new PlannedRecord(node, parent, new HashMap<>(match.targets()), null, match.identity(), match.versions()).withReason(match.reason());
		record.baseline = SerializationUtils.clone(new HashMap<>(match.storedTargets()));
		record.linkedRoot = match.linkedRoot();
		return record;
	}

	public Map<String, Object> baseline() {
		if (original != null) {
			return original;
		}
		return baseline == null ? Map.of() : baseline;
	}

	PlannedRecord withReason(MatchReason reason) {
		this.reason = reason;
		return this;
	}

	public ImportNode linkedRoot() {
		return linkedRoot;
	}

	public MatchReason reason() {
		return reason;
	}

	public ImportNode node() {
		return node;
	}

	public PlannedRecord parent() {
		return parent;
	}

	public Map<String, Object> targets() {
		return targets;
	}

	@SuppressWarnings("unchecked")
	public <T> T target(String name) {
		return (T) targets.get(name);
	}

	public Map<String, Object> original() {
		return original;
	}

	@SuppressWarnings("unchecked")
	public <T> T originalTarget(String name) {
		return original == null ? null : (T) original.get(name);
	}

	public boolean isNew() {
		return original == null;
	}

	public String identity() {
		return identity;
	}

	public List<Date> versions() {
		return versions;
	}

	public List<FieldChangeDto> changes() {
		return changes;
	}

	public void addChange(FieldChangeDto change) {
		changes.add(change);
	}

	public List<PlannedRecord> children() {
		return children;
	}

	public List<PlannedRecord> children(ImportLevel level) {
		return children.stream().filter(child -> child.node.level() == level).collect(Collectors.toList());
	}

	public void reportIssue(String column, String message) {
		node.reportIssue(column, message);
	}

	public OperationType operation() {
		if (isNew()) {
			return OperationType.CREATE;
		}
		return changes.isEmpty() ? OperationType.UNCHANGED : OperationType.UPDATE;
	}

	public Stream<PlannedRecord> subtree() {
		return Stream.concat(Stream.of(this), children.stream().flatMap(PlannedRecord::subtree));
	}
}
