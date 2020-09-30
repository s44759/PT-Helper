package cn.nkym.pt.window;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;

@Slf4j
public class WindowInfo {

    public static void showInfo(String title, String subTitle){
        try {
            if (SystemTray.isSupported()) {
                displayTray(title, subTitle);
            } else {
                System.err.println("System tray not supported!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("win10通知出错", e);
        }
    }

    private static void displayTray(String title1, String title2){
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("PT 新种通知");
            tray.add(trayIcon);
            trayIcon.displayMessage(title1, title2, TrayIcon.MessageType.INFO);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("win10通知出错", e);
        }
    }
}
