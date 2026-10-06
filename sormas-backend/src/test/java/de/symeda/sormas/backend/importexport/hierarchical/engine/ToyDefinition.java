package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;

import de.symeda.sormas.api.importexport.hierarchical.FieldChangeDto;

public final class ToyDefinition implements ImportDefinition {

	public static final ImportLevel PARENT = new ImportLevel("parent", ImportLevel.Kind.ENTITY, "parent");
	public static final ImportLevel CHILD = new ImportLevel("child", ImportLevel.Kind.ENTITY, "child");
	public static final ImportLevel NOTE = new ImportLevel("note", ImportLevel.Kind.EMBEDDED, "note");
	public static final String HEADER = "| parent.key | parent.name | child.code | child.amount | note.text |";

	public final Map<String, ToyParent> parents = new HashMap<>();
	public final Map<String, Date> parentVersions = new HashMap<>();
	public final Map<String, ToyChild> children = new HashMap<>();
	public final Map<String, Integer> lookups = new HashMap<>();
	public final List<Integer> committed = new ArrayList<>();
	private final int maxRows;

	public ToyDefinition() {
		this(100);
	}

	public ToyDefinition(int maxRows) {
		this.maxRows = maxRows;
	}

	public ToyDefinition withParent(String key, String name, Date version) {
		ToyParent parent = new ToyParent();
		parent.setKey(key);
		parent.setName(name);
		parents.put(key, parent);
		parentVersions.put(key, version);
		return this;
	}

	public ToyDefinition withChild(String parentKey, String code, Integer amount) {
		ToyChild child = new ToyChild();
		child.setCode(code);
		child.setAmount(amount);
		children.put(parentKey + "/" + code, child);
		return this;
	}

	@Override
	public String id() {
		return "toy";
	}

	@Override
	public ImportSchema schema() {
		return new ImportSchema(PARENT, CHILD, NOTE);
	}

	@Override
	public List<ColumnSpec> columns() {
		return List.of(
			new ColumnSpec("parent.key", "Key"),
			new ColumnSpec("parent.name", "Name", "name"),
			new ColumnSpec("child.code", "Code", "code"),
			new ColumnSpec("child.amount", "Amount", "amount"),
			new ColumnSpec("note.text", "Note"));
	}

	@Override
	public CellWriter cellWriter() {
		return new ToyCellWriter();
	}

	@Override
	public LevelHandler handler(ImportLevel level) {
		if (level == PARENT) {
			return new ParentHandler();
		}
		if (level == CHILD) {
			return new ChildHandler();
		}
		throw new IllegalArgumentException(level.name());
	}

	@Override
	public BlockCommitter committer() {
		return block -> {
			ToyParent parent = block.rootRecord().target("parent");
			if ("explode".equals(parent.getName())) {
				throw new IllegalStateException("exploded");
			}
			committed.add(block.index());
		};
	}

	@Override
	public int maxRows() {
		return maxRows;
	}

	private final class ParentHandler implements LevelHandler {

		@Override
		public Map<String, Object> newTargets(ImportNode node, PlannedRecord parent) {
			ToyParent created = new ToyParent();
			created.setKey(node.cell("parent.key"));
			Map<String, Object> targets = new HashMap<>();
			targets.put("parent", created);
			return targets;
		}

		@Override
		public Optional<String> key(PlannedRecord draft) {
			return Optional.of(draft.node().cell("parent.key")).filter(key -> !key.isEmpty());
		}

		@Override
		public Map<ImportNode, Match> match(List<PlannedRecord> drafts) {
			lookups.merge("parent", 1, Integer::sum);
			Map<ImportNode, Match> matches = new IdentityHashMap<>();
			for (PlannedRecord draft : drafts) {
				String key = draft.node().cell("parent.key");
				if (key.startsWith("!")) {
					matches.put(draft.node(), Match.error("parent.key", "unknown parent " + key));
				} else if (parents.containsKey(key)) {
					matches.put(
						draft.node(),
						Match.existing(
							"parent:" + key,
							Map.of("parent", SerializationUtils.clone(parents.get(key))),
							Collections.singletonList(parentVersions.get(key))));
				}
			}
			return matches;
		}

		@Override
		public String label(PlannedRecord record) {
			ToyParent parent = record.target("parent");
			return parent.getName() != null ? parent.getName() : parent.getKey();
		}

		@Override
		public void complete(PlannedRecord record) {
		}
	}

	private final class ChildHandler implements LevelHandler {

		@Override
		public Map<String, Object> newTargets(ImportNode node, PlannedRecord parent) {
			Map<String, Object> targets = new HashMap<>();
			targets.put("child", new ToyChild());
			return targets;
		}

		@Override
		public Optional<String> key(PlannedRecord draft) {
			return Optional.ofNullable(draft.<ToyChild> target("child").getCode());
		}

		@Override
		public Map<ImportNode, Match> match(List<PlannedRecord> drafts) {
			lookups.merge("child", 1, Integer::sum);
			Map<ImportNode, Match> matches = new IdentityHashMap<>();
			for (PlannedRecord draft : drafts) {
				if (draft.parent().isNew()) {
					continue;
				}
				String id = draft.parent().<ToyParent> target("parent").getKey() + "/" + draft.<ToyChild> target("child").getCode();
				if (children.containsKey(id)) {
					matches.put(draft.node(), Match.existing("child:" + id, Map.of("child", SerializationUtils.clone(children.get(id))), List.of()));
				}
			}
			return matches;
		}

		@Override
		public String label(PlannedRecord record) {
			return record.<ToyChild> target("child").getCode();
		}

		@Override
		public void complete(PlannedRecord record) {
			List<String> notes = record.node().children(NOTE).stream().map(note -> note.cell("note.text")).collect(Collectors.toList());
			if (notes.isEmpty()) {
				return;
			}
			if (notes.contains("boom")) {
				record.reportIssue("note.text", "boom is not allowed");
				return;
			}
			ToyChild child = record.target("child");
			String joined = String.join(", ", notes);
			String old = child.getNotes();
			child.setNotes(joined);
			if (!record.isNew() && !joined.equals(old)) {
				record.addChange(new FieldChangeDto("note.text", old == null ? "" : old, joined));
			}
		}
	}

	public static class ToyParent implements Serializable {

		private String key;
		private String name;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class ToyChild implements Serializable {

		private String code;
		private Integer amount;
		private String notes;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public Integer getAmount() {
			return amount;
		}

		public void setAmount(Integer amount) {
			this.amount = amount;
		}

		public String getNotes() {
			return notes;
		}

		public void setNotes(String notes) {
			this.notes = notes;
		}
	}
}
