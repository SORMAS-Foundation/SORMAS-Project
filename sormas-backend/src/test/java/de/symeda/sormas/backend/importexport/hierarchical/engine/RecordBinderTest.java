package de.symeda.sormas.backend.importexport.hierarchical.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class RecordBinderTest {

	private static final ImportLevel ITEM = new ImportLevel("item", ImportLevel.Kind.ENTITY, "item", "owner");
	private static final List<ColumnSpec> COLUMNS = List.of(
		new ColumnSpec("item.key", "Key"),
		new ColumnSpec("item.name", "Name", "name"),
		new ColumnSpec("item.count", "Count", "count"),
		new ColumnSpec("owner.city", "City", "address", "city"));

	@Test
	void writesFilledCellsOntoTheTargetNamedByThePrefix() {
		Map<String, Object> targets = targets();

		new RecordBinder(new ToyCellWriter()).bind(node(Map.of("item.name", "Bolt", "item.count", "4", "owner.city", "Kampala")), COLUMNS, targets);

		assertEquals("Bolt", item(targets).getName());
		assertEquals(Integer.valueOf(4), item(targets).getCount());
		assertEquals("Kampala", ((Owner) targets.get("owner")).getAddress().getCity());
	}

	@Test
	void leavesFieldsAloneForBlankAndKeyOnlyCells() {
		Map<String, Object> targets = targets();
		item(targets).setName("Existing");
		ToyCellWriter writer = new ToyCellWriter();

		new RecordBinder(writer).bind(node(Map.of("item.key", "K1", "item.count", "2")), COLUMNS, targets);

		assertEquals("Existing", item(targets).getName());
		assertEquals(List.of("item.count"), writer.writtenHeaders);
	}

	@Test
	void reportsAParseErrorAndKeepsBindingTheOtherCells() {
		Map<String, Object> targets = targets();
		ImportNode node = node(Map.of("item.count", "abc", "item.name", "Bolt"));

		new RecordBinder(new ToyCellWriter()).bind(node, COLUMNS, targets);

		assertNull(item(targets).getCount());
		assertEquals("Bolt", item(targets).getName());
		assertEquals(1, node.issues().size());
		assertEquals("item.count", node.issues().get(0).getColumn());
		assertEquals(Integer.valueOf(7), node.issues().get(0).getLineNumber());
	}

	@Test
	void appliesColumnsInDefinitionOrder() {
		Map<String, String> cellsInFileOrder = new LinkedHashMap<>();
		cellsInFileOrder.put("owner.city", "Kampala");
		cellsInFileOrder.put("item.count", "1");
		cellsInFileOrder.put("item.name", "Bolt");
		ToyCellWriter writer = new ToyCellWriter();

		new RecordBinder(writer).bind(node(cellsInFileOrder), COLUMNS, targets());

		assertEquals(List.of("item.name", "item.count", "owner.city"), writer.writtenHeaders);
	}

	private static ImportNode node(Map<String, String> cells) {
		return new ImportNode(ITEM, 7, cells);
	}

	private static Map<String, Object> targets() {
		Map<String, Object> targets = new HashMap<>();
		targets.put("item", new Item());
		targets.put("owner", new Owner());
		return targets;
	}

	private static Item item(Map<String, Object> targets) {
		return (Item) targets.get("item");
	}

	public static class Item implements Serializable {

		private String name;
		private Integer count;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}
	}

	public static class Owner implements Serializable {

		private Address address = new Address();

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}
	}

	public static class Address implements Serializable {

		private String city;

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}
	}
}
