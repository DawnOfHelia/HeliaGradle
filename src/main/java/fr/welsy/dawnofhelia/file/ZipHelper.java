package fr.welsy.dawnofhelia.file;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipHelper {
    public static void addFilesToZip(File source, File files, String path){
        Path myFilePath = files.toPath();
        Path zipFilePath = source.toPath();

        Map<String, String> zipProperties = new HashMap<>();
        zipProperties.put("create", "true");

        try(FileSystem fs = FileSystems.newFileSystem(zipFilePath, zipProperties)){
            Path fileInsideZipPath = fs.getPath(String.format("%s%s", path, files.getName()));
            System.out.printf("%s%s\n", path, files.getName());
            Files.createDirectories(fs.getPath(path));
            Files.copy(myFilePath, fileInsideZipPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
