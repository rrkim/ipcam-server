package com.rrkim.ipcamserver.core.file.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {
    private String currentDirectory = System.getProperty("user.dir");

    public void saveFileByDataStream(String fileName, String text) throws IOException {
        File file = Paths.get(currentDirectory, fileName).toFile();
        if(!file.exists()) { file.createNewFile(); }

        try(FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            DataOutputStream dos = new DataOutputStream(bos)) {

            dos.writeUTF(text);
        }
    }

}
