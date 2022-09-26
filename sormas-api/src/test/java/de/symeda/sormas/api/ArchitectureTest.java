package de.symeda.sormas.api;

import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.name;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static de.symeda.sormas.api.audit.Constants.createPrefix;
import static de.symeda.sormas.api.audit.Constants.deletePrefix;
import static de.symeda.sormas.api.audit.Constants.executePrefix;
import static de.symeda.sormas.api.audit.Constants.readPrefix;
import static de.symeda.sormas.api.audit.Constants.updatePrefix;
import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Remote;

import org.junit.runner.RunWith;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.properties.CanBeAnnotated;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.uuid.HasUuid;

@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "de.symeda.sormas.api")
public class ArchitectureTest {

	@ArchTest
	public static final ArchRule dontUseFacadeProviderRule =
		ArchRuleDefinition.theClass(FacadeProvider.class).should().onlyBeAccessed().byClassesThat().belongToAnyOf(FacadeProvider.class);

	private static final Set<String> allowedPrefix = new HashSet<String>() {

		{
			addAll(createPrefix);
			addAll(readPrefix);
			addAll(updatePrefix);
			addAll(deletePrefix);
			addAll(executePrefix);

		}
	};

	@ArchTest
	public static final ArchRule testCorrectNamedMethods = classes().that().resideInAPackage("de.symeda.sormas.api.(*)..").

		should(new ArchCondition<JavaClass>("have proper method prefixes") {

			@Override
			public void check(JavaClass javaClass, ConditionEvents events) {
				if (!javaClass.isAnnotatedWith(Remote.class)) {
					String message = javaClass.getName() + " can be ignored as it is not annotated with @Remote";
					events.add(new SimpleConditionEvent(javaClass, true, message));
					return;
				}

				List<JavaMethod> wrongNaming = javaClass.getMethods()
					.stream()
					.filter(javaMethod -> allowedPrefix.stream().noneMatch(p -> javaMethod.getName().startsWith(p)))
					.collect(toList());

				boolean satisfied = wrongNaming.isEmpty();
				if (satisfied) {
					String message = "All methods in " + javaClass.getName() + " are correctly named";
					events.add(new SimpleConditionEvent(javaClass, satisfied, message));
				} else {
					String message = String.format("%s contains the following wrongly named methods: %s", javaClass.getName(), wrongNaming);
					events.add(new SimpleConditionEvent(javaClass, satisfied, message));
				}

			}
		});

	@ArchTest
	public static final ArchRule testDtosWithUuidFieldMustImplementHasUuid = classes().that()
		.resideInAPackage("de.symeda.sormas.api.(*)..")
		.and()
		.haveSimpleNameEndingWith("Dto")
		.and()
		.containAnyFieldsThat(name("uuid"))
		.should()
		.implement(HasUuid.class);

	@ArchTest
	public static final ArchRule testDtosAreAuditable = classes().that()
		.resideInAPackage("de.symeda.sormas.api.(*)..")
		.and()
		.haveSimpleNameEndingWith("Dto")
		.should()
		.beAnnotatedWith(AuditedClass.class)
		.orShould()
		.beAssignableTo(CanBeAnnotated.Predicates.annotatedWith(AuditedClass.class)); // covers inheritance

	// fields that resides in a class that implements HasUuid should be annotated with AuditInclude

	@ArchTest
	public static final ArchRule testDtosWithUuidFieldMustImplementHasUuidAndMustBeAuditable = fields().that()
		.areDeclaredInClassesThat()
		.resideInAPackage("de.symeda.sormas.api.(*)..")
		.and()
		.haveName("uuid")
		.should()
		.beAnnotatedWith(AuditInclude.class);
}
