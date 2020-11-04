package de.symeda.sormas.api.docgeneneration;

import static de.symeda.sormas.api.docgeneneration.TemplateFileType.DOCX;

import java.util.ArrayList;
import java.util.List;

public enum DocumentWorkflow {

	// rootEntityNames define the root variables in a document template.
	// These names are arbitrarily chosen per workflow.

	// In templates for quarantine orders, the root variable "case" refers
	// to either a CaseDataDto or a ContactDto, depending on from where
	// it is called. So "${case.person.firstName}" in the template refers
	// to the case's or contact's person's first name in either case.
	QUARANTINE_ORDER("quarantine", DOCX, "case");

	private String templateDirectory;
	private TemplateFileType fileType;
	private List<String> rootEntityNames;

	DocumentWorkflow(String templateDirectory, TemplateFileType fileType, String... rootEntityNames) {
		this.templateDirectory = templateDirectory;
		this.fileType = fileType;
		this.rootEntityNames = new ArrayList<>();
		for (String rootEntityName : rootEntityNames) {
			this.rootEntityNames.add(rootEntityName.toLowerCase());
		}
	}

	public String getTemplateDirectory() {
		return templateDirectory;
	}

	public TemplateFileType getFileType() {
		return fileType;
	}

	public List<String> getRootEntityNames() {
		return rootEntityNames;
	}

	public String getFileExtension() {
		return fileType.getExtension();
	}
}
