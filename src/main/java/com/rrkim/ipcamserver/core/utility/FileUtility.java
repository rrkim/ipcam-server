package com.rrkim.ipcamserver.core.utility;

import java.io.*;
import java.nio.file.Paths;

public class FileUtility {
    private static String currentDirectory = System.getProperty("user.dir");

    public static String readFileByDataStream(String fileName) throws IOException {
        File file = Paths.get(currentDirectory, fileName).toFile();
        if(!file.exists()) { return null; }

        StringBuilder text = new StringBuilder();
        try(FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
        }
        return text.toString();
    }

}
