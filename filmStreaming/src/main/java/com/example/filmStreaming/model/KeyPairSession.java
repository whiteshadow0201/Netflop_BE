package com.example.filmStreaming.model;
import lombok.Getter;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
@Getter
public class KeyPairSession {
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    public KeyPairSession(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

}
