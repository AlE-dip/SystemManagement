package client;

import com.sun.jna.platform.win32.GDI32Util;
import com.sun.jna.platform.win32.WinDef.HWND;
import org.opencv.core.Core;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class CLient2 {

    //    public static void main(String[] args) {
//        try {
//            //Get JNA User32 Instace
//            com.sun.jna.platform.win32.User32 user32 = com.sun.jna.platform.win32.User32.INSTANCE;
//            //Get desktop windows handler
//            HWND hwnd = user32.GetDesktopWindow();
//            //Create a BufferedImage
//            BufferedImage bi;
//            //Function that take screenshot and set to BufferedImage bi
//            bi = GDI32Util.getScreenshot(hwnd);
//            //Save screenshot to a file
//            ImageIO.write(bi, "png", new java.io.File("screenshot.png"));
//        } catch (IOException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Client client = new Client();
    }
}