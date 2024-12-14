package com.example.filmStreaming.controller;


import com.example.filmStreaming.dto.ReqRes;
//import com.example.filmStreaming.model.KeyPairSession;
//import com.example.filmStreaming.service.AuthService;
//import com.example.filmStreaming.service.KeyPairManager;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
@Log4j2
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Getter
public class AuthController {
//    @Autowired
//    private AuthService authService;
//
//    @Autowired
//    private KeyPairManager keyPairManager;
//
//    @PostMapping("/signup/{sessionId}")
//    public ResponseEntity<ReqRes> signUp (@RequestBody  ReqRes signUpRequest, @PathVariable String sessionId) throws Exception {
//        KeyPairSession keyPair = keyPairManager.getKeyPairForSession(sessionId);
//        if (keyPair == null) {
//            return ResponseEntity.status(404).body(null);
//        }
//        PrivateKey privateKey = keyPair.getPrivateKey();
//        String decryptedEmail = KeyPairManager.decryptRSA(signUpRequest.getEmail(), privateKey);
//        String decryptedName = KeyPairManager.decryptRSA(signUpRequest.getName(), privateKey);
//        String decryptedPassword = KeyPairManager.decryptRSA(signUpRequest.getPassword(), privateKey);
//        ReqRes decryptedSignUpRequest = new ReqRes();
//        decryptedSignUpRequest.setName(decryptedName);
//        decryptedSignUpRequest.setEmail(decryptedEmail);
//        decryptedSignUpRequest.setPassword(decryptedPassword);
//
//        return ResponseEntity.ok(authService.SignUp(decryptedSignUpRequest));}
//
//    @PostMapping("/signin/{sessionId}")
//    public ResponseEntity<ReqRes> signIn (@RequestBody ReqRes signInRequest, @PathVariable String sessionId) throws Exception {
//        KeyPairSession keyPair = keyPairManager.getKeyPairForSession(sessionId);
//        if (keyPair == null) {
//            return ResponseEntity.status(404).body(null);
//        }
//        PrivateKey privateKey = keyPair.getPrivateKey();
//        String decryptedEmail = KeyPairManager.decryptRSA(signInRequest.getEmail(), privateKey);
//        String decryptedPassword = KeyPairManager.decryptRSA(signInRequest.getPassword(), privateKey);
//
//        ReqRes decryptedSignUpRequest = new ReqRes();
//        decryptedSignUpRequest.setEmail(decryptedEmail);
//        decryptedSignUpRequest.setPassword(decryptedPassword);
//
//        return ResponseEntity.ok(authService.SignIn(decryptedSignUpRequest));
//    }


//    @GetMapping("/generate")
//    public ResponseEntity<String> generateKeyPair() {
//        try {
//            String sessionId = keyPairManager.generateKeyPairForSession();
//            return ResponseEntity.ok(sessionId);
//        } catch (NoSuchAlgorithmException e) {
//            return ResponseEntity.status(500).body("Error generating key pair: " + e.getMessage());
//        }
//    }
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

//    @GetMapping("/publicKey/{sessionId}")
//    public ResponseEntity<Map<String, String>> getPublicKey(@PathVariable String sessionId) {
//        KeyPairSession keyPair = keyPairManager.getKeyPairForSession(sessionId);
//        if (keyPair != null) {
//            RSAPublicKey publicKey = keyPair.getPublicKey();
//            BigInteger modulus = publicKey.getModulus();
//            BigInteger exponent = publicKey.getPublicExponent();
//
//            Map<String, String> response = new HashMap<>();
//            response.put("n", modulus.toString());
//            response.put("e", exponent.toString());
//
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.status(404).body(null);
//        }
//    }

    private String refreshToken;

    @Value("" +
            "${keycloak.token.endpoint}")
    private String keycloakTokenEndpoint;


    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret}")
    private String clientSecret;

    private final WebClient webClient;
    public AuthController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    @GetMapping("/keycloakLogin")
    public ResponseEntity<?> keycloakLogin(
                                           @RequestParam("code") String sessionCode,
                                           @RequestParam("redirect_uri") String redirectUri) {
        try{

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);
            formData.add("redirect_uri", redirectUri);
            formData.add("code", sessionCode);

            Map<String, Object> response = webClient.post()
                    .uri(keycloakTokenEndpoint)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            String accessToken = (String) response.get("access_token");
            refreshToken = (String) response.get("refresh_token");
            //System.out.println(refreshToken);
            return ResponseEntity.ok(Map.of(
                    "access_token", accessToken,
                    "refresh_token", refreshToken));
        } catch (Exception e){
            log.error("This is an ERROR message: ", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to exchange session code for token", "details",
                            e.getMessage()
                    ));
        }
    }
//    @PostMapping("/keyCloak/logout")
//    public ResponseEntity<String> keycloakLogout(@RequestHeader("Authorization") String authorizationHeader) {
//        try {
//            String token = "";
//            // Kiểm tra Authorization header
//            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//                token = authorizationHeader.substring(7); // Lấy token từ Bearer token
//            } else {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authorization header is missing or invalid");
//            }
//
//            // Chuẩn bị dữ liệu gửi đi trong form
//            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//            formData.add("client_id", clientId); // client_id của ứng dụng
//            formData.add("refresh_token", token); // Refresh token từ Authorization header
//            formData.add("client_secret", clientSecret);
//            // Gửi yêu cầu logout đến Keycloak
//            Map<String, Object> response = webClient.post()
//                    .uri(logoutEndpoint)
//                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                    .bodyValue(formData)
//                    .retrieve()
//                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
//                    .block();
//
//            // Kiểm tra phản hồi từ Keycloak (nếu cần)
//            if (response != null && response.containsKey("status") && response.get("status").equals("success")) {
//                return ResponseEntity.ok("Logout successful");
//            } else {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during logout");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during logout");
//        }
//    }
}
