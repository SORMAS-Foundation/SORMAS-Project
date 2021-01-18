package de.symeda.sormas.ui.docgeneration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.UserProvider;

public class QuarantineOrderLayout extends AbstractDocgenerationLayout {

	private final ReferenceDto caseReferenceDto;

	private ComboBox<SampleIndexDto> sampleSelector;
	private ComboBox<PathogenTestDto> pathogenTestSelector;

	public QuarantineOrderLayout(ReferenceDto referenceDto) {
		super(I18nProperties.getCaption(Captions.DocumentTemplate_QuarantineOrder));
		this.caseReferenceDto = referenceDto;
		createSampleSelector(referenceDto);
	}

	protected void createSampleSelector(ReferenceDto referenceDto) {
		SampleCriteria sampleCriteria = new SampleCriteria();
		if (CaseReferenceDto.class.isAssignableFrom(referenceDto.getClass())) {
			sampleCriteria.caze((CaseReferenceDto) referenceDto);
		} else if (ContactReferenceDto.class.isAssignableFrom(referenceDto.getClass())) {
			sampleCriteria.contact((ContactReferenceDto) referenceDto);
		}
		List<SampleIndexDto> samples =
			FacadeProvider.getSampleFacade().getIndexList(sampleCriteria, 0, 20, Arrays.asList(new SortProperty("sampleDateTime", false)));

		pathogenTestSelector = new ComboBox<>(I18nProperties.getCaption(Captions.PathogenTest));
		pathogenTestSelector.setWidth(100F, Unit.PERCENTAGE);
		pathogenTestSelector.setEnabled(false);

		sampleSelector = new ComboBox<>(I18nProperties.getCaption(Captions.Sample));
		sampleSelector.setWidth(100F, Unit.PERCENTAGE);
		sampleSelector.setItems(samples);
		sampleSelector.addValueChangeListener(e -> {
			pathogenTestSelector.clear();
			if (e != null && e.getValue() != null) {
				pathogenTestSelector.setItems(FacadeProvider.getPathogenTestFacade().getAllBySample(e.getValue().toReference()));
				pathogenTestSelector.setEnabled(true);
			} else {
				pathogenTestSelector.setEnabled(false);
			}
		});

		additionalParametersComponent.addComponent(sampleSelector);
		additionalParametersComponent.addComponent(pathogenTestSelector);
	}

	@Override
	protected List<String> getAvailableTemplates() {
		return FacadeProvider.getQuarantineOrderFacade().getAvailableTemplates();
	}

	@Override
	protected String generateFilename(String templateFile) {
		String uuid = caseReferenceDto.getUuid();
		return uuid.substring(0, Math.min(5, uuid.length())) + "_" + templateFile;
	}

	@Override
	protected DocumentVariables getDocumentVariables(String templateFile) throws IOException {
		return FacadeProvider.getQuarantineOrderFacade().getDocumentVariables(templateFile);
	}

	@Override
	protected StreamResource createStreamResource(String templateFile, String filename) {
		return new StreamResource((StreamSource) () -> {
			QuarantineOrderFacade quarantineOrderFacade = FacadeProvider.getQuarantineOrderFacade();
			UserReferenceDto userReference = UserProvider.getCurrent().getUser().toReference();
			SampleReferenceDto sampleReference = sampleSelector.getValue() != null ? sampleSelector.getValue().toReference() : null;
			PathogenTestReferenceDto pathogenTestReference =
				pathogenTestSelector.getValue() != null ? pathogenTestSelector.getValue().toReference() : null;
			try {
				return new ByteArrayInputStream(
					quarantineOrderFacade.getGeneratedDocument(
						templateFile,
						caseReferenceDto,
						userReference,
						sampleReference,
						pathogenTestReference,
						readAdditionalVariables()));
			} catch (IOException | IllegalArgumentException e) {
				new Notification("Document generation failed", e.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
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
		if (documentVariables != null && (documentVariables.isUsedEntity("sample") || documentVariables.isUsedEntity("pathogentest"))) {
			showAdditionalParameters();
		} else {
			hideAdditionalParameters();
		}
	}
}
