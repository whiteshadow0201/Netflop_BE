package com.example.filmStreaming.service;
import com.example.filmStreaming.repository.OurUsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
public class OurUserDetailsService implements UserDetailsService{
    @Autowired
    private OurUsersRepository ourUsersRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return ourUsersRepository.findByEmail(username).orElseThrow();
    }

}
