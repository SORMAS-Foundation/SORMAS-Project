package de.symeda.sormas.ui.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.personaldata.PersonalDataProviderFacade;
import de.symeda.sormas.api.personaldata.PersonalDataSummaryDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UiUtil;

class ExternalPersonDataLookupTest {

	@Test
	void requiresConfiguredAdapterAndPermission() {
		try (Context context = new Context()) {
			context.ui.when(() -> UiUtil.permitted(UserRight.EXTERNAL_PERSONAL_DATA_PROVIDER_ACCESS)).thenReturn(true);
			for (String missing : new String[] {
				null,
				"",
				" " }) {
				when(context.configuration.getValue(PersonalDataProviderFacade.PERSONAL_DATA_PROVIDER_ADAPTER_JNDI_CONFIG_KEY)).thenReturn(missing);
				assertFalse(ExternalPersonDataLookup.isAvailable());
			}
			when(context.configuration.getValue(PersonalDataProviderFacade.PERSONAL_DATA_PROVIDER_ADAPTER_JNDI_CONFIG_KEY))
				.thenReturn("adapter-jndi");
			assertTrue(ExternalPersonDataLookup.isAvailable());
			context.ui.when(() -> UiUtil.permitted(UserRight.EXTERNAL_PERSONAL_DATA_PROVIDER_ACCESS)).thenReturn(false);
			assertFalse(ExternalPersonDataLookup.isAvailable());
			verifyNoInteractions(context.provider);
		}
	}

	@Test
	void comparesCurrentValuesAndAppliesOnlySelectedFieldsToCurrentForm() {
		try (Context context = new Context()) {
			ExternalPersonDataLookup lookup = context.create();
			verify(context.provider).findDataByNationalHealthId("health-id");
			GridLayout comparison = (GridLayout) lookup.getComponent(0);
			assertEquals("Current first", ((Label) comparison.getComponent(1, 1)).getValue());
			assertEquals("External first", ((Label) comparison.getComponent(2, 1)).getValue());
			assertEquals("Current first", context.first.getValue());
			Button apply = context.apply(lookup);
			assertFalse(apply.isEnabled());
			context.select(lookup, 1).setValue(true);
			context.select(lookup, 3).setValue(true);
			assertTrue(apply.isEnabled());
			apply.click();
			assertEquals("External first", context.first.getValue());
			assertEquals("Current last", context.last.getValue());
			assertEquals(2000, context.year.getValue());
			assertEquals(2, context.month.getValue());
			assertEquals(29, context.day.getValue());
		}
	}

	@Test
	void cancelLeavesAllValuesUnchanged() {
		try (Context context = new Context()) {
			ExternalPersonDataLookup lookup = context.create();
			context.select(lookup, 2).setValue(true);
			((Button) ((HorizontalLayout) lookup.getComponent(1)).getComponent(1)).click();
			assertEquals("Current first", context.first.getValue());
			assertEquals("Current last", context.last.getValue());
			assertEquals(1999, context.year.getValue());
		}
	}

	@Test
	void emptyProviderValuesCannotOverwriteExistingValues() {
		try (Context context = new Context()) {
			context.summary.setFirstName(" ");
			context.summary.setLastName(null);
			context.summary.setBirthDate(null);
			ExternalPersonDataLookup lookup = context.create();
			for (int row = 1; row <= 3; row++) {
				assertFalse(context.select(lookup, row).isEnabled());
			}
			assertFalse(context.apply(lookup).isEnabled());
		}
	}

	@Test
	void respectsReadOnlyAndDisabledFieldsIncludingChangesAfterOpening() {
		try (Context context = new Context()) {
			context.first.setReadOnly(true);
			context.day.setEnabled(false);
			ExternalPersonDataLookup lookup = context.create();
			assertFalse(context.select(lookup, 1).isEnabled());
			assertFalse(context.select(lookup, 3).isEnabled());
			context.select(lookup, 2).setValue(true);
			context.last.setReadOnly(true);
			context.apply(lookup).click();
			assertEquals("Current last", context.last.getValue());
		}
	}

