package de.symeda.sormas.backend;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption.OnlyIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

@AnalyzeClasses(importOptions = {
	OnlyIncludeTests.class })
public class JUnitFrameworkTest {

	private static final String[] JUNIT4_CLASSES = {
		"org.junit.Test",
		"org.junit.Assert",
		"org.junit.BeforeClass",
		"org.junit.Before",
		"org.junit.After",
		"org.junit.AfterClass",
		"org.junit.runner.RunWith", };

	/**
	 * {@code org.testcontainers} still relies on JUnit4 ({@code junit:junit}), therefore the dependency is not excludeable.
	 * This test checks that no (regular used) JUnit4 classes are used in tests.
	 */
	@ArchTest
	public void testThatJUnit4IsNotUsed(JavaClasses classes) {

		for (String junit4ClassName : JUNIT4_CLASSES) {
			noClasses().should().dependOnClassesThat().haveFullyQualifiedName(junit4ClassName).check(classes);
		}
	}
}
