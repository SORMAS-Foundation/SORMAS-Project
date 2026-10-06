package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class Fingerprint {

	private Fingerprint() {
	}

	static String of(PlannedRecord root) {
		StringBuilder canonical = new StringBuilder();
		root.subtree().forEach(record -> {
			canonical.append(record.node().level().name())
				.append('|')
				.append(record.node().lineNumber())
				.append('|')
				.append(record.operation())
				.append('|')
				.append(record.identity())
				.append('|');
			record.versions().forEach(version -> canonical.append(version == null ? "-" : version.getTime()).append(','));
			record.changes()
				.forEach(change -> canonical.append(change.getField()).append('=').append(change.getOldValue()).append('>').append(change.getNewValue()).append(';'));
			canonical.append('\n');
		});
		return sha256(canonical.toString());
	}

	private static String sha256(String text) {
		try {
			byte[] hash = MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
			StringBuilder hex = new StringBuilder();
			for (byte value : hash) {
				hex.append(String.format("%02x", value));
			}
			return hex.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
}
