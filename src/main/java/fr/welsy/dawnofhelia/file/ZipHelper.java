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
       /* try{
            File tmpZip = File.createTempFile(source.getName(), null);
            tmpZip.delete();
            if(!source.renameTo(tmpZip)){
                throw new Exception("Could not make temp file (" + source.getName() + ")");
            }
            byte[] buffer = new byte[4096];
            ZipInputStream zin = new ZipInputStream(new FileInputStream(tmpZip));
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(source));
            for(int i = 0; i < files.length; i++){
                InputStream in = new FileInputStream(files[i]);
                out.putNextEntry(new ZipEntry(path + files[i].getName()));
                for(int read = in.read(buffer); read > -1; read = in.read(buffer)){
                    out.write(buffer, 0, read);
                }
                out.closeEntry();
                in.close();
            }
            for(ZipEntry ze = zin.getNextEntry(); ze != null; ze = zin.getNextEntry()){
                if(!zipEntryMatch(ze.getName(), files, path)){
                    out.putNextEntry(ze);
                    for(int read = zin.read(buffer); read > -1; read = zin.read(buffer)){
                        out.write(buffer, 0, read);
                    }
                    out.closeEntry();
                }
            }
            out.close();
            tmpZip.delete();
        }catch(Exception e){
            e.printStackTrace();
        }*/

        Path myFilePath = files.toPath();

        Path zipFilePath = source.toPath();
        Map<String, String> zipProperties = new HashMap<>();
        zipProperties.put("create", "true");

        try( FileSystem fs = FileSystems.newFileSystem(zipFilePath, zipProperties) ){
            Path fileInsideZipPath = fs.getPath(String.format("%s%s", path, files.getName()));
            System.out.printf("%s%s\n", path, files.getName());
            Files.createDirectories(fs.getPath(path));
            Files.copy(myFilePath, fileInsideZipPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean zipEntryMatch(String zeName, File[] files, String path){
        for(int i = 0; i < files.length; i++){
            if((path + files[i].getName()).equals(zeName)){
                return true;
            }
        }
        return false;
    }
}
