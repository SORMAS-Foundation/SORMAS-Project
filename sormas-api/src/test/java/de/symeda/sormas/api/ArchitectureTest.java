package de.symeda.sormas.api;

import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.name;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
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


import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.properties.CanBeAnnotated;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.uuid.HasUuid;

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
		.and()
//TODO: #10750 Remove exception from test
		.doNotHaveSimpleName("ExternalDataDto")
		.should()
		.implement(HasUuid.class);

	@ArchTest
	public static final ArchRule testDtoClassesWithUuidFieldMustBeAnnotatedWithAuditedClass = classes().that()
		.resideInAPackage("de.symeda.sormas.api.(*)..")
		.and()
		.containAnyFieldsThat(name("uuid"))
		.should()
		.beAnnotatedWith(AuditedClass.class)
		.orShould()
		.beAssignableTo(CanBeAnnotated.Predicates.annotatedWith(AuditedClass.class)); // covers inheritance

	@ArchTest
	public static final ArchRule testUuidDtoFieldMustBeAnnotatedWithAuditInclude = fields().that()
		.areDeclaredInClassesThat()
		.resideInAPackage("de.symeda.sormas.api.(*)..")
		.and()
		.haveName("uuid")
		.should()
		.beAnnotatedWith(AuditInclude.class);

	@ArchTest
	public static final ArchRule testTypesInFacadeAreAuditable = methods().that()
		.areDeclaredInClassesThat()
		.haveSimpleNameEndingWith("Facade")
		.should(new ArchCondition<JavaMethod>("have parameters and return type which is annotated with @AuditedClass") {

			@Override
			public void check(JavaMethod javaMethod, ConditionEvents conditionEvents) {
				javaMethod.getParameters().forEach(parameter -> {
					// audit parameters
					JavaClass rawType = parameter.getRawType();
					if (!mustAudit(rawType)) {
						return;
					}
					conditionEvents.add(
						SimpleConditionEvent.violated(
							javaMethod,
							"Parameter " + parameter + " of type " + rawType.getName() + " is not annotated with @AuditedClass"));

				});

				// audit return type
				final JavaClass rawReturnType = javaMethod.getRawReturnType();
				if (!mustAudit(rawReturnType)) {
					return;
				}
				conditionEvents.add(
					SimpleConditionEvent.violated(
						javaMethod,
						String.format("Return type %s of method %s is not annotated with @AuditedClass", rawReturnType, javaMethod)));

			}

			private boolean mustAudit(JavaClass rawType) {
				String rawTypeName = rawType.getName();
				// [L shows up in case of varargs
				if (rawTypeName.startsWith("[L")) {
					String componentTypeName = rawTypeName.substring(2, rawTypeName.length() - 1);
					try {
						Class<?> clazz = getClass().getClassLoader().loadClass(componentTypeName);
						// only case right now for varargs is enums so this is fine for now...
						if (clazz.isEnum()) {
							return false;
						}
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
				}

				// ignore enums as they can just be audited with toString()
				if (rawType.isEnum()) {
					return false;
				}

				// violated rule if parameter is a list and the list type is not annotated with @AuditedClass
				final Class<?> reflect = rawType.reflect();
				// todo collections etc are ignored with this, but I was not able to get access the concrete generic
				//  type to check it further. This means we need to resort to warnings printed about @AuditedClass
				//  being missing in the concrete type.
				if (rawTypeName.startsWith("java.") || reflect.isPrimitive() || rawTypeName.startsWith("com.fasterxml.jackson.databind.JsonNode")) {
					return false;
				}

				// ignore primitive arrays
				if (rawType.isArray() && rawType.getComponentType().isPrimitive()) {
					return false;
				}

				// ignore string arrays as they just can be audited with toString() if need be
				if (rawType.isArray() && rawType.getComponentType().isEquivalentTo(String.class)) {
					return false;
				}

				// ignore two-dimensional string arrays as they just can be audited with toString() if need be
				if (rawType.isArray()
					&& rawType.getComponentType().isArray()
					&& rawType.getComponentType().getComponentType().isEquivalentTo(String.class)) {
					return false;
				}

				// ignore byte arrays
				if (rawType.isArray() && rawType.getComponentType().isEquivalentTo(byte.class)) {
					return false;
				}
				return !rawType.isAnnotatedWith(AuditedClass.class)
					&& !rawType.isAssignableTo(CanBeAnnotated.Predicates.annotatedWith(AuditedClass.class));
			}
		});
}
