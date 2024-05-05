package com.example.filmStreaming.controller;

import com.example.filmStreaming.dto.ReqRes;
import com.example.filmStreaming.model.Film;
import com.example.filmStreaming.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.io.File;
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
//
    @PostMapping("/admin/uploadFilmInfo")
    public ResponseEntity<UUID> uploadFilm(@RequestBody ReqRes filmRequest) {
//        String storagePath = "/home/wh1t3sh4d0w/Desktop/filmList";
        String storagePath = "/root/filmList";
        Film filmToSave = new Film();
        if (filmRequest.getDescription()== null)
            filmToSave.setDescription("");
        else filmToSave.setDescription(filmRequest.getDescription());
        filmToSave.setFilmName(filmRequest.getFilmName());
        filmToSave.setPath("");
        Film savedFilm = filmRepository.save(filmToSave);
        String filePath = storagePath + File.separator + savedFilm.getUuid().toString();
        savedFilm.setPath(filePath); // Cập nhật đường dẫn của đối tượng Film
        savedFilm = filmRepository.save(savedFilm);
        File directory = new File(filePath);
        if (!directory.exists()) {
            // Nếu thư mục chưa tồn tại, sử dụng phương thức mkdir() để tạo thư mục
            boolean created = directory.mkdir();
        }
        return ResponseEntity.ok(savedFilm.getUuid());
    }

    @PostMapping("/admin/upload")
    public ResponseEntity<String> handleFileUpload(@RequestPart("file") MultipartFile file) {
        if (file == null) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }

        try {
            // Xử lý tệp được tải lên ở đây
            // Ví dụ: lưu tệp vào thư mục trên máy chủ
            String fileName = file.getOriginalFilename();
            String filePath = "/root/filmList"+ File.separator + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);

            // Trả về phản hồi thành công nếu upload thành công
            return ResponseEntity.ok().body("File uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }

    @GetMapping(value = "/public/media/{uuid}/{fileName}", produces = MediaType.ALL_VALUE)
    public FileSystemResource getMediaFile(@PathVariable  UUID uuid, @PathVariable String fileName) {
        // Lấy đường dẫn cơ sở của thư mục chứa các tệp
        Optional<String> pathQuery = filmRepository.findPathByID(uuid);
        String basePath = pathQuery.orElse("Path not found");
        if (basePath.equals("Path not found")) {
            return null;
        }
        // Kết hợp đường dẫn cơ sở với tên tệp được yêu cầu
        String fullPath = basePath + File.separator + fileName;

        // Trả về FileSystemResource cho tệp được yêu cầu
        return new FileSystemResource(fullPath);
    }
}