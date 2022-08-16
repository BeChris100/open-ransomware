package com.bechris100.open_ransomware.managers;

import com.bechris100.open_ransomware.ResourceManager;
import com.bechris100.open_ransomware.utils.RuntimeEnvironment;

import java.awt.*;
import java.io.IOException;

public class NotificationManager {

    public static void notifyUser(String title, String message) {
        if (RuntimeEnvironment.isLinux()) {
            try {
                ProcessBuilder builder = new ProcessBuilder("zenity",
                        "--notification",
                        "--title=\"" + title + "\"",
                        "--text=\"" + message + "\"");
                builder.inheritIO().start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (RuntimeEnvironment.isWindows()) {
            try {
                SystemTray tray = SystemTray.getSystemTray();

                TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit()
                        .createImage(ResourceManager.getPath("res/tray_icon.png")));
                trayIcon.setImageAutoSize(true);
                trayIcon.setToolTip("OpenSoftware");
                tray.add(trayIcon);

                trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);

                Thread.sleep(20000);

                tray.remove(trayIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (RuntimeEnvironment.isMac()) {
            try {
                ProcessBuilder builder = new ProcessBuilder("osascript", "-e",
                        "display notification \"" + message + "\" with title \"" + title + "\"");
                builder.inheritIO().start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
