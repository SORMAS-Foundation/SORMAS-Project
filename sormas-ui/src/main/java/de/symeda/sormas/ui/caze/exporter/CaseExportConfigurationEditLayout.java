package de.symeda.sormas.ui.caze.exporter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.symeda.sormas.ui.utils.ButtonHelper;
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

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportGroup;
import de.symeda.sormas.api.importexport.ExportGroupType;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.importexport.ExportType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class CaseExportConfigurationEditLayout extends VerticalLayout {

	private TextField tfName;
	private Label lblDescription;
	private Map<ExportGroupType, Label> groupTypeLabels;
	private Map<ExportGroupType, List<CheckBox>> checkBoxGroups;
	private Map<CheckBox, String> checkBoxes;

	private ExportConfigurationDto exportConfiguration;

	public CaseExportConfigurationEditLayout(ExportConfigurationDto exportConfiguration, Consumer<ExportConfigurationDto> resultCallback, Runnable discardCallback) {
		if (exportConfiguration == null) {
			exportConfiguration = ExportConfigurationDto.build(UserProvider.getCurrent().getUserReference());
			exportConfiguration.setExportType(ExportType.CASE);
		}
		this.exportConfiguration = exportConfiguration;

		tfName = new TextField(I18nProperties.getPrefixCaption(ExportConfigurationDto.I18N_PREFIX, ExportConfigurationDto.NAME));
		tfName.setWidth(350, Unit.PIXELS);
		tfName.setRequiredIndicatorVisible(true);
		if (this.exportConfiguration.getName() != null) {
			tfName.setValue(this.exportConfiguration.getName());
		}
		addComponent(tfName);

		lblDescription = new Label(I18nProperties.getString(Strings.infoEditExportConfiguration));
		lblDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblDescription);

		addComponent(buildSelectionButtonLayout());

		int totalCheckBoxCount = buildCheckBoxGroups();

		groupTypeLabels = new HashMap<>();
		for (ExportGroupType groupType : checkBoxGroups.keySet()) {
			if (ExportGroupType.CASE_MANAGEMENT == groupType && !UserProvider.getCurrent().hasUserRight(UserRight.CASE_MANAGEMENT_ACCESS)) {
				continue;
			}
			Label groupTypeLabel = new Label(I18nProperties.getEnumCaption(groupType));
			CssStyles.style(groupTypeLabel, CssStyles.H3);
			groupTypeLabels.put(groupType, groupTypeLabel);
		}

		addComponent(buildCheckBoxLayout(totalCheckBoxCount));
		HorizontalLayout buttonLayout = buildButtonLayout(resultCallback, discardCallback);
		addComponent(buttonLayout);
		setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT);
	}

	private int buildCheckBoxGroups() {
		checkBoxGroups = new HashMap<>();
		checkBoxes = new HashMap<>();
		int checkBoxCount = 0;

		List<Method> readMethods = new ArrayList<Method>();
		readMethods.addAll(Arrays.stream(CaseExportDto.class.getDeclaredMethods())
				.filter(m -> (m.getName().startsWith("get") || m.getName().startsWith("is")) 
						&& m.isAnnotationPresent(ExportGroup.class))
				.sorted((a, b) -> Integer.compare(a.getAnnotationsByType(Order.class)[0].value(), 
						b.getAnnotationsByType(Order.class)[0].value()))
				.collect(Collectors.toList()));

		Set<String> combinedProperties = new HashSet<>();
		for (Method method : readMethods) {
			ExportGroupType groupType = method.getAnnotation(ExportGroup.class).value();

			if (ExportGroupType.CASE_MANAGEMENT == groupType && !UserProvider.getCurrent().hasUserRight(UserRight.CASE_MANAGEMENT_ACCESS)) {
				continue;
			}

			if (!checkBoxGroups.containsKey(groupType)) {
				checkBoxGroups.put(groupType, new ArrayList<>());
			}

			String property = method.getAnnotation(ExportProperty.class).value();
			String caption = I18nProperties.getPrefixCaption(CaseExportDto.I18N_PREFIX, property,
					I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, property,
							I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, property,
									I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, property,
											I18nProperties.getPrefixCaption(EpiDataDto.I18N_PREFIX, property,
													I18nProperties.getPrefixCaption(HospitalizationDto.I18N_PREFIX, property,
															I18nProperties.getPrefixCaption(HealthConditionsDto.I18N_PREFIX, property)))))));

			if (method.getAnnotation(ExportProperty.class).combined()) {
				if (combinedProperties.contains(property)) {
					continue;
				} else {
					combinedProperties.add(property);
				}
			}

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
			if (ExportGroupType.CASE_MANAGEMENT == groupType && !UserProvider.getCurrent().hasUserRight(UserRight.CASE_MANAGEMENT_ACCESS)) {
				continue;
			}

			int side = 0;
			if (currentCheckBoxCount < (float) totalCheckBoxCount * (float) 1/3) {
				firstColumnLayout.addComponent(groupTypeLabels.get(groupType));
			} else if (currentCheckBoxCount < (float) totalCheckBoxCount * (float) 2/3){
				secondColumnLayout.addComponent(groupTypeLabels.get(groupType));
				side = 1;
			} else {
				thirdColumnLayout.addComponent(groupTypeLabels.get(groupType));
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
			new Notification(null, I18nProperties.getValidationError(Validations.exportNoNameSpecified), Type.ERROR_MESSAGE, false).show(Page.getCurrent());
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
	}

}
