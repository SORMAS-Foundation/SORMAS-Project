package de.symeda.sormas.api.importexport.hierarchical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

class RecordOperationDtoTest {

	@Test
	void carriesItsMatchReason() {
		RecordOperationDto operation = new RecordOperationDto("case", OperationType.UPDATE, "A", 2, List.of(), MatchReason.BY_SIMILARITY);
		assertEquals(MatchReason.BY_SIMILARITY, operation.getMatchReason());
		assertNull(new RecordOperationDto("sample", OperationType.CREATE, "S", 3, List.of()).getMatchReason());
	}
}
