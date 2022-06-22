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

import static de.symeda.sormas.ui.utils.CssStyles.LABEL_WHITE_SPACE_NORMAL;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_4;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_5;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import de.symeda.sormas.api.caze.CaseDataDto;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
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
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
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

	private static final String HTML_LAYOUT = fluidRowLocs(SormasToSormasOptionsDto.ORGANIZATION)
		+ fluidRowLocs(TARGET_VALIDATION_ERROR_LOC)
		+ fluidRowLocs(SormasToSormasOptionsDto.HAND_OVER_OWNERSHIP)
		+ fluidRowLocs(SormasToSormasOptionsDto.PSEUDONYMIZE_PERSONAL_DATA)
		+ fluidRowLocs(SormasToSormasOptionsDto.PSEUDONYMIZE_SENSITIVE_DATA)
		+ CUSTOM_OPTIONS_PLACE_HOLDER
		+ fluidRowLocs(SormasToSormasOptionsDto.COMMENT);

	private final List<String> customOptions;

	private List<SormasToSormasShareInfoDto> currentShares;

	private final boolean hasOptions;

	private final BiConsumer<SormasToSormasOptionsForm, SormasToSormasShareInfoDto> updateCustomOptionsByPreviousShare;
	private final Consumer<SormasToSormasOptionsForm> customFieldDependencies;
	private Function<SormasServerDescriptor, String> targetValidator;

	private ComboBox targetCombo;

	public static SormasToSormasOptionsForm forCase(CaseDataDto caze, List<SormasToSormasShareInfoDto> currentShares) {
		SormasToSormasOptionsForm optionsForm = new SormasToSormasOptionsForm(
			currentShares,
			true,
			Arrays.asList(
				SormasToSormasOptionsDto.WITH_ASSOCIATED_CONTACTS,
				SormasToSormasOptionsDto.WITH_SAMPLES,
				SormasToSormasOptionsDto.WITH_IMMUNIZATIONS),
			(f, s) -> {
				((CheckBox) f.getField(SormasToSormasOptionsDto.WITH_ASSOCIATED_CONTACTS)).setValue(s.isWithAssociatedContacts());
				((CheckBox) f.getField(SormasToSormasOptionsDto.WITH_SAMPLES)).setValue(s.isWithSamples());
				((CheckBox) f.getField(SormasToSormasOptionsDto.WITH_IMMUNIZATIONS)).setValue(s.isWithImmunizations());
			},
			(form) -> {
				FieldHelper.setEnabledWhen(
					form.getFieldGroup(),
					SormasToSormasOptionsDto.HAND_OVER_OWNERSHIP,
					Boolean.FALSE,
					SormasToSormasOptionsDto.WITH_SAMPLES,
					false);
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
					SormasToSormasOptionsDto.WITH_SAMPLES,
					Boolean.TRUE);

				FieldHelper.setValueWhen(
					form.getFieldGroup(),
					SormasToSormasOptionsDto.HAND_OVER_OWNERSHIP,
					Boolean.TRUE,
					SormasToSormasOptionsDto.WITH_IMMUNIZATIONS,
					Boolean.TRUE);
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
			(f, s) -> {
				((CheckBox) f.getField(SormasToSormasOptionsDto.WITH_SAMPLES)).setValue(s.isWithSamples());
				((CheckBox) f.getField(SormasToSormasOptionsDto.WITH_IMMUNIZATIONS)).setValue(s.isWithImmunizations());
			},
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
			(f, s) -> {
				((CheckBox) f.getField(SormasToSormasOptionsDto.WITH_EVENT_PARTICIPANTS)).setValue(s.isWithEvenParticipants());
				((CheckBox) f.getField(SormasToSormasOptionsDto.WITH_SAMPLES)).setValue(s.isWithSamples());
				((CheckBox) f.getField(SormasToSormasOptionsDto.WITH_IMMUNIZATIONS)).setValue(s.isWithImmunizations());
			},
			(form) -> {
				FieldHelper.setVisibleWhen(
					form.getFieldGroup(),
					SormasToSormasOptionsDto.WITH_SAMPLES,
					SormasToSormasOptionsDto.WITH_EVENT_PARTICIPANTS,
					Boolean.TRUE,
					true);

				FieldHelper.setVisibleWhen(
					form.getFieldGroup(),
					SormasToSormasOptionsDto.WITH_IMMUNIZATIONS,
					SormasToSormasOptionsDto.WITH_EVENT_PARTICIPANTS,
					Boolean.TRUE,
					true);
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

	public static SormasToSormasOptionsForm withoutOptions() {
		return new SormasToSormasOptionsForm(null, false, null, null, null);
	}

	private SormasToSormasOptionsForm(
		List<SormasToSormasShareInfoDto> currentShares,
		boolean hasOptions,
		List<String> customOptions,
		BiConsumer<SormasToSormasOptionsForm, SormasToSormasShareInfoDto> updateCustomOptionsByPreviousShare,
		Consumer<SormasToSormasOptionsForm> customFieldDependencies) {
		super(SormasToSormasOptionsDto.class, SormasToSormasOptionsDto.I18N_PREFIX, false);

		this.customOptions = customOptions == null ? Collections.emptyList() : customOptions;
		this.currentShares = currentShares == null ? Collections.emptyList() : currentShares;
		this.updateCustomOptionsByPreviousShare = updateCustomOptionsByPreviousShare;
		this.customFieldDependencies = customFieldDependencies;
		this.hasOptions = hasOptions;

		addFields();

		setWidthUndefined();
		hideValidationUntilNextCommit();
	}

	@Override
	protected String createHtmlLayout() {
		String customLocs = customOptions.stream().map(LayoutUtil::fluidRowLocs).collect(Collectors.joining());

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

					if (updateCustomOptionsByPreviousShare != null) {
						updateCustomOptionsByPreviousShare.accept(this, s);
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

			addFields(customOptions);

			TextArea comment = addField(SormasToSormasOptionsDto.COMMENT, TextArea.class);
			comment.setRows(3);

			if (customFieldDependencies != null) {
				customFieldDependencies.accept(this);
			}

			if (CollectionUtils.isEmpty(customOptions)) {
				pseudonymizeSensitiveData.addStyleNames(CssStyles.VSPACE_3);
			} else {
				getField(customOptions.get(customOptions.size() - 1)).addStyleNames(CssStyles.VSPACE_3);
			}
		}
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
}
