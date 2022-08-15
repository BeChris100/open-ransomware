package com.bechris100.open_ransomware.config;

import com.bechris100.open_ransomware.ResourceManager;
import com.bechris100.open_ransomware.utils.Utility;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ConfigParser {

    public static List<Configuration> list = new ArrayList<>();

    private static String removeStartSpaces(String line) {
        String results = line;

        if (results.startsWith(" "))
            results = results.replaceFirst(" ", "");

        if (results.startsWith("\t"))
            results = results.replaceFirst("\t", "");

        if (results.startsWith(" ") || results.startsWith("\t"))
            removeStartSpaces(results);

        return results;
    }

    public static void loadFromResource(String resourceFile) {
        try {
            InputStream is = ResourceManager.getResourceInputStream(resourceFile);
            StringBuilder read = new StringBuilder();
            int data;

            while ((data = is.read()) != -1)
                read.append((char)data);

            is.close();

            String contents = read.toString();
            String[] lines = contents.split(Utility.getLineSeparator(contents));

            for (String line : lines) {
                if (line.startsWith("#"))
                    continue;

                String[] opts;

                if (line.startsWith(" ") || line.startsWith("\t")) {
                    String overwrittenLine = removeStartSpaces(line);
                    opts = overwrittenLine.split(" = ", 2);
                } else
                    opts = line.split(" = ", 2);

                Configuration config = new Configuration(opts[0], opts[1]);
                list.add(config);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addConfiguration(String name, String value) {
        if (name == null || value == null)
            return;

        Configuration config = new Configuration(name, value);
        list.add(config);
    }

    public static void removeConfiguration(String name) {
        if (name == null)
            return;

        boolean found = false;

        for (int i = 0; i < list.size(); i++) {
            Configuration config = list.get(i);

            if (config.name.equals(name)) {
                list.remove(i);
                found = true;
                break;
            }
        }

        if (!found)
            throw new ConfigurationNotFoundException("No name holder of \"" + name + "\" has been found");
    }

}
