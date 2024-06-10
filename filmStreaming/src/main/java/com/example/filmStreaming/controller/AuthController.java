package com.example.filmStreaming.controller;


import com.example.filmStreaming.dto.ReqRes;
import com.example.filmStreaming.model.KeyPairSession;
import com.example.filmStreaming.service.AuthService;
import com.example.filmStreaming.service.KeyPairManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private KeyPairManager keyPairManager;

    @PostMapping("/signup/{sessionId}")
    public ResponseEntity<ReqRes> signUp (@RequestBody  ReqRes signUpRequest, @PathVariable String sessionId) throws Exception {
        KeyPairSession keyPair = keyPairManager.getKeyPairForSession(sessionId);
        if (keyPair == null) {
            return ResponseEntity.status(404).body(null);
        }
        PrivateKey privateKey = keyPair.getPrivateKey();
        String decryptedEmail = KeyPairManager.decryptRSA(signUpRequest.getEmail(), privateKey);
        String decryptedName = KeyPairManager.decryptRSA(signUpRequest.getName(), privateKey);
        String decryptedPassword = KeyPairManager.decryptRSA(signUpRequest.getPassword(), privateKey);
        ReqRes decryptedSignUpRequest = new ReqRes();
        decryptedSignUpRequest.setName(decryptedName);
        decryptedSignUpRequest.setEmail(decryptedEmail);
        decryptedSignUpRequest.setPassword(decryptedPassword);

        return ResponseEntity.ok(authService.SignUp(decryptedSignUpRequest));}

    @PostMapping("/signin/{sessionId}")
    public ResponseEntity<ReqRes> signIn (@RequestBody ReqRes signInRequest, @PathVariable String sessionId) throws Exception {
        KeyPairSession keyPair = keyPairManager.getKeyPairForSession(sessionId);
        if (keyPair == null) {
            return ResponseEntity.status(404).body(null);
        }
        PrivateKey privateKey = keyPair.getPrivateKey();
        String decryptedEmail = KeyPairManager.decryptRSA(signInRequest.getEmail(), privateKey);
        String decryptedPassword = KeyPairManager.decryptRSA(signInRequest.getPassword(), privateKey);

        ReqRes decryptedSignUpRequest = new ReqRes();
        decryptedSignUpRequest.setEmail(decryptedEmail);
        decryptedSignUpRequest.setPassword(decryptedPassword);

        return ResponseEntity.ok(authService.SignIn(decryptedSignUpRequest));
    }


    @GetMapping("/generate")
    public ResponseEntity<String> generateKeyPair() {
        try {
            String sessionId = keyPairManager.generateKeyPairForSession();
            return ResponseEntity.ok(sessionId);
        } catch (NoSuchAlgorithmException e) {
            return ResponseEntity.status(500).body("Error generating key pair: " + e.getMessage());
        }
    }
//    @PostMapping("/encrypt/{sessionId}/{data}")
//    public ResponseEntity<String> encryption(@PathVariable String sessionId, @PathVariable String data){
//        KeyPairSession keyPair = keyPairManager.getKeyPairForSession(sessionId);
//        if (keyPair == null) {
//            return ResponseEntity.status(404).body(null);
//        }
//        RSAPublicKey publicKey = keyPair.getPublicKey();
//        try {
//            String encryptedData = KeyPairManager.EncryptRSA(data, publicKey);
//            return ResponseEntity.ok(encryptedData);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error encrypting data: " + e.getMessage());
//        }
//    }

    @GetMapping("/publicKey/{sessionId}")
    public ResponseEntity<Map<String, String>> getPublicKey(@PathVariable String sessionId) {
        KeyPairSession keyPair = keyPairManager.getKeyPairForSession(sessionId);
        if (keyPair != null) {
            RSAPublicKey publicKey = keyPair.getPublicKey();
            BigInteger modulus = publicKey.getModulus();
            BigInteger exponent = publicKey.getPublicExponent();

            Map<String, String> response = new HashMap<>();
            response.put("n", modulus.toString());
            response.put("e", exponent.toString());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }



}