	@Test
	void additionalValuesUseSuppliedLabelsAndHaveNoOverwriteControls() {
		try (Context context = new Context()) {
			context.summary.setAdditionalFields(Map.of("Translated label", "<b>Literal value</b>"));
			ExternalPersonDataLookup lookup = context.create();
			GridLayout additional = (GridLayout) lookup.getComponent(1);
			assertEquals(2, additional.getComponentCount());
			assertEquals("Translated label", ((Label) additional.getComponent(0, 0)).getValue());
			assertEquals("<b>Literal value</b>", ((Label) additional.getComponent(1, 0)).getValue());
			assertEquals(com.vaadin.shared.ui.ContentMode.TEXT, ((Label) additional.getComponent(1, 0)).getContentMode());
		}
	}

	@Test
	void blankHealthIdDoesNotCallProvider() {
		try (Context context = new Context()) {
			ExternalPersonDataLookup lookup =
				new ExternalPersonDataLookup(" ", context.first, context.last, context.year, context.month, context.day);
			assertEquals(Strings.errorNoNationalHealthIdInContext, ((Label) lookup.getComponent(0)).getValue());
			verifyNoInteractions(context.provider);
		}
	}

	@Test
	void handlesMissingSummaryAndProviderFailureWithoutOverwriteControls() {
		try (Context context = new Context()) {
			when(context.provider.findDataByNationalHealthId("health-id")).thenReturn(null);
			ExternalPersonDataLookup missing = context.create();
			assertEquals(1, missing.getComponentCount());
			assertEquals(Strings.infoNoExternalPersonData, ((Label) missing.getComponent(0)).getValue());
			when(context.provider.findDataByNationalHealthId("health-id")).thenThrow(new IllegalStateException("Unavailable"));
			ExternalPersonDataLookup failure = context.create();
			assertEquals(1, failure.getComponentCount());
			assertEquals(Strings.errorExternalPersonDataLoad, ((Label) failure.getComponent(0)).getValue());
			assertEquals("Current first", context.first.getValue());
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
		private final PersonalDataProviderFacade provider = mock(PersonalDataProviderFacade.class);
		private final SystemConfigurationValueFacade configuration = mock(SystemConfigurationValueFacade.class);
		private final PersonalDataSummaryDto summary = new PersonalDataSummaryDto();
		private final TextField first = new TextField();
		private final TextField last = new TextField();
		private final ComboBox year = new ComboBox();
		private final ComboBox month = new ComboBox();
		private final ComboBox day = new ComboBox();

		private Context() {
			facades.when(FacadeProvider::getPersonalDataProviderFacade).thenReturn(provider);
			facades.when(FacadeProvider::getSystemConfigurationValueFacade).thenReturn(configuration);
			i18n.when(I18nProperties::getUserLanguage).thenReturn(de.symeda.sormas.api.Language.EN);
			first.setValue("Current first");
			last.setValue("Current last");
			year.addItems(1999, 2000);
			month.addItems(1, 2);
			day.addItems(DateHelper.getDaysInMonth(1, 1999));
			year.setValue(1999);
			month.setValue(1);
			day.setValue(31);
			// Match the form listeners that rebuild the available days on year/month changes.
			year.addValueChangeListener(event -> updateDays());
			month.addValueChangeListener(event -> updateDays());
			summary.setFirstName("External first");
			summary.setLastName("External last");
			summary.setBirthDate(LocalDate.of(2000, 2, 29));
			when(provider.findDataByNationalHealthId("health-id")).thenReturn(summary);
		}

		private void updateDays() {
			day.removeAllItems();
			day.addItems(DateHelper.getDaysInMonth((Integer) month.getValue(), (Integer) year.getValue()));
		}

		private ExternalPersonDataLookup create() {
			return new ExternalPersonDataLookup("health-id", first, last, year, month, day);
		}

		private CheckBox select(ExternalPersonDataLookup lookup, int row) {
			return (CheckBox) ((GridLayout) lookup.getComponent(0)).getComponent(3, row);
		}

		private Button apply(ExternalPersonDataLookup lookup) {
			return (Button) ((HorizontalLayout) lookup.getComponent(lookup.getComponentCount() - 1)).getComponent(0);
		}

		@Override
		public void close() {
			i18n.close();
			ui.close();
			facades.close();
		}
	}
}
