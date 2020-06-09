/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.ui.configuration.outbreak;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class OutbreakOverviewGrid extends Grid implements ItemClickListener {

	private static final String REGION = Captions.region;

	private UserDto user;

	public OutbreakOverviewGrid() {
		super();
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);

		user = UserProvider.getCurrent().getUser();

		addColumn(REGION, RegionReferenceDto.class).setMaximumWidth(200);
		getColumn(REGION).setHeaderCaption(I18nProperties.getCaption(Captions.region));

		for (Disease disease : FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true)) {
			addColumn(disease, OutbreakRegionConfiguration.class).setMaximumWidth(200)
				.setHeaderCaption(disease.toShortString())
				.setConverter(new Converter<String, OutbreakRegionConfiguration>() {

					@Override
					public OutbreakRegionConfiguration convertToModel(
						String value,
						Class<? extends OutbreakRegionConfiguration> targetType,
						Locale locale)
						throws ConversionException {
						throw new UnsupportedOperationException("Can only convert from OutbreakRegionConfiguration to String");
					}

					@Override
					public String convertToPresentation(OutbreakRegionConfiguration value, Class<? extends String> targetType, Locale locale)
						throws ConversionException {

						boolean styleAsButton = UserProvider.getCurrent().hasUserRight(UserRight.OUTBREAK_CONFIGURE_ALL)
							|| (UserProvider.getCurrent().hasUserRight(UserRight.OUTBREAK_CONFIGURE_RESTRICTED)
								&& DataHelper.equal(UserProvider.getCurrent().getUser().getRegion(), value.getRegion()));
						boolean moreThanHalfOfDistricts = value.getAffectedDistricts().size() >= value.getTotalDistricts() / 2.0f;

						String styles;
						if (styleAsButton) {
							if (moreThanHalfOfDistricts) {
								styles = CssStyles.buildVaadinStyle(CssStyles.VAADIN_BUTTON, CssStyles.BUTTON_CRITICAL);
							} else if (!value.getAffectedDistricts().isEmpty()) {
								styles = CssStyles.buildVaadinStyle(CssStyles.VAADIN_BUTTON, CssStyles.BUTTON_WARNING);
							} else {
								styles = CssStyles.buildVaadinStyle(CssStyles.VAADIN_BUTTON);
							}

						} else {
							if (moreThanHalfOfDistricts) {
								styles = CssStyles.buildVaadinStyle(CssStyles.VAADIN_LABEL, CssStyles.LABEL_CRITICAL);
							} else if (!value.getAffectedDistricts().isEmpty()) {
								styles = CssStyles.buildVaadinStyle(CssStyles.VAADIN_LABEL, CssStyles.LABEL_WARNING);
							} else {
								styles = CssStyles.buildVaadinStyle(CssStyles.VAADIN_LABEL);
							}
						}
						return LayoutUtil.divCss(styles, value.toString());
					}

					@Override
					public Class<OutbreakRegionConfiguration> getModelType() {
						return OutbreakRegionConfiguration.class;
					}

					@Override
					public Class<String> getPresentationType() {
						return String.class;
					}

				})
				.setRenderer(new HtmlRenderer());
		}

		setCellDescriptionGenerator(cell -> getCellDescription(cell));

		setCellStyleGenerator(new CellStyleGenerator() {

			@Override
			public String getStyle(CellReference cell) {
				if (cell.getProperty().getValue() instanceof OutbreakRegionConfiguration) {
					return CssStyles.ALIGN_CENTER;
				}
				return null;
			}
		});

		addItemClickListener(this);
	}

	private String getCellDescription(CellReference cell) {
		Item item = cell.getItem();

		if (cell.getPropertyId() == REGION) {
			return "";
		}

		Set<DistrictReferenceDto> affectedDistricts =
			((OutbreakRegionConfiguration) item.getItemProperty((Disease) cell.getPropertyId()).getValue()).getAffectedDistricts();

		if (affectedDistricts.isEmpty()) {
			return I18nProperties.getCaption(Captions.outbreakNoOutbreak);
		}

		StringBuilder affectedDistrictsStringBuilder = new StringBuilder();
		affectedDistrictsStringBuilder.append(I18nProperties.getCaption(Captions.outbreakAffectedDistricts)).append(": ");

		int index = 0;
		for (DistrictReferenceDto affectedDistrict : affectedDistricts) {
			affectedDistrictsStringBuilder.append(affectedDistrict.toString());
			if (index < affectedDistricts.size() - 1) {
				affectedDistrictsStringBuilder.append(", ");
			}
			index++;
		}

		return affectedDistrictsStringBuilder.toString();
	}

	public void reload() {

		Container.Indexed container = getContainerDataSource();
		container.removeAllItems();

		// Initially set all columns to their default value
		for (RegionReferenceDto region : FacadeProvider.getRegionFacade().getAllActiveAsReference()) {
			addItem(region);
		}

		// Alter cells with regions and diseases that actually have an outbreak
		OutbreakCriteria criteria = new OutbreakCriteria().active(true);
		criteria.disease(UserProvider.getCurrent().getUser().getLimitedDisease());
		List<OutbreakDto> activeOutbreaks = FacadeProvider.getOutbreakFacade().getActive(criteria);

		for (OutbreakDto outbreak : activeOutbreaks) {
			DistrictReferenceDto outbreakDistrict = outbreak.getDistrict();
			RegionReferenceDto outbreakRegion = FacadeProvider.getDistrictFacade().getDistrictByUuid(outbreakDistrict.getUuid()).getRegion();
			Disease outbreakDisease = outbreak.getDisease();

			// Only show the Outbreak if its Disease is active on the system
			if (FacadeProvider.getDiseaseConfigurationFacade().isActiveDisease(outbreakDisease)) {
				Item item = container.getItem(outbreakRegion);
				if (item != null) { // region may be no longer active
					OutbreakRegionConfiguration configuration = (OutbreakRegionConfiguration) item.getItemProperty(outbreakDisease).getValue();
					configuration.getAffectedDistricts().add(outbreakDistrict);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void addItem(RegionReferenceDto region) {

		int totalDistricts = FacadeProvider.getDistrictFacade().getCountByRegion(region.getUuid());
		Item item = getContainerDataSource().addItem(region);
		item.getItemProperty(REGION).setValue(region);
		for (Disease disease : FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true)) {
			item.getItemProperty(disease).setValue(new OutbreakRegionConfiguration(disease, region, totalDistricts, new HashSet<>()));
		}
	}

	@Override
	public void itemClick(ItemClickEvent event) {
		Item clickedItem = event.getItem();

		if (event.getPropertyId() == REGION) {
			return;
		}

		// Open the outbreak configuration window for the clicked row when
		// a) the user is allowed to configure all existing outbreaks or
		// b) the user is allowed to configure outbreaks in his assigned region and has clicked the respective row
		if (UserProvider.getCurrent().hasUserRight(UserRight.OUTBREAK_CONFIGURE_ALL)) {
			ControllerProvider.getOutbreakController()
				.openOutbreakConfigurationWindow(
					(Disease) event.getPropertyId(),
					(OutbreakRegionConfiguration) clickedItem.getItemProperty((Disease) event.getPropertyId()).getValue());
		} else if (UserProvider.getCurrent().hasUserRight(UserRight.OUTBREAK_CONFIGURE_RESTRICTED)) {
			if (user.getRegion().equals(clickedItem.getItemProperty(REGION).getValue())) {
				ControllerProvider.getOutbreakController()
					.openOutbreakConfigurationWindow(
						(Disease) event.getPropertyId(),
						(OutbreakRegionConfiguration) clickedItem.getItemProperty((Disease) event.getPropertyId()).getValue());
			}
		} else {
			return;
		}
	}
}
