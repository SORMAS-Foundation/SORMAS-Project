package org.sormas.e2etests.entities.pojo.helpers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class ShortUUIDGenerator {

  public static String generateShortUUID() throws NoSuchAlgorithmException {
    UUID uuid = UUID.randomUUID();
    String uuidStr = uuid.toString();
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] hashBytes = md.digest(uuidStr.getBytes(StandardCharsets.UTF_8));
    String hashHex = bytesToHex(hashBytes);
    return hashHex.substring(0, 6).toUpperCase()
        + "-"
        + hashHex.substring(6, 12).toUpperCase()
        + "-"
        + hashHex.substring(12, 18).toUpperCase()
        + "-"
        + hashHex.substring(18, 26).toUpperCase();
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}
