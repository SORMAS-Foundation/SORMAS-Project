/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.configuration.featureconfiguration;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Administrator view for toggling the server-level feature flags that have no region/disease scope.
 * Backed by the typed {@link FeatureType} enum and the feature-configuration facade.
 */
public class FeatureConfigurationView extends AbstractConfigurationView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/featureConfiguration";

	/**
	 * The server features exposed for administrator toggling in this view. Add a feature here (and a
	 * {@code FeatureType.<NAME>} caption to enum.properties) to surface it.
	 */
	private static final List<FeatureType> CONFIGURABLE_FEATURES =
		Arrays.asList(FeatureType.SAMPLE_ADD_PATHOGEN_TEST, FeatureType.PATHOGEN_TEST_RESULT_REQUIRED);

	private final Map<FeatureType, CheckBox> checkBoxes = new EnumMap<>(FeatureType.class);

	public FeatureConfigurationView() {

		super(VIEW_NAME);

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setStyleName("crud-main-layout");

		Label heading = new Label(I18nProperties.getCaption(Captions.featureConfiguration));
		heading.addStyleName(CssStyles.H2);
		layout.addComponent(heading);

		Label info = new Label(I18nProperties.getString(Strings.infoFeatureConfiguration));
		CssStyles.style(info, CssStyles.VSPACE_3);
		layout.addComponent(info);

		for (FeatureType featureType : CONFIGURABLE_FEATURES) {
			CheckBox checkBox = new CheckBox(featureType.toString());
			checkBoxes.put(featureType, checkBox);
			layout.addComponent(checkBox);
		}

		layout.addComponent(ButtonHelper.createButton(Captions.actionSave, e -> save(), CssStyles.FORCE_CAPTION));

		addComponent(layout);
	}

	private void save() {

		checkBoxes.forEach(
			(featureType, checkBox) -> FacadeProvider.getFeatureConfigurationFacade().setServerFeatureEnabled(featureType, checkBox.getValue()));

		loadFeatureStates();

		VaadinUiUtil.showSimplePopupWindow(
			I18nProperties.getString(Strings.headingFeatureConfiguration),
			I18nProperties.getString(Strings.messageFeatureConfigurationSaved));
	}

	private void loadFeatureStates() {
		checkBoxes
			.forEach((featureType, checkBox) -> checkBox.setValue(FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(featureType)));
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {

		super.enter(event);
		loadFeatureStates();
	}
}
