package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;

final class GroupingFixture {

	private final ImportSheet sheet;
	private final String expected;

	private GroupingFixture(ImportSheet sheet, String expected) {
		this.sheet = sheet;
		this.expected = expected;
	}

	static GroupingFixture load(String resource) throws IOException {
		String text;
		try (InputStream in = GroupingFixture.class.getClassLoader().getResourceAsStream(resource)) {
			text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
		String[] parts = text.split("\n===\n", 2);
		return new GroupingFixture(SheetFixtures.table(parts[0].strip().split("\n")), parts[1].strip());
	}

	ImportSheet sheet() {
		return sheet;
	}

	String expected() {
		return expected;
	}

	static String render(GroupingResult result) {
		List<String> lines = new ArrayList<>();
		result.roots().forEach(root -> renderNode(root, 0, lines));
		result.issues().forEach(issue -> lines.add("! unattached@" + issue.getLineNumber()));
		result.roots().forEach(root -> root.issues().forEach(issue -> lines.add("! " + root.label() + "@" + issue.getLineNumber())));
		return String.join("\n", lines);
	}

	private static void renderNode(ImportNode node, int depth, List<String> lines) {
		lines.add("  ".repeat(depth) + node.label() + "@" + node.lineNumber());
		node.children().forEach(child -> renderNode(child, depth + 1, lines));
	}
}
