//package com.example.filmStreaming.service;
//import java.nio.charset.StandardCharsets;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.NoSuchAlgorithmException;
//import java.security.PrivateKey;
//import java.security.interfaces.RSAPrivateKey;
//import java.security.interfaces.RSAPublicKey;
//import java.util.Base64;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//
//import com.example.filmStreaming.model.KeyPairSession;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.Cipher;
//
//@Service
//public class KeyPairManager {
//    private final ConcurrentHashMap<String, KeyPairSession> sessionKeyPairs = new ConcurrentHashMap<>();
//
//    public String generateKeyPairForSession() throws NoSuchAlgorithmException {
//        // Generate a new key pair
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//        keyPairGenerator.initialize(7680);
//        KeyPair keyPair = keyPairGenerator.genKeyPair();
//
//        // Create a UserKeyPair object
//        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//        KeyPairSession keyPairSession = new KeyPairSession(publicKey, privateKey);
//
//        // Generate a unique session ID and store the key pair
//        String sessionId = UUID.randomUUID().toString();
//        sessionKeyPairs.put(sessionId, keyPairSession);
//        return sessionId;
//    }
//
//    public KeyPairSession getKeyPairForSession(String sessionId) {
//        return sessionKeyPairs.get(sessionId);
//    }
//
//    public void removeKeyPairForSession(String sessionId) {
//        sessionKeyPairs.remove(sessionId);
//    }
//
//    public static String decryptRSA(String encryptedData, PrivateKey privateKey) throws Exception {
//        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
//        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
//        cipher.init(Cipher.DECRYPT_MODE, privateKey);
//        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
//
//        // Remove any leading zero bytes resulting from no padding
//        int startIndex = 0;
//        while (startIndex < decryptedBytes.length && decryptedBytes[startIndex] == 0) {
//            startIndex++;
//        }
//
//        return new String(decryptedBytes, startIndex, decryptedBytes.length - startIndex, StandardCharsets.UTF_8);
//    }
//    public static String EncryptRSA(String data, RSAPublicKey publicKey) throws Exception {
//        Cipher cipher = Cipher.getInstance("RSA");
//        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
//        return Base64.getEncoder().encodeToString(encryptedBytes);
//    }
//
//}
