package admin;

import admin.gui.AdminGui;
import admin.gui.OshiGui;
import core.UtilContent;
import org.opencv.core.Core;

import java.io.IOException;
import java.net.Socket;

public class Admin {
    public Admin(){
        try {
            AdminGui gui = new AdminGui();
            Socket socket = new Socket(UtilContent.address, UtilContent.port);
            AdminSession session = new AdminSession(socket, gui);
            session.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("admin stop!");
        }
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Admin admin = new Admin();
    }
}
