package com.example.filmStreaming.service;

import com.example.filmStreaming.dto.ReqRes;
import com.example.filmStreaming.model.OurUsers;
import com.example.filmStreaming.repository.OurUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthService
{
    @Autowired
    private OurUsersRepository ourUsersRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public ReqRes SignUp (ReqRes registrationRequest)
    {
        ReqRes resp = new ReqRes();
        try{
            String email = registrationRequest.getEmail();
            String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                OurUsers ourUsers = new OurUsers();
                if (ourUsersRepository.findByEmail(registrationRequest.getEmail()).isPresent())
                {
                    resp.setStatusCode(400);
                    resp.setMessage("Email already exists");
                    return resp;
                }
                ourUsers.setName(registrationRequest.getName());
                ourUsers.setEmail(registrationRequest.getEmail());
                ourUsers.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
                ourUsers.setRole("user");
                OurUsers ourUserResult = ourUsersRepository.save(ourUsers);
                if (ourUserResult !=null && ourUserResult.getId() > 0)
                {
                    resp.setOurUsers(ourUserResult);
                    resp.setMessage("User Registered Successfully");
                    resp.setStatusCode(200);
                }
            } else {
                resp.setStatusCode(400);
                resp.setMessage("Invalid email format");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes SignIn (ReqRes signInRequest)
    {
        ReqRes response = new ReqRes();
        try{
           authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));
           var user = ourUsersRepository.findByEmail(signInRequest.getEmail()).orElseThrow();
           System.out.println(user);
           var jwt = jwtUtils.generateToken(user);
           var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(),user);
           response.setStatusCode(200);
           response.setToken(jwt);
           response.setRefreshToken(refreshToken);
           response.setExpirationTime("12hrs"); // 12 hours
           response.setMessage("User Logged in Successfully");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

//    public ReqRes refreshToken (ReqRes refreshTokenRequest)
//    {
//        ReqRes response = new ReqRes();
//        String ourEmail = jwtUtils.extractUsername(refreshTokenRequest.getRefreshToken());
//        OurUsers users = ourUsersRepository.findByEmail(ourEmail).orElseThrow();
//        if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), users)){
//            var jwt = jwtUtils.generateToken(users);
//            response.setStatusCode(200);
//            response.setToken(jwt);
//            response.setExpirationTime("12hrs");
//            response.setMessage("Token Refreshed Successfully");
//
//        }
//        response.setStatusCode(500);
//        return response;
//    }
}
