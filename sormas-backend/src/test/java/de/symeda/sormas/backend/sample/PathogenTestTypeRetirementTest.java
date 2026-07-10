/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.LegacyEnumNames;

/**
 * The set of retired {@link PathogenTestType} names lives in three artifacts that no compiler ties together: the
 * {@link LegacyEnumNames} annotations, Postgres migration 649 and the sormas-app SQLite mirror. A name added to one
 * and forgotten in another leaves stored values un-migrated on that platform, silently.
 */
public class PathogenTestTypeRetirementTest {

	private static final Path SCHEMA = Paths.get("src/main/resources/sql/sormas_schema.sql");
	private static final Path DATABASE_HELPER =
		Paths.get("../sormas-app/app/src/main/java/de/symeda/sormas/app/backend/common/DatabaseHelper.java");

	/** The IN-list of retired names, as it appears in both the Postgres and the SQLite statements. */
	private static final Pattern RETIRED_IN_LIST = Pattern.compile("testtype IN \\(([^)]*)\\)", Pattern.CASE_INSENSITIVE);

	@Test
	public void migration649RetiresExactlyTheNamesTheEnumDeclares() throws IOException {
		String sql = migration649();
		assertThat(retiredNamesIn(sql), equalTo(declaredRetiredNames()));
		assertEveryRetiredNameIsRewrittenInTheRequestedTestsSet(sql, "migration 649");
	}

	@Test
	public void theAndroidMirrorRetiresExactlyTheNamesTheEnumDeclares() throws IOException {
		assumeTrue(Files.exists(DATABASE_HELPER), "sormas-app is not checked out next to sormas-backend");
		String sql = androidCase362();
		assertThat(retiredNamesIn(sql), equalTo(declaredRetiredNames()));
		assertEveryRetiredNameIsRewrittenInTheRequestedTestsSet(sql, "sormas-app case 362");
	}

	/**
	 * The `testtype IN (...)` lists only cover the performed-test tables. The requested-test set is rewritten by
	 * comma-wrapped token matching on requestedpathogentestsstring, so assert each name appears there too.
	 */
	private static void assertEveryRetiredNameIsRewrittenInTheRequestedTestsSet(String sql, String what) throws IOException {

		for (String retired : declaredRetiredNames()) {
			assertThat(what + " never matches the token ," + retired + ",", sql.contains("," + retired + ","), is(true));
		}
	}

	private static Set<String> declaredRetiredNames() throws IOException {

		Set<String> declared = new LinkedHashSet<>();
		for (PathogenTestType type : PathogenTestType.values()) {
			LegacyEnumNames annotation;
			try {
				annotation = PathogenTestType.class.getField(type.name()).getAnnotation(LegacyEnumNames.class);
			} catch (NoSuchFieldException e) {
				throw new IllegalStateException(e);
			}
			if (annotation != null) {
				declared.addAll(Arrays.asList(annotation.value()));
			}
		}
		return declared;
	}

	/** @return every distinct name appearing in a {@code testtype IN (...)} list, which is the retired set. */
	private static Set<String> retiredNamesIn(String sql) {

		Set<String> names = new LinkedHashSet<>();
		Matcher matcher = RETIRED_IN_LIST.matcher(sql);
		while (matcher.find()) {
			for (String name : matcher.group(1).split(",")) {
				String trimmed = name.trim().replace("'", "").replace("\"", "");
				// the CASE that routes the successorless names to OTHER also uses an IN-list, a strict subset
				if (!trimmed.isEmpty()) {
					names.add(trimmed);
				}
			}
		}
		return names;
	}

	private static String migration649() throws IOException {
		return slice(read(SCHEMA), "VALUES (648,", "VALUES (649,");
	}

	private static String androidCase362() throws IOException {
		// the SQL is a Java expression, so glue its adjacent string literals back together first
		return slice(read(DATABASE_HELPER), "case 362:", "default:").replaceAll("\"\\s*\\+\\s*\"", "");
	}

	private static String read(Path path) throws IOException {
		return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
	}

	private static String slice(String content, String after, String before) {

		int start = content.indexOf(after);
		int end = content.indexOf(before, start);
		if (start < 0 || end < 0) {
			throw new IllegalStateException("could not locate the block between \"" + after + "\" and \"" + before + "\"");
		}
		return content.substring(start, end);
	}
}
