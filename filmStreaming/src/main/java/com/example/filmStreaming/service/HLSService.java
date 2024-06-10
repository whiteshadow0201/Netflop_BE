package com.example.filmStreaming.service;

import com.example.filmStreaming.repository.FilmRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;


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
//        outputM3U8Path = "/home/wh1t3sh4d0w/Desktop/filmList/test_output";

      String MP4_Input_Filename = uuid.toString() + ".mp4";
//        String MP4_Input_Filename = "test.mp4";
//        String MP4_Saver_Directory = "/home/wh1t3sh4d0w/Desktop/filmList";
        String MP4_Saver_Directory = "/root/filmList";

        String inputVideoPath = MP4_Saver_Directory + File.separator + MP4_Input_Filename;
//        try {
//            ProcessBuilder builder = new ProcessBuilder(
//                    "ffmpeg",
//                    "-threads", "4",
//                    "-filter_complex_threads", "4",
//                    "-vsync", "1",
//                    "-i", inputVideoPath,
//
//                    // Video Stream 1
//                    "-map", "0:v:0",
//                    "-s:v:0", "1920x1080",
//                    "-c:v:0", "libx264",
//                    "-pix_fmt:v:0", "yuv420p",
//                    "-b:v:0", "5000k",
//                    "-maxrate:v:0", "5000k",
//                    "-bufsize:v:0", "10000k",
//                    "-profile:v:0", "high",
//                    "-preset:v:0", "medium",
//                    "-tune:v:0", "zerolatency",
//                    "-force_key_frames:v:0", "expr:gte(t,n_forced*2.000)",
//                    "-map", "0:a:0?",
//                    "-c:a:0", "aac",
//                    "-b:a:0", "128000",
//                    "-ac:a:0", "2",
//                    "-ar:a:0", "48000",
//
//                    // Video Stream 2 (similar structure for remaining streams)
//                    "-map", "0:v:0",
//                    "-s:v:1", "1280x720",
//                    "-c:v:1", "libx264",
//                    "-pix_fmt:v:1", "yuv420p",
//                    "-b:v:1", "3000k",
//                    "-maxrate:v:1", "3000k",
//                    "-bufsize:v:1", "6000k",
//                    "-profile:v:1", "high",
//                    "-preset:v:1", "medium",
//                    "-tune:v:1", "zerolatency",
//                    "-force_key_frames:v:1", "expr:gte(t,n_forced*2.000)",
//                    "-map", "0:a:0?",
//                    "-c:a:1", "aac",
//                    "-b:a:1", "128000",
//                    "-ac:a:1", "2",
//                    "-ar:a:1", "48000",
//
//
//
//                    // Video Stream 3
//                    "-map", "0:v:0",
//                    "-s:v:2", "854x480",
//                    "-c:v:2", "libx264",
//                    "-pix_fmt:v:2", "yuv420p",
//                    "-b:v:2", "1500k",
//                    "-maxrate:v:2", "1500k",
//                    "-bufsize:v:2", "3000k",
//                    "-profile:v:2", "high",
//                    "-preset:v:2", "medium",
//                    "-tune:v:2", "zerolatency",
//                    "-force_key_frames:v:2", "expr:gte(t,n_forced*2.000)",
//                    "-map", "0:a:0?",
//                    "-c:a:2", "aac",
//                    "-b:a:2", "128000",
//                    "-ac:a:2", "2",
//                    "-ar:a:2", "48000",
//
//
//                    //Video Stream 4
//                    "-map", "0:v:0",
//                    "-s:v:3", "640x360",
//                    "-c:v:3", "libx264",
//                    "-pix_fmt:v:3", "yuv420p",
//                    "-b:v:3", "1000k",
//                    "-maxrate:v:3", "1000k",
//                    "-bufsize:v:3", "2000k",
//                    "-profile:v:3", "high",
//                    "-preset:v:3", "medium",
//                    "-tune:v:3", "zerolatency",
//                    "-force_key_frames:v:3", "expr:gte(t,n_forced*2.000)",
//                    "-map", "0:a:0?",
//                    "-c:a:3", "aac",
//                    "-b:a:3", "128000",
//                    "-ac:a:3", "2",
//                    "-ar:a:3", "48000",
//
//
//
//                    // Video Stream 5
//                    "-map", "0:v:0",
//                    "-s:v:4", "426x240",
//                    "-c:v:4", "libx264",
//                    "-pix_fmt:v:4", "yuv420p",
//                    "-b:v:4", "500k",
//                    "-maxrate:v:4", "500k",
//                    "-bufsize:v:4", "1000k",
//                    "-profile:v:4", "high",
//                    "-preset:v:4", "medium",
//                    "-tune:v:4", "zerolatency",
//                    "-force_key_frames:v:4", "expr:gte(t,n_forced*2.000)",
//                    "-map", "0:a:0?",
//                    "-c:a:4", "aac",
//                    "-b:a:4", "128000",
//                    "-ac:a:4", "2",
//                    "-ar:a:4", "48000",
//
//                    "-f", "hls",
//                    "-hls_time", "10",
//                    "-hls_list_size", "0",
//                    "-var_stream_map", "v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3 v:4,a:4",
//                    "-master_pl_name", "master.m3u8",
//                    "-hls_segment_filename", outputM3U8Path + File.separator + "%v_%03d.ts", outputM3U8Path+ File.separator + "%v_index.m3u8"
//            );
//            builder.redirectErrorStream(true);
//            Process process = builder.start();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
//            int exitCode = process.waitFor();
//            System.out.println("Exited with error code " + exitCode);
//            if (exitCode != 0) {
//                return "HLS generation failed";
//            }
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
        int maxWidth = 0;
        int maxHeight = 0;
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-select_streams", "v:0",
                    "-show_entries", "stream=width,height",
                    "-of", "csv=s=x:p=0",
                    inputVideoPath
            );
            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            if ((line = reader.readLine()) != null) {
                String[] resolution = line.split("x");
                maxWidth = Integer.parseInt(resolution[0]);
                maxHeight = Integer.parseInt(resolution[1]);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Failed to get video resolution");
                return "Failed to get video resolution";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error occurred while getting video resolution";
        }

