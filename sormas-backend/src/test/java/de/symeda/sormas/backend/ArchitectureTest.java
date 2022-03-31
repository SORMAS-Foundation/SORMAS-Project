package de.symeda.sormas.backend;

import com.tngtech.archunit.lang.syntax.elements.GivenMethodsConjunction;
import de.symeda.sormas.backend.caze.caseimport.CaseImportFacadeEjb;
import de.symeda.sormas.backend.externaljournal.ExternalJournalFacadeEjb;
import javax.annotation.security.RolesAllowed;

import org.junit.runner.RunWith;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.backend.bagexport.BAGExportFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportFacadeEjb;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
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
	public void testFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(CaseFacadeEjb.class, classes);
		assertFacadeEjbAnnotated(ContactFacadeEjb.class, classes);
		assertFacadeEjbAnnotated(VisitFacadeEjb.class, classes);
		assertFacadeEjbAnnotated(BAGExportFacadeEjb.class, classes);
		assertFacadeEjbAnnotated(CaseImportFacadeEjb.class, true, classes);
		assertFacadeEjbAnnotated(ExternalJournalFacadeEjb.class, true, classes);
		assertFacadeEjbAnnotated(PrescriptionFacadeEjb.class, classes);
		assertFacadeEjbAnnotated(TreatmentFacadeEjb.class, classes);
		assertFacadeEjbAnnotated(SurveillanceReportFacadeEjb.class, classes);
		assertFacadeEjbAnnotated(ClinicalVisitFacadeEjb.class, classes);
	}

	private void assertFacadeEjbAnnotated(Class<?> facadeEjbClass, JavaClasses classes) {
		assertFacadeEjbAnnotated(facadeEjbClass, false, classes);
	}

	private void assertFacadeEjbAnnotated(Class<?> facadeEjbClass, boolean classAuthOnly, JavaClasses classes) {
		ArchRuleDefinition.theClass(facadeEjbClass).should().beAnnotatedWith(RolesAllowed.class).check(classes);

		GivenMethodsConjunction methods = ArchRuleDefinition.methods()
				.that()
				.areDeclaredIn(facadeEjbClass)
				.and()
				.arePublic();

		if(classAuthOnly){
			methods.should().notBeAnnotatedWith(RolesAllowed.class);
		} else {
			methods
					.should()
					.beAnnotatedWith(RolesAllowed.class)
					.orShould()
					.haveNameMatching("^(get|count|is|does|has|validate|to|pseudonymize|convertToReferenceDto|fillOrBuild|convertToDto|fromDto).*")
					.check(classes);
		}
	}
}
