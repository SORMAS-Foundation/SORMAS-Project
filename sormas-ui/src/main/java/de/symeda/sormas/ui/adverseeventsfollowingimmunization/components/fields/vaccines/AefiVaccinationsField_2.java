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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CustomField;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

@SuppressWarnings({
	"serial",
	"rawtypes" })
public class AefiVaccinationsField_2 extends CustomField<Collection> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private VerticalLayout mainLayout;
	private Label captionLabel;
	private Button addButton;
	private Grid<VaccinationDto> vaccinationGrid;
	private List<VaccinationDto> value = new ArrayList<>();
	private AefiDto aefiDto;
	private VaccinationDto primarySuspectVaccination;
	protected UiFieldAccessCheckers fieldAccessCheckers;

	public AefiVaccinationsField_2(UiFieldAccessCheckers fieldAccessCheckers) {
		this.fieldAccessCheckers = fieldAccessCheckers;

		getContent();
		//setValue(value);
	}

	public void initializeGrid() {

		vaccinationGrid = new Grid<>();
		vaccinationGrid.setSizeFull();
		vaccinationGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
		/* vaccinationGrid.setHeightMode(HeightMode.ROW); */

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

		vaccinationGrid.setStyleGenerator(vaccinationDto -> {
			if (primarySuspectVaccination != null) {
				return vaccinationDto.getUuid().equals(primarySuspectVaccination.getUuid()) ? CssStyles.GRID_ROW_SELECTED : null;
			}
			return null;
		});
	}

	@Override
	protected Component initContent() {
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(false);
		mainLayout.setMargin(false);

		HorizontalLayout headerLayout = new HorizontalLayout();
		{
			headerLayout.setWidth(100, Unit.PERCENTAGE);

			captionLabel = new Label(getCaption());
			captionLabel.setSizeUndefined();
			headerLayout.addComponent(captionLabel);
			headerLayout.setComponentAlignment(captionLabel, Alignment.BOTTOM_LEFT);
			headerLayout.setExpandRatio(captionLabel, 0);

			addButton = ButtonHelper.createButton(Captions.actionAefiSelectPrimarySuspectVaccination, (event) -> {
				ControllerProvider.getAefiController().selectPrimarySuspectVaccination(aefiDto, this::selectPrimarySuspectVaccination);
			}, ValoTheme.BUTTON_LINK);
			headerLayout.addComponent(addButton);
			headerLayout.setComponentAlignment(addButton, Alignment.BOTTOM_RIGHT);
			headerLayout.setExpandRatio(addButton, 1);
		}
		mainLayout.addComponent(headerLayout);

		initializeGrid();
		mainLayout.addComponent(vaccinationGrid);

		return mainLayout;
	}

	@Override
	public Class<? extends Collection> getType() {
		return Collection.class;
	}

	@Override
	public void setPropertyDataSource(Property newDataSource) {
		super.setPropertyDataSource(newDataSource);
	}

	/*
	 * @Override
	 * protected void setValue(Collection newFieldValue, boolean repaintIsNotNeeded, boolean ignoreReadOnly)
	 * throws ReadOnlyException, Converter.ConversionException, Validator.InvalidValueException {
	 * super.setValue(newFieldValue, repaintIsNotNeeded, ignoreReadOnly);
	 * value = new ArrayList<>(newFieldValue);
	 * vaccinationGrid.setItems(newFieldValue);
	 * fireValueChange(repaintIsNotNeeded);
	 * }
	 */

	@Override
	public void setValue(Collection newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		value = new ArrayList<>(newFieldValue);
		vaccinationGrid.setItems(newFieldValue);

		super.setValue(newFieldValue);
	}

	/*
	 * @Override
	 * protected void doSetValue(Collection<VaccinationDto> collection) {
	 * value = new ArrayList<>(collection);
	 * vaccinationGrid.setItems(collection);
	 * }
	 */

	@Override
	public Collection getValue() {
		return value;
	}

	public void setAefiDto(AefiDto aefiDto) {
		this.aefiDto = aefiDto;
	}

	public void setPrimarySuspectVaccination(VaccinationDto primarySuspectVaccination) {
		this.primarySuspectVaccination = primarySuspectVaccination;
	}

	public void selectPrimarySuspectVaccination(VaccinationDto vaccinationDto) {
		primarySuspectVaccination = vaccinationDto;
		vaccinationGrid.select(primarySuspectVaccination);
	}
}
