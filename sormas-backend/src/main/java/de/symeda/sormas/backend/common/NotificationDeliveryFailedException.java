package de.symeda.sormas.backend.common;

@SuppressWarnings("serial")
public class NotificationDeliveryFailedException extends Exception {

	private MessageType messageType;
	
	public NotificationDeliveryFailedException(String message, MessageType messageType, Throwable cause) {
		super(message, cause);
		this.messageType = messageType;
	}
	
	public MessageType getMessageType() {
		return messageType;
	}
	
}
