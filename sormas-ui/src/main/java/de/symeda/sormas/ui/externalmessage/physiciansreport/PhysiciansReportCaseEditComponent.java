/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.externalmessage.physiciansreport;

import static de.symeda.sormas.ui.externalmessage.processing.ExternalMessageProcessingUIHelper.addProcessedInMeantimeCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.caze.CaseDataForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DirtyCheckPopup;
import de.symeda.sormas.ui.utils.ViewMode;

public class PhysiciansReportCaseEditComponent extends CommitDiscardWrapperComponent<VerticalLayout> {

	private static final long serialVersionUID = 1452145334216485391L;

	private final SubMenu tabsMenu;
	private final Button backButton;
	private final Button nextButton;
	private final Button saveAndOpenCaseButton;
	private final List<TabConfig> tabConfigs;
	private final ViewMode viewMode = ViewMode.NORMAL;
	private final CaseDataDto caze;
	private final ExternalMessageDto externalMessage;
	private CommitDiscardWrapperComponent<?> activeTabComponent;
	private int activeTabIndex;

	public PhysiciansReportCaseEditComponent(CaseDataDto caze, ExternalMessageDto externalMessage) {
		super(createLayout());
		this.caze = caze;
		this.externalMessage = externalMessage;

		setMargin(new MarginInfo(false, true));

		tabsMenu = new SubMenu();
		tabConfigs = createTabConfigs();

		// save on active tab on commit
		setPreCommitListener(callback -> {
			if (activeTabComponent.commitAndHandle()) {
				callback.run();
			}
		});

		// setup buttons
		backButton = ButtonHelper.createButton(Captions.actionBack, (e) -> {
			if (activeTabIndex > 0) {
				String previousTab = tabConfigs.get(activeTabIndex - 1).captionTag;

				if (activeTabComponent.isDirty()) {
					DirtyCheckPopup.show(activeTabComponent, () -> setActiveTab(previousTab));
				} else {
					setActiveTab(previousTab);
				}
			}
		}, ValoTheme.BUTTON_PRIMARY);
		getButtonsPanel().addComponent(backButton, 0);

		nextButton = ButtonHelper.createButton(Captions.actionNext, (b) -> {
			if (activeTabIndex <= tabConfigs.size() - 1) {
				boolean committed = activeTabComponent.commitAndHandle();
				if (committed) {
					setActiveTab(tabConfigs.get(activeTabIndex + 1).captionTag);
				}
			}
		}, ValoTheme.BUTTON_PRIMARY);
		getButtonsPanel().addComponent(nextButton);

		saveAndOpenCaseButton = ButtonHelper.createButton(Captions.actionSaveAndOpenCase, (b) -> {
			if (commitAndHandle()) {
				ControllerProvider.getCaseController().navigateToCase(caze.getUuid());
			}
		}, ValoTheme.BUTTON_PRIMARY);

		getButtonsPanel().addComponent(saveAndOpenCaseButton, getButtonsPanel().getComponentIndex(getCommitButton()));

		setPrimaryCommitListener(() -> {
			if (activeTabComponent.isDirty()) {
				activeTabComponent.commitAndHandle();
			}
		});

		// configure submenu
		for (TabConfig tabConfig : tabConfigs) {
			tabsMenu.addView(tabConfig.captionTag, I18nProperties.getCaption(tabConfig.captionTag), () -> {
				if (activeTabComponent.isDirty()) {
					DirtyCheckPopup.show(activeTabComponent, () -> {
						setActiveTab(tabConfig.captionTag);
						tabsMenu.setActiveView(tabConfig.captionTag);
					});

					return false;
				} else {
					setActiveTab(tabConfig.captionTag);

					return true;
				}
			});
		}

		getWrappedComponent().addComponent(tabsMenu);
		setActiveTab(Captions.CaseData_hospitalization);
	}

	private static VerticalLayout createLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setWidthFull();

		return layout;
	}

	private void setActiveTab(String tabCaptionTag) {
		tabsMenu.setActiveView(tabCaptionTag);
		TabConfig tabConfig = getTabConfig(tabCaptionTag);
		setActiveTabComponent(tabConfig.contentSupplier.get(), tabConfigs.indexOf(tabConfig));

	}

	private TabConfig getTabConfig(String tabCaptionTag) {
		return tabConfigs.stream()
			.filter(c -> c.captionTag.equals(tabCaptionTag))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Tab [" + tabCaptionTag + "] not found"));

	}

	private List<TabConfig> createTabConfigs() {
		List<TabConfig> configs = new ArrayList<>();

		configs.add(TabConfig.of(Captions.CaseData, () -> {
			CommitDiscardWrapperComponent<CaseDataForm> caseDataForm =
				ControllerProvider.getCaseController().getCaseDataEditComponent(caze.getUuid(), viewMode);
			caseDataForm.getWrappedComponent().getField(CaseDataDto.DISEASE).setEnabled(false);
			return caseDataForm;
		}));
		configs.add(
			TabConfig.of(
				Captions.CaseData_person,
				() -> ControllerProvider.getPersonController()
					.getPersonEditComponent(
						PersonContext.CASE,
						caze.getPerson().getUuid(),
						caze.getDisease(),
						caze.getDiseaseDetails(),
						UserRight.CASE_EDIT,
						viewMode)));
		configs.add(
			TabConfig.of(
				Captions.CaseData_hospitalization,
				() -> ControllerProvider.getCaseController().getHospitalizationComponent(caze.getUuid(), viewMode)));
		configs.add(
			TabConfig
				.of(Captions.CaseData_symptoms, () -> ControllerProvider.getCaseController().getSymptomsEditComponent(caze.getUuid(), viewMode)));
		configs.add(TabConfig.of(Captions.CaseData_epiData, () -> ControllerProvider.getCaseController().getEpiDataComponent(caze.getUuid(), null)));
		configs.add(TabConfig.of(Captions.physiciansReportCaseImmunizations, () -> new PhysiciansReportCaseImmunizationsComponent(caze)));

		return configs;
	}

	private void setActiveTabComponent(CommitDiscardWrapperComponent<?> activeTabComponent, int activeTabIndex) {
		if (this.activeTabComponent != null) {
			getWrappedComponent().removeComponent(this.activeTabComponent);
		}

		activeTabComponent.setShortcutsEnabled(false);
		activeTabComponent.setButtonsVisible(false);
		activeTabComponent.setWidthFull();
		addProcessedInMeantimeCheck(activeTabComponent, externalMessage, false);
		getWrappedComponent().addComponent(activeTabComponent);
		this.activeTabComponent = activeTabComponent;
		this.activeTabIndex = activeTabIndex;

		backButton.setVisible(activeTabIndex > 0);

		boolean isLastTab = activeTabIndex == tabConfigs.size() - 1;
		Button commitButton = getCommitButton();
		if (isLastTab) {
			commitButton.setVisible(true);
			nextButton.setVisible(false);
			saveAndOpenCaseButton.setVisible(true);
		} else {
			commitButton.setVisible(false);
			nextButton.setVisible(true);
			saveAndOpenCaseButton.setVisible(false);
		}
	}

	private static class TabConfig {

		private String captionTag;

		private Supplier<CommitDiscardWrapperComponent<?>> contentSupplier;

		static TabConfig of(String captionTag, Supplier<CommitDiscardWrapperComponent<?>> contentSupplier) {
			TabConfig config = new TabConfig();
			config.captionTag = captionTag;
			config.contentSupplier = contentSupplier;

			return config;
		}
	}
}
