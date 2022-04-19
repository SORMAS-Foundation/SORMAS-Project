package de.symeda.sormas.api;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import javax.ejb.Remote;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static de.symeda.sormas.api.audit.Constants.createPrefix;
import static de.symeda.sormas.api.audit.Constants.deletePrefix;
import static de.symeda.sormas.api.audit.Constants.executePrefix;
import static de.symeda.sormas.api.audit.Constants.readPrefix;
import static de.symeda.sormas.api.audit.Constants.updatePrefix;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.util.stream.Collectors.toList;

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

}
