package client;

import core.Camera;
import core.Screenshot;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.opencv.highgui.HighGui.waitKey;

public class MainClient extends JFrame{
    private JPanel pannel;
    private JLabel label;
    private JButton btRun;
    private JButton btTest;
    private JPanel pn1;
    private boolean run = false;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        new MainClient();
    }

    public MainClient(){
        setContentPane(pannel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(1000, 700);
        setVisible(true);

        btRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                run = true;
                VideoCapture videoCapture = new VideoCapture();
                Mat frame = new Mat();
                videoCapture.open(0);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(videoCapture.isOpened()){
                            while (run){
                                try {
                                    TimeUnit.MICROSECONDS.sleep(1);
                                    videoCapture.read(frame);
                                    ImageIcon icon = new ImageIcon(HighGui.toBufferedImage(frame));
                                    Thread thread1 = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            label.setIcon(icon);
                                        }
                                    });
                                    thread1.start();
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                });
                thread.start();
            }
        });

        btTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Socket socket = new Socket("localhost", 50000);

                    //demo camera
                   Camera.start(socket);

                    //demo screenshot
                    //Screenshot.record(socket);

                } catch (IOException /*| AWTException*/ ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static class Mouse {
        public static final int FIVE_SECONDS = 5000;
        public static final int MAX_Y = 400;
        public static final int MAX_X = 400;

        public static void main(String... args) throws Exception {
            Robot robot = new Robot();
            Random random = new Random();
            while (true) {
                robot.mouseMove(random.nextInt(MAX_X), random.nextInt(MAX_Y));
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);

                Thread.sleep(FIVE_SECONDS);
            }


        }
    }
}
