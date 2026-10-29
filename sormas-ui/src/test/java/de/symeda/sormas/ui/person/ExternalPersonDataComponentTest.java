package de.symeda.sormas.ui.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataForDisplayDto;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataIndexDto;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataProviderFacade;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.externalpersondata.ExternalPersonDataComponent;
import de.symeda.sormas.ui.externalpersondata.ExternalPersonsView;

class ExternalPersonDataComponentTest extends de.symeda.sormas.ui.AbstractUnitTest {

	private ExternalPersonDataComponent victim;

	@Test
	void loadsExternalDataByHealthIdWithoutLoadingLocalPerson() {
		try (Context context = new Context(true, "true")) {
			new ExternalPersonDataComponent("health-id");
			verify(context.provider).findDisplayDataByNationalHealthId("health-id");
			verifyNoInteractions(context.personFacade);
		}
	}

	@Test
	void searchUsesOnlyVisibleCriteriaAndDoesNotSearchOnArrival() {
		try (Context context = new Context(true, "true")) {
			de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchResponse response =
				new de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchResponse();
			response.setResults(java.util.Collections.emptyList());
			when(context.provider.search(org.mockito.ArgumentMatchers.any())).thenReturn(response);
			ExternalPersonsView view = new ExternalPersonsView();
			com.vaadin.ui.VerticalLayout content = (com.vaadin.ui.VerticalLayout) view.getComponent(1);
			com.vaadin.ui.HorizontalLayout actions = (com.vaadin.ui.HorizontalLayout) content.getComponent(3);
			com.vaadin.ui.Button search = (com.vaadin.ui.Button) actions.getComponent(0);
			verifyNoInteractions(context.provider);
			search.click();
			verifyNoInteractions(context.provider);
			com.vaadin.ui.TextField id = (com.vaadin.ui.TextField) content.getComponent(0);
			id.setValue("invalid-id");
			search.click();
			verifyNoInteractions(context.provider);
			((com.vaadin.ui.Button) content.getComponent(1)).click();
			com.vaadin.ui.GridLayout filters = (com.vaadin.ui.GridLayout) content.getComponent(2);
			((com.vaadin.ui.TextField) filters.getComponent(0, 0)).setValue("Doe");
			search.click();
			org.mockito.ArgumentCaptor<de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchRequest> request =
				org.mockito.ArgumentCaptor.forClass(de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchRequest.class);
			verify(context.provider).search(request.capture());
			assertEquals("Doe", request.getValue().getLastName());
			assertEquals(null, request.getValue().getNationalHealthId());
		}
	}

	@Test
	void directoryRetainsFiltersAndResultsUntilReset() {
		try (Context context = new Context(true, "true")) {
			ExternalPersonDataIndexDto person = new ExternalPersonDataIndexDto();
			person.setNationalHealthId("123");
			de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchResponse response =
				new de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchResponse();
			response.setResults(java.util.Collections.singletonList(person));
			response.setTotalCount(2);
			when(context.provider.search(org.mockito.ArgumentMatchers.any())).thenReturn(response);
			ExternalPersonsView view = new ExternalPersonsView();
			com.vaadin.ui.VerticalLayout content = (com.vaadin.ui.VerticalLayout) view.getComponent(1);
			((com.vaadin.ui.Button) content.getComponent(1)).click();
			((com.vaadin.ui.TextField) ((com.vaadin.ui.GridLayout) content.getComponent(2)).getComponent(0, 0)).setValue("Doe");
			((com.vaadin.ui.Button) ((com.vaadin.ui.HorizontalLayout) content.getComponent(3)).getComponent(0)).click();
			com.vaadin.ui.VerticalLayout restored = (com.vaadin.ui.VerticalLayout) new ExternalPersonsView().getComponent(1);
			org.junit.jupiter.api.Assertions.assertTrue(restored.getComponent(2).isVisible());
			assertEquals("Doe", ((com.vaadin.ui.TextField) ((com.vaadin.ui.GridLayout) restored.getComponent(2)).getComponent(0, 0)).getValue());
			com.vaadin.ui.Grid<?> grid = (com.vaadin.ui.Grid<?>) restored.getComponent(5);
			assertEquals(1, ((com.vaadin.data.provider.ListDataProvider<?>) grid.getDataProvider()).getItems().size());
			((com.vaadin.ui.Button) ((com.vaadin.ui.HorizontalLayout) restored.getComponent(3)).getComponent(1)).click();
			assertEquals(0, ((com.vaadin.data.provider.ListDataProvider<?>) grid.getDataProvider()).getItems().size());
			com.vaadin.ui.VerticalLayout reset = (com.vaadin.ui.VerticalLayout) new ExternalPersonsView().getComponent(1);
			assertEquals("", ((com.vaadin.ui.TextField) ((com.vaadin.ui.GridLayout) reset.getComponent(2)).getComponent(0, 0)).getValue());
		}
	}

