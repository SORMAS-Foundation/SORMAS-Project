package de.symeda.sormas.backend.importexport.hierarchical.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueKind;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;

class PlannerTest {

	private static final Date V1 = new Date(1_000L);
	private static final Date V2 = new Date(2_000L);

	@Test
	void createsTheWholeTreeForNewRecords() {
		ImportPlan plan = plan(new ToyDefinition(), "| P1 | Alpha | C1 | 3 | |", "|    |       | C2 | 4 | |");

		PlannedBlock block = plan.blocks().get(0);
		assertEquals(BlockStatus.READY, block.status());
		assertEquals(List.of(OperationType.CREATE, OperationType.CREATE, OperationType.CREATE), operations(block));
		assertEquals(Integer.valueOf(4), block.rootRecord().children().get(1).<ToyDefinition.ToyChild> target("child").getAmount());
	}

	@Test
	void marksAnIdenticalExistingTreeUnchanged() {
		ToyDefinition toy = new ToyDefinition().withParent("P1", "Alpha", V1).withChild("P1", "C1", 3);

		PlannedBlock block = plan(toy, "| P1 | Alpha | C1 | 3 | |").blocks().get(0);

		assertEquals(BlockStatus.UNCHANGED, block.status());
	}

	@Test
	void listsTheFieldsAnUpdateChanges() {
		ToyDefinition toy = new ToyDefinition().withParent("P1", "Alpha", V1);

		PlannedRecord parent = plan(toy, "| P1 | Beta | | | |").blocks().get(0).rootRecord();

		assertEquals(OperationType.UPDATE, parent.operation());
		assertEquals("parent.name", parent.changes().get(0).getField());
		assertEquals("Alpha", parent.changes().get(0).getOldValue());
		assertEquals("Beta", parent.changes().get(0).getNewValue());
	}

	@Test
	void blankCellsDoNotOverwriteExistingValues() {
		ToyDefinition toy = new ToyDefinition().withParent("P1", "Alpha", V1);

		PlannedBlock block = plan(toy, "| P1 | | C9 | 1 | |").blocks().get(0);

		assertEquals(List.of(OperationType.UNCHANGED, OperationType.CREATE), operations(block));
		assertEquals("Alpha", block.rootRecord().<ToyDefinition.ToyParent> target("parent").getName());
	}

	@Test
	void aMatchErrorFailsTheBlockAndSkipsItsChildren() {
		PlannedBlock block = plan(new ToyDefinition(), "| !P | Alpha | C1 | 1 | |").blocks().get(0);

		assertEquals(BlockStatus.ERROR, block.status());
		assertEquals("parent.key", block.issues().get(0).getColumn());
		assertEquals(0, block.records().count());
	}

	@Test
	void aRootKeyRepeatedInALaterBlockFailsThatBlock() {
		ImportPlan plan = plan(new ToyDefinition(), "| P1 | Alpha | C1 | 1 | |", "| P2 | Beta | C1 | 1 | |", "| P1 | Again | C2 | 1 | |");

		assertEquals(BlockStatus.READY, plan.blocks().get(0).status());
		assertEquals(BlockStatus.READY, plan.blocks().get(1).status());
		assertEquals(BlockStatus.ERROR, plan.blocks().get(2).status());
		assertTrue(plan.blocks().get(2).issues().get(0).getMessage().contains("2"));
	}

	@Test
	void twoBlocksMatchingTheSameStoredRecordFailTheLaterOne() {
		ToyDefinition toy = new ToyDefinition().withParent("P1", "Alpha", V1);

		ImportPlan plan = plan(toy, "| P1 | Beta | | | |", "| ~P1 | Gamma | | | |");

		assertEquals(BlockStatus.READY, plan.blocks().get(0).status());
		assertEquals(BlockStatus.ERROR, plan.blocks().get(1).status());
		assertTrue(plan.blocks().get(1).issues().get(0).getMessage().contains("2"));
	}

	@Test
	void aChildKeyRepeatedUnderTheSameParentFails() {
		ImportPlan plan = plan(new ToyDefinition(), "| P1 | Alpha | C1 | 1 | |", "|    |       | C1 | 2 | |");

		assertEquals(BlockStatus.ERROR, plan.blocks().get(0).status());
		assertEquals(Integer.valueOf(3), plan.blocks().get(0).issues().get(0).getLineNumber());
	}

