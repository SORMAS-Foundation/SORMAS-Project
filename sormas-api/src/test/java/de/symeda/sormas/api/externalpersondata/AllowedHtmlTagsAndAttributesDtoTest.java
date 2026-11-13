package de.symeda.sormas.api.externalpersondata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.AbstractUnitTest;

class AllowedHtmlTagsAndAttributesDtoTest extends AbstractUnitTest {

	private AllowedHtmlTagsAndAttributesDto victim;

	@Test
	void createsEmptyConfigurationForNullMap() {
		// PREPARE
		Map<String, Set<String>> configuration = null;

		// EXECUTE
		victim = AllowedHtmlTagsAndAttributesDto.of(configuration);

		// CHECK
		assertTrue(victim.getAllowedTagsWithoutAttributes().isEmpty());
		assertTrue(victim.getAllowedAttributesByTag().isEmpty());
	}

	@Test
	void createsEmptyConfigurationForEmptyMap() {
		// PREPARE
		Map<String, Set<String>> configuration = Map.of();

		// EXECUTE
		victim = AllowedHtmlTagsAndAttributesDto.of(configuration);

		// CHECK
		assertTrue(victim.getAllowedTagsWithoutAttributes().isEmpty());
		assertTrue(victim.getAllowedAttributesByTag().isEmpty());
	}

	@Test
	void separatesTagsWithAttributesFromTagsWithoutAttributes() {
		// PREPARE
		Map<String, Set<String>> configuration = Map.of("a", Set.of("href", "title"), "div", Set.of("style"), "p", Set.of(), "br", Set.of());

		// EXECUTE
		victim = AllowedHtmlTagsAndAttributesDto.of(configuration);

		// CHECK
		assertEquals(Set.of("p", "br"), victim.getAllowedTagsWithoutAttributes());
		assertEquals(Map.of("a", Set.of("href", "title"), "div", Set.of("style")), victim.getAllowedAttributesByTag());
	}

	@Test
	void removesNullEmptyAndWhitespaceAttributes() {
		// PREPARE
		Set<String> attributes = new HashSet<>(Arrays.asList(null, "", " ", "\t", "\r\n", "href", "title"));
		Map<String, Set<String>> configuration = Map.of("a", attributes);

		// EXECUTE
		victim = AllowedHtmlTagsAndAttributesDto.of(configuration);

		// CHECK
		assertEquals(Map.of("a", Set.of("href", "title")), victim.getAllowedAttributesByTag());
		assertTrue(victim.getAllowedTagsWithoutAttributes().isEmpty());
	}

	@Test
	void treatsTagsWithOnlyBlankAttributesAsTagsWithoutAttributes() {
		// PREPARE
		Map<String, Set<String>> configuration = Map.of("p", new HashSet<>(Arrays.asList(null, "", " ", "\t", "\n")));

		// EXECUTE
		victim = AllowedHtmlTagsAndAttributesDto.of(configuration);

		// CHECK
		assertEquals(Set.of("p"), victim.getAllowedTagsWithoutAttributes());
		assertTrue(victim.getAllowedAttributesByTag().isEmpty());
	}

	@Test
	void leavesOriginalMapAndAttributeSetsUnchanged() {
		// PREPARE
		Set<String> attributes = new HashSet<>(Arrays.asList(null, "", "style"));
		Map<String, Set<String>> configuration = new HashMap<>();
		configuration.put("div", attributes);
		configuration.put("p", new HashSet<>());

		// EXECUTE
		victim = AllowedHtmlTagsAndAttributesDto.of(configuration);

		// CHECK
		assertEquals(Set.of("div", "p"), configuration.keySet());
		assertEquals(new HashSet<>(Arrays.asList(null, "", "style")), attributes);
		assertTrue(configuration.get("p").isEmpty());
		assertEquals(Map.of("div", Set.of("style")), victim.getAllowedAttributesByTag());
		assertEquals(Set.of("p"), victim.getAllowedTagsWithoutAttributes());
	}

	@Test
	void doesNotShareMapOrAttributeSetsWithSource() {
		// PREPARE
		Set<String> attributes = new HashSet<>(Set.of("href"));
		Map<String, Set<String>> configuration = new HashMap<>();
		configuration.put("a", attributes);
		configuration.put("p", new HashSet<>());
		victim = AllowedHtmlTagsAndAttributesDto.of(configuration);

		// EXECUTE
		attributes.add("title");
		configuration.get("p").add("style");
		configuration.clear();

		// CHECK
		assertEquals(Map.of("a", Set.of("href")), victim.getAllowedAttributesByTag());
		assertEquals(Set.of("p"), victim.getAllowedTagsWithoutAttributes());
	}
}