	@Test
	void doesNotLoadPersonOrExternalDataWithoutPermission() {
		try (Context context = new Context(false, "true")) {
			assertEquals(0, context.create().getComponentCount());
			verifyNoInteractions(context.personFacade, context.provider);
		}
	}

	@Test
	void doesNotLoadPersonOrExternalDataWhenDisabled() {
		try (Context context = new Context(true, "false")) {
			assertEquals(0, context.create().getComponentCount());
			verifyNoInteractions(context.personFacade, context.provider);
		}
	}

	@Test
	void doesNotLoadPersonOrExternalDataWhenConfigurationIsMissing() {
		try (Context context = new Context(true, null)) {
			assertEquals(0, context.create().getComponentCount());
			verifyNoInteractions(context.personFacade, context.provider);
		}
	}

	@Test
	void missingHealthIdPrefillsCriteriaWithoutCallingAdapter() {
		try (Context context = new Context(true, "true")) {
			// PREPARE
			context.person.setNationalHealthId(" ");
			context.person.setFirstName("Jane");
			context.person.setLastName("Doe");
			context.person.setBirthdateYYYY(1980);
			context.person.setBirthdateMM(5);
			context.person.setBirthdateDD(12);
			// EXECUTE
			victim = context.create();
			// CHECK
			com.vaadin.ui.VerticalLayout content = (com.vaadin.ui.VerticalLayout) ((ExternalPersonsView) victim.getComponent(0)).getComponent(1);
			com.vaadin.ui.GridLayout filters = (com.vaadin.ui.GridLayout) content.getComponent(3);
			org.junit.jupiter.api.Assertions.assertTrue(filters.isVisible());
			org.junit.jupiter.api.Assertions.assertFalse(content.getComponent(0).isVisible());
			assertEquals("Doe", ((com.vaadin.ui.TextField) filters.getComponent(0, 0)).getValue());
			assertEquals("Jane", ((com.vaadin.ui.TextField) filters.getComponent(1, 0)).getValue());
			assertEquals(java.time.LocalDate.of(1980, 5, 12), ((com.vaadin.ui.DateField) filters.getComponent(3, 0)).getValue());
			verifyNoInteractions(context.provider);
		}
	}

	@Test
	void partialBirthdateUsesYearRangeAndSingleResultOpensWithoutSaving() {
		try (Context context = new Context(true, "true")) {
			// PREPARE
			context.person.setNationalHealthId(null);
			context.person.setBirthdateYYYY(1980);
			context.person.setBirthdateMM(5);
			ExternalPersonDataIndexDto external = new ExternalPersonDataIndexDto();
			external.setNationalHealthId("external-id");
			de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchResponse response =
				new de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchResponse();
			response.setResults(java.util.Collections.singletonList(external));
			when(context.provider.search(org.mockito.ArgumentMatchers.any())).thenReturn(response);
			// EXECUTE
			victim = context.create();
			com.vaadin.ui.VerticalLayout content = (com.vaadin.ui.VerticalLayout) ((ExternalPersonsView) victim.getComponent(0)).getComponent(1);
			((com.vaadin.ui.Button) ((com.vaadin.ui.HorizontalLayout) content.getComponent(4)).getComponent(0)).click();
			// CHECK
			org.mockito.ArgumentCaptor<de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchRequest> request =
				org.mockito.ArgumentCaptor.forClass(de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchRequest.class);
			verify(context.provider).search(request.capture());
			assertEquals(1980, request.getValue().getBirthYearFrom());
			assertEquals(1980, request.getValue().getBirthYearTo());
			assertEquals(null, request.getValue().getBirthDate());
			verify(context.provider).findDisplayDataByNationalHealthId("external-id");
			org.mockito.Mockito.verify(context.personFacade, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any());
			org.junit.jupiter.api.Assertions.assertInstanceOf(com.vaadin.ui.Button.class, victim.getComponent(0));
		}
	}

