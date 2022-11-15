package com.bechris100.open_ransomware.windows;

import com.bechris100.open_ransomware.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/*
Fully working Window
 */
public class WarningPrompt {

    private static final int WIDTH = 400, HEIGHT = 225;

    public static boolean EXECUTING_TASK = false;

    public WarningPrompt() {
    }

    public void show() {
        JFrame frame = new JFrame();
        frame.setLayout(null);
        frame.setSize(WIDTH, HEIGHT);
        frame.setTitle("Ransomware Prompt");
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.decode("#1d2021"));
        frame.setIconImage(Toolkit.getDefaultToolkit().createImage(ResourceManager.getPath("res/tray_icon.png")));
        frame.addWindowListener(new WarningWindowListener(frame));

        JLabel jMessage = new JLabel("Ransomware is not a joke. Continue?");
        jMessage.setBounds(WIDTH / 2 - 150, 0, 300, 50);
        jMessage.setForeground(Color.WHITE);
        jMessage.setFont(new Font(jMessage.getFont().getName(), jMessage.getFont().getStyle(), 16));
        frame.add(jMessage);

        JButton jPositive = new JButton("Allow Execution");
        jPositive.setBounds(0, HEIGHT - 65, WIDTH / 2, 25);
        jPositive.setFont(new Font(jPositive.getFont().getName(), jPositive.getFont().getStyle(), 16));
        jPositive.setForeground(Color.WHITE);
        jPositive.setBackground(Color.decode("#191919"));
        jPositive.addActionListener(actionEvent -> {
            EXECUTING_TASK = true;
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        });
        frame.add(jPositive);

        JButton jNegative = new JButton("Disallow Execution");
        jNegative.setBounds(WIDTH / 2, HEIGHT - 65, WIDTH / 2, 25);
        jNegative.setFont(new Font(jNegative.getFont().getName(), jPositive.getFont().getStyle(), 16));
        jNegative.setForeground(Color.WHITE);
        jNegative.setBackground(Color.decode("#191919"));
        jNegative.addActionListener(actionEvent -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));
        frame.add(jNegative);

        frame.setVisible(true);
    }

}
