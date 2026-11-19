package de.symeda.sormas.backend.common;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.theClass;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.EvaluationResult;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.util.RightsAllowed;

public class CronServicePermissionTest extends AbstractUnitTest {

	public static final String BACKEND_ROOT_PACKAGE_NAME = "de.symeda.sormas.backend";

	@Test
	void methodsCalledByCronServiceAllowSystemWhenRightsAreRestricted() {
		// PREPARE
		JavaClasses classes = new ClassFileImporter().importPackages("de.symeda.sormas.backend");
		ArchCondition<JavaClass> callMethodsAllowingSystem =
			new ArchCondition<>("Call only methods without @RightsAllowed or ones with UserRight._SYSTEM") {

				@Override
				public void check(JavaClass cronService, ConditionEvents events) {
					for (JavaMethodCall call : cronService.getMethodCallsFromSelf()) {
						if (!call.getTargetOwner().getPackageName().startsWith(BACKEND_ROOT_PACKAGE_NAME)
							|| call.getTargetOwner().equals(cronService)) {
							continue;
						}
						JavaMethod method = call.getTarget().resolveMember().orElse(null);
						if (method == null) {
							events.add(SimpleConditionEvent.violated(call, "Could not resolve " + call.getDescription()));
							continue;
						}
						RightsAllowed rightsAllowedAnnotation = method.tryGetAnnotationOfType(RightsAllowed.class).orElse(null);
						if (rightsAllowedAnnotation != null && !Arrays.asList(rightsAllowedAnnotation.value()).contains(UserRight._SYSTEM)) {
							events.add(SimpleConditionEvent.violated(call, call.getDescription() + " does not allow SYSTEM"));
						}
					}
				}
			};

		// EXECUTE
		EvaluationResult result = theClass(CronService.class).should(callMethodsAllowingSystem).evaluate(classes);

		// CHECK
		assertFalse(result.hasViolation(), () -> result.getFailureReport().toString());
	}
}
