package de.symeda.sormas.backend;

import java.util.Collections;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;

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
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.labmessage.LabMessageFacadeEjb;
import de.symeda.sormas.backend.labmessage.TestReportFacadeEjb;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb;
import de.symeda.sormas.backend.report.AggregateReportFacadeEjb;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.task.TaskFacadeEjb;
import de.symeda.sormas.backend.therapy.PrescriptionFacadeEjb;
import de.symeda.sormas.backend.therapy.TreatmentFacadeEjb;
import de.symeda.sormas.backend.travelentry.TravelEntryFacadeEjb;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb;
import de.symeda.sormas.backend.visit.VisitFacadeEjb;

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
		assertFacadeEjbAnnotated(CaseImportFacadeEjb.class, AuthMode.CLASS_ONLY, classes);
	}

	@ArchTest
	public void testExternalJournalFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(ExternalJournalFacadeEjb.class, AuthMode.CLASS_ONLY, Collections.singletonList("notifyExternalJournal"), classes);
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
		assertFacadeEjbAnnotated(
			LabMessageFacadeEjb.class,
			AuthMode.CLASS_ONLY,
			Collections.singletonList("fetchAndSaveExternalLabMessages"),
			classes);
	}

	@ArchTest
	public void testTestReportFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(TestReportFacadeEjb.class, AuthMode.CLASS_ONLY, classes);
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
		assertFacadeEjbAnnotated(facadeEjbClass, AuthMode.CLASS_AND_METHODS, Collections.emptyList(), classes);
	}

	private void assertFacadeEjbAnnotated(Class<?> facadeEjbClass, AuthMode authMode, JavaClasses classes) {
		assertFacadeEjbAnnotated(facadeEjbClass, authMode, Collections.emptyList(), classes);
	}

	private void assertFacadeEjbAnnotated(Class<?> facadeEjbClass, AuthMode authMode, @NotNull List<String> exceptedMethods, JavaClasses classes) {
		if (authMode != AuthMode.METHODS_ONLY) {
			ArchRuleDefinition.theClass(facadeEjbClass).should().beAnnotatedWith(RolesAllowed.class).check(classes);
		}

		GivenMethodsConjunction methods = ArchRuleDefinition.methods().that().areDeclaredIn(facadeEjbClass).and().arePublic();
		String exceptedMethodsMatcher = "^(" + String.join("|", exceptedMethods) + ")$";

		if (authMode == AuthMode.CLASS_ONLY) {
			methods.and().haveNameNotMatching(exceptedMethodsMatcher).should().notBeAnnotatedWith(RolesAllowed.class).check(classes);
			methods.and().haveNameMatching(exceptedMethodsMatcher).should().beAnnotatedWith(RolesAllowed.class).check(classes);
		} else {
			// TODO - add exceptedMethods handling when needed

			MethodsShouldConjunction methodChecks = methods.should().beAnnotatedWith(RolesAllowed.class).orShould().beAnnotatedWith(PermitAll.class);

			if (authMode == AuthMode.CLASS_AND_METHODS) {
				methodChecks = methodChecks.orShould()
					.haveNameMatching(
						"^(get|count|is|does|has|validate|to|pseudonymize|convertToReferenceDto|fillOrBuild|convertToDto|fromDto|exists).*");
			}

			methodChecks.check(classes);
		}
	}

	private enum AuthMode {
		CLASS_AND_METHODS,
		CLASS_ONLY,
		METHODS_ONLY
	}
}