	@Test
	void rendersHtmlForTheSuppliedPerson() {
		try (Context context = new Context(true, "true")) {
			ExternalPersonDataForDisplayDto display = new ExternalPersonDataForDisplayDto();
			display.setHtml("<p>External person</p>");
			when(context.provider.findDisplayDataByNationalHealthId("health-id")).thenReturn(display);
			Panel panel = (Panel) context.create().getComponent(0);
			Label html = (Label) panel.getContent();
			assertEquals(display.getHtml(), html.getValue());
			assertEquals(ContentMode.HTML, html.getContentMode());
			verify(context.personFacade).getByUuid("person-uuid");
			verify(context.provider).findDisplayDataByNationalHealthId("health-id");
		}
	}

	@org.junit.jupiter.params.ParameterizedTest
	@org.junit.jupiter.params.provider.CsvSource({
		"2, ALIVE",
		"1, DEAD",
		"0, BURIED",
		"0, UNKNOWN" })
	void criteriaPresetsReplaceFieldsWithoutSearching(int birthDateParts, de.symeda.sormas.api.person.PresentCondition condition) {
		try (Context context = new Context(true, "true")) {
			// PREPARE
			context.person.setNationalHealthId(null);
			context.person.setFirstName("Jane");
			context.person.setLastName("Doe");
			context.person.setSex(de.symeda.sormas.api.person.Sex.FEMALE);
			context.person.setPresentCondition(condition);
			context.person.setApproximateAge(42);
			context.person.setApproximateAgeType(de.symeda.sormas.api.person.ApproximateAgeType.MONTHS);
			if (birthDateParts > 0) {
				context.person.setBirthdateYYYY(1980);
			}
			if (birthDateParts == 2) {
				context.person.setBirthdateMM(5);
				context.person.setBirthdateDD(12);
			}
			de.symeda.sormas.api.location.LocationDto address = new de.symeda.sormas.api.location.LocationDto();
			address.setCountry(new de.symeda.sormas.api.infrastructure.country.CountryReferenceDto("country", "Luxembourg", "LU"));
			address.setCity("Locality");
			address.setCommunity(new de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto("community", "Municipality", null));
			context.person.setAddress(address);
			de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchResponse response =
				new de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchResponse();
			response.setResults(java.util.Collections.emptyList());
			when(context.provider.search(org.mockito.ArgumentMatchers.any())).thenReturn(response);
			victim = context.create();
			com.vaadin.ui.VerticalLayout content = (com.vaadin.ui.VerticalLayout) ((ExternalPersonsView) victim.getComponent(0)).getComponent(1);
			com.vaadin.ui.VerticalLayout presets = (com.vaadin.ui.VerticalLayout) content.getComponent(2);
			com.vaadin.ui.HorizontalLayout buttons = (com.vaadin.ui.HorizontalLayout) presets.getComponent(1);
			com.vaadin.ui.GridLayout filters = (com.vaadin.ui.GridLayout) content.getComponent(3);
			com.vaadin.ui.Button search = (com.vaadin.ui.Button) ((com.vaadin.ui.HorizontalLayout) content.getComponent(4)).getComponent(0);
			com.vaadin.ui.Grid<ExternalPersonDataIndexDto> grid = (com.vaadin.ui.Grid<ExternalPersonDataIndexDto>) content.getComponent(6);
			grid.setItems(new ExternalPersonDataIndexDto());
			((com.vaadin.ui.TextField) filters.getComponent(0, 0)).setValue("Edited name");
			((com.vaadin.ui.Button) content.getComponent(1)).click();
			((com.vaadin.ui.TextField) content.getComponent(0)).setValue("stale-id");
			grid.setItems(new ExternalPersonDataIndexDto());
			// EXECUTE
			((com.vaadin.ui.Button) buttons.getComponent(1)).click();
			// CHECK
			verifyNoInteractions(context.provider);
			assertEquals("externalPersonsLinkSearchExplanation", ((Label) presets.getComponent(0)).getValue());
			org.junit.jupiter.api.Assertions.assertTrue(filters.isVisible());
			assertEquals("", ((com.vaadin.ui.TextField) content.getComponent(0)).getValue());
			assertEquals(0, ((com.vaadin.data.provider.ListDataProvider<?>) grid.getDataProvider()).getItems().size());
			search.click();
			org.mockito.ArgumentCaptor<de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchRequest> request =
				org.mockito.ArgumentCaptor.forClass(de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchRequest.class);
			verify(context.provider).search(request.capture());
			de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchRequest expected =
				new de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchRequest();
			expected.setFirstName("Jane");
			expected.setLastName("Doe");
			if (birthDateParts == 2) {
				expected.setBirthDate(java.time.LocalDate.of(1980, 5, 12));
			} else if (birthDateParts == 1) {
				expected.setBirthYearFrom(1980);
				expected.setBirthYearTo(1980);
			} else {
				expected.setAgeFrom(3);
				expected.setAgeTo(3);
			}
			expected.setSex(de.symeda.sormas.api.person.Sex.FEMALE);
			expected.setCountryOfResidence("LU");
			expected.setLocalityOfResidence("Locality");
			expected.setMunicipalityOfResidence("Municipality");
			expected.setAlive(
				condition == de.symeda.sormas.api.person.PresentCondition.UNKNOWN
					? null
					: condition == de.symeda.sormas.api.person.PresentCondition.ALIVE);
			assertEquals(expected, request.getValue());
			org.mockito.Mockito.clearInvocations(context.provider);
			grid.setItems(new ExternalPersonDataIndexDto());
			((com.vaadin.ui.Button) buttons.getComponent(0)).click();
			verifyNoInteractions(context.provider);
			assertEquals(0, ((com.vaadin.data.provider.ListDataProvider<?>) grid.getDataProvider()).getItems().size());
			search.click();
			verify(context.provider).search(request.capture());
			expected.setSex(null);
			expected.setCountryOfResidence(null);
			expected.setLocalityOfResidence(null);
			expected.setMunicipalityOfResidence(null);
			expected.setAlive(null);
			expected.setAgeFrom(null);
			expected.setAgeTo(null);
			assertEquals(expected, request.getValue());
			((com.vaadin.ui.Button) ((com.vaadin.ui.HorizontalLayout) content.getComponent(4)).getComponent(1)).click();
			assertEquals("", ((com.vaadin.ui.TextField) filters.getComponent(0, 0)).getValue());
		}
	}

