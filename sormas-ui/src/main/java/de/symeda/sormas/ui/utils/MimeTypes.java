package de.symeda.sormas.ui.utils;

public enum MimeTypes {
    CSV("text/csv", ".csv", "CSV"),
    XSLX("application/vnd.openxmlformats-officedocument. spreadsheetml.sheet", ".xlsx", "XLSX");

    String mimeType;
    String fileExtension;
    String name;
    MimeTypes(String mimeType, String fileExtension, String name) {
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
        this.name = name;
    }

    public String getMimeType() { return mimeType; }
    public String getFileExtension() { return fileExtension; }
    public String getName() { return name; }
}
