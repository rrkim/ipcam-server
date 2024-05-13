package com.rrkim.ipcamserver.core.file.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class FileService {
    private String currentDirectory = System.getProperty("user.dir");

    public void saveFileByDataStream(String fileName, String text) throws IOException {
        File file = Paths.get(currentDirectory, fileName).toFile();
        if(!file.exists()) { file.createNewFile(); }

//        try(FileOutputStream fos = new FileOutputStream(file);
//            BufferedOutputStream bos = new BufferedOutputStream(fos);
//            DataOutputStream dos = new DataOutputStream(bos)) {
//            dos.writeUTF(text);
//            dos.flush();
//        }

        try(FileWriter fw = new FileWriter(file)) {
            fw.write(text);
            fw.flush();
        }
    }

    public String readFileByDataStream(String fileName) throws IOException {
        File file = Paths.get(currentDirectory, fileName).toFile();
        if(!file.exists()) { return null; }

//        String text;
//        try(FileInputStream fis = new FileInputStream(file);
//            BufferedInputStream bis = new BufferedInputStream(fis);
//            DataInputStream dis = new DataInputStream(bis)) {
//            text = dis.readUTF();
//        }
//
//        return text;
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

    public DataInputStream getDataInputStream(String fileName) throws IOException {
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
