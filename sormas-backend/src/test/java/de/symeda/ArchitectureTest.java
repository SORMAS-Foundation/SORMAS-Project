package de.symeda;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import org.junit.runner.RunWith;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchRule;

@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "de.symeda.sormas.backend")
public class ArchitectureTest {

	@ArchTest
	public static final ArchRule testNoDtosInBackend =
		classes().that().resideInAPackage("de.symeda.sormas.backend.(*)..").should().haveSimpleNameNotEndingWith("Dto");
}
