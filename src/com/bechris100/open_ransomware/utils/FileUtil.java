package com.bechris100.open_ransomware.utils;

import com.bechris100.open_ransomware.config.ConfigParser;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtil {

    public static boolean exists(String path) {
        return new File(path).exists();
    }

    public static void createFile(String filePath, boolean createDirs) throws IOException {
        File file = new File(filePath);
        File dirs = new File(filePath.substring(0, Utility.getLastPathSeparator(filePath, false)));

        if (!dirs.exists()) {
            if (createDirs)
                createDirectory(dirs.getPath());
            else
                throw new FileNotFoundException("No directories at \"" + dirs.getPath() + "\" " +
                        "were found");
        }

        if (!file.createNewFile())
            throw new IOException("File at \"" + filePath + "\" could not be created");
    }

    public static void createDirectory(String path) throws IOException {
        File dir = new File(path.substring(0, Utility.getLastPathSeparator(path, true)));

        if (dir.exists()) {
            if (dir.isFile())
                throw new FileAlreadyExistsException("File at \"" + path + "\" already exists");
            else
                throw new FileAlreadyExistsException("Directory at \"" + path + "\" already exists");
        }

        if (!dir.mkdirs())
            throw new IOException("Could not create new directories at \"" + path + "\"");
    }

    public static void write(String filePath, char[] contents) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            createFile(filePath, true);

        if (!file.canWrite())
            throw new AccessDeniedException("The current user \"" + System.getProperty("user.name") + "\" does" +
                    " not have access to \"" + filePath + "\"");

        if (file.isDirectory())
            throw new IllegalStateException("\"" + filePath + "\" is a directory");

        FileOutputStream fos = new FileOutputStream(file, false);

        for (char c : contents)
            fos.write(c);

        fos.close();
    }

    public static char[] read(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            throw new FileNotFoundException("File \"" + filePath + "\" not found");

        if (!file.canRead())
            throw new AccessDeniedException("The current user \"" + System.getProperty("user.name") + "\" " +
                    "does not have access to \"" + filePath + "\"");

        if (file.isDirectory())
            throw new IllegalStateException("\"" + filePath + "\" is a directory");

        FileInputStream fis = new FileInputStream(file);
        StringBuilder str = new StringBuilder();
        int data;

        while ((data = fis.read()) != -1)
            str.append((char) data);

        fis.close();
        return str.toString().toCharArray();
    }

    public static String removePath(String path) {
        return path.substring(Utility.getLastPathSeparator(path, false));
    }

    public static long getSize(String path) throws IOException {
        File file = new File(path);
        long size = 0;

        if (!file.exists())
            throw new FileNotFoundException("\"" + path + "\" does not exist");

        if (file.isFile())
            size += file.length();

        if (file.isDirectory()) {
            for (String item : listDirectory(path, false, false)) {
                File subFile = new File(item);

                if (subFile.isFile())
                    size += subFile.length();

                if (subFile.isDirectory()) {
                    size += subFile.length();
                    size += getSize(subFile.getPath());
                }
            }
        }

        return size;
    }

    public static List<String> scanForSpecifiedName(String path, String name, boolean excludeAddingFolders) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile()) {
            if (path.contains(name))
                results.add(path);

            return results;
        }

        List<String> data = listDirectory(path, true, false);

        for (String item : data) {
            File file = new File(item);

            if (excludeAddingFolders) {
                if (file.getPath().contains(name) && file.isFile())
                    results.add(file.getPath());
            } else {
                if (file.getPath().contains(name))
                    results.add(file.getPath());
            }

            if (file.isDirectory())
                results.addAll(scanForSpecifiedName(item, name, excludeAddingFolders));
        }

        return results;
    }

    public static boolean isFile(String path) {
        File file = new File(path);

        if (!file.exists())
            return false;

        return file.isFile();
    }

    public static boolean isDirectory(String path) {
        File file = new File(path);

        if (!file.exists())
            return false;

        return file.isDirectory();
    }

    public static List<String> listDirectory(String path, boolean sortNames, boolean removePaths) throws IOException {
        List<String> data = new ArrayList<>();
        File fileData = new File(path);

        if (!fileData.exists())
            throw new FileNotFoundException("\"" + path + "\" not found");

        if (!fileData.canRead())
            throw new AccessDeniedException("Access to user \"" + RuntimeEnvironment.USER_NAME + "\" " +
                    "was not given to \"" + path + "\"");

        if (fileData.isFile()) {
            data.add(path);

            if (removePaths)
                data.set(0, removePath(data.get(0)));

            return data;
        }

        File[] files = fileData.listFiles();
        if (files == null)
            return data;

        for (File file : files)
            data.add(file.getPath());

        if (removePaths) {
            if (data.size() != 0)
                data.replaceAll(FileUtil::removePath);
        }

        if (sortNames) {
            if (data.size() != 0)
                Collections.sort(data);
        }

        return data;
    }

    public static void delete(String path) throws IOException {
        File data = new File(path);

        if (!data.exists())
            throw new IOException("File or directory at \"" + path + "\" does not exist");

        if (data.isFile()) {
            if (!data.delete())
                throw new IOException("Could not delete \"" + path + "\"");

            return;
        }

        if (listDirectory(path, false, false).size() == 0) {
            if (!data.delete())
                throw new IOException("Could not delete \"" + path + "\"");

            return;
        }

        File[] files = data.listFiles();
        if (files == null) {
            if (!data.delete())
                throw new IOException("Could not delete \"" + path + "\"");

            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                if (!file.delete())
                    throw new IOException("Could not delete \"" + file.getPath() + "\"");
            }

            if (file.isDirectory())
                delete(file.getPath());
        }

        if (!data.delete())
            throw new IOException("Could not delete \"" + path + "\"");
    }

}
