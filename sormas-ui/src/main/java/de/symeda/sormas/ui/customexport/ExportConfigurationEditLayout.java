/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.customexport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportGroupType;
import de.symeda.sormas.api.importexport.ExportPropertyMetaInfo;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class ExportConfigurationEditLayout extends VerticalLayout {

	private TextField tfName;
	private CheckBox checkBoxPublicExport;
	private Label lblDescription;
	private Map<ExportGroupType, Label> groupTypeLabels;
	private Map<ExportGroupType, List<CheckBox>> checkBoxGroups;
	private Map<CheckBox, String> checkBoxes;

	private ExportConfigurationDto exportConfiguration;

	public ExportConfigurationEditLayout(
		ExportConfigurationDto exportConfiguration,
		List<ExportPropertyMetaInfo> availableProperties,
		Consumer<ExportConfigurationDto> resultCallback,
		Runnable discardCallback) {

		this.exportConfiguration = exportConfiguration;

		addComponent(buildConfigNameAndSharedToPublicCheckboxLayout());

		lblDescription = new Label(I18nProperties.getString(Strings.infoEditExportConfiguration));
		lblDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblDescription);

		addComponent(buildSelectionButtonLayout());

		int totalCheckBoxCount = buildCheckBoxGroups(availableProperties);

		groupTypeLabels = new HashMap<>();
		for (ExportGroupType groupType : checkBoxGroups.keySet()) {
			Label groupTypeLabel = new Label(I18nProperties.getEnumCaption(groupType));
			CssStyles.style(groupTypeLabel, CssStyles.H3);
			groupTypeLabels.put(groupType, groupTypeLabel);
		}

		addComponent(buildCheckBoxLayout(totalCheckBoxCount));
		HorizontalLayout buttonLayout = buildButtonLayout(resultCallback, discardCallback);
		addComponent(buttonLayout);
		setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT);
	}

	private HorizontalLayout buildConfigNameAndSharedToPublicCheckboxLayout() {

		HorizontalLayout configNameAndSharedToPublicCheckboxLayout = new HorizontalLayout();
		configNameAndSharedToPublicCheckboxLayout.setMargin(false);

		tfName = new TextField(I18nProperties.getPrefixCaption(ExportConfigurationDto.I18N_PREFIX, Captions.ExportConfiguration_NAME));
		tfName.setWidth(350, Unit.PIXELS);
		tfName.setRequiredIndicatorVisible(true);
		if (this.exportConfiguration.getName() != null) {
			tfName.setValue(this.exportConfiguration.getName());
		}
		configNameAndSharedToPublicCheckboxLayout.addComponent(tfName);

		checkBoxPublicExport =
			new CheckBox(I18nProperties.getPrefixCaption(ExportConfigurationDto.I18N_PREFIX, Captions.ExportConfiguration_sharedToPublic));
		checkBoxPublicExport.setWidth(350, Unit.PIXELS);
		checkBoxPublicExport.setValue(this.exportConfiguration.isSharedToPublic());
		checkBoxPublicExport.setEnabled(hasManagePublicExportRights());
		checkBoxPublicExport.setStyleName(CssStyles.FORCE_CAPTION_CHECKBOX);

		HorizontalLayout publicExportLayout = new HorizontalLayout();
		publicExportLayout.setMargin(false);

		publicExportLayout.addComponent(checkBoxPublicExport);

		configNameAndSharedToPublicCheckboxLayout.addComponent(publicExportLayout);

		return configNameAndSharedToPublicCheckboxLayout;
	}

	private boolean hasManagePublicExportRights() {
		return UiUtil.permitted(UserRight.MANAGE_PUBLIC_EXPORT_CONFIGURATION)
			&& (UiUtil.getUserReference().equals(this.exportConfiguration.getUser()));
	}

	private int buildCheckBoxGroups(List<ExportPropertyMetaInfo> exportExportProperties) {

		checkBoxGroups = new HashMap<>();
		checkBoxes = new HashMap<>();
		int checkBoxCount = 0;

		for (ExportPropertyMetaInfo meta : exportExportProperties) {
			ExportGroupType groupType = meta.getExportGroupType();
			String property = meta.getPropertyId();
			if (!checkBoxGroups.containsKey(groupType)) {
				checkBoxGroups.put(groupType, new ArrayList<>());
			}

			String caption = meta.getCaption();
			CheckBox cb = new CheckBox(caption);

			if (!CollectionUtils.isEmpty(exportConfiguration.getProperties())) {
				cb.setValue(exportConfiguration.getProperties().contains(property));
			}

			checkBoxGroups.get(groupType).add(cb);
			checkBoxes.put(cb, property);
			checkBoxCount++;
		}

		return checkBoxCount;
	}

	private HorizontalLayout buildCheckBoxLayout(int totalCheckBoxCount) {

		HorizontalLayout checkBoxLayout = new HorizontalLayout();
		checkBoxLayout.setMargin(false);

		VerticalLayout firstColumnLayout = new VerticalLayout();
		firstColumnLayout.setMargin(false);
		firstColumnLayout.setSpacing(false);
		CssStyles.style(firstColumnLayout, CssStyles.HSPACE_RIGHT_3);
		checkBoxLayout.addComponent(firstColumnLayout);
		VerticalLayout secondColumnLayout = new VerticalLayout();
		secondColumnLayout.setMargin(false);
		secondColumnLayout.setSpacing(false);
		CssStyles.style(secondColumnLayout, CssStyles.HSPACE_RIGHT_3);
		checkBoxLayout.addComponent(secondColumnLayout);
		VerticalLayout thirdColumnLayout = new VerticalLayout();
		thirdColumnLayout.setMargin(false);
		thirdColumnLayout.setSpacing(false);
		checkBoxLayout.addComponent(thirdColumnLayout);

		int currentCheckBoxCount = 0;
		for (ExportGroupType groupType : ExportGroupType.values()) {
			int side = 0;
			if (groupTypeLabels.containsKey(groupType)) {
				Label groupLabel = groupTypeLabels.get(groupType);
				if (currentCheckBoxCount < (float) totalCheckBoxCount * (float) 1 / 3) {
					firstColumnLayout.addComponent(groupLabel);
				} else if (currentCheckBoxCount < (float) totalCheckBoxCount * (float) 2 / 3) {
					secondColumnLayout.addComponent(groupLabel);
					side = 1;
				} else {
					thirdColumnLayout.addComponent(groupLabel);
					side = 2;
				}

				for (CheckBox checkBox : checkBoxGroups.get(groupType)) {
					if (side == 0) {
						firstColumnLayout.addComponent(checkBox);
					} else if (side == 1) {
						secondColumnLayout.addComponent(checkBox);
					} else {
						thirdColumnLayout.addComponent(checkBox);
					}
					currentCheckBoxCount++;
				}
			}
		}

		return checkBoxLayout;
	}

	private HorizontalLayout buildSelectionButtonLayout() {

		HorizontalLayout selectionButtonLayout = new HorizontalLayout();
		selectionButtonLayout.setMargin(false);

		Button btnSelectAll = ButtonHelper.createButton(Captions.actionSelectAll, e -> {
			for (CheckBox checkBox : checkBoxes.keySet()) {
				checkBox.setValue(true);
			}
		}, ValoTheme.BUTTON_LINK);

		selectionButtonLayout.addComponent(btnSelectAll);

		Button btnDeselectAll = ButtonHelper.createButton(Captions.actionDeselectAll, e -> {
			for (CheckBox checkBox : checkBoxes.keySet()) {
				checkBox.setValue(false);
			}
		}, ValoTheme.BUTTON_LINK);

		selectionButtonLayout.addComponent(btnDeselectAll);

		return selectionButtonLayout;
	}

	private HorizontalLayout buildButtonLayout(Consumer<ExportConfigurationDto> resultCallback, Runnable discardCallback) {

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setMargin(false);

		Button btnDiscard = ButtonHelper.createButton(Captions.actionDiscard, e -> discardCallback.run());
		buttonLayout.addComponent(btnDiscard);

		Button btnSave = ButtonHelper.createButton(Captions.actionSave, e -> {
			if (validate()) {
				updateExportConfiguration();
				resultCallback.accept(exportConfiguration);
			}
		}, ValoTheme.BUTTON_PRIMARY);

		buttonLayout.addComponent(btnSave);

		return buttonLayout;
	}

	private boolean validate() {

		if (!StringUtils.isEmpty(tfName.getValue())) {
			return true;
		} else {
			new Notification(null, I18nProperties.getValidationError(Validations.exportNoNameSpecified), Type.ERROR_MESSAGE, false)
				.show(Page.getCurrent());
			return false;
		}
	}

	private void updateExportConfiguration() {

		Set<String> properties = new HashSet<>();
		for (CheckBox checkBox : checkBoxes.keySet()) {
			if (Boolean.TRUE == checkBox.getValue()) {
				properties.add(checkBoxes.get(checkBox));
			}
		}
		exportConfiguration.setProperties(properties);
		exportConfiguration.setName(tfName.getValue());
		exportConfiguration.setSharedToPublic(checkBoxPublicExport.getValue());
	}
}
