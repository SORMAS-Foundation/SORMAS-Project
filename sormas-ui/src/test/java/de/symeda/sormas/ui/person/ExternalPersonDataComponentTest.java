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
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.personaldata.PersonalDataForDisplayDto;
import de.symeda.sormas.api.personaldata.PersonalDataProviderFacade;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;

class ExternalPersonDataComponentTest {

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
			de.symeda.sormas.api.personaldata.PersonalDataSearchResponse response =
				new de.symeda.sormas.api.personaldata.PersonalDataSearchResponse();
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
			org.mockito.ArgumentCaptor<de.symeda.sormas.api.personaldata.PersonalDataSearchRequest> request =
				org.mockito.ArgumentCaptor.forClass(de.symeda.sormas.api.personaldata.PersonalDataSearchRequest.class);
			verify(context.provider).search(request.capture());
			assertEquals("Doe", request.getValue().getLastName());
			assertEquals(null, request.getValue().getNationalHealthId());
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
	void missingHealthIdShowsErrorWithoutCallingAdapter() {
		try (Context context = new Context(true, "true")) {
			context.person.setNationalHealthId(" ");
			Label message = (Label) context.create().getComponent(0);
			assertEquals(Strings.errorNoNationalHealthIdInContext, message.getValue());
			verifyNoInteractions(context.provider);
		}
	}

	@Test
	void rendersHtmlForTheSuppliedPerson() {
		try (Context context = new Context(true, "true")) {
			PersonalDataForDisplayDto display = new PersonalDataForDisplayDto();
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

	@Test
	void missingExternalDataShowsLocalizedMessage() {
		try (Context context = new Context(true, "true")) {
			assertEquals(Strings.infoNoExternalPersonData, ((Label) context.create().getComponent(0)).getValue());
		}
	}

	@Test
	void blankExternalHtmlShowsLocalizedMessage() {
		try (Context context = new Context(true, "true")) {
			PersonalDataForDisplayDto display = new PersonalDataForDisplayDto();
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

		private final MockedStatic<FacadeProvider> facades = mockStatic(FacadeProvider.class);
		private final MockedStatic<UiUtil> ui = mockStatic(UiUtil.class);
		private final MockedStatic<I18nProperties> i18n = mockStatic(I18nProperties.class, invocation -> {
			if (invocation.getMethod().getReturnType() == String.class && invocation.getArguments().length > 0) {
				return invocation.getArgument(0);
			}
			return org.mockito.Answers.RETURNS_DEFAULTS.answer(invocation);
		});
		private final PersonFacade personFacade = mock(PersonFacade.class);
		private final PersonalDataProviderFacade provider = mock(PersonalDataProviderFacade.class);
		private final PersonDto person = new PersonDto();

		private Context(boolean permitted, String enabled) {
			i18n.when(() -> I18nProperties.getValidationError(de.symeda.sormas.api.i18n.Validations.invalidNationalHealthId))
				.thenReturn("Invalid national health ID");
			i18n.when(() -> I18nProperties.getString("externalPersonsInvalidRange")).thenReturn("Invalid range");
			SystemConfigurationValueFacade configuration = mock(SystemConfigurationValueFacade.class);
			facades.when(FacadeProvider::getSystemConfigurationValueFacade).thenReturn(configuration);
			facades.when(FacadeProvider::getPersonFacade).thenReturn(personFacade);
			facades.when(FacadeProvider::getPersonalDataProviderFacade).thenReturn(provider);
			ui.when(() -> UiUtil.permitted(UserRight.EXTERNAL_PERSONAL_DATA_PROVIDER_ACCESS)).thenReturn(permitted);
			when(configuration.getValue(PersonalDataProviderFacade.EXTERNAL_PERSONAL_DATA_PROVIDER_ENABLED)).thenReturn(enabled);
			person.setNationalHealthId("health-id");
			when(personFacade.getByUuid("person-uuid")).thenReturn(person);
			i18n.when(() -> I18nProperties.getString(Strings.errorNoNationalHealthIdInContext)).thenReturn(Strings.errorNoNationalHealthIdInContext);
			i18n.when(() -> I18nProperties.getString(Strings.errorExternalPersonDataLoad)).thenReturn(Strings.errorExternalPersonDataLoad);
			i18n.when(() -> I18nProperties.getString(Strings.infoNoExternalPersonData)).thenReturn(Strings.infoNoExternalPersonData);
		}

		private ExternalPersonDataComponent create() {
			return new ExternalPersonDataComponent(new PersonReferenceDto("person-uuid"));
		}

		@Override
		public void close() {
			i18n.close();
			ui.close();
			facades.close();
		}
	}
}
