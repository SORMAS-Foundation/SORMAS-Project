package de.symeda.sormas.ui.importer;

public class ImportColumnInformation {

	String entry;
	String[] entryHeaderPath;
	Class<?> entityClass;
	
	public ImportColumnInformation(String entry, String[] entryHeaderPath, Class<?> entityClass) {
		this.entry = entry;
		this.entryHeaderPath = entryHeaderPath;
		this.entityClass = entityClass;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public String[] getEntryHeaderPath() {
		return entryHeaderPath;
	}

	public void setEntryHeaderPath(String[] entryHeaderPath) {
		this.entryHeaderPath = entryHeaderPath;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}
	
}
