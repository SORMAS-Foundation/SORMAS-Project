package de.symeda.sormas.backend;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.lang.syntax.elements.GivenMethodsConjunction;
import com.tngtech.archunit.lang.syntax.elements.MethodsShouldConjunction;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FeatureIndependent;
import de.symeda.sormas.backend.action.ActionFacadeEjb;
import de.symeda.sormas.backend.bagexport.BAGExportFacadeEjb;
import de.symeda.sormas.backend.campaign.CampaignFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.caseimport.CaseImportFacadeEjb;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportFacadeEjb;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitFacadeEjb;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.dashboard.DashboardFacadeEjb;
import de.symeda.sormas.backend.dashboard.sample.SampleDashboardFacadeEjb;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb;
import de.symeda.sormas.backend.document.DocumentFacadeEjb;
import de.symeda.sormas.backend.environment.EnvironmentFacadeEjb;
import de.symeda.sormas.backend.environment.EnvironmentImportFacadeEjb;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSampleFacadeEjb;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventGroupFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb;
import de.symeda.sormas.backend.event.eventimport.EventImportFacadeEjb;
import de.symeda.sormas.backend.externaljournal.ExternalJournalFacadeEjb;
import de.symeda.sormas.backend.externalmessage.ExternalMessageFacadeEjb;
import de.symeda.sormas.backend.externalmessage.labmessage.TestReportFacadeEjb;
import de.symeda.sormas.backend.externalsurveillancetool.ExternalSurveillanceToolGatewayFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.info.InfoFacadeEjb;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureFacadeEjb;
import de.symeda.sormas.backend.infrastructure.ClientInfraSyncFacadeEjb;
import de.symeda.sormas.backend.infrastructure.PopulationDataFacadeEjb;
import de.symeda.sormas.backend.infrastructure.area.AreaFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb;
import de.symeda.sormas.backend.report.AggregateReportFacadeEjb;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.AbstractSormasToSormasInterface;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.entities.caze.SormasToSormasCaseFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.entities.contact.SormasToSormasContactFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.entities.event.SormasToSormasEventFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.entities.externalmessage.SormasToSormasExternalMessageFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.incoming.SormasToSormasShareRequestFacadeEJB;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoFacadeEjb;
import de.symeda.sormas.backend.task.TaskFacadeEjb;
import de.symeda.sormas.backend.therapy.PrescriptionFacadeEjb;
import de.symeda.sormas.backend.therapy.TreatmentFacadeEjb;
import de.symeda.sormas.backend.travelentry.TravelEntryFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.util.RightsAllowed;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb;
import de.symeda.sormas.backend.visit.VisitFacadeEjb;

@AnalyzeClasses(packages = {
	"de.symeda.sormas.api",
	"de.symeda.sormas.backend" })
public class ArchitectureTest {

	@ArchTest
	public static final ArchRule testNoDtosInBackend =
		classes().that().resideInAPackage("de.symeda.sormas.backend.(*)..").should().haveSimpleNameNotEndingWith("Dto");

	@ArchTest
	public static final ArchRule dontUseFacadeProviderRule =
		ArchRuleDefinition.theClass(FacadeProvider.class).should().onlyBeAccessed().byClassesThat().belongToAnyOf(FacadeProvider.class);

	/**
	 * @RolesAllowed annotation was replaced by @RightsAllowed for performance reasons
	 */
	@ArchTest
	public static final ArchRule dontUseRolesAllowedClassAnnotationRule = classes().should().notBeAnnotatedWith(RolesAllowed.class);

	@ArchTest
	public static final ArchRule dontUseRolesAllowedMethodAnnotationRule = methods().should().notBeAnnotatedWith(RolesAllowed.class);

	private static final DescribedPredicate<JavaClass> classesInDataDictionary =
		new DescribedPredicate<JavaClass>("are used as data dictionary entity") {

			@Override
			public boolean test(JavaClass javaClass) {
				return InfoFacadeEjb.DATA_DICTIONARY_ENTITIES.stream().anyMatch(e -> javaClass.isEquivalentTo(e.getEntityClass()));
			}
		};

