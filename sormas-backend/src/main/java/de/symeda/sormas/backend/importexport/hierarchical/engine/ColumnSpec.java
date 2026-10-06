package de.symeda.sormas.backend.importexport.hierarchical.engine;

public final class ColumnSpec {

	private final String header;
	private final String caption;
	private final String[] propertyPath;

	public ColumnSpec(String header, String caption, String... propertyPath) {
		this.header = header;
		this.caption = caption;
		this.propertyPath = propertyPath.clone();
	}

	public String header() {
		return header;
	}

	public String caption() {
		return caption;
	}

	public String target() {
		return ImportLevel.prefixOf(header);
	}

	public String[] propertyPath() {
		return propertyPath.clone();
	}

	public boolean isProperty() {
		return propertyPath.length > 0;
	}
}
