package com.example.filmStreaming.service;

import com.example.filmStreaming.repository.FilmRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

import java.io.File;
import java.io.IOException;


@Service
public class HLSService {
    @Autowired
    private FilmRepository filmRepository;


    public HLSService() throws IOException {
    }
    public String generate(UUID uuid) {
        Optional<String> pathQuery = filmRepository.findPathByID(uuid);
        String outputM3U8Path;
        outputM3U8Path = pathQuery.orElse("Path not found");
        if (outputM3U8Path.equals("Path not found")) {
            return "Path not found";
        }

      String MP4_Input_Filename = uuid.toString() + ".mp4";
//        String MP4_Input_Filename = "test.mp4";
//        String MP4_Saver_Directory = "/home/wh1t3sh4d0w/Desktop/filmList";
        String MP4_Saver_Directory = "/root/filmList";

        String inputVideoPath = MP4_Saver_Directory + File.separator + MP4_Input_Filename;
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "ffmpeg",
                    "-threads", "4",
                    "-filter_complex_threads", "4",
                    "-vsync", "1",
                    "-i", inputVideoPath,

                    // Video Stream 1
                    "-map", "0:v:0",
                    "-s:v:0", "1920x1080",
                    "-c:v:0", "libx264",
                    "-pix_fmt:v:0", "yuv420p",
                    "-crf:v:0", "30",
                    "-profile:v:0", "high",
                    "-preset:v:0", "medium",
                    "-tune:v:0", "zerolatency",
                    "-force_key_frames:v:0", "expr:gte(t,n_forced*2.000)",
                    "-map", "0:a:0?",
                    "-c:a:0", "aac",
                    "-b:a:0", "128000",
                    "-ac:a:0", "2",
                    "-ar:a:0", "48000",

                    // Video Stream 2 (similar structure for remaining streams)
                    "-map", "0:v:0",
                    "-s:v:1", "1280x720",
                    "-c:v:1", "libx264",
                    "-pix_fmt:v:1", "yuv420p",
                    "-crf:v:1", "30",
                    "-profile:v:1", "high",
                    "-preset:v:1", "medium",
                    "-tune:v:1", "zerolatency",
                    "-force_key_frames:v:1", "expr:gte(t,n_forced*2.000)",
                    "-map", "0:a:0?",
                    "-c:a:1", "aac",
                    "-b:a:1", "128000",
                    "-ac:a:1", "2",
                    "-ar:a:1", "48000",



                    // Video Stream 3
                    "-map", "0:v:0",
                    "-s:v:2", "854x480",
                    "-c:v:2", "libx264",
                    "-pix_fmt:v:2", "yuv420p",
                    "-crf:v:2", "30",
                    "-profile:v:2", "high",
                    "-preset:v:2", "medium",
                    "-tune:v:2", "zerolatency",
                    "-force_key_frames:v:2", "expr:gte(t,n_forced*2.000)",
                    "-map", "0:a:0?",
                    "-c:a:2", "aac",
                    "-b:a:2", "128000",
                    "-ac:a:2", "2",
                    "-ar:a:2", "48000",


                    //Video Stream 4
                    "-map", "0:v:0",
                    "-s:v:3", "640x360",
                    "-c:v:3", "libx264",
                    "-pix_fmt:v:3", "yuv420p",
                    "-crf:v:3", "30",
                    "-profile:v:3", "high",
                    "-preset:v:3", "medium",
                    "-tune:v:3", "zerolatency",
                    "-force_key_frames:v:3", "expr:gte(t,n_forced*2.000)",
                    "-map", "0:a:0?",
                    "-c:a:3", "aac",
                    "-b:a:3", "128000",
                    "-ac:a:3", "2",
                    "-ar:a:3", "48000",



                    // Video Stream 5
                    "-map", "0:v:0",
                    "-s:v:4", "426x240",
                    "-c:v:4", "libx264",
                    "-pix_fmt:v:4", "yuv420p",
                    "-crf:v:4", "30",
                    "-profile:v:4", "high",
                    "-preset:v:4", "medium",
                    "-tune:v:4", "zerolatency",
                    "-force_key_frames:v:4", "expr:gte(t,n_forced*2.000)",
                    "-map", "0:a:0?",
                    "-c:a:4", "aac",
                    "-b:a:4", "128000",
                    "-ac:a:4", "2",
                    "-ar:a:4", "48000",

                    "-f", "hls",
                    "-hls_time", "10",
                    "-hls_list_size", "0",
                    "-var_stream_map", "v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3 v:4,a:4",
                    "-master_pl_name", "master.m3u8",
                    "-hls_segment_filename", outputM3U8Path + File.separator + "%v_%03d.ts", outputM3U8Path+ File.separator + "%v_index.m3u8"
            );
            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("Exited with error code " + exitCode);
            if (exitCode != 0) {
                return "HLS generation failed";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "HLS generation started";



    }
}
