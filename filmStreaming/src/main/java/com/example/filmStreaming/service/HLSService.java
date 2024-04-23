package com.example.filmStreaming.service;

import com.example.filmStreaming.repository.FilmRepository;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Service
public class HLSService {
    @Autowired
    private FilmRepository filmRepository;

    private final FFmpeg ffmpeg = new FFmpeg();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public HLSService() throws IOException {
    }
    public String generate(UUID uuid) {
        Optional<String> pathQuery = filmRepository.findPathByID(uuid);
        String outputM3U8Path;
        outputM3U8Path = pathQuery.orElse("Path not found");
        if (outputM3U8Path.equals("Path not found")) {
            return "Path not found";
        }
//      String MP4_Input_Filename = uuid.toString() + ".mp4";
        String MP4_Input_Filename = "test.mp4";
        String MP4_Saver_Directory = "/home/wh1t3sh4d0w/Desktop/filmList";
        String inputVideoPath = MP4_Saver_Directory + File.separator + MP4_Input_Filename;


        try {
            Thread thread720p =
                    new Thread() {
                        public void run() {
                            generateHLSWithResolution(inputVideoPath, outputM3U8Path,  "720p", 1280, 720, 4000);
                        }
                    };
            thread720p.start();

            Thread thread480p =
                    new Thread() {
                        public void run() {
                            generateHLSWithResolution(inputVideoPath, outputM3U8Path,  "480p", 854, 480, 2000);
                        }
                    };
            thread480p.start();

            Thread thread360p =
                    new Thread() {
                        public void run() {
                            generateHLSWithResolution(inputVideoPath, outputM3U8Path,  "360p", 640, 360, 1000);
                        }
                    };
            thread360p.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Failed HLS generation for film: " + uuid;
        }
        return "Successful HLS generation for film: " + uuid;
    }

    private void generateHLSWithResolution(String inputVideoPath, String outputM3U8Path, String resolution, int width, int height, int bitrate) {
        String outputDirectoryPath = outputM3U8Path +  File.separator  + resolution ;
        File outputDirectory = new File(outputDirectoryPath);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        executorService.submit(() -> {
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(inputVideoPath)
                    .addOutput( outputDirectoryPath + File.separator  + resolution + ".m3u8")
                    .setFormat("hls")
                    .setAudioCodec("aac")
                    .setVideoCodec("libx264")
                    .setVideoResolution(width, height)
                    .setVideoFrameRate(30)
                    .addExtraArgs("-hls_list_size", "0") // Thiết lập hls_list_size thành 0 để bao gồm tất cả các phân đoạn
//                    .setVideoBitRate(bitrate)
                    .done();

            try {
                new FFmpegExecutor(ffmpeg).createJob(builder).run();
                return "Successful HLS generation for resolution: " + resolution;
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed HLS generation for resolution: " + resolution;
            }
        });
    }
}
