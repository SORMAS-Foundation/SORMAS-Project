package de.symeda.sormas.backend.importexport.hierarchical.engine;

public final class ColumnSpec {

	private final String header;
	private final String caption;
	private final String[] propertyPath;
	private final String target;

	public ColumnSpec(String header, String caption, String... propertyPath) {
		this(header, caption, null, propertyPath);
	}

	private ColumnSpec(String header, String caption, String target, String[] propertyPath) {
		this.header = header;
		this.caption = caption;
		this.target = target;
		this.propertyPath = propertyPath.clone();
	}

	public static ColumnSpec forTarget(String header, String caption, String target, String[] propertyPath) {
		return new ColumnSpec(header, caption, target, propertyPath);
	}

	public String header() {
		return header;
	}

	public String caption() {
		return caption;
	}

	public String target() {
		return target != null ? target : ImportLevel.prefixOf(header);
	}

	public String[] propertyPath() {
		return propertyPath.clone();
	}

	public boolean isProperty() {
		return propertyPath.length > 0;
	}
}
