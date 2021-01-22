package de.symeda.sormas.ui.utils;

public enum MimeTypes {
    CSV("text/csv", ".csv"),
    XSL("application/msexcel", ".xls"),
    XSLX("application/vnd.openxmlformats-officedocument. spreadsheetml.sheet", ".xlsx");

    String mimeType;
    String fileExtension;
    MimeTypes(String mimeType, String fileExtension) {
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
    }

    public String getMimeType() { return mimeType; }
    public String getFileExtension() { return fileExtension; }
}
