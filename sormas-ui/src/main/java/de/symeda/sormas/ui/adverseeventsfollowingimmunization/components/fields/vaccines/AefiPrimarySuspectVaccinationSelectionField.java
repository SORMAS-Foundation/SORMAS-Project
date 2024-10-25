/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.fields.vaccines;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class AefiPrimarySuspectVaccinationSelectionField extends CustomField<VaccinationDto> {

	private VerticalLayout mainLayout;
	private Grid<VaccinationDto> vaccinationGrid;
	private final String infoAefiSelectPrimarySuspectVaccine;
	private Consumer<Boolean> selectionChangeCallback;
	private List<VaccinationDto> vaccinationDtoList;
	private VaccinationDto primarySuspectVaccine;

	public AefiPrimarySuspectVaccinationSelectionField(List<VaccinationDto> vaccinationDtoList, VaccinationDto primarySuspectVaccine) {
		this.vaccinationDtoList = vaccinationDtoList;
		this.primarySuspectVaccine = primarySuspectVaccine;
		this.infoAefiSelectPrimarySuspectVaccine = I18nProperties.getString(Strings.infoAefiSelectPrimarySuspectVaccine);

		initializeGrid();
	}

	private void addInfoComponent() {
		mainLayout.addComponent(VaadinUiUtil.createInfoComponent(infoAefiSelectPrimarySuspectVaccine));
	}

	public void initializeGrid() {

		vaccinationGrid = new Grid<>();
		vaccinationGrid.setSizeFull();
		vaccinationGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
		vaccinationGrid.setHeightByRows(5);
		vaccinationGrid.setHeightMode(HeightMode.ROW);

		vaccinationGrid.setItems(vaccinationDtoList);

		if (primarySuspectVaccine != null) {
			vaccinationGrid.select(primarySuspectVaccine);
		}

		vaccinationGrid.addColumn(vaccinationDto -> DateFormatHelper.formatDate(vaccinationDto.getVaccinationDate()))
			.setCaption(I18nProperties.getPrefixCaption(VaccinationDto.I18N_PREFIX, VaccinationDto.VACCINATION_DATE));

		vaccinationGrid
			.addColumn(
				vaccinationDto -> Vaccine.OTHER.equals(vaccinationDto.getVaccineName())
					? vaccinationDto.getOtherVaccineName()
					: vaccinationDto.getVaccineName())
			.setCaption(I18nProperties.getPrefixCaption(VaccinationDto.I18N_PREFIX, VaccinationDto.VACCINE_NAME));

		vaccinationGrid
			.addColumn(
				vaccinationDto -> VaccineManufacturer.OTHER.equals(vaccinationDto.getVaccineManufacturer())
					? vaccinationDto.getOtherVaccineManufacturer()
					: vaccinationDto.getVaccineManufacturer())
			.setCaption(I18nProperties.getPrefixCaption(VaccinationDto.I18N_PREFIX, VaccinationDto.VACCINE_MANUFACTURER));

		vaccinationGrid.addColumn(VaccinationDto::getVaccineType)
			.setCaption(I18nProperties.getPrefixCaption(VaccinationDto.I18N_PREFIX, VaccinationDto.VACCINE_TYPE));

		vaccinationGrid.addColumn(VaccinationDto::getVaccineDose)
			.setCaption(I18nProperties.getPrefixCaption(VaccinationDto.I18N_PREFIX, VaccinationDto.VACCINE_DOSE));

		vaccinationGrid.addSelectionListener(e -> {

			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getAllSelectedItems().isEmpty());
			}
		});
	}

	@Override
	protected Component initContent() {

		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(false);
		mainLayout.setSizeUndefined();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(mainLayout, CssStyles.VSPACE_2);

		addInfoComponent();

		mainLayout.addComponent(vaccinationGrid);

		return mainLayout;
	}

	@Override
	protected void doSetValue(VaccinationDto vaccinationDto) {
		if (vaccinationDto != null) {
			vaccinationGrid.select(vaccinationDto);
		}
	}

	@Override
	public VaccinationDto getValue() {
		if (vaccinationGrid != null) {
			VaccinationDto value = vaccinationGrid.getSelectedItems().stream().findFirst().orElse(null);
			return value;
		}

		return null;
	}

	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}

	public Grid<VaccinationDto> getVaccinationGrid() {
		return vaccinationGrid;
	}
}
