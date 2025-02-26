package de.symeda.sormas.api.docgeneneration;

import java.io.Serializable;
import java.util.Properties;

import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.vaccination.VaccinationReferenceDto;

public class QuarantineOrderDocumentOptionsDto implements Serializable {

	private DocumentTemplateReferenceDto template;
    private SampleReferenceDto sample;
    private PathogenTestReferenceDto pathogenTest;
    private VaccinationReferenceDto vaccinationReference;
    private Properties extraProperties;
    private Boolean shouldUploadGeneratedDoc;
    private DocumentWorkflow documentWorkflow;


	public DocumentTemplateReferenceDto getTemplate() {
		return template;
    }

	public void setTemplate(DocumentTemplateReferenceDto template) {
		this.template = template;
    }

    public SampleReferenceDto getSample() {
        return sample;
    }

    public void setSample(SampleReferenceDto sample) {
        this.sample = sample;
    }

    public PathogenTestReferenceDto getPathogenTest() {
        return pathogenTest;
    }

    public void setPathogenTest(PathogenTestReferenceDto pathogenTest) {
        this.pathogenTest = pathogenTest;
    }

    public VaccinationReferenceDto getVaccinationReference() {
        return vaccinationReference;
    }

    public void setVaccinationReference(VaccinationReferenceDto vaccinationReference) {
        this.vaccinationReference = vaccinationReference;
    }

    public Properties getExtraProperties() {
        return extraProperties;
    }

    public void setExtraProperties(Properties extraProperties) {
        this.extraProperties = extraProperties;
    }

    public Boolean getShouldUploadGeneratedDoc() {
        return shouldUploadGeneratedDoc;
    }

    public void setShouldUploadGeneratedDoc(Boolean shouldUploadGeneratedDoc) {
        this.shouldUploadGeneratedDoc = shouldUploadGeneratedDoc;
    }

    public DocumentWorkflow getDocumentWorkflow() {
        return documentWorkflow;
    }

    public void setDocumentWorkflow(DocumentWorkflow documentWorkflow) {
        this.documentWorkflow = documentWorkflow;
    }
}
