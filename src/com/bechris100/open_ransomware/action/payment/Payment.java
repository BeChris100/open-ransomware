package com.bechris100.open_ransomware.action.payment;

public class Payment {

    public enum Type {
        /**
         * The old traditional way, when the Hackers / Scammers create Ransomware Software
         */
        BITCOIN,

        /**
         * Preferable way to transform money, but the money can be easily traced
         */
        PAYPAL
    }

}
