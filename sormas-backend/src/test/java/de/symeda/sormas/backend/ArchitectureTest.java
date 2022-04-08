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
import com.tngtech.archunit.lang.syntax.elements.MethodsShouldConjunction;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.backend.bagexport.BAGExportFacadeEjb;
import de.symeda.sormas.backend.campaign.CampaignFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.caseimport.CaseImportFacadeEjb;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportFacadeEjb;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.dashboard.DashboardFacadeEjb;
import de.symeda.sormas.backend.externaljournal.ExternalJournalFacadeEjb;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb;
import de.symeda.sormas.backend.report.AggregateReportFacadeEjb;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb;
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
		assertFacadeEjbAnnotated(CaseImportFacadeEjb.class, AuthMode.CLASS_ONLY, classes, null);
	}

	@ArchTest
	public void testExternalJournalFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(ExternalJournalFacadeEjb.class, AuthMode.CLASS_ONLY, classes, null);
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
	public void testDashboardFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(DashboardFacadeEjb.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testWeeklyReportFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(WeeklyReportFacadeEjb.class, AuthMode.CLASS_AND_METHODS, classes);
	}

	@ArchTest
	public void testAggregateReportFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(AggregateReportFacadeEjb.class, AuthMode.CLASS_AND_METHODS, classes);
	}

	@ArchTest
	public void testOutbreakFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(OutbreakFacadeEjb.class, AuthMode.CLASS_AND_METHODS, classes);
	}

	@ArchTest
	public void testCampaignFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(CampaignFacadeEjb.class, AuthMode.CLASS_AND_METHODS, classes);
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
		assertFacadeEjbAnnotated(facadeEjbClass, AuthMode.CLASS_AND_METHODS, classes, null);
	}

	private void assertFacadeEjbAnnotated(Class<?> facadeEjbClass, AuthMode authMode, JavaClasses classes) {
		if (authMode != AuthMode.METHODS_ONLY) {
			ArchRuleDefinition.theClass(facadeEjbClass).should().beAnnotatedWith(RolesAllowed.class).check(classes);
		}

		GivenMethodsConjunction methods = ArchRuleDefinition.methods().that().areDeclaredIn(facadeEjbClass).and().arePublic();

		if (authMode == AuthMode.CLASS_ONLY) {
			methods.should().notBeAnnotatedWith(RolesAllowed.class);
			getMethodsShouldNotBeAnnotated(allPublicMethods, specificMethodNames).should().notBeAnnotatedWith(RolesAllowed.class);
			if (specificMethodNames != null) {
				specificMethodNames.forEach(
						specificMethodName -> allPublicMethods.and().haveFullName(specificMethodName).should().beAnnotatedWith(RolesAllowed.class));
			}
		} else {
			MethodsShouldConjunction methodChecks = methods.should().beAnnotatedWith(RolesAllowed.class);

			if (authMode == AuthMode.CLASS_AND_METHODS) {
				methodChecks = methodChecks.orShould()
					.haveNameMatching(
						"^(get|count|is|does|has|validate|to|pseudonymize|convertToReferenceDto|fillOrBuild|convertToDto|fromDto).*");
			}

			methodChecks.check(classes);
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

	private enum AuthMode {
		CLASS_AND_METHODS,
		CLASS_ONLY,
		METHODS_ONLY
	}
}
