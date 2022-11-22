package de.symeda.sormas.api.utils;

public class FileContentsDoNotMatchExtensionException extends ValidationRuntimeException {

    public FileContentsDoNotMatchExtensionException(String message) {
        super(message);
    }
}