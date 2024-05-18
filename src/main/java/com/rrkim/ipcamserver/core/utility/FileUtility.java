package com.rrkim.ipcamserver.core.utility;

import java.io.*;
import java.nio.file.Paths;

public class FileUtility {
    private static String currentDirectory = System.getProperty("user.dir");

    public static void saveFileByDataStream(String fileName, String text) throws IOException {
        File file = Paths.get(currentDirectory, fileName).toFile();
        if (!file.exists()) {
            file.createNewFile();
        }

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(text);
            fw.flush();
        }
    }

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

    public static DataInputStream getDataInputStream(String fileName) throws IOException {
        // read file and return InputStream

        File file = Paths.get(currentDirectory, fileName).toFile();
        if(!file.exists()) { return null; }

        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);

            return dis;
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