	@org.junit.jupiter.params.ParameterizedTest
	@org.junit.jupiter.params.provider.CsvSource({
		"persons/externalPersonData/person-uuid, persons/data/person-uuid",
		"cases/externalPersonData/case-uuid, cases/person/case-uuid",
		"contacts/externalPersonData/contact-uuid, contacts/person/contact-uuid",
		"events/eventparticipants/externalPersonData/participant-uuid, events/eventparticipants/person/participant-uuid",
		"immunizations/externalPersonData/immunization-uuid, immunizations/person/immunization-uuid",
		"travelEntries/externalPersonData/entry-uuid, travelEntries/person/entry-uuid" })
	void linkingReturnsToPersonFormAndLeavesIdUnsaved(String source, String target) {
		com.vaadin.ui.UI previous = com.vaadin.ui.UI.getCurrent();
		try (Context context = new Context(true, "true")) {
			// PREPARE
			context.person.setUuid("person-uuid");
			context.person.setNationalHealthId(null);
			context.person.setFirstName("Jane");
			context.ui.when(() -> UiUtil.permitted(UserRight.PERSON_EDIT)).thenReturn(true);
			when(context.personFacade.isEditAllowed("person-uuid")).thenReturn(true);
			ExternalPersonDataIndexDto external = new ExternalPersonDataIndexDto();
			external.setNationalHealthId("external-id");
			de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchResponse response =
				new de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchResponse();
			response.setResults(java.util.Collections.singletonList(external));
			when(context.provider.search(org.mockito.ArgumentMatchers.any())).thenReturn(response);
			PersonEditForm form = mock(PersonEditForm.class);
			com.vaadin.v7.ui.TextField id = new com.vaadin.v7.ui.TextField();
			when(form.getField(PersonDto.NATIONAL_HEALTH_ID)).thenReturn(id);
			when(form.getValue()).thenReturn(context.person);
			com.vaadin.ui.UI ui = mock(com.vaadin.ui.UI.class);
			com.vaadin.navigator.Navigator navigator = mock(com.vaadin.navigator.Navigator.class);
			when(ui.getNavigator()).thenReturn(navigator);
			when(navigator.getState()).thenReturn(source);
			TestPersonView targetView = new TestPersonView(form);
			when(navigator.getCurrentView()).thenReturn(targetView);
			com.vaadin.ui.UI.setCurrent(ui);
			// EXECUTE
			victim = context.create();
			com.vaadin.ui.VerticalLayout content = (com.vaadin.ui.VerticalLayout) ((ExternalPersonsView) victim.getComponent(0)).getComponent(1);
			((com.vaadin.ui.Button) ((com.vaadin.ui.HorizontalLayout) content.getComponent(4)).getComponent(0)).click();
			((com.vaadin.ui.Button) victim.getComponent(0)).click();
			// CHECK
			verify(navigator).navigateTo(target);
			assertEquals("external-id", id.getValue());
			org.mockito.Mockito.verify(context.personFacade, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any());
		} finally {
			com.vaadin.ui.UI.setCurrent(previous);
		}
	}

