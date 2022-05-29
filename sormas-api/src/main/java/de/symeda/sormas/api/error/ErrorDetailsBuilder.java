package de.symeda.sormas.api.error;

import java.util.List;

public class ErrorDetailsBuilder {
    private String message;
    private String entity;
    private int messageId;
    private List<String> argumentsList;

    public ErrorDetailsBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public ErrorDetailsBuilder setEntity(String entity) {
        this.entity = entity;
        return this;
    }

    public ErrorDetailsBuilder setMessageId(int messageId) {
        this.messageId = messageId;
        return this;
    }

    public ErrorDetailsBuilder setArgumentsList(List<String> argumentsList) {
        this.argumentsList = argumentsList;
        return this;
    }

    public ErrorDetails build() {
        return new ErrorDetails(message, entity, messageId, argumentsList);
    }

}