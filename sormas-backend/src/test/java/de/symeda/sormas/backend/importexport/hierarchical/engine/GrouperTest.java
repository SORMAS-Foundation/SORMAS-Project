package de.symeda.sormas.backend.importexport.hierarchical.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GrouperTest {

	private static final ImportSchema SCHEMA = new ImportSchema(
		new ImportLevel("case", ImportLevel.Kind.ENTITY, "case"),
		new ImportLevel("sample", ImportLevel.Kind.ENTITY, "sample"),
		new ImportLevel("test", ImportLevel.Kind.ENTITY, "test"),
		new ImportLevel("ast", ImportLevel.Kind.EMBEDDED, "ast"));

	@ParameterizedTest
	@ValueSource(strings = {
		"user-example",
		"orphans",
		"spacers-and-whitespace",
		"new-case-without-key",
		"ast-embedded" })
	void groupsRowsIntoTheExpectedTree(String fixtureName) throws IOException {
		GroupingFixture fixture = GroupingFixture.load("hierarchical-import/grouping/" + fixtureName + ".txt");

		GroupingResult result = new Grouper().group(fixture.sheet(), SCHEMA);

		assertEquals(fixture.expected(), GroupingFixture.render(result));
	}
}
