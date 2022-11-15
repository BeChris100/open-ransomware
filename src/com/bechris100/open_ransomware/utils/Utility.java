package com.bechris100.open_ransomware.utils;

import java.util.List;
import java.util.Random;

public class Utility {

    public static String returnOptions(boolean checkFor, String input, String defaultValue) {
        return (checkFor ? defaultValue : input);
    }

    public static int getRandomInteger(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static String formatString(String input) {
        String formatted = input;

        if (formatted.contains("%UserDir%"))
            formatted = formatted.replaceAll("%UserDir%", RuntimeEnvironment.WORKING_DIRECTORY.getPath());

        if (formatted.contains("%UserHome%") || formatted.contains("~"))
            formatted = formatted.replaceAll("%UserHome%", RuntimeEnvironment.USER_HOME.getPath());

        if (formatted.contains("%JavaHome%"))
            formatted = formatted.replaceAll("%JavaHome%", RuntimeEnvironment.JAVA_HOME.getPath());

        if (formatted.contains("%WinDir%")) {
            if (RuntimeEnvironment.isWindows())
                formatted = formatted.replaceAll("%WinDir%", System.getenv("windir"));
        }

        if (formatted.contains("%SystemDrive%")) {
            if (RuntimeEnvironment.isWindows())
                formatted = formatted.replaceAll("%SystemDrive%", System.getenv("SystemDrive"));
        }

        if (formatted.contains("%SystemRoot%")) {
            if (RuntimeEnvironment.isWindows())
                formatted = formatted.replaceAll("%SystemRoot%", System.getenv("SystemRoot"));
        }

        return formatted;
    }

    public static String getLineSeparator(String contents) {
        char[] chars = contents.toCharArray();

        long r = 0;
        long n = 0;

        for (char c : chars) {
            if (c == '\r')
                r++;

            if (c == '\n')
                n++;
        }

        if (r == n)
            return "\r\n";
        else if (r >= 1 && n == 0)
            return "\r";
        else if (n >= 1 && r == 0)
            return "\n";
        else
            return "";
    }

    public static int getLastPathSeparator(String path, boolean toEnd) {
        if (path == null)
            return 0;

        if (path.isEmpty())
            return 0;

        if (!(path.contains("\\") || path.contains("/")))
            return 0;

        int lastSep;
        if (path.contains("\\") && path.contains("/")) {
            path = path.replaceAll("\\\\", "/");
            lastSep = path.lastIndexOf('/');
        } else if (path.contains("\\"))
            lastSep = path.lastIndexOf('\\');
        else
            lastSep = path.lastIndexOf('/');

        lastSep++;

        if (toEnd) {
            String sub = path.substring(lastSep);
            if (!sub.isEmpty())
                lastSep = path.length();
        }

        return lastSep;
    }

    public static String fromList(List<String> list, String spliterator) {
        if (list == null)
            return "";

        if (list.size() == 0)
            return "";

        StringBuilder str = new StringBuilder();

        for (String item : list)
            str.append(item).append(spliterator);

        return str.substring(0, str.toString().length() - spliterator.length());
    }

    public static long getRandomLong() {
        return new Random().nextLong();
    }

}
