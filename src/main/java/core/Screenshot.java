package core;

import com.sun.jna.platform.win32.GDI32Util;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Screenshot {
    public static boolean run = false;

    public static BufferedImage capture() throws AWTException {
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
        return screenFullImage;
    }

    public static void record(Socket socket) throws AWTException, IOException {
        run = true;
        User32 user32 = User32.INSTANCE;
        //Get desktop windows handler
        WinDef.HWND hwnd = user32.GetDesktopWindow();
        //Create a BufferedImage
        BufferedImage screenshot = GDI32Util.getScreenshot(hwnd);
        //File file = new File("screenshot.png");
        //Save screenshot to a file
        //ImageIO.write(image, "png", file);
//        Robot robot = new Robot();
//        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
//        image = robot.createScreenCapture(screenRect);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Record start...");
                int i = 0;
                while (run){
                    BufferedImage screenshot = GDI32Util.getScreenshot(hwnd);
                    try {
                        paintCursor(screenshot);

                        Mat mat = Core.bufferedImageToMat(screenshot);
                        //Imgproc.resize(mat, mat, new Size(900, 400));
                        Core.writeMatToBuffer(mat, writer);

                        TimeUnit.MICROSECONDS.sleep(10);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("Record stop.");
                        return;
                    }
                }
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Record stop.");
                }
                System.out.println("Record stop.");
            }
        });
        thread.start();
    }

    public static void observer(Socket socket, JPanel panel, JLabel label) throws IOException {
        run = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        File file = new File("screenshot.png");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                System.out.println("Screenshot.observer running...");
                while (run){
                    try {

                        Mat mat = Core.readBufferToMat(reader);
                        BufferedImage screenshot = (BufferedImage) HighGui.toBufferedImage(mat);

                        Graphics2D graphics2D = screenshot.createGraphics();
                        //panel.paint(graphics2D);

                        //ImageIcon icon = scaleImage(new ImageIcon(screenshot), label.getWidth(), label.getHeight());
                        screenshot = Core.resize(screenshot, 900, 600);
                        label.setIcon(new ImageIcon(screenshot));
//                        label.setIcon(icon);

                    } catch (IOException e) {
                        System.out.println("Screenshot.observer stop!");
                        e.printStackTrace();
                        return;
                    }
                }
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Screenshot.observer stop!");
                }
                System.out.println("Screenshot.observer stop!");
            }
        });
        thread.start();
    }

    public static void paintCursor(BufferedImage image) throws IOException {
        Image cursor = ImageIO.read(new File("image\\cursor.png"));
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int width = dimension.width;
        int height = dimension.height;
        double x = MouseInfo.getPointerInfo().getLocation().x;
        double y = MouseInfo.getPointerInfo().getLocation().y;
        double cx = x / width;
        double cy = y / height;
        Graphics2D graphics2D = image.createGraphics();
        int w = image.getWidth();
        int h = image.getHeight();
        graphics2D.drawImage(cursor, (int)(cx * w), (int)(cy * h), 16, 16, null);
    }

    public static ImageIcon scaleImage(ImageIcon icon, int w, int h)
    {
        int nw = icon.getIconWidth();
        int nh = icon.getIconHeight();

        if(icon.getIconWidth() > w)
        {
            nw = w;
            nh = (nw * icon.getIconHeight()) / icon.getIconWidth();
        }

        if(nh > h)
        {
            nh = h;
            nw = (icon.getIconWidth() * nh) / icon.getIconHeight();
        }

        return new ImageIcon(icon.getImage().getScaledInstance(nw, nh, Image.SCALE_DEFAULT));
    }
}
