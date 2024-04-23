package com.example.filmStreaming.controller;

import com.example.filmStreaming.dto.ReqRes;
import com.example.filmStreaming.model.Film;
import com.example.filmStreaming.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
public class AdminUser {
    @Autowired
    private FilmRepository filmRepository;

    @PostMapping("/admin/getAllFilmsInfo")
    public ResponseEntity<Object> getAllFilms() {
        return ResponseEntity.ok(filmRepository.findAll());
    }

    @PostMapping("/user/getFilm")
    public ResponseEntity<Object> getFilm(@RequestBody ReqRes reqRes) {
        return ResponseEntity.ok(filmRepository.findPathByID(reqRes.getUuid()));
    }

    @PostMapping("/admin/uploadFilmInfo")
    public ResponseEntity<UUID> uploadFilm(@RequestBody ReqRes filmRequest) {
        String storagePath = "/home/wh1t3sh4d0w/Desktop/filmList";
        Film filmToSave = new Film();
        filmToSave.setFilmName(filmRequest.getFilmName());
        filmToSave.setPath("");
        Film savedFilm = filmRepository.save(filmToSave);
        String filePath = storagePath + File.separator + savedFilm.getUuid().toString();
        savedFilm.setPath(filePath); // Cập nhật đường dẫn của đối tượng Film
        savedFilm = filmRepository.save(savedFilm);
        return ResponseEntity.ok(savedFilm.getUuid());
    }


}