// Proceed only if we have a valid resolution
        if (maxWidth == 0 || maxHeight == 0) {
            return "Invalid video resolution";
        }

        try {
            // Adjust according to the maximum resolution
            List<String[]> streamSettings = new ArrayList<>();
            streamSettings.add(new String[]{"426x240", "500k", "1000k"});
            streamSettings.add(new String[]{"640x360", "1000k", "2000k"});
            streamSettings.add(new String[]{"854x480", "1500k", "3000k"});
            streamSettings.add(new String[]{"1280x720", "3000k", "6000k"});
            streamSettings.add(new String[]{"1920x1080", "5000k", "10000k"});

            int streamCount = 0;
            for (String[] settings : streamSettings) {
                if (maxWidth >= Integer.parseInt(settings[0].split("x")[0]) && maxHeight >= Integer.parseInt(settings[0].split("x")[1])) {
//                    addStream.accept(i, settings);
                    streamCount++;
                }
            }
            List<String> command = new ArrayList<>();
            command.add("ffmpeg");
            command.add("-threads");
            command.add(String.valueOf(streamCount));
            command.add("-filter_complex_threads");
            command.add(String.valueOf(streamCount));
            command.add("-vsync");
            command.add("1");
            command.add("-i");
            command.add(inputVideoPath);

            // Function to add video stream settings
            BiConsumer<Integer, String[]> addStream = (index, settings) -> {
                command.add("-map");
                command.add("0:v:0");
                command.add("-s:v:" + index);
                command.add(settings[0]);
                command.add("-c:v:" + index);
                command.add("libx264");
                command.add("-pix_fmt:v:" + index);
                command.add("yuv420p");
                command.add("-b:v:" + index);
                command.add(settings[1]);
                command.add("-maxrate:v:" + index);
                command.add(settings[1]);
                command.add("-bufsize:v:" + index);
                command.add(settings[2]);
                command.add("-profile:v:" + index);
                command.add("high");
                command.add("-preset:v:" + index);
                command.add("medium");
                command.add("-tune:v:" + index);
                command.add("zerolatency");
                command.add("-force_key_frames:v:" + index);
                command.add("expr:gte(t,n_forced*2.000)");
                command.add("-map");
                command.add("0:a:0?");
                command.add("-c:a:" + index);
                command.add("aac");
                command.add("-b:a:" + index);
                command.add("128000");
                command.add("-ac:a:" + index);
                command.add("2");
                command.add("-ar:a:" + index);
                command.add("48000");
            };
            for (int i = streamCount - 1; i >= 0; i--) {
                String[] settings = streamSettings.get(i);
                    addStream.accept(streamCount - 1 -i, settings);
            }



            command.add("-f");
            command.add("hls");
            command.add("-hls_time");
            command.add("10");
            command.add("-hls_list_size");
            command.add("0");
            command.add("-var_stream_map");
            command.add(generateVarStreamMap(streamCount));
            command.add("-master_pl_name");
            command.add("master.m3u8");
            command.add("-hls_segment_filename");
            command.add(outputM3U8Path + File.separator + "%v_%03d.ts");
            command.add(outputM3U8Path + File.separator + "%v_index.m3u8");

            ProcessBuilder builder = new ProcessBuilder(command);
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
            return "Error occurred during HLS generation";
        }
        return "HLS generation started";

    }
    private String generateVarStreamMap(int streamCount) {
        StringBuilder varStreamMap = new StringBuilder();
        for (int i = 0; i < streamCount; i++) {
            varStreamMap.append("v:").append(i).append(",a:").append(i).append(" ");
        }
        return varStreamMap.toString().trim();
    }
}
