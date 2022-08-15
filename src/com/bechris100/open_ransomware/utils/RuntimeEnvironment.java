package com.bechris100.open_ransomware.utils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

public class RuntimeEnvironment {

    public static final String OS_NAME = System.getProperty("os.name");
    public static final String OS_ARCH = System.getProperty("os.arch");
    public static final String OS_VERSION = System.getProperty("os.version");
    public static final String PATH_SEPARATOR = System.getProperty("path.separator");
    public static final File WORKING_DIRECTORY = new File(System.getProperty("user.dir"));
    public static final File USER_HOME = new File(System.getProperty("user.home"));
    public static final String USER_NAME = System.getProperty("user.name");
    public static final String HOST_NAME = System.getProperty("host.name");
    public static final String SYSTEM_LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String JAVA_VERSION = System.getProperty("java.version");
    public static final String JAVA_VENDOR_URL = System.getProperty("java.vendor.url");
    public static final String JAVA_VENDOR = System.getProperty("java.vendor");
    public static final File JAVA_HOME = new File(System.getProperty("java.home"));
    public static final File CLASS_PATH = new File(System.getProperty("java.class.path"));
    public static final char SYSTEM_PATH_SEPARATOR = System.getProperty("file.separator").toCharArray()[0];

    public static int countRuntimeModifications() {
        List<String> modifications = ManagementFactory.getRuntimeMXBean().getInputArguments();

        int items = 0;

        for (String modification : modifications) {
            if (modification.startsWith("-Dos.name=") ||
                    modification.startsWith("-Dos.arch=") ||
                    modification.startsWith("-Dos.version="))
                items++;
        }

        return items;
    }

    public static boolean osNameModified() {
        List<String> modifications = ManagementFactory.getRuntimeMXBean().getInputArguments();

        for (String mod : modifications) {
            if (mod.startsWith("-Dos.name="))
                return true;
        }

        return false;
    }

    public static boolean osArchModified() {
        List<String> modifications = ManagementFactory.getRuntimeMXBean().getInputArguments();

        for (String mod : modifications) {
            if (mod.startsWith("-Dos.arch="))
                return true;
        }

        return false;
    }

    public static boolean osVersionModified() {
        List<String> modifications = ManagementFactory.getRuntimeMXBean().getInputArguments();

        for (String mod : modifications) {
            if (mod.startsWith("-Dos.version="))
                return true;
        }

        return false;
    }

    public static boolean isLinux() {
        String os = OS_NAME.toLowerCase();
        return os.contains("nix") || os.contains("nux") || os.contains("aix");
    }

    public static boolean isWindows() {
        return OS_NAME.toLowerCase().contains("win");
    }

    public static boolean isMac() {
        return OS_NAME.toLowerCase().contains("mac");
    }

    public static boolean isSunOs() {
        return OS_NAME.toLowerCase().contains("sunos");
    }

    public static String getHostName() {
        if (HOST_NAME != null)
            return HOST_NAME;
        else {
            if (isLinux()) {
                try {
                    if (FileUtil.exists("/etc/hostname"))
                        return new String(FileUtil.read("/etc/hostname"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }

    public static class LinuxEnvironment {

        public static String getName() {
            try {
                String contents = new String(FileUtil.read("/etc/os-release"));
                String[] lines = contents.split(Utility.getLineSeparator(contents));

                for (String line : lines) {
                    if (line.startsWith("#"))
                        continue;

                    String[] opts = line.split("=", 2);
                    if (opts[0].equals("NAME"))
                        return opts[1];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        public static String getPrettyName() {
            try {
                String contents = new String(FileUtil.read("/etc/os-release"));
                String[] lines = contents.split(Utility.getLineSeparator(contents));

                for (String line : lines) {
                    if (line.startsWith("#"))
                        continue;

                    String[] opts = line.split("=", 2);
                    if (opts[0].equals("PRETTY_NAME") || opts[0].equals("PRETTY-NAME") ||
                            opts[0].equals("PRETTY"))
                        return opts[1];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

    }

}
