package com.bechris100.open_ransomware.utils;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bechris100.open_ransomware.utils.Utility.removePath;

public class NewFileUtil {

    private final String path;

    public NewFileUtil(String path) {
        this.path = path;
    }

    public boolean exists() {
        return new File(path).exists();
    }

    public void fileCreate(boolean createDirs) throws IOException {
        File file = new File(path);
        File dirs = new File(path.substring(0, Utility.getLastPathSeparator(path, false)));

        if (!dirs.exists()) {
            if (createDirs)
                dirCreate(dirs.getPath());
            else
                throw new FileNotFoundException("No directories at \"" + dirs.getPath() + "\" " +
                        "were found");
        }

        if (!file.createNewFile())
            throw new IOException("File at \"" + path + "\" could not be created");
    }

    private void dirCreate(String mPath) throws IOException {
        File dir = new File(mPath.substring(0, Utility.getLastPathSeparator(path, true)));

        if (dir.exists()) {
            if (dir.isFile())
                throw new FileAlreadyExistsException("File at \"" + path + "\" already exists");
            else
                throw new FileAlreadyExistsException("Directory at \"" + path + "\" already exists");
        }

        if (!dir.mkdirs())
            throw new IOException("Could not create new directories at \"" + path + "\"");
    }

    public void dirCreate() throws IOException {
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

    public void openWrite(char[] contents) throws IOException {
        File file = new File(path);

        if (!file.exists())
            fileCreate(true);

        if (!file.canWrite())
            throw new AccessDeniedException("The current user \"" + System.getProperty("user.name") + "\" does not have access to \"" + path + "\"");

        if (file.isDirectory())
            throw new IllegalStateException("\"" + path + "\" is a directory");

        FileOutputStream fos = new FileOutputStream(file, false);

        for (char c : contents)
            fos.write(c);

        fos.close();
    }

    public char[] openRead() throws IOException {
        File file = new File(path);

        if (!file.exists())
            throw new FileNotFoundException("File \"" + path + "\" not found");

        if (!file.canRead())
            throw new AccessDeniedException("The current user \"" + System.getProperty("user.name") + "\" does not have access to \"" + path + "\"");

        if (file.isDirectory())
            throw new IllegalStateException("\"" + path + "\" is a directory");

        FileInputStream fis = new FileInputStream(file);
        StringBuilder str = new StringBuilder();
        int data;

        while ((data = fis.read()) != -1)
            str.append((char) data);

        fis.close();
        return str.toString().toCharArray();
    }

    private long size(String path) throws IOException {
        File file = new File(path);
        long size = 0;

        if (!file.exists())
            throw new FileNotFoundException("\"" + path + "\" does not exist");

        if (file.isFile())
            size += file.length();

        if (file.isDirectory()) {
            for (String item : listDir(path, false, false)) {
                File subFile = new File(item);

                if (subFile.isFile())
                    size += subFile.length();

                if (subFile.isDirectory()) {
                    size += subFile.length();
                    size += size(subFile.getPath());
                }
            }
        }

        return size;
    }

    public long size() throws IOException {
        File file = new File(path);
        long size = 0;

        if (!file.exists())
            throw new FileNotFoundException("\"" + path + "\" does not exist");

        if (file.isFile())
            size += file.length();

        if (file.isDirectory()) {
            for (String item : listDir(path, false, false)) {
                File subFile = new File(item);

                if (subFile.isFile())
                    size += subFile.length();

                if (subFile.isDirectory()) {
                    size += subFile.length();
                    size += size(subFile.getPath());
                }
            }
        }

        return size;
    }

    private @NotNull List<String> nameScan(String mPath, String name, boolean excludeFolders) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(mPath);

        if (root.isFile()) {
            if (path.contains(name))
                results.add(path);

            return results;
        }

        List<String> data = listDir(mPath, true, false);
        if (data.size() == 0)
            return results;

        for (String item : data) {
            File file = new File(item);

            if (excludeFolders) {
                if (file.getPath().contains(name) && file.isFile())
                    results.add(file.getPath());
            } else {
                if (file.getPath().contains(name))
                    results.add(file.getPath());
            }

            if (file.isDirectory())
                results.addAll(nameScan(item, name, excludeFolders));
        }

        return results;
    }

