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

package de.symeda.sormas.ui.sormastosormas;

import static de.symeda.sormas.ui.utils.CssStyles.INACCESSIBLE_LABEL;
import static de.symeda.sormas.ui.utils.CssStyles.LABEL_WHITE_SPACE_NORMAL;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_4;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_5;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_5;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.shareinfo.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class SormasToSormasOptionsForm extends AbstractEditForm<SormasToSormasOptionsDto> {

	private static final String TARGET_VALIDATION_ERROR_LOC = "targetValidationErrorLoc";
	private static final String CUSTOM_OPTIONS_PLACE_HOLDER = "__custom__";

	private static final String INACTIVE_OPTION_LOC_SUFFIX = "_inactive";

	private static final String HTML_LAYOUT = fluidRowLocs(SormasToSormasOptionsDto.ORGANIZATION)
		+ fluidRowLocs(TARGET_VALIDATION_ERROR_LOC)
		+ fluidRowLocs(SormasToSormasOptionsDto.HAND_OVER_OWNERSHIP)
		+ fluidRowLocs(SormasToSormasOptionsDto.PSEUDONYMIZE_PERSONAL_DATA)
		+ fluidRowLocs(SormasToSormasOptionsDto.PSEUDONYMIZE_SENSITIVE_DATA)
		+ CUSTOM_OPTIONS_PLACE_HOLDER
		+ fluidRowLocs(SormasToSormasOptionsDto.COMMENT);

	private static final Map<String, OptionFeatureTypeProperty> optionFeaturePropertyMap = new HashMap<>();
	private static final Map<String, Function<SormasToSormasShareInfoDto, Object>> optionValueGetterMap = new HashMap<>();

	static {
		optionFeaturePropertyMap.put(
			SormasToSormasOptionsDto.WITH_ASSOCIATED_CONTACTS,
			OptionFeatureTypeProperty.of(FeatureTypeProperty.SHARE_ASSOCIATED_CONTACTS, Strings.messageShareAssociatedContactsDisabled));
		optionFeaturePropertyMap.put(SormasToSormasOptionsDto.WITH_SAMPLES, OptionFeatureTypeProperty.of(FeatureTypeProperty.SHARE_SAMPLES));
		optionFeaturePropertyMap
			.put(SormasToSormasOptionsDto.WITH_IMMUNIZATIONS, OptionFeatureTypeProperty.of(FeatureTypeProperty.SHARE_IMMUNIZATIONS));
	}

	static {
		optionValueGetterMap.put(SormasToSormasOptionsDto.WITH_ASSOCIATED_CONTACTS, SormasToSormasShareInfoDto::isWithAssociatedContacts);
		optionValueGetterMap.put(SormasToSormasOptionsDto.WITH_SAMPLES, SormasToSormasShareInfoDto::isWithSamples);
		optionValueGetterMap.put(SormasToSormasOptionsDto.WITH_IMMUNIZATIONS, SormasToSormasShareInfoDto::isWithImmunizations);
		optionValueGetterMap.put(SormasToSormasOptionsDto.WITH_EVENT_PARTICIPANTS, SormasToSormasShareInfoDto::isWithEvenParticipants);
	}

	private final List<String> availableCustomOptions;
	private final List<String> allowedCustomOptions;

	private final List<SormasToSormasShareInfoDto> currentShares;

	private final boolean hasOptions;

	private final Consumer<SormasToSormasOptionsForm> customFieldDependencies;
	private Function<SormasServerDescriptor, String> targetValidator;

	private ComboBox targetCombo;

	private SormasToSormasOptionsForm(
		List<SormasToSormasShareInfoDto> currentShares,
		boolean hasOptions,
		List<String> availableCustomOptions,
		FeatureType featureType,
		Consumer<SormasToSormasOptionsForm> customFieldDependencies) {
		super(SormasToSormasOptionsDto.class, SormasToSormasOptionsDto.I18N_PREFIX, false);

		this.availableCustomOptions = availableCustomOptions == null ? Collections.emptyList() : availableCustomOptions;
		this.allowedCustomOptions = filterOptionsByFeatureProperty(featureType, this.availableCustomOptions);
		this.currentShares = currentShares == null ? Collections.emptyList() : currentShares;
		this.customFieldDependencies = customFieldDependencies;
		this.hasOptions = hasOptions;

		addFields();

		setWidthUndefined();
		hideValidationUntilNextCommit();
	}

	public static SormasToSormasOptionsForm forCase(CaseDataDto caze, List<SormasToSormasShareInfoDto> currentShares) {
		SormasToSormasOptionsForm optionsForm = new SormasToSormasOptionsForm(
			currentShares,
			true,
			Arrays.asList(
				SormasToSormasOptionsDto.WITH_ASSOCIATED_CONTACTS,
				SormasToSormasOptionsDto.WITH_SAMPLES,
				SormasToSormasOptionsDto.WITH_IMMUNIZATIONS),
			FeatureType.SORMAS_TO_SORMAS_SHARE_CASES,
			(form) -> {
				if (form.allowedCustomOptions.contains(SormasToSormasOptionsDto.WITH_SAMPLES)) {
					FieldHelper.setEnabledWhen(
						form.getFieldGroup(),
						SormasToSormasOptionsDto.HAND_OVER_OWNERSHIP,
						Boolean.FALSE,
						SormasToSormasOptionsDto.WITH_SAMPLES,
						false);

					FieldHelper.setValueWhen(
						form.getFieldGroup(),
						SormasToSormasOptionsDto.HAND_OVER_OWNERSHIP,
						Boolean.TRUE,
						SormasToSormasOptionsDto.WITH_SAMPLES,
						Boolean.TRUE);
				}

				if (form.allowedCustomOptions.contains(SormasToSormasOptionsDto.WITH_IMMUNIZATIONS)) {
					FieldHelper.setEnabledWhen(
						form.getFieldGroup(),
						SormasToSormasOptionsDto.HAND_OVER_OWNERSHIP,
						Boolean.FALSE,
						SormasToSormasOptionsDto.WITH_IMMUNIZATIONS,
						false);

					FieldHelper.setValueWhen(
						form.getFieldGroup(),
						SormasToSormasOptionsDto.HAND_OVER_OWNERSHIP,
						Boolean.TRUE,
						SormasToSormasOptionsDto.WITH_IMMUNIZATIONS,
						Boolean.TRUE);
				}
			});

		if (caze != null) {
			// in case of bulk send, only backend validation is possible
			optionsForm.setTargetValidator(t -> {
				if (t == null) {
					return null;
				}

				String targetOrganizationId = t.getId();

				SormasToSormasShareInfoDto caseShareInfo =
					FacadeProvider.getSormasToSormasShareInfoFacade().getCaseShareInfoByOrganization(caze.toReference(), targetOrganizationId);

				if (caseShareInfo != null && caseShareInfo.getRequestStatus() == ShareRequestStatus.PENDING) {
					return I18nProperties.getString(Strings.errorSormasToSormasExistingPendingRequest);
				}

				return null;
			});
		}

		return optionsForm;
	}

	public static SormasToSormasOptionsForm forContact(ContactDto contact, List<SormasToSormasShareInfoDto> currentShares) {
		SormasToSormasOptionsForm optionsForm = new SormasToSormasOptionsForm(
			currentShares,
			true,
			Arrays.asList(SormasToSormasOptionsDto.WITH_SAMPLES, SormasToSormasOptionsDto.WITH_IMMUNIZATIONS),
			FeatureType.SORMAS_TO_SORMAS_SHARE_CONTACTS,
			null);

		if (contact != null) {
			// in case of bulk send, only backend validation is possible
			optionsForm.setTargetValidator(t -> {
				if (t == null) {
					return null;
				}

				String targetOrganizationId = t.getId();

				SormasToSormasOriginInfoDto originInfo = contact.getSormasToSormasOriginInfo();
				if (originInfo == null || !originInfo.getOrganizationId().equals(targetOrganizationId)) {
					SormasToSormasShareInfoDto caseShareInfo =
						FacadeProvider.getSormasToSormasShareInfoFacade().getCaseShareInfoByOrganization(contact.getCaze(), targetOrganizationId);

					if (caseShareInfo == null
						|| (caseShareInfo.getRequestStatus() != ShareRequestStatus.PENDING
							&& caseShareInfo.getRequestStatus() != ShareRequestStatus.ACCEPTED)) {
						return I18nProperties.getString(Strings.errorSormasToSormasShareContactWithUnsharedSourceCase);
					}
				}

				SormasToSormasShareInfoDto contactShareInfo =
					FacadeProvider.getSormasToSormasShareInfoFacade().getContactShareInfoByOrganization(contact.toReference(), targetOrganizationId);

				if (contactShareInfo != null && contactShareInfo.getRequestStatus() == ShareRequestStatus.PENDING) {
					return I18nProperties.getString(Strings.errorSormasToSormasExistingPendingRequest);
				}

				return null;
			});
		}

		return optionsForm;
	}

	public static SormasToSormasOptionsForm forEvent(EventDto event, List<SormasToSormasShareInfoDto> currentShares) {
		SormasToSormasOptionsForm optionsForm = new SormasToSormasOptionsForm(
			currentShares,
			true,
			Arrays.asList(
				SormasToSormasOptionsDto.WITH_EVENT_PARTICIPANTS,
				SormasToSormasOptionsDto.WITH_SAMPLES,
				SormasToSormasOptionsDto.WITH_IMMUNIZATIONS),
			FeatureType.SORMAS_TO_SORMAS_SHARE_EVENTS,
			(form) -> {
				if (form.allowedCustomOptions.contains(SormasToSormasOptionsDto.WITH_SAMPLES)) {
					FieldHelper.setVisibleWhen(
						form.getFieldGroup(),
						SormasToSormasOptionsDto.WITH_SAMPLES,
						SormasToSormasOptionsDto.WITH_EVENT_PARTICIPANTS,
						Boolean.TRUE,
						true);
				}

				if (form.allowedCustomOptions.contains(SormasToSormasOptionsDto.WITH_IMMUNIZATIONS)) {
					FieldHelper.setVisibleWhen(
						form.getFieldGroup(),
						SormasToSormasOptionsDto.WITH_IMMUNIZATIONS,
						SormasToSormasOptionsDto.WITH_EVENT_PARTICIPANTS,
						Boolean.TRUE,
						true);
				}
			});

		optionsForm.setTargetValidator(t -> {
			if (t == null) {
				return null;
			}

			String targetOrganizationId = t.getId();

			SormasToSormasShareInfoDto eventShareInfo =
				FacadeProvider.getSormasToSormasShareInfoFacade().getEventShareInfoByOrganization(event.toReference(), targetOrganizationId);

			if (eventShareInfo != null && eventShareInfo.getRequestStatus() == ShareRequestStatus.PENDING) {
				return I18nProperties.getString(Strings.errorSormasToSormasExistingPendingRequest);
			}

			return null;
		});

		return optionsForm;

	}

	public static SormasToSormasOptionsForm forExternalMessage() {
		return new SormasToSormasOptionsForm(null, false, null, FeatureType.SORMAS_TO_SORMAS_SHARE_EXTERNAL_MESSAGES, null);
	}

	@Override
	protected String createHtmlLayout() {
		String customLocs = availableCustomOptions.stream()
			.map(o -> LayoutUtil.fluidRowLocs(allowedCustomOptions.contains(o) ? o : o + INACTIVE_OPTION_LOC_SUFFIX))
			.collect(Collectors.joining());

		return HTML_LAYOUT.replace(CUSTOM_OPTIONS_PLACE_HOLDER, customLocs);
	}

	@Override
	protected void addFields() {
		targetCombo = addField(SormasToSormasOptionsDto.ORGANIZATION, ComboBox.class);
		targetCombo.setRequired(true);
		List<SormasServerDescriptor> availableServers = FacadeProvider.getSormasToSormasFacade().getAllAvailableServers();
		targetCombo.addItems(availableServers);

		if (hasOptions) {

			CheckBox handoverOwnership = addField(SormasToSormasOptionsDto.HAND_OVER_OWNERSHIP);
			CheckBox pseudonimyzePersonalData = addField(SormasToSormasOptionsDto.PSEUDONYMIZE_PERSONAL_DATA);
			CheckBox pseudonymizeSensitiveData = addField(SormasToSormasOptionsDto.PSEUDONYMIZE_SENSITIVE_DATA);

			pseudonymizeSensitiveData.addStyleNames(CssStyles.VSPACE_3);

			targetCombo.addValueChangeListener(e -> {
				SormasServerDescriptor selectedServer = (SormasServerDescriptor) e.getProperty().getValue();

				if (selectedServer == null) {
					getContent().removeComponent(TARGET_VALIDATION_ERROR_LOC);

					return;
				}

				if (targetValidator != null) {
					String validationMessage = targetValidator.apply(selectedServer);

					if (StringUtils.isNoneBlank(validationMessage)) {
						Label messageLabel = new Label(VaadinIcons.WARNING.getHtml() + " " + validationMessage, ContentMode.HTML);
						messageLabel.addStyleNames(LABEL_WHITE_SPACE_NORMAL, VSPACE_4, VSPACE_TOP_5);
						getContent().addComponent(messageLabel, TARGET_VALIDATION_ERROR_LOC);
					} else {
						getContent().removeComponent(TARGET_VALIDATION_ERROR_LOC);
					}
				}

				Optional<SormasToSormasShareInfoDto> previousShare = findShareByOrganization(currentShares, selectedServer.getId());

				previousShare.ifPresent(s -> {
					if (s.getRequestStatus() != ShareRequestStatus.ACCEPTED) {
						handoverOwnership.setValue(s.isOwnershipHandedOver());
					}
					pseudonimyzePersonalData.setValue(s.isPseudonymizedPersonalData());
					pseudonimyzePersonalData.setValue(s.isPseudonymizedSensitiveData());

					if (CollectionUtils.isNotEmpty(allowedCustomOptions)) {
						allowedCustomOptions.forEach(o -> {
							if (optionValueGetterMap.containsKey(o)) {
								Object optionValue = optionValueGetterMap.get(o).apply(s);
								this.<Field<Object>> getField(o).setValue(optionValue);
							}
						});
					}
				});
			});

			handoverOwnership.addValueChangeListener(e -> {
				boolean ownershipHandedOver = (boolean) e.getProperty().getValue();
				pseudonimyzePersonalData.setEnabled(!ownershipHandedOver);
				pseudonymizeSensitiveData.setEnabled(!ownershipHandedOver);

				if (ownershipHandedOver) {
					pseudonimyzePersonalData.setValue(false);
					pseudonymizeSensitiveData.setValue(false);
				}
			});

			pseudonimyzePersonalData.addValueChangeListener(e -> {
				boolean pseudonimyze = (boolean) e.getProperty().getValue() || pseudonymizeSensitiveData.getValue();
				handoverOwnership.setEnabled(!pseudonimyze);
				if (pseudonimyze) {
					handoverOwnership.setValue(false);
				}
			});

			pseudonymizeSensitiveData.addValueChangeListener(e -> {
				boolean pseudonimyze = (boolean) e.getProperty().getValue() || pseudonimyzePersonalData.getValue();
				handoverOwnership.setEnabled(!pseudonimyze);
				if (pseudonimyze) {
					handoverOwnership.setValue(false);
				}
			});

			availableCustomOptions.forEach(option -> {
				if (allowedCustomOptions.contains(option)) {
					addField(option);
				} else {
					String placeholderMessageTag = optionFeaturePropertyMap.get(option).messageTagFeatureNotEnabled;
					if (placeholderMessageTag != null) {
						Label placeholder = new Label(I18nProperties.getString(placeholderMessageTag));
						placeholder.addStyleNames(VSPACE_5, INACCESSIBLE_LABEL);
						getContent().addComponent(placeholder, option + INACTIVE_OPTION_LOC_SUFFIX);
					}
				}
			});

			TextArea comment = addField(SormasToSormasOptionsDto.COMMENT, TextArea.class);
			comment.setRows(3);

			if (customFieldDependencies != null) {
				customFieldDependencies.accept(this);
			}

			if (CollectionUtils.isEmpty(allowedCustomOptions)) {
				pseudonymizeSensitiveData.addStyleNames(CssStyles.VSPACE_3);
			} else {
				getField(allowedCustomOptions.get(allowedCustomOptions.size() - 1)).addStyleNames(CssStyles.VSPACE_3);
			}
		}
	}

	private List<String> filterOptionsByFeatureProperty(FeatureType featureType, List<String> options) {
		return options.stream().filter(option -> {
			if (optionFeaturePropertyMap.containsKey(option)) {
				return FacadeProvider.getFeatureConfigurationFacade()
					.isPropertyValueTrue(featureType, optionFeaturePropertyMap.get(option).featureTypeProperty);
			}

			return true;
		}).collect(Collectors.toList());
	}

	public void disableOrganization() {
		getField(SormasToSormasOptionsDto.ORGANIZATION).setEnabled(false);
	}

	public void disableAllOptions() {
		getFieldGroup().getFields().forEach(f -> {
			if (!SormasToSormasOptionsDto.COMMENT.equals(f.getId())) {
				f.setEnabled(false);
			}
		});
	}

	public void setTargetValidator(Function<SormasServerDescriptor, String> targetValidator) {
		this.targetValidator = targetValidator;
	}

	private static Optional<SormasToSormasShareInfoDto> findShareByOrganization(List<SormasToSormasShareInfoDto> shares, String organizationId) {
		for (SormasToSormasShareInfoDto share : shares) {
			if (share.getTargetDescriptor().getId().equals(organizationId)) {
				return Optional.of(share);
			}
		}

		return Optional.empty();
	}

	public ComboBox getTargetCombo() {
		return targetCombo;
	}

	public boolean isTargetValid() {
		return targetValidator == null || targetValidator.apply((SormasServerDescriptor) targetCombo.getValue()) == null;
	}

	private static class OptionFeatureTypeProperty {

		private final FeatureTypeProperty featureTypeProperty;

		private final String messageTagFeatureNotEnabled;

		private OptionFeatureTypeProperty(FeatureTypeProperty featureTypeProperty, String messageTagFeatureNotEnabled) {
			this.featureTypeProperty = featureTypeProperty;
			this.messageTagFeatureNotEnabled = messageTagFeatureNotEnabled;
		}

		public static OptionFeatureTypeProperty of(FeatureTypeProperty featureTypeProperty) {
			return new OptionFeatureTypeProperty(featureTypeProperty, null);
		}

		public static OptionFeatureTypeProperty of(FeatureTypeProperty featureTypeProperty, String messageTagFeatureNotEnabled) {
			return new OptionFeatureTypeProperty(featureTypeProperty, messageTagFeatureNotEnabled);
		}
	}
}
