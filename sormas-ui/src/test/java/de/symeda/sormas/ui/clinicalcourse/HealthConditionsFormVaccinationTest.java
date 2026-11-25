package de.symeda.sormas.ui.clinicalcourse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.function.Predicate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.immunization.ImmunizationController;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

@SuppressWarnings("unchecked")
class HealthConditionsFormVaccinationTest extends AbstractUiBeanTest {

	private final PersonReferenceDto person = new PersonReferenceDto("PERSON-UUID");
	private final CaseReferenceDto origin = new CaseReferenceDto("CASE-UUID");
	private final ImmunizationController immunizationController = mock(ImmunizationController.class);
	private MockedStatic<ControllerProvider> controllers;
	private HealthConditionsForm form;

	@Override
	public void init() {
		super.init();
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		mockCurrentUiWithFullDataAccess();

		controllers = Mockito.mockStatic(ControllerProvider.class);
		controllers.when(ControllerProvider::getImmunizationController).thenReturn(immunizationController);

		form = new HealthConditionsForm(
			Disease.DENGUE,
			FieldVisibilityCheckers.withDisease(Disease.DENGUE),
			UiFieldAccessCheckers.getNoop(),
			person,
			origin);
		form.setValue(HealthConditionsDto.build());
	}

	@AfterEach
	void closeControllers() {
		controllers.close();
	}

	@Test
	void savesTheCaseBeforeOpeningThePopup() {

		Predicate<Runnable> saver = mock(Predicate.class);
		Mockito.when(saver.test(any())).thenAnswer(call -> {
			call.<Runnable> getArgument(0).run();
			return true;
		});
		form.setOwnerFormSaver(saver);

		answer(YesNoUnknown.YES);

		InOrder order = inOrder(saver, immunizationController);
		order.verify(saver).test(any());
		order.verify(immunizationController).create(eq(person), eq(Disease.DENGUE), isNull(), notNull(), eq(origin));
	}

	@Test
	void doesNotOpenThePopupWhenTheCaseCannotBeSaved() {

		Predicate<Runnable> saver = mock(Predicate.class);
		Mockito.when(saver.test(any())).thenReturn(false);
		form.setOwnerFormSaver(saver);

		answer(YesNoUnknown.YES);

		verify(saver).test(any());
		verify(immunizationController, never()).create(any(), any(), any(), any(), any());
		assertNull(vaccinatedField().getNullableValue());
	}

	@Test
	void keepsTodaysBehaviourWithoutACommitter() {

		answer(YesNoUnknown.YES);

		verify(immunizationController).create(eq(person), eq(Disease.DENGUE), isNull(), any(), eq(origin));
	}

	@Test
	void answeringNoNeitherSavesNorOpensThePopup() {

		Predicate<Runnable> saver = mock(Predicate.class);
		form.setOwnerFormSaver(saver);

		answer(YesNoUnknown.NO);

		verify(saver, never()).test(any());
		verify(immunizationController, never()).create(any(), any(), any(), any(), any());
		assertEquals(YesNoUnknown.NO, vaccinatedField().getNullableValue());
	}

	@Test
	void loadingTheFormDoesNotOpenThePopup() {

		HealthConditionsForm loaded = new HealthConditionsForm(
			Disease.DENGUE,
			FieldVisibilityCheckers.withDisease(Disease.DENGUE),
			UiFieldAccessCheckers.getNoop(),
			person,
			origin);
		Predicate<Runnable> saver = mock(Predicate.class);
		loaded.setOwnerFormSaver(saver);
		HealthConditionsDto vaccinated = HealthConditionsDto.build();
		vaccinated.setVaccinatedAgainstMosquitoBorneViruses(YesNoUnknown.YES);

		loaded.setValue(vaccinated);

		verify(saver, never()).test(any());
		verify(immunizationController, never()).create(any(), any(), any(), any(), any());
	}

	@Test
	void discardingBackToASavedYesNeitherSavesNorOpensThePopup() {

		HealthConditionsDto savedYes = HealthConditionsDto.build();
		savedYes.setVaccinatedAgainstMosquitoBorneViruses(YesNoUnknown.YES);
		form.setValue(savedYes);
		Predicate<Runnable> saver = mock(Predicate.class);
		form.setOwnerFormSaver(saver);
		answer(YesNoUnknown.NO);

		form.discard();

		assertEquals(YesNoUnknown.YES, vaccinatedField().getNullableValue());
		verify(saver, never()).test(any());
		verify(immunizationController, never()).create(any(), any(), any(), any(), any());
	}

	private void answer(YesNoUnknown answer) {
		vaccinatedField().setNullableValue(answer);
	}

	private NullableOptionGroup vaccinatedField() {
		return form.getField(HealthConditionsDto.VACCINATED_AGAINST_MOSQUITO_BORNE_VIRUSES);
	}
}