    public List<String> nameScan(String name, boolean excludeFolders) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile()) {
            if (path.contains(name))
                results.add(path);

            return results;
        }

        List<String> data = listDir(path, true, false);
        if (data.size() == 0)
            return results;

        for (String item : data) {
            File file = new File(item);

            if (excludeFolders) {
                if (file.getPath().contains(name) && file.isFile())
                    results.add(file.getPath());
            } else {
                if (file.getPath().contains(name))
                    results.add(file.getPath());
            }

            if (file.isDirectory())
                results.addAll(nameScan(item, name, excludeFolders));
        }

        return results;
    }

    private @NotNull List<String> dirScan(String mPath) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(mPath);

        if (root.isFile())
            return results;

        List<String> data = listDir(mPath, true, false);
        if (data.size() == 0)
            return results;

        for (String item : data) {
            File file = new File(item);

            if (!file.exists())
                continue;

            if (file.isDirectory()) {
                results.add(item);
                results.addAll(dirScan(item));
            }
        }

        return results;
    }

    public List<String> dirScan() throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile())
            return results;

        List<String> data = listDir(path, true, false);
        if (data.size() == 0)
            return results;

        for (String item : data) {
            File file = new File(item);

            if (!file.exists())
                continue;

            if (file.isDirectory()) {
                results.add(item);
                results.addAll(dirScan(item));
            }
        }

        return results;
    }

    private @NotNull List<String> fileScan(String mPath) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(mPath);

        if (root.isFile())
            return results;

        List<String> data = listDir(mPath, true, false);
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

            results.addAll(fileScan(item));
        }

        return results;
    }

    public List<String> fileScan() throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile())
            return results;

        List<String> data = listDir(path, true, false);
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

            results.addAll(fileScan(item));
        }

        return results;
    }

    public boolean asFile() {
        File file = new File(path);

        if (!file.exists())
            return false;

        return file.isFile();
    }

    public boolean asDir() {
        File file = new File(path);

        if (!file.exists())
            return false;

        return file.isDirectory();
    }

    private @NotNull List<String> listDir(String mPath, boolean sortNames, boolean removePaths) throws IOException {
        List<String> data = new ArrayList<>();
        File fileData = new File(mPath);

        if (!fileData.exists())
            throw new FileNotFoundException("\"" + mPath + "\" not found");

        if (!fileData.canRead())
            throw new AccessDeniedException("Access to user \"" + RuntimeEnvironment.USER_NAME + "\" " +
                    "was not given to \"" + mPath + "\"");

        if (fileData.isFile()) {
            data.add(mPath);

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

    public List<String> listDir(boolean sortNames, boolean removePaths) throws IOException {
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

    private void delete(String mPath) throws IOException {
        File data = new File(mPath);

        if (!data.exists())
            throw new IOException("File or directory at \"" + mPath + "\" does not exist");

        if (data.isFile()) {
            if (!data.delete())
                throw new IOException("Could not delete \"" + mPath + "\"");

            return;
        }

        if (listDir(false, false).size() == 0) {
            if (!data.delete())
                throw new IOException("Could not delete \"" + mPath + "\"");

            return;
        }

        File[] files = data.listFiles();
        if (files == null) {
            if (!data.delete())
                throw new IOException("Could not delete \"" + mPath + "\"");

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
            throw new IOException("Could not delete \"" + mPath + "\"");
    }

    public void delete() throws IOException {
        File data = new File(path);

        if (!data.exists())
            throw new IOException("File or directory at \"" + path + "\" does not exist");

        if (data.isFile()) {
            if (!data.delete())
                throw new IOException("Could not delete \"" + path + "\"");

            return;
        }

        if (listDir(false, false).size() == 0) {
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

    private void wipeDir(String mPath) throws IOException {
        File data = new File(mPath);

        if (!data.exists())
            throw new IOException("File or directory at \"" + mPath + "\" does not exist");

        if (data.isFile())
            return;

        if (listDir(false, false).size() == 0) {
            if (!data.delete())
                throw new IOException("Could not delete \"" + mPath + "\"");

            return;
        }

        File[] files = data.listFiles();
        if (files == null) {
            if (!data.delete())
                throw new IOException("Could not delete \"" + mPath + "\"");

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
            throw new IOException("Could not delete \"" + mPath + "\"");
    }

    public void wipeDir() throws IOException {
        File data = new File(path);

        if (!data.exists())
            throw new IOException("File or directory at \"" + path + "\" does not exist");

        if (data.isFile())
            return;

        if (listDir(false, false).size() == 0) {
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
    }

    public boolean userWrite() throws IOException {
        File data = new File(path);

        if (!data.exists())
            throw new FileNotFoundException("File at \"" + path + "\" not found");

        return data.canWrite();
    }

    public boolean userRead() throws IOException {
        File data = new File(path);

        if (!data.exists())
            throw new FileNotFoundException("File at \"" + path + "\" not found");

        return data.canRead();
    }

    public boolean userExec() throws IOException {
        File data = new File(path);

        if (!data.exists())
            throw new FileNotFoundException("File at \"" + path + "\" not found");

        return data.canExecute();
    }

    public void setUserWrite(boolean write) throws IOException {
        File data = new File(path);

        if (!data.exists())
            throw new FileNotFoundException("File at \"" + path + "\" not found");

        if (!data.setWritable(write))
            throw new IOException("Could not set file attribute at \"" + path + "\" to write");
    }

    public void setUserRead(boolean read) throws IOException {
        File data = new File(path);

        if (!data.exists())
            throw new FileNotFoundException("File at \"" + path + "\" not found");

        if (!data.setReadable(read))
            throw new IOException("Could not set file attribute at \"" + path + "\" to read");
    }

    public void serUserExec(boolean exec) throws IOException {
        File data = new File(path);

        if (!data.exists())
            throw new FileNotFoundException("File at \"" + path + "\" not found");

        if (!data.setExecutable(exec))
            throw new IOException("Could not set file attribute at \"" + path + "\" to exec");
    }

    public void setUserWrite(boolean write, boolean onlyOwner) throws IOException {
        File data = new File(path);

        if (!data.exists())
            throw new FileNotFoundException("File at \"" + path + "\" not found");

        if (!data.setWritable(write, onlyOwner))
            throw new IOException("Could not set file attribute at \"" + path + "\" to write for only the file owner");
    }

    public void setUserRead(boolean read, boolean onlyOwner) throws IOException {
        File data = new File(path);

        if (!data.exists())
            throw new FileNotFoundException("File at \"" + path + "\" not found");

        if (!data.setReadable(read, onlyOwner))
            throw new IOException("Could not set file attribute at \"" + path + "\" to read for only the file owner");
    }

    public void serUserExec(boolean exec, boolean onlyOwner) throws IOException {
        File data = new File(path);

        if (!data.exists())
            throw new FileNotFoundException("File at \"" + path + "\" not found");

        if (!data.setExecutable(exec, onlyOwner))
            throw new IOException("Could not set file attribute at \"" + path + "\" to exec for only the file owner");
    }

}
