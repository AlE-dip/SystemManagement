package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.Core;
import core.Session;
import core.SystemSR;
import core.UtilContent;
import core.model.Action;
import core.model.StringMat;
import core.system.SystemInfo;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ClientSession extends Session {
    private Thread threadSystemInfo;
    private String id;

    public ClientSession(Socket skConnect) throws IOException {
        super(skConnect);
        Core.writeString(writerConnect, UtilContent.client);
        id = readerConnect.readLine();
    }

    public void createConnectSystemInfo() throws IOException {
        skSystemInfo = new Socket(UtilContent.address, UtilContent.port);
        createBufferedSystemInfo();
        Action action = new Action(UtilContent.createConnectSystemInfo, id);
        String stringAction = new ObjectMapper().writeValueAsString(action);
        Core.writeString(writerSystemInfo, stringAction);
        sendSystemInfo(writerSystemInfo, readerSystemInfo);
    }

    private void createConnectCamera() throws IOException {
        skCamera = new Socket(UtilContent.address, UtilContent.port);
        createBufferedCamera();
        Action action = new Action(UtilContent.createConnectCamera, id);
        String stringAction = new ObjectMapper().writeValueAsString(action);
        Core.writeString(writerCamera, stringAction);
    }

    public void sendSystemInfo(BufferedWriter writer, BufferedReader reader) throws IOException {
        oshi.SystemInfo si = new oshi.SystemInfo();
        SystemInfo systemInfo = new SystemInfo(si);
        ObjectMapper mapper = new ObjectMapper();
        threadSystemInfo = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        systemInfo.refresh();
                        String dataSystem = mapper.writeValueAsString(systemInfo);
                        Core.writeString(writer, dataSystem);

                        String feedback = reader.readLine();
                        if (feedback.equals(UtilContent.continues)){
                            Thread.sleep(UtilContent.timeSystemInfo);
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("SystemInfo stopped.");
                        break;
                    }
                }
            }
        });

        String wait = reader.readLine();
        if(wait.equals(UtilContent.systemForwardReady)){
            threadSystemInfo.start();
        }
    }

    public static void cameraStart(BufferedWriter writer) {
        VideoCapture videoCapture = new VideoCapture();
        Mat frame = new Mat();
        videoCapture.open(0);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Camera start...");
                while (true){
                    try {
                        videoCapture.read(frame);
                        StringMat stringMat = new StringMat(frame);
                        String dataCamera = new ObjectMapper().writeValueAsString(stringMat);
                        Core.writeString(writer, dataCamera);

                        Thread.sleep(UtilContent.timeCamera);

                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                        if(videoCapture.isOpened()){
                            videoCapture.release();
                        }
                        System.out.println("Camera stop...");
                        return;
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void run() {
        super.run();
        System.out.println("Client start...");
        try {
            while (true){
                String stringAction = readerConnect.readLine();
                if(stringAction.equals(UtilContent.createConnectSystemInfo)){
                    createConnectSystemInfo();
                    System.out.println("SystemInfo running...");
                } else if (stringAction.equals(UtilContent.reset)) {
                    threadSystemInfo.interrupt();
                    closeSocket();
                    System.out.println("Wait...");
                } else if (stringAction.equals(UtilContent.createConnectCamera)) {
                    createConnectCamera();
                    cameraStart(writerCamera);
                }else if(stringAction.equals(UtilContent.stopCamera)) {
                    resetCamera();
                } else {
//                    Action action = new ObjectMapper().readerFor(Action.class).readValue(stringAction);
//                    switch (action.getAction()){
//
//                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