	@Test
	void aParseErrorReportsItsLineAndColumn() {
		PlannedBlock block = plan(new ToyDefinition(), "| P1 | Alpha | C1 | 1 | |", "|    |       | C2 | abc | |").blocks().get(0);

		assertEquals(BlockStatus.ERROR, block.status());
		assertEquals(Integer.valueOf(3), block.issues().get(0).getLineNumber());
		assertEquals("child.amount", block.issues().get(0).getColumn());
	}

	@Test
	void looksEachLevelUpOnceWhateverTheRowCount() {
		ToyDefinition toy = new ToyDefinition();
		String[] rows = new String[20];
		for (int i = 0; i < 10; i++) {
			rows[2 * i] = "| P" + i + " | Name | C1 | 1 | |";
			rows[2 * i + 1] = "|      |      | C2 | 2 | |";
		}

		plan(toy, rows);

		assertEquals(Integer.valueOf(1), toy.lookups.get("parent"));
		assertEquals(Integer.valueOf(1), toy.lookups.get("child"));
	}

	@Test
	void fingerprintIsStableAndFollowsTheStoredVersion() {
		ToyDefinition toy = new ToyDefinition().withParent("P1", "Alpha", V1);

		String first = plan(toy, "| P1 | Beta | | | |").blocks().get(0).fingerprint();
		String second = plan(toy, "| P1 | Beta | | | |").blocks().get(0).fingerprint();
		toy.parentVersions.put("P1", V2);
		String afterEdit = plan(toy, "| P1 | Beta | | | |").blocks().get(0).fingerprint();

		assertEquals(first, second);
		assertNotEquals(first, afterEdit);
	}

	@Test
	void headerProblemsStopTheWholeFile() {
		ImportSheet unknown = SheetFixtures.table("| parent.key | parent.colour |", "| P1 | red |");
		ImportSheet duplicate = SheetFixtures.table("| parent.key | parent.key |", "| P1 | P1 |");

		ImportPlan unknownPlan = new Planner().plan(new ToyDefinition(), unknown, null);
		ImportPlan duplicatePlan = new Planner().plan(new ToyDefinition(), duplicate, null);
		ImportPlan tooLong = new Planner().plan(new ToyDefinition(1), sheet("| P1 | A | | | |", "| P2 | B | | | |"), null);

		assertEquals(ImportIssueKind.FILE, unknownPlan.fileIssues().get(0).getKind());
		assertEquals("parent.colour", unknownPlan.fileIssues().get(0).getColumn());
		assertEquals(0, unknownPlan.blocks().size());
		assertEquals("parent.key", duplicatePlan.fileIssues().get(0).getColumn());
		assertTrue(tooLong.hasFileIssues());
	}

	@Test
	void reportsOrphanRowsBeforeAndInsideBlocks() {
		ImportPlan plan = plan(new ToyDefinition(), "| | | C0 | 1 | |", "| P1 | Alpha | | | |", "| | | | | stray |");

		assertEquals(Integer.valueOf(2), plan.rowIssues().get(0).getLineNumber());
		assertEquals(BlockStatus.ERROR, plan.blocks().get(0).status());
	}

	@Test
	void foldsEmbeddedNotesIntoTheirParentRecord() {
		ToyDefinition toy = new ToyDefinition();

		PlannedBlock ok = plan(toy, "| P1 | Alpha | C1 | 1 | a |", "| | | | | b |").blocks().get(0);
		PlannedBlock boom = plan(toy, "| P1 | Alpha | C1 | 1 | boom |").blocks().get(0);

		assertEquals("a, b", ok.rootRecord().children().get(0).<ToyDefinition.ToyChild> target("child").getNotes());
		assertEquals(BlockStatus.ERROR, boom.status());
	}

	@Test
	void plansOnlyTheSelectedBlocks() {
		ImportPlan plan =
			new Planner().plan(new ToyDefinition(), sheet("| P1 | Alpha | | | |", "| P2 | Beta | | | |", "| P3 | Gamma | | | |"), Set.of(1));

		assertEquals(1, plan.blocks().size());
		assertEquals(1, plan.blocks().get(0).index());
		assertEquals("P2", plan.blocks().get(0).root().cell("parent.key"));
	}

	private static ImportPlan plan(ToyDefinition toy, String... rows) {
		return new Planner().plan(toy, sheet(rows), null);
	}

	private static ImportSheet sheet(String... rows) {
		String[] lines = new String[rows.length + 1];
		lines[0] = ToyDefinition.HEADER;
		System.arraycopy(rows, 0, lines, 1, rows.length);
		return SheetFixtures.table(lines);
	}

	private static List<OperationType> operations(PlannedBlock block) {
		return block.records().map(PlannedRecord::operation).collect(Collectors.toList());
	}
}
