package com.lyf.lib.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class CryptoToolkit {

  private static CryptoToolkit INS;

  private static final Logger logger = LoggerFactory.getLogger(CryptoToolkit.class);

  private static final String AES_KEY_ALGORITHM = "AES";
  private static final String AES_CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";

  private final MessageDigest MD_SHA3_256;
  private final MessageDigest MD_SHA_256;
  private final MessageDigest MD_MD5;

  public static CryptoToolkit ins() {

    if(INS == null) {
      INS = new CryptoToolkit();
    }
    return INS;
  }

  private CryptoToolkit() {

    try {
      MD_SHA3_256 = MessageDigest.getInstance("SHA3-256");
      MD_SHA_256 = MessageDigest.getInstance("SHA-256");
      MD_MD5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }

  }

  public byte[] generateAesKey() {
    KeyGenerator aes;
    try {
      aes = KeyGenerator.getInstance(AES_KEY_ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("Generate the Aes-Key failed, " + e.getMessage(), e);
    }
    aes.init(256);
    SecretKey secretKey = aes.generateKey();
    return secretKey.getEncoded();
  }

  public String hmacInHex(String raw, String key) {
    return encodeHex(hmac(raw, key));
  }

  public byte[] hmac(String raw, String key) {
    HmacUtils hmacUtils = new HmacUtils("HmacSHA256", key);
    return hmacUtils.hmac(raw);
  }

  public String hashInHex(String raw, String method) {
    if(raw == null) return null;
    return encodeHex(hash(raw.getBytes(StandardCharsets.UTF_8), method));
  }

  public String hashInHex(String raw) {
    if(raw == null) return null;
    return encodeHex(hash(raw.getBytes(StandardCharsets.UTF_8), "SHA3-256"));
  }

  public String hashInHex(byte[] bytes, String method) {
    return encodeHex(hash(bytes, method));
  }

  public String hashInHex(byte[] bytes) {
    return encodeHex(hash(bytes, "SHA3-256"));
  }

  public byte[] hash(String raw) {
    return (raw == null)  ? null : hash(raw.getBytes(StandardCharsets.UTF_8), "SHA3-256");
  }

  public byte[] hash(byte[] bytes, String method) {

    byte[] result;
    switch (method) {
      case "MD5" -> result = MD_MD5.digest(bytes);
      case "SHA_256", "SHA-256" -> result = MD_SHA_256.digest(bytes);
      default -> result = MD_SHA3_256.digest(bytes);
    }
    return result;
  }

  public String encodeHex(String raw) {
    return (raw == null)  ? null : Hex.encodeHexString(raw.getBytes(StandardCharsets.UTF_8));
  }

  public String encodeHex(byte[] bytes) {
    return (bytes == null)  ? null : Hex.encodeHexString(bytes);
  }

  public byte[] decodeHex(String data) {
    try {
      return (data == null)  ? null : Hex.decodeHex(data);
    } catch (DecoderException e) {
      throw new RuntimeException(e);
    }
  }

  public String encodeBase32(String raw) {
    Base32 base32 = new Base32();
    return (raw == null)  ? null : base32.encodeAsString(raw.getBytes(StandardCharsets.UTF_8));
  }

  public byte[] decodeBase32(String raw) {
    Base32 base32 = new Base32();
    return (raw == null)  ? null : base32.decode(raw.getBytes(StandardCharsets.UTF_8));
  }

  public String decodeBase32ToString(String raw) {
    byte[] bytes = decodeBase32(raw);
    return bytes == null ? null : StringUtils.toEncodedString(bytes, StandardCharsets.UTF_8);
  }

  public String encodeBase64(String raw) {
    return (raw == null)  ? null : Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
  }

  public String encodeBase64(byte[] raw) {
    return (raw == null)  ? null : Base64.getEncoder().withoutPadding().encodeToString(raw);
  }

  public byte[] decodeBase64(String raw) {
    return (raw == null)  ? null : Base64.getDecoder().decode(raw);
  }

  public String decodeBase64ToString(String raw) {
    return (raw == null)  ? null : StringUtils.toEncodedString(decodeBase64(raw), StandardCharsets.UTF_8);
  }

  public byte[] encryptByAes(byte[] aesKey, byte [] data) {
    SecretKeySpec keySpec = new SecretKeySpec(aesKey, AES_KEY_ALGORITHM);
    try {
      Cipher cipher = Cipher.getInstance(AES_CIPHER_TRANSFORMATION);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec);
      return cipher.doFinal(data);
    }catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
      throw new RuntimeException(e.getMessage() + "aes-key:[" + encodeHex(aesKey) +"].", e);
    }
  }

  public byte[] decryptByAes(byte[] aesKey, byte[] encryptedText)  {
    SecretKeySpec keySpec = new SecretKeySpec(aesKey, AES_KEY_ALGORITHM);
    try {
      Cipher cipher = Cipher.getInstance(AES_CIPHER_TRANSFORMATION);
      cipher.init(Cipher.DECRYPT_MODE, keySpec);
      return cipher.doFinal(encryptedText);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
      throw new RuntimeException(e.getMessage() + ", aes-key:[" + encodeHex(aesKey) +"].", e);
    }
  }

  public String stringArrayToBase64(String[] input) {
    StringBuilder sb = new StringBuilder();
    for (String str : input) {
      if (str != null) {
        sb.append(str);
      }
      sb.append("\0");
    }

    byte[] byteArray = sb.toString().getBytes(StandardCharsets.UTF_8);
    return Base64.getEncoder().encodeToString(byteArray);
  }

  public String[] decodeBase64ToStringArray(String raw) {
    byte[] byteArray = Base64.getDecoder().decode(raw);
    String decodedString = new String(byteArray, StandardCharsets.UTF_8);

    return decodedString.split("\0");
  }

}
