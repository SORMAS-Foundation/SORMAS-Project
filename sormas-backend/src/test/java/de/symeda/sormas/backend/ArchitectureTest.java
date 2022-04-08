package de.symeda.sormas.backend;

import javax.annotation.security.RolesAllowed;

import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.labmessage.LabMessageFacadeEjb;
import de.symeda.sormas.backend.labmessage.TestReportFacadeEjb;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.task.TaskFacadeEjb;
import de.symeda.sormas.backend.travelentry.TravelEntryFacadeEjb;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb;
import org.junit.runner.RunWith;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.lang.syntax.elements.GivenMethodsConjunction;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.backend.bagexport.BAGExportFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.caseimport.CaseImportFacadeEjb;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportFacadeEjb;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.externaljournal.ExternalJournalFacadeEjb;
import de.symeda.sormas.backend.therapy.PrescriptionFacadeEjb;
import de.symeda.sormas.backend.therapy.TreatmentFacadeEjb;
import de.symeda.sormas.backend.visit.VisitFacadeEjb;

import java.util.ArrayList;
import java.util.List;

@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = {
	"de.symeda.sormas.api",
	"de.symeda.sormas.backend" })
public class ArchitectureTest {

	@ArchTest
	public static final ArchRule dontUseFacadeProviderRule =
		ArchRuleDefinition.theClass(FacadeProvider.class).should().onlyBeAccessed().byClassesThat().belongToAnyOf(FacadeProvider.class);

	@ArchTest
	public void testCaseFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(CaseFacadeEjb.class, classes);
	}

	@ArchTest
	public void testContactFacadeEjbAuthorization(JavaClasses classes) {

		assertFacadeEjbAnnotated(ContactFacadeEjb.class, classes);
	}

	@ArchTest
	public void testVisitFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(VisitFacadeEjb.class, classes);
	}

	@ArchTest
	public void testBAGExportFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(BAGExportFacadeEjb.class, classes);
	}

	@ArchTest
	public void testCaseImportFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(CaseImportFacadeEjb.class, true, classes, null);
	}

	@ArchTest
	public void testExternalJournalFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(ExternalJournalFacadeEjb.class, true, classes, null);
	}

	@ArchTest
	public void testPrescriptionFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(PrescriptionFacadeEjb.class, classes);
	}

	@ArchTest
	public void testTreatmentFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(TreatmentFacadeEjb.class, classes);

	}

	@ArchTest
	public void testSurveillanceReportFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(SurveillanceReportFacadeEjb.class, classes);
	}

	@ArchTest
	public void testClinicalVisitFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(ClinicalVisitFacadeEjb.class, classes);

	}

	@ArchTest
	public void testSampleFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(SampleFacadeEjb.class, classes);
	}

	@ArchTest
	public void testLabMessageFacadeEjbAuthorization(JavaClasses classes) {
		List<String> specificMethodNames = new ArrayList<>();
		specificMethodNames.add("fetchAndSaveExternalLabMessages");
		assertFacadeEjbAnnotated(LabMessageFacadeEjb.class, true, classes, specificMethodNames);
	}

	@ArchTest
	public void testTestReportFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(TestReportFacadeEjb.class, true, classes, null);
	}

	@ArchTest
	public void testImmunizationFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(ImmunizationFacadeEjb.class, classes);
	}

	@ArchTest
	public void testVaccinationFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(VaccinationFacadeEjb.class, classes);
	}

	@ArchTest
	public void testTravelEntryFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(TravelEntryFacadeEjb.class, classes);
	}

	@ArchTest
	public void testTaskFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(TaskFacadeEjb.class, classes);
	}

	private void assertFacadeEjbAnnotated(Class<?> facadeEjbClass, JavaClasses classes) {
		assertFacadeEjbAnnotated(facadeEjbClass, false, classes, null);
	}

	private void assertFacadeEjbAnnotated(Class<?> facadeEjbClass, boolean classAuthOnly, JavaClasses classes, List<String> specificMethodNames) {
		ArchRuleDefinition.theClass(facadeEjbClass).should().beAnnotatedWith(RolesAllowed.class).check(classes);
		GivenMethodsConjunction allPublicMethods = ArchRuleDefinition.methods().that().areDeclaredIn(facadeEjbClass).and().arePublic();

		if (classAuthOnly) {
			getMethodsShouldNotBeAnnotated(allPublicMethods, specificMethodNames).should().notBeAnnotatedWith(RolesAllowed.class);
			if (specificMethodNames != null) {
				specificMethodNames.forEach(
					specificMethodName -> allPublicMethods.and().haveFullName(specificMethodName).should().beAnnotatedWith(RolesAllowed.class));
			}
		} else {
			allPublicMethods.should()
				.beAnnotatedWith(RolesAllowed.class)
				.orShould()
				.haveNameMatching("^(get|count|is|does|has|validate|to|pseudonymize|convertToReferenceDto|fillOrBuild|convertToDto|fromDto|exists).*")
				.check(classes);
		}
	}

	private GivenMethodsConjunction getMethodsShouldNotBeAnnotated(GivenMethodsConjunction methods, List<String> specificMethodNames) {
		return specificMethodNames != null ? getPublicMethodsWithoutSpecificMethods(methods, specificMethodNames) : methods;
	}

	private GivenMethodsConjunction getPublicMethodsWithoutSpecificMethods(GivenMethodsConjunction methods, List<String> specificMethodNames) {
		for (String specificMethodName : specificMethodNames) {
			methods = methods.and().doNotHaveFullName(specificMethodName);
		}
		return methods;
	}
}
