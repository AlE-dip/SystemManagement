package core;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class Camera {
    public static boolean run = false;

    public static void start(Socket socket) {
        run = true;
        VideoCapture videoCapture = new VideoCapture();
        Mat frame = new Mat();
        videoCapture.open(0);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Camera start...");
                    while (run){
                        try {
                            videoCapture.read(frame);
//                            byte[] points = new byte[(int) (frame.total() * frame.channels())];
//                            frame.get(0, 0, points);
//                            String imageInString = Base64.getEncoder().encodeToString(points);
//                            writer.write(frame.rows() + "\r\n");
//                            writer.write(frame.cols() + "\r\n");
//                            writer.write(frame.type() + "\r\n");
//                            writer.write(imageInString);
//                            writer.newLine();
//                            writer.flush();
                            Core.writeMatToBuffer(frame, writer);
                            TimeUnit.MICROSECONDS.sleep(500);

                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                            System.out.println("Camera stop...");
                            return;
                        }
                    }
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Camera stop...");
                        return;
                    }
                    if(videoCapture.isOpened()){
                        videoCapture.release();
                    }
                    System.out.println("Camera stop...");
                }
            });
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public static void observer(Socket socket, JLabel label){
        run = true;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Camera.observer running...");
                    while (run){
                        try {
//                            int rows = Integer.parseInt(reader.readLine());
//                            int cols = Integer.parseInt(reader.readLine());
//                            int types = Integer.parseInt(reader.readLine());
//                            byte[] points = Base64.getDecoder().decode(reader.readLine());
//
//                            Mat frame = new Mat(rows, cols, types);
                            //frame.put(0,0, points);
                            Mat image = Core.readBufferToMat(reader);

                            label.setIcon(new ImageIcon(HighGui.toBufferedImage(image)));
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Camera.observer stop!");
                            return;
                        }
                    }
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Camera.observer stop!");
                        return;
                    }
                    System.out.println("Camera.observer stop!");
                }
            });
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

}
