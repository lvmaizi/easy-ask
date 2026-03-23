package com.lvmaizi.easy.ask.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class FileReadUtil {

    private static final int MAX_FILE_SIZE = 200 * 1024;

    public static String getContent(String filePath) {
        Charset[] charsets = {
                StandardCharsets.UTF_8,
                Charset.forName("GBK"),
                Charset.forName("GB2312"),
                Charset.forName("GB18030"),
                StandardCharsets.ISO_8859_1,
                StandardCharsets.UTF_16
        };

        for (Charset charset : charsets) {
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(filePath));

                if (bytes.length > MAX_FILE_SIZE) {
                    log.warn("File {} size ({}) exceeds limit ({}), truncating",
                            filePath, bytes.length, MAX_FILE_SIZE);
                    byte[] truncated = new byte[MAX_FILE_SIZE];
                    System.arraycopy(bytes, 0, truncated, 0, MAX_FILE_SIZE);
                    return new String(truncated, charset);
                }

                return new String(bytes, charset);
            } catch (IOException e) {
                log.info("Error reading file: {}, charset: {}", filePath, charset, e);
            }
        }

        log.warn("Failed to read file: {}", filePath);
        return "";
    }
}
