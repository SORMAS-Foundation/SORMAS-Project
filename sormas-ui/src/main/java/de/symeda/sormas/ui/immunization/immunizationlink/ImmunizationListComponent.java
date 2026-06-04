package de.symeda.sormas.ui.immunization.immunizationlink;

import java.util.function.Consumer;
import java.util.function.Supplier;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationListCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.immunization.components.panel.VaccinationStatusPanel;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class ImmunizationListComponent extends SideComponent {

	private final VaccinationStatusPanel vaccinationStatusPanel;

	public ImmunizationListComponent(
		Supplier<ImmunizationListCriteria> criteriaSupplier,
		String activeUuid,
		Consumer<Runnable> actionCallback,
		boolean isEditAllowed) {
		this(criteriaSupplier, activeUuid, actionCallback, isEditAllowed, null, null);
	}

	public ImmunizationListComponent(
		Supplier<ImmunizationListCriteria> criteriaSupplier,
		String activeUuid,
		Consumer<Runnable> actionCallback,
		boolean isEditAllowed,
		VaccinationStatusPanel vaccinationStatusPanel) {
		this(criteriaSupplier, activeUuid, actionCallback, isEditAllowed, vaccinationStatusPanel, null);
	}

	public ImmunizationListComponent(
		Supplier<ImmunizationListCriteria> criteriaSupplier,
		String activeUuid,
		Consumer<Runnable> actionCallback,
		boolean isEditAllowed,
		VaccinationStatusPanel vaccinationStatusPanel,
		Runnable quickSavedCallback) {
		super(I18nProperties.getString(Strings.entityImmunization), actionCallback);

		if (isEditAllowed) {
			addCreateButton(I18nProperties.getCaption(Captions.immunizationNewImmunization), () -> {
				ImmunizationListCriteria immunizationListCriteria = criteriaSupplier.get();
				ControllerProvider.getImmunizationController()
					.create(immunizationListCriteria.getPerson(), immunizationListCriteria.getDisease(), quickSavedCallback);
			}, UserRight.IMMUNIZATION_CREATE);
		}

		if (vaccinationStatusPanel != null) {
			this.vaccinationStatusPanel = vaccinationStatusPanel;
			addComponent(vaccinationStatusPanel);
		} else {
			this.vaccinationStatusPanel = null;
		}

		ImmunizationList immunizationList = new ImmunizationList(criteriaSupplier.get(), isEditAllowed);
		immunizationList.setActiveUuid(activeUuid);
		addComponent(immunizationList);
		immunizationList.reload();
	}

	public VaccinationStatusPanel getVaccinationStatusPanel() {
		return vaccinationStatusPanel;
	}
}
