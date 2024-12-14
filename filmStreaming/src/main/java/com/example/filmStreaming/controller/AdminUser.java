package com.example.filmStreaming.controller;

import com.example.filmStreaming.dto.ReqRes;
import com.example.filmStreaming.model.Film;
import com.example.filmStreaming.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.io.File;
@CrossOrigin(origins = "*")
@RestController
public class AdminUser {
    @Autowired
    private FilmRepository filmRepository;

    @GetMapping("/user/getAllFilmsInfo")
    public ResponseEntity<Object> getAllFilms() {
        List<Film> films = filmRepository.findAll();
        return ResponseEntity.ok(films);
    }

    @GetMapping("/user/getFilm/{uuid}")
    public ResponseEntity<Object> getFilm(@PathVariable UUID uuid) {
        Optional<Film> film = filmRepository.findById(uuid);
        return film.<ResponseEntity<Object>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
//
    @PostMapping("/admin/uploadFilmInfo")
    public ResponseEntity<UUID> uploadFilm(@RequestBody ReqRes filmRequest) {
//        String storagePath = "/home/wh1t3sh4d0w/Desktop/filmList";
        String storagePath = "/root/HLS_File_Folder";
        Film filmToSave = new Film();
        if (filmRequest.getDescription()== null)
            filmToSave.setDescription("");
        else filmToSave.setDescription(filmRequest.getDescription());
        if (filmRequest.getPoster()==null)
            filmToSave.setPoster("");
        else filmToSave.setPoster(filmRequest.getPoster());
        if (filmRequest.getReleasedDate()==null)
            filmToSave.setReleasedDate("");
        else filmToSave.setReleasedDate(filmRequest.getReleasedDate());
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
            return ResponseEntity.badRequest().body("Please select a file to upload.\n");
        }

        try {
            // Xử lý tệp được tải lên ở đây
            // Ví dụ: lưu tệp vào thư mục trên máy chủ
            String fileName = file.getOriginalFilename();
            String filePath = "/root/filmList"+ File.separator + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);

            // Trả về phản hồi thành công nếu upload thành công
            return ResponseEntity.ok().body("File uploaded successfully.\n");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }
    @PostMapping("/admin/uploadPoster/{uuid}")
    public ResponseEntity<String> handleImageUpload(@RequestPart("file") MultipartFile file, @PathVariable UUID uuid) {
        if (file == null) {
            return ResponseEntity.badRequest().body("Please select a file to upload.\n");
        }

        try {
            Film updatedFilm = filmRepository.findById(uuid).orElse(null);
            if (updatedFilm == null){
                return ResponseEntity.ok().body("Requested film not found.\n");
            }
            // Xử lý tệp được tải lên ở đây
            // Ví dụ: lưu tệp vào thư mục trên máy chủ
            String fileName = file.getOriginalFilename();
            String filePath = "/root/HLS_File_Folder"+ File.separator + uuid + File.separator + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);
            updatedFilm.setPoster(fileName);
            // Trả về phản hồi thành công nếu upload thành công
            return ResponseEntity.ok().body("File uploaded successfully.\n");
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

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret}")
    private String clientSecret;

    @Value("${keycloak.logout.endpoint}")
    private String logoutEndpoint;

    private final WebClient webClient;
    public AdminUser(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @PostMapping("/user/keyCloak/logout")
    public ResponseEntity<String> keycloakLogout(@RequestBody Map<String, String> request ) {
        try {
                String refreshToken = request.get("refresh_token");
                System.out.println(refreshToken);
                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                formData.add("client_id", clientId); // client_id của ứng dụng
                formData.add("refresh_token", refreshToken); // Refresh token từ Authorization header
                formData.add("client_secret", clientSecret);
                // Gửi yêu cầu logout đến Keycloak
                Map<String, Object> response = webClient.post()
                        .uri(logoutEndpoint)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .bodyValue(formData)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();
                System.out.println(response);
            // Kiểm tra phản hồi từ Keycloak (nếu cần)
            return ResponseEntity.ok("Logout successful");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during logout");
        }
    }
    @PostMapping("/admin/test")
    public ResponseEntity<String> testAdminrole(){
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body("This is admin");
    }


}