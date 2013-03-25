package me.darqy.backpacks.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    /**
     * Recursively delete a directory, or deletes a file
     *
     * @param file
     * @return true if the file existed and could be deleted
     */
    public static boolean delete(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists() && file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f.isDirectory()) {
                    delete(f);
                } else {
                    f.delete();
                }
            }
        }
        return file.delete();
    }

    /**
     * Zips a file or directory into output. If the input File is null,
     * non-existent, or an empty directory, no zip file will be created.
     *
     * @param input
     * @param output
     */
    public static void zip(File input, File output) {
        if (!(input == null || output == null || 
                input.exists() || (input.isDirectory() && input.listFiles().length == 0))) {
            return;
        }
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output));
            recursiveZip(input.getParentFile(), input, zos);
            zos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void recursiveZip(File start, File file, ZipOutputStream zos)
            throws FileNotFoundException, IOException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f.isDirectory()) {
                    recursiveZip(start, f, zos);
                } else {
                    zipFile(start, f, zos);
                }
            }
        } else {
            zipFile(start, file, zos);
        }
    }

    private static void zipFile(File dir, File file, ZipOutputStream zos)
            throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry anEntry = new ZipEntry(dir.toPath().relativize(file.toPath()).toString());
        zos.putNextEntry(anEntry);
        int bytesIn;
        byte[] readBuffer = new byte[1024];
        while ((bytesIn = fis.read(readBuffer)) != -1) {
            zos.write(readBuffer, 0, bytesIn);
        }
        zos.closeEntry();
        fis.close();
    }
    
    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static String getFileTimestamp() {
        return format.format(new Date());
    }
}