	@ArchTest
	public static final ArchRule dataDictionaryClassesHaveAnnotation = classes().that(classesInDataDictionary)
		.should()
		.beAnnotatedWith(DependingOnFeatureType.class)
		.orShould()
		.beAnnotatedWith(FeatureIndependent.class);

	@ArchTest
	public void testDataDictionaryReferencedClassesAreAnnotated(JavaClasses classes) {
		fields().that()
			.areDeclaredInClassesThat(classesInDataDictionary)
			.and()
			.areNotStatic()
			.and()
			.haveRawType(new DescribedPredicate<JavaClass>("*Dto") {

				@Override
				public boolean test(JavaClass javaClass) {
					return javaClass.getSimpleName().toLowerCase().endsWith("dto");
				}
			})
			.should(new ArchCondition<JavaField>("have type annotated") {

				@Override
				public void check(JavaField javaField, ConditionEvents conditionEvents) {
					if (!javaField.getRawType().isAnnotatedWith(DependingOnFeatureType.class)
						&& !javaField.getRawType().isAnnotatedWith(FeatureIndependent.class)) {
						conditionEvents.add(
							SimpleConditionEvent.violated(
								javaField.getRawType(),
								"Class <" + javaField.getRawType().getFullName()
									+ "> is not annotated with @DependingOnFeatureType or @FeatureIndependent in "
									+ javaField.getRawType().getSourceCodeLocation()));
					}
				}
			})
			.check(classes);
	}

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
		assertFacadeEjbAnnotated(EventImportFacadeEjb.class, AuthMode.CLASS_ONLY, classes);
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
	public void testExternalMessageFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(
			ExternalMessageFacadeEjb.class,
			AuthMode.CLASS_ONLY,
			Arrays.asList(
				"getExternalMessagesAdapterVersion",
				"fetchAndSaveExternalMessages",
				"bulkAssignExternalMessages",
				"delete"),
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

	@ArchTest
	public void testUserFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(UserFacadeEjb.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testAbstractInfrastructureFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(AbstractInfrastructureFacadeEjb.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testContinentFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(ContinentFacadeEjb.class, classes);
	}

	@ArchTest
	public void testSubcontinentFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(SubcontinentFacadeEjb.class, classes);
	}

	@ArchTest
	public void testCountryFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(CountryFacadeEjb.class, classes);
	}

