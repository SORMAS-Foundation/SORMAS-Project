package de.symeda.sormas.api.docgeneneration;

public enum TemplateFileType {

	DOCX("docx"),
	TXT("txt"),
	HTML("html");

	private String extension;

	TemplateFileType(String extension) {
		this.extension = extension;
	}

	String getExtension() {
		return extension;
	}
}
