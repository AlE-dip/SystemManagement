package admin;

import core.Camera;
import core.Screenshot;
import org.opencv.core.Core;

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class MainAdmin extends JFrame{
    private JButton btClick;
    private JPanel panel;
    private JPanel pn1;
    private JLabel label;
    private boolean run = false;

    public MainAdmin(){
        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(1000, 700);
        setVisible(true);


        try {
            ServerSocket serverSocket = new ServerSocket();
            SocketAddress http = new InetSocketAddress(50000);
            serverSocket.bind(http);
            System.out.println("Server running...");
            Socket socket = serverSocket.accept();
//            Robot robot = new Robot();
//            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            //Demo camera
            //Camera.observer(socket, label);
            //Demo screenshot
            Screenshot.observer(socket, pn1, label);
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (true){
//                        try {
//                            TimeUnit.MICROSECONDS.sleep(500);
//
//                            BufferedImage screenshot = robot.createScreenCapture(screenRect);
//
//                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                            ImageIO.write(screenshot, "jpg", baos);
//                            byte[] bytes = baos.toByteArray();
//                            System.out.println(bytes.length + " ");
//
//                            Image cursor = ImageIO.read(new File("oshi-demo\\image\\cursor.png"));
//                            int x = MouseInfo.getPointerInfo().getLocation().x;
//                            int y = MouseInfo.getPointerInfo().getLocation().y;
//
//                            Graphics2D graphics2D = screenshot.createGraphics();
//                            graphics2D.drawImage(cursor, x, y, 16, 16, null);
//
//                            label.setIcon(new ImageIcon(screenshot));
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//            });
//            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server stoped.");
    }

    public static void test(){
        try {
            ServerSocket serverSocket = new ServerSocket();
            SocketAddress http = new InetSocketAddress(50000);
            serverSocket.bind(http);

            System.out.println("Server running...");
            Socket con = serverSocket.accept();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            System.out.println("Conn: " + bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server stoped.");
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new MainAdmin();
    }


}
