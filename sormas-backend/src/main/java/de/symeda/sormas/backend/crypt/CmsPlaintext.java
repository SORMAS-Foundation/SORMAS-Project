package de.symeda.sormas.backend.crypt;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Naive Sign-then-Encrypt is not secure, read
 * https://theworld.com/~dtd/sign_encrypt/sign_encrypt7.html
 * for details.
 *
 * This class enforces that both, the sender's and the receiver's identity are included in the plain text to
 * mitigate surreptitious forwarding.
 */
public class CmsPlaintext {

	private final String senderId;
	private final String receiverId;
	private final byte[] message;

	public CmsPlaintext(String sender, String receiver, Object message) throws JsonProcessingException {
		this.senderId = sender;
		this.receiverId = receiver;

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		objectMapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY);

		this.message = objectMapper.writeValueAsBytes(message);
	}

	@JsonCreator
	public CmsPlaintext(@JsonProperty("sender") String sender, @JsonProperty("receiver") String receiver, @JsonProperty("message") byte[] message) {
		this.senderId = sender;
		this.receiverId = receiver;
		this.message = message;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public String getSenderId() {
		return senderId;
	}

	public byte[] getMessage() {
		return message;
	}
}
