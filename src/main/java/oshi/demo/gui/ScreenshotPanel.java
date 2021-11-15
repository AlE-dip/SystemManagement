package oshi.demo.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenshotPanel extends JPanel {
    public ScreenshotPanel(String PathName) {
        try {
            BufferedImage myPicture = ImageIO.read(new File(PathName));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            add(picLabel);
        } catch (IOException e) {
            System.out.println(e);
        }

        //Bonus mở hình bằng default image viewer
        try {
            File file = new File(PathName);
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    public static void main (String[] args) throws Exception {
        JFrame frame = new JFrame();
        ScreenshotPanel screenshotPanel = new ScreenshotPanel("screenshot.png");
        frame.setSize(1920,1030);
        frame.add(screenshotPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