	@ArchTest
	public void testAreaFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(AreaFacadeEjb.class, classes);
	}

	@ArchTest
	public void testRegionFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(RegionFacadeEjb.class, classes);
	}

	@ArchTest
	public void testDistrictFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(DistrictFacadeEjb.class, classes);
	}

	@ArchTest
	public void testCommunityFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(CommunityFacadeEjb.class, classes);
	}

	@ArchTest
	public void testFacilityFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(FacilityFacadeEjb.class, classes);
	}

	@ArchTest
	public void testPointOfEntryFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(PointOfEntryFacadeEjb.class, classes);
	}

	@ArchTest
	public void testClientInfraSyncFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(ClientInfraSyncFacadeEjb.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testPopulationDataFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(PopulationDataFacadeEjb.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testAbstractSormasToSormasInterfaceAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(AbstractSormasToSormasInterface.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testSormasToSormasCaseFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(SormasToSormasCaseFacadeEjb.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testSormasToSormasContactFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(SormasToSormasContactFacadeEjb.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testSormasToSormasEventFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(SormasToSormasEventFacadeEjb.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testSormasToSormasExternalMessageFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(SormasToSormasExternalMessageFacadeEjb.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testSormasToSormasShareInfoFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(SormasToSormasShareInfoFacadeEjb.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testSormasToSormasShareRequestFacadeEJBAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(SormasToSormasShareRequestFacadeEJB.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testSormasToSormasOriginInfoFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(SormasToSormasOriginInfoFacadeEjb.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testSormasToSormasFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(SormasToSormasFacadeEjb.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testShareRequestInfoFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(ShareRequestInfoFacadeEjb.class, AuthMode.CLASS_ONLY, classes);
	}

	@ArchTest
	public void testDocumentFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(DocumentFacadeEjb.class, AuthMode.CLASS_AND_METHODS, classes);
	}

	@ArchTest
	public void testDocumentTemplateFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(DocumentTemplateFacadeEjb.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testAbstractBaseEjbNoAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(AbstractBaseEjb.class, AuthMode.NONE, classes);
	}

	@ArchTest
	public void testExternalSurveillanceToolGatewayFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(ExternalSurveillanceToolGatewayFacadeEjb.class, AuthMode.METHODS_ONLY, classes);
	}

	@ArchTest
	public void testSampleDashboardFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(SampleDashboardFacadeEjb.class, AuthMode.CLASS_ONLY, classes);
	}

	@ArchTest
	public void testEnvironmentFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(EnvironmentFacadeEjb.class, classes);
	}

	@ArchTest
	public void testEnvironmentSampleFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(EnvironmentSampleFacadeEjb.class, classes);
	}

	@ArchTest
	public void testEnvironmentImportFacadeEjbAuthorization(JavaClasses classes) {
		assertFacadeEjbAnnotated(EnvironmentImportFacadeEjb.class, AuthMode.CLASS_ONLY, classes);
	}

	private void assertFacadeEjbAnnotated(Class<?> facadeEjbClass, JavaClasses classes) {
		assertFacadeEjbAnnotated(facadeEjbClass, AuthMode.CLASS_AND_METHODS, Collections.emptyList(), classes);
	}

	private void assertFacadeEjbAnnotated(Class<?> facadeEjbClass, AuthMode authMode, JavaClasses classes) {
		assertFacadeEjbAnnotated(facadeEjbClass, authMode, Collections.emptyList(), classes);
	}

	private void assertFacadeEjbAnnotated(Class<?> facadeEjbClass, AuthMode authMode, @NotNull List<String> exceptedMethods, JavaClasses classes) {
		if (authMode == AuthMode.METHODS_ONLY || authMode == AuthMode.NONE) {
			ArchRuleDefinition.theClass(facadeEjbClass).should().notBeAnnotatedWith(RightsAllowed.class).check(classes);
		} else {
			ArchRuleDefinition.theClass(facadeEjbClass).should().beAnnotatedWith(RightsAllowed.class).check(classes);
		}

		GivenMethodsConjunction methods = ArchRuleDefinition.methods().that().areDeclaredIn(facadeEjbClass).and().arePublic().and().areNotStatic();
		String exceptedMethodsMatcher = "^(" + String.join("|", exceptedMethods) + ")$";

		Function<GivenMethodsConjunction, MethodsShouldConjunction> annotatedRule = (m) -> m.should()
			.beAnnotatedWith(RightsAllowed.class)
			.orShould()
			.beAnnotatedWith(PermitAll.class)
			.orShould()
			.beAnnotatedWith(DenyAll.class);

		Function<GivenMethodsConjunction, MethodsShouldConjunction> notAnnotatedRule = (m) -> m.should()
			.notBeAnnotatedWith(RightsAllowed.class)
			.andShould()
			.notBeAnnotatedWith(PermitAll.class)
			.andShould()
			.notBeAnnotatedWith(DenyAll.class);

		if (authMode == AuthMode.CLASS_ONLY || authMode == AuthMode.NONE) {
			notAnnotatedRule.apply(methods.and().haveNameNotMatching(exceptedMethodsMatcher)).check(classes);
			annotatedRule.apply(methods.and().haveNameMatching(exceptedMethodsMatcher)).allowEmptyShould(exceptedMethods.isEmpty()).check(classes);
		} else {
			// TODO - add exceptedMethods handling when needed
			MethodsShouldConjunction methodChecks = annotatedRule.apply(methods);
			if (authMode == AuthMode.CLASS_AND_METHODS) {
				methodChecks = methodChecks.orShould()
					.haveNameMatching(
						"^(get|count|is|does|has|validate|to|pseudonymize|convertToReferenceDto|fillOrBuild|convertToDto|fromDto|convertToDetailedReferenceDto|exists).*");
			}

			methodChecks.check(classes);
		}
	}

	private enum AuthMode {
		CLASS_AND_METHODS,
		CLASS_ONLY,
		METHODS_ONLY,
		NONE,
	}
}
