package admin;

import admin.gui.AdminGui;
import admin.gui.OshiGui;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkContrastIJTheme;
import core.UtilContent;
import org.opencv.core.Core;

import javax.swing.*;
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

        try {
            //UIManager.setLookAndFeel(new FlatDarkLaf());
            //FlatArcOrangeIJTheme.setup();
            //FlatAtomOneLightContrastIJTheme.setup();
            FlatArcDarkContrastIJTheme.setup();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Admin admin = new Admin();


    }
}
