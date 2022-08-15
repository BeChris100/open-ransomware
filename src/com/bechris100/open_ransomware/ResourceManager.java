package com.bechris100.open_ransomware;

import com.bechris100.open_ransomware.utils.RuntimeEnvironment;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.util.Objects;

public class ResourceManager {

    public static boolean resourceExists(String resName) {
        return ResourceManager.class.getResource(resName) != null;
    }

    public static String getPath(String resName) {
        return Objects.requireNonNull(ResourceManager.class.getResource(resName)).getPath();
    }

    public static InputStream getResourceInputStream(String resName) {
        return ResourceManager.class.getResourceAsStream(resName);
    }

    public static void copy(String resource, File outputFile) throws IOException {
        if (ResourceManager.class.getResource(resource) == null)
            throw new FileNotFoundException("Resource file \"" + resource + "\" not found");

        InputStream is = ResourceManager.class.getResourceAsStream(resource);

        if (is == null)
            throw new IOException("Could not open stream for resource file \"" + resource + "\"");

        if (outputFile.exists() && !outputFile.canWrite())
            throw new AccessDeniedException("User \"" + RuntimeEnvironment.USER_NAME + "\" does not have access " +
                    "to \"" + outputFile.getPath() + "\"");

        if (!outputFile.canWrite())
            throw new AccessDeniedException("User \"" + RuntimeEnvironment.USER_NAME + "\" does not have access " +
                    "to \"" + outputFile.getPath() + "\"");

        FileOutputStream fos = new FileOutputStream(outputFile);

        int data;
        while ((data = is.read()) != -1)
            fos.write(data);

        fos.close();
        is.close();
    }

}
