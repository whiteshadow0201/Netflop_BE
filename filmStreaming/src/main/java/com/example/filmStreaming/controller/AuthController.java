package com.example.filmStreaming.controller;


import com.example.filmStreaming.dto.ReqRes;
import com.example.filmStreaming.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ReqRes> signUp (@RequestBody  ReqRes signUpRequest)
    {
        return ResponseEntity.ok(authService.SignUp(signUpRequest));
    }

    @PostMapping("/signin")
    public ResponseEntity<ReqRes> signIn (@RequestBody ReqRes signInRequest)
    {
        return ResponseEntity.ok(authService.SignIn(signInRequest));
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<ReqRes> refreshToken (@RequestBody ReqRes refreshTokenRequest)
//    {
//        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
//    }

}
