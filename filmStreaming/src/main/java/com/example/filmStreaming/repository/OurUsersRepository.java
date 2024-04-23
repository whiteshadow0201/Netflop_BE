package com.example.filmStreaming.repository;

import java.util.Optional;
import com.example.filmStreaming.model.OurUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OurUsersRepository extends JpaRepository<OurUsers, Long> {
    Optional<OurUsers> findByEmail(String email);

}
