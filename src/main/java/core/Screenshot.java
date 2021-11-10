package core;

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
    private static BufferedImage image;

    public static BufferedImage capture() throws AWTException {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            return screenFullImage;
    }

    public static void record(Socket socket) throws AWTException, IOException {
        run = true;
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        image = robot.createScreenCapture(screenRect);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Record start...");
                int i = 0;
                while (run){
                    BufferedImage screenshot = robot.createScreenCapture(screenRect);
                    try {
                        paintCursor(screenshot);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(screenshot, "png", baos);
                        baos.flush();

                        Mat mat = Imgcodecs.imdecode(new MatOfByte(baos.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
                        Core.writeBuffer(mat, writer);

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

    public static void observer(Socket socket, JLabel label) throws IOException {
        run = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                System.out.println("Screenshot.observer running...");
                while (run){
                    try {

                        Mat mat = Core.readBuffer(reader);
                        Image screenshot = HighGui.toBufferedImage(mat);

                        ImageIcon icon = scaleImage(new ImageIcon(screenshot), label.getWidth(), label.getHeight());
                        label.setIcon(icon);

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
        int x = MouseInfo.getPointerInfo().getLocation().x;
        int y = MouseInfo.getPointerInfo().getLocation().y;
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.drawImage(cursor, x, y, 16, 16, null);
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
