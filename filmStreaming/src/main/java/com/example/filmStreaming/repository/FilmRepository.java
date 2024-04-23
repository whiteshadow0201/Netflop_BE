package com.example.filmStreaming.repository;

import com.example.filmStreaming.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FilmRepository extends JpaRepository<Film, UUID> {

    @Override
    Optional<Film> findById(UUID uuid);

    @Query("SELECT f.path FROM Film f WHERE f.uuid = :uuid")
    Optional<String> findPathByID(@Param("uuid") UUID uuid);

    @Query("SELECT f.filmName FROM Film f WHERE f.uuid = :uuid")
    Optional<String> findFilmNameByUuid(@Param("uuid") UUID uuid);
}
