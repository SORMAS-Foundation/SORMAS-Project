package de.symeda.sormas.ui.importer;

public class ImportCellData {

	String value;
	String entityClass;
	String[] entityPropertyPath;

	public ImportCellData(String value, String entityClass, String[] entityPropertyPath) {
		this.value = value;
		this.entityClass = entityClass;
		this.entityPropertyPath = entityPropertyPath;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}

	public String[] getEntityPropertyPath() {
		return entityPropertyPath;
	}

	public void setEntityPropertyPath(String[] entityPropertyPath) {
		this.entityPropertyPath = entityPropertyPath;
	}
}
