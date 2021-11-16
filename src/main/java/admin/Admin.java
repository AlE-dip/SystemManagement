package admin;

import core.UtilContent;
import java.io.IOException;
import java.net.Socket;

public class Admin {
    public Admin(){
        try {
            Socket socket = new Socket(UtilContent.address, UtilContent.port);
            AdminSession session = new AdminSession(socket);
            session.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("admin stop!");
        }
    }

    public static void main(String[] args) {
        Admin admin = new Admin();
    }
}
