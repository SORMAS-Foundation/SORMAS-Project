/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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

	/**
	 * Loads a grouping fixture from the test classpath. A fixture is a text file with the sheet rows, a line {@code ===} and the
	 * expected tree, e.g. {@code "case-1\nsample-1\n===\ncase@1\n  sample@2"}.
	 * <p>
	 * Windows line endings ({@code \r\n}) and old Mac line endings ({@code \r}) are converted to {@code \n} first: with
	 * {@code core.autocrlf=true}, Git checks the fixtures out with {@code \r\n} on Windows, so {@code "case-1\r\n===\r\ncase@1"}
	 * becomes {@code "case-1\n===\ncase@1"} and is split into the sheet {@code "case-1"} and the expected tree {@code "case@1"}.
	 * A fixture without the {@code ===} line gets an empty expected tree, so the test fails with a readable assertion instead of an
	 * {@link ArrayIndexOutOfBoundsException}.
	 *
	 * @param resource
	 *            classpath path of the fixture, e.g. {@code "hierarchical-import/grouping/orphans.txt"}
	 * @return the sheet and the expected tree of the fixture
	 * @throws IOException
	 *             if the fixture cannot be read or does not exist on the classpath
	 */
	static GroupingFixture load(String resource) throws IOException {
		String text;
		try (InputStream in = GroupingFixture.class.getClassLoader().getResourceAsStream(resource)) {
			if (in == null) {
				throw new IOException("Grouping fixture not found on the test classpath: " + resource);
			}
			text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
		text = text.replace("\r\n", "\n").replace('\r', '\n');
		String[] parts = text.split("\n===\n", 2);
		String expected = parts.length > 1 ? parts[1].strip() : "";
		return new GroupingFixture(SheetFixtures.table(parts[0].strip().split("\n")), expected);
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
