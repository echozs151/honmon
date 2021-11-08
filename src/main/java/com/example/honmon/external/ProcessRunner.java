package com.example.honmon.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ProcessRunner {
    public static void parseWords() throws IOException
    {
        ProcessBuilder processBuilder = new ProcessBuilder("python", "external/script.py");
        Process process = processBuilder.start();
        InputStream iptStrm = process.getInputStream();
        StringBuilder textBuilder = new StringBuilder();
        
        try (Reader reader = new BufferedReader(new InputStreamReader(iptStrm, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }

        System.out.print(textBuilder.toString());
    }

}
