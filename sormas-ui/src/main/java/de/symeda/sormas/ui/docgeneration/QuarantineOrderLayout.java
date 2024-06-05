/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.docgeneration;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import javax.annotation.Nullable;

import de.symeda.sormas.ui.utils.CssStyles;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderDocumentOptionsDto;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.vaccination.VaccinationCriteria;
import de.symeda.sormas.api.vaccination.VaccinationListEntryDto;
import de.symeda.sormas.api.vaccination.VaccinationReferenceDto;
import de.symeda.sormas.ui.document.DocumentListComponent;

public class QuarantineOrderLayout extends AbstractDocgenerationLayout {

	private static final long serialVersionUID = 8188715887512127569L;

	private final DocumentWorkflow workflow;
	private final DocumentStreamSupplier documentStreamSupplier;

	private ComboBox<SampleIndexDto> sampleSelector;
	private ComboBox<PathogenTestDto> pathogenTestSelector;
	private ComboBox<VaccinationListEntryDto> vaccinationSelector;
	private DocumentListComponent documentListComponent;

	public QuarantineOrderLayout(
		DocumentWorkflow workflow,
		@Nullable SampleCriteria sampleCriteria,
		@Nullable VaccinationCriteria vaccinationCriteria,
		DocumentListComponent documentListComponent,
		DocumentStreamSupplier documentStreamSupplier,
		Function<String, String> fileNameFunction) {
		super(
			I18nProperties.getCaption(Captions.DocumentTemplate_QuarantineOrder),
			fileNameFunction,
			isNull(sampleCriteria) && isNull(documentListComponent),
			false);
		this.workflow = workflow;
		this.documentStreamSupplier = documentStreamSupplier;

		this.documentListComponent = documentListComponent;

		init();

		if (sampleCriteria != null) {
			createSampleSelector(sampleCriteria);
		}

		if (vaccinationCriteria != null
			&& FacadeProvider.getFeatureConfigurationFacade().isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
			createVaccinationSelector(vaccinationCriteria);
		}
	}

	public QuarantineOrderLayout(DocumentWorkflow workflow) {
		super(I18nProperties.getCaption(Captions.DocumentTemplate_QuarantineOrder), null, true, true);

		this.workflow = workflow;
		this.documentStreamSupplier = null;
		init();
		buttonBar.setVisible(false);
		checkBoxUploadGeneratedDoc.setValue(true);
		checkBoxUploadGeneratedDoc.setStyleName(CssStyles.VSPACE_3);
	}

	protected void createSampleSelector(SampleCriteria sampleCriteria) {
		List<SampleIndexDto> samples = FacadeProvider.getSampleFacade()
			.getIndexList(sampleCriteria, 0, 20, Collections.singletonList(new SortProperty("sampleDateTime", false)));

		pathogenTestSelector = new ComboBox<>(I18nProperties.getCaption(Captions.PathogenTest));
		pathogenTestSelector.setWidth(100F, Unit.PERCENTAGE);
		pathogenTestSelector.setItemCaptionGenerator(e -> e.buildCaption());
		pathogenTestSelector.setEnabled(false);
		pathogenTestSelector.setItemCaptionGenerator(item -> item.buildCaption());

		sampleSelector = new ComboBox<>(I18nProperties.getCaption(Captions.Sample));
		sampleSelector.setItemCaptionGenerator(item -> item.buildCaption());
		sampleSelector.setWidth(100F, Unit.PERCENTAGE);
		sampleSelector.setItemCaptionGenerator(e -> e.getCaption());
		sampleSelector.setItems(samples);
		sampleSelector.setEnabled(!samples.isEmpty());
		sampleSelector.addValueChangeListener(e -> {
			pathogenTestSelector.clear();
			if (e != null && e.getValue() != null) {
				List<PathogenTestDto> pathogenTests = FacadeProvider.getPathogenTestFacade().getAllBySample(e.getValue().toReference());
				pathogenTestSelector.setItems(pathogenTests);
				pathogenTestSelector.setEnabled(!pathogenTests.isEmpty());
				if (pathogenTests.size() == 1) {
					pathogenTestSelector.setSelectedItem(pathogenTests.get(0));
				}
			} else {
				pathogenTestSelector.setEnabled(false);
			}
		});
		if (samples.size() == 1) {
			sampleSelector.setSelectedItem(samples.get(0));
		}

		additionalParametersComponent.addComponent(sampleSelector);
		additionalParametersComponent.addComponent(pathogenTestSelector);
	}

	protected void createVaccinationSelector(VaccinationCriteria vaccinationCriteria) {
		List<VaccinationListEntryDto> vaccinations = FacadeProvider.getVaccinationFacade()
			.getEntriesList(vaccinationCriteria, 0, 20, Collections.singletonList(new SortProperty("vaccinationDate", false)));

		vaccinationSelector = new ComboBox<>(I18nProperties.getCaption(Captions.Vaccination));
		vaccinationSelector.setWidth(100F, Unit.PERCENTAGE);
		vaccinationSelector.setItemCaptionGenerator(e -> e.getCaption());
		vaccinationSelector.setItems(vaccinations);
		vaccinationSelector.setEnabled(!vaccinations.isEmpty());

		if (vaccinations.size() == 1) {
			vaccinationSelector.setSelectedItem(vaccinations.get(0));
		}

		additionalParametersComponent.addComponent(vaccinationSelector);
	}

