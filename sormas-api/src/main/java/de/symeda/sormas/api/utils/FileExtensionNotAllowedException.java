package de.symeda.sormas.api.utils;

public class FileExtensionNotAllowedException extends ValidationRuntimeException {

    public FileExtensionNotAllowedException(String message) {
        super(message);
    }
}