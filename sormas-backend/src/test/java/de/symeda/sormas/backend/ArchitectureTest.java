package de.symeda.sormas.backend;

import de.symeda.sormas.backend.action.ActionFacadeEjb;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventGroupFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb;
import de.symeda.sormas.backend.event.eventimport.EventImportFacadeEjb;
import javax.annotation.security.RolesAllowed;

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
		assertFacadeEjbAnnotated(CaseImportFacadeEjb.class, true, classes);
	}

	@ArchTest
	public void testExternalJournalFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(ExternalJournalFacadeEjb.class, true, classes);
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
	public void testEventFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(EventFacadeEjb.class, classes);
	}

	@ArchTest
	public void testEventParticipantFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(EventParticipantFacadeEjb.class, classes);
	}

	@ArchTest
	public void testActionFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(ActionFacadeEjb.class, classes);
	}

	@ArchTest
	public void testEventGroupFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(EventGroupFacadeEjb.class, classes);
	}

	@ArchTest
	public void testEventImportFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(EventImportFacadeEjb.class, true, classes);
	}

	private void assertFacadeEjbAnnotated(Class<?> facadeEjbClass, JavaClasses classes) {
		assertFacadeEjbAnnotated(facadeEjbClass, false, classes);
	}

	private void assertFacadeEjbAnnotated(Class<?> facadeEjbClass, boolean classAuthOnly, JavaClasses classes) {
		ArchRuleDefinition.theClass(facadeEjbClass).should().beAnnotatedWith(RolesAllowed.class).check(classes);

		GivenMethodsConjunction methods = ArchRuleDefinition.methods().that().areDeclaredIn(facadeEjbClass).and().arePublic();

		if (classAuthOnly) {
			methods.should().notBeAnnotatedWith(RolesAllowed.class);
		} else {
			methods.should()
				.beAnnotatedWith(RolesAllowed.class)
				.orShould()
				.haveNameMatching("^(get|count|is|does|has|validate|to|pseudonymize|convertToReferenceDto|fillOrBuild|convertToDto|fromDto|convertToDetailedReferenceDto|exists).*")
				.check(classes);
		}
	}
}