	@Override
	protected List<String> getAvailableTemplates() {
		try {
			return FacadeProvider.getQuarantineOrderFacade().getAvailableTemplates(workflow);
		} catch (Exception e) {
			new Notification(I18nProperties.getString(Strings.errorProcessingTemplate), e.getMessage(), Notification.Type.ERROR_MESSAGE)
				.show(Page.getCurrent());
			return Collections.emptyList();
		}
	}

	@Override
	protected DocumentVariables getDocumentVariables(String templateFile) throws DocumentTemplateException {
		return FacadeProvider.getQuarantineOrderFacade().getDocumentVariables(workflow, templateFile);
	}

	@Override
	protected StreamResource createStreamResource(String templateFile, String filename) {
		return new StreamResource((StreamSource) () -> {
			SampleReferenceDto sampleReference =
				sampleSelector != null && sampleSelector.getValue() != null ? sampleSelector.getValue().toReference() : null;
			PathogenTestReferenceDto pathogenTestReference =
				pathogenTestSelector != null && pathogenTestSelector.getValue() != null ? pathogenTestSelector.getValue().toReference() : null;

			VaccinationReferenceDto vaccinationReference =
				vaccinationSelector != null && vaccinationSelector.getValue() != null ? vaccinationSelector.getValue().toReference() : null;

			try {
				InputStream stream = documentStreamSupplier.getStream(
					templateFile,
					sampleReference,
					pathogenTestReference,
					vaccinationReference,
					readAdditionalVariables(),
					shouldUploadGeneratedDocument());

				new Notification(
					I18nProperties.getString(Strings.headingDocumentCreated),
					I18nProperties.getString(Strings.messageQuarantineOrderDocumentCreated),
					Notification.Type.TRAY_NOTIFICATION,
					false).show(Page.getCurrent());

				if (nonNull(documentListComponent)) {
					documentListComponent.reload();
				}

				return stream;
			} catch (DocumentTemplateException e) {
				LoggerFactory.getLogger(getClass()).error("Error while reading document variables.", e);
				new Notification(I18nProperties.getString(Strings.errorProcessingTemplate), e.getMessage(), Notification.Type.ERROR_MESSAGE)
					.show(Page.getCurrent());
				return null;
			}
		}, filename);
	}

	@Override
	protected String getWindowCaption() {
		return Captions.DocumentTemplate_QuarantineOrder_create;
	}

	@Override
	protected void performTemplateUpdates() {
		if (documentVariables == null) {
			hideAdditionalParameters();
			return;
		}
		boolean showSampleSelector = documentVariables.isUsedEntity(RootEntityType.ROOT_SAMPLE.getEntityName());
		boolean showPathogenTestSelector = documentVariables.isUsedEntity(RootEntityType.ROOT_PATHOGEN_TEST.getEntityName());
		boolean showVaccinationSelector = documentVariables.isUsedEntity(RootEntityType.ROOT_VACCINATION.getEntityName());
		if (showSampleSelector || showPathogenTestSelector || showVaccinationSelector) {
			showAdditionalParameters();
			if (sampleSelector != null) {
				sampleSelector.setVisible(showSampleSelector);
			}
			if (pathogenTestSelector != null) {
				pathogenTestSelector.setVisible(showPathogenTestSelector);
			}
			if (vaccinationSelector != null) {
				vaccinationSelector.setVisible(showVaccinationSelector);
			}
		} else {
			hideAdditionalParameters();
		}
	}

	public QuarantineOrderDocumentOptionsDto getFieldValues() {
		QuarantineOrderDocumentOptionsDto options = new QuarantineOrderDocumentOptionsDto();
		options.setDocumentWorkflow(workflow);
		options.setExtraProperties(readAdditionalVariables());
		if (sampleSelector != null) {
			options.setSample(new SampleReferenceDto(sampleSelector.getValue().getUuid()));
		}
		if (pathogenTestSelector != null) {
			options.setPathogenTest(new PathogenTestReferenceDto(pathogenTestSelector.getValue().getUuid()));
		}
		if (templateSelector != null) {
			options.setTemplateFile(templateSelector.getValue());
		}
		options.setShouldUploadGeneratedDoc(shouldUploadGeneratedDocument());

		if (vaccinationSelector != null) {
			options.setVaccinationReference(
				new VaccinationReferenceDto(vaccinationSelector.getValue().getUuid(), vaccinationSelector.getValue().getCaption()));
		}

		return options;
	}

	public interface DocumentStreamSupplier {

		InputStream getStream(
			String templateFile,
			SampleReferenceDto sample,
			PathogenTestReferenceDto pathogenTest,
			VaccinationReferenceDto vaccinationReference,
			Properties extraProperties,
			Boolean shouldUploadGeneratedDoc)
			throws DocumentTemplateException;
	}
}
