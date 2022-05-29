package de.symeda.sormas.api.error.implementations;

import java.util.List;

import javax.ejb.ApplicationException;
import javax.ws.rs.core.Response;

@ApplicationException(rollback = true)
public class CustomizedException extends RuntimeException {

	private Response.Status status;

	private String message;

	private String entity;

	private int messageId;

	private List<String> argumentsList;

	public CustomizedException(CustomizedException exception) {
		this.status = exception.getStatus();
		this.message = exception.getMessage();
		this.entity = exception.getEntity();
		this.messageId = exception.getMessageId();
		this.argumentsList = exception.getArgumentsList();
	}

	public Response.Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public String getEntity() {
		return entity;
	}

	public int getMessageId() {
		return messageId;
	}

	public List<String> getArgumentsList() {
		return argumentsList;
	}

	public CustomizedException(Response.Status status, String message) {
		this.status = status;
		this.message = message;
	}

	public CustomizedException(Response.Status status, String message, String entity) {
		this.status = status;
		this.message = message;
		this.entity = entity;
	}

	public CustomizedException(Response.Status status, String message, String entity, int messageId) {
		this.status = status;
		this.message = message;
		this.entity = entity;
		this.messageId = messageId;
	}

	public CustomizedException(Response.Status status, String message, int messageId) {
		this.status = status;
		this.message = message;
		this.messageId = messageId;
	}

	public CustomizedException(Response.Status status, String message, Class entity, int messageId, List<String> argumentsList) {
		this.status = status;
		this.message = message;
		this.entity = entity.getCanonicalName();
		this.messageId = messageId;
		this.argumentsList = argumentsList;
	}

}
