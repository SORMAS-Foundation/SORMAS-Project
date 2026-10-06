package de.symeda.sormas.backend.importexport.hierarchical.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.importexport.hierarchical.FieldChangeDto;

class FieldDiffTest {

	private static final ImportLevel ITEM = new ImportLevel("item", ImportLevel.Kind.ENTITY, "item");
	private static final List<ColumnSpec> COLUMNS =
		List.of(new ColumnSpec("item.key", "Key"), new ColumnSpec("item.name", "Name", "name"), new ColumnSpec("item.count", "Count", "count"));

	@Test
	void listsOnlyFilledColumnsWhoseValueChanged() {
		RecordBinderTest.Item before = new RecordBinderTest.Item();
		before.setName("Bolt");
		before.setCount(3);
		RecordBinderTest.Item after = SerializationUtils.clone(before);
		after.setName("Nut");
		ImportNode node = new ImportNode(ITEM, 2, Map.of("item.key", "K1", "item.name", "Nut", "item.count", "3"));

		List<FieldChangeDto> changes = FieldDiff.diff(node, COLUMNS, Map.of("item", before), Map.of("item", after));

		assertEquals(1, changes.size());
		assertEquals("item.name", changes.get(0).getField());
		assertEquals("Bolt", changes.get(0).getOldValue());
		assertEquals("Nut", changes.get(0).getNewValue());
	}

	@Test
	void treatsAStoredTimestampAndAParsedDateOfTheSameInstantAsEqual() {
		DatedItem before = new DatedItem();
		before.setWhen(new Timestamp(1_700_000_000_000L));
		DatedItem after = new DatedItem();
		after.setWhen(new Date(1_700_000_000_000L));
		ImportNode node = new ImportNode(ITEM, 2, Map.of("item.when", "11/14/2023"));

		List<FieldChangeDto> changes =
			FieldDiff.diff(node, List.of(new ColumnSpec("item.when", "When", "when")), Map.of("item", before), Map.of("item", after));

		assertEquals(List.of(), changes);
	}

	@Test
	void showsAnEmptyOldValueForAFieldThatWasNotSet() {
		RecordBinderTest.Item before = new RecordBinderTest.Item();
		RecordBinderTest.Item after = new RecordBinderTest.Item();
		after.setCount(5);
		ImportNode node = new ImportNode(ITEM, 2, Map.of("item.count", "5"));

		List<FieldChangeDto> changes = FieldDiff.diff(node, COLUMNS, Map.of("item", before), Map.of("item", after));

		assertEquals("", changes.get(0).getOldValue());
		assertEquals("5", changes.get(0).getNewValue());
	}

	public static class DatedItem implements Serializable {

		private Date when;

		public Date getWhen() {
			return when;
		}

		public void setWhen(Date when) {
			this.when = when;
		}
	}
}