	private static class TestPersonView extends com.vaadin.ui.VerticalLayout implements com.vaadin.navigator.View {

		private TestPersonView(PersonEditForm form) {
			addComponent(form);
		}
	}

	@Test
	void missingExternalDataShowsLocalizedMessage() {
		try (Context context = new Context(true, "true")) {
			assertEquals(Strings.infoNoExternalPersonData, ((Label) context.create().getComponent(0)).getValue());
		}
	}

	@Test
	void blankExternalHtmlShowsLocalizedMessage() {
		try (Context context = new Context(true, "true")) {
			ExternalPersonDataForDisplayDto display = new ExternalPersonDataForDisplayDto();
			display.setHtml(" ");
			when(context.provider.findDisplayDataByNationalHealthId("health-id")).thenReturn(display);
			assertEquals(Strings.infoNoExternalPersonData, ((Label) context.create().getComponent(0)).getValue());
		}
	}

	@Test
	void adapterFailureShowsLocalizedError() {
		try (Context context = new Context(true, "true")) {
			when(context.provider.findDisplayDataByNationalHealthId("health-id")).thenThrow(new IllegalStateException("Unavailable"));
			assertEquals(Strings.errorExternalPersonDataLoad, ((Label) context.create().getComponent(0)).getValue());
		}
	}

	private static class Context implements AutoCloseable {

		private final MockedStatic<de.symeda.sormas.ui.ViewModelProviders> viewModels = mockStatic(de.symeda.sormas.ui.ViewModelProviders.class);

		private final MockedStatic<FacadeProvider> facades = mockStatic(FacadeProvider.class);
		private final MockedStatic<UiUtil> ui = mockStatic(UiUtil.class);
		private final MockedStatic<I18nProperties> i18n = mockStatic(I18nProperties.class, invocation -> {
			if (invocation.getMethod().getReturnType() == String.class && invocation.getArguments().length > 0) {
				Object value = invocation.getArgument(0);
				return value instanceof Enum<?> ? ((Enum<?>) value).name() : value;
			}
			return org.mockito.Answers.RETURNS_DEFAULTS.answer(invocation);
		});
		private final PersonFacade personFacade = mock(PersonFacade.class);
		private final ExternalPersonDataProviderFacade provider = mock(ExternalPersonDataProviderFacade.class);
		private final PersonDto person = new PersonDto();

		private Context(boolean permitted, String enabled) {
			viewModels.when(() -> de.symeda.sormas.ui.ViewModelProviders.of(ExternalPersonsView.class))
				.thenReturn(new de.symeda.sormas.ui.ViewModelProvider());
			i18n.when(() -> I18nProperties.getValidationError(de.symeda.sormas.api.i18n.Validations.invalidNationalHealthId))
				.thenReturn("Invalid national health ID");
			i18n.when(() -> I18nProperties.getString("externalPersonsInvalidRange")).thenReturn("Invalid range");
			SystemConfigurationValueFacade configuration = mock(SystemConfigurationValueFacade.class);
			facades.when(FacadeProvider::getSystemConfigurationValueFacade).thenReturn(configuration);
			facades.when(FacadeProvider::getPersonFacade).thenReturn(personFacade);
			facades.when(FacadeProvider::getExternalPersonDataProviderFacade).thenReturn(provider);
			ui.when(() -> UiUtil.permitted(UserRight.EXTERNAL_PERSON_DATA_PROVIDER_ACCESS)).thenReturn(permitted);
			org.mockito.Mockito.lenient()
				.when(configuration.getValue(ExternalPersonDataProviderFacade.EXTERNAL_PERSON_DATA_PROVIDER_ENABLED))
				.thenReturn(enabled);
			person.setNationalHealthId("health-id");
			org.mockito.Mockito.lenient().when(personFacade.getByUuid("person-uuid")).thenReturn(person);
			i18n.when(() -> I18nProperties.getString(Strings.errorNoNationalHealthIdInContext)).thenReturn(Strings.errorNoNationalHealthIdInContext);
			i18n.when(() -> I18nProperties.getString(Strings.errorExternalPersonDataLoad)).thenReturn(Strings.errorExternalPersonDataLoad);
			i18n.when(() -> I18nProperties.getString(Strings.infoNoExternalPersonData)).thenReturn(Strings.infoNoExternalPersonData);
		}

		private ExternalPersonDataComponent create() {
			return new ExternalPersonDataComponent(new PersonReferenceDto("person-uuid"));
		}

		@Override
		public void close() {
			viewModels.close();
			i18n.close();
			ui.close();
			facades.close();
		}
	}
}
