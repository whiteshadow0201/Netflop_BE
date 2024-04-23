package com.example.filmStreaming.dto;

import com.example.filmStreaming.model.Film;
import com.example.filmStreaming.model.OurUsers;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties (ignoreUnknown = true)
@JsonInclude (JsonInclude.Include.NON_NULL)
public class ReqRes {
    private int statusCode;
    private String message;
    private String error;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String filmName;
    private String role;
    private String email;
    private String password;
    private OurUsers ourUsers;
    private List<Film> films;
    private String path;
    private UUID uuid;

}
