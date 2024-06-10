package com.example.filmStreaming.controller;
import com.example.filmStreaming.dto.ReqRes;
import com.example.filmStreaming.repository.FilmRepository;
import com.example.filmStreaming.service.HLSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/hls")
public class HLSController {

    @Autowired
    private HLSService hlsService;
/*    @Autowired
    private FilmRepository filmRepository;*/



    @PostMapping("/generate/{uuid}")
    public String generate(@PathVariable UUID uuid) {
        return hlsService.generate(uuid);
    }


}
