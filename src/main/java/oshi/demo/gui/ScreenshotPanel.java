package oshi.demo.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenshotPanel extends OshiJPanel {
    public ScreenshotPanel(String PathName) {
        setSize(1000, 1000); //fitImage() cần phải đọc size của Panel
        try {
            BufferedImage bi_Img = ImageIO.read(new File(PathName));
            Image img = fitImage(bi_Img);
            JLabel imgLabel = new JLabel(new ImageIcon(img));
            add(imgLabel);
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

    public Image fitImage (BufferedImage img) {
        float HeSo = (float) img.getWidth()/img.getHeight();
        float Width = this.getWidth();
        float Height = Width/HeSo;
        if (this.getHeight() < Height) {
            Height = this.getHeight();
            Width = Height*HeSo;
        }
        return img.getScaledInstance((int) Width, (int) Height, Image.SCALE_SMOOTH);
    }

    public static void main (String[] args) throws Exception {
        JFrame frame = new JFrame();
        ScreenshotPanel screenshotPanel = new ScreenshotPanel("screenshot.png");
        frame.setSize(1100,1100);
        frame.add(screenshotPanel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
