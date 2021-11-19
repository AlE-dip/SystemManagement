package admin;

import admin.gui.OshiGui;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.Core;
import core.Session;
import core.SystemSR;
import core.UtilContent;
import core.model.Action;
import core.model.ClientInfo;
import core.model.Clients;
import core.model.StringMat;
import core.system.SystemInfo;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class AdminSession extends Session {
    private Thread threadSystemInfo;
    private String id;
    private Thread camera;

    public AdminSession(Socket skConnect) throws IOException {
        super(skConnect);
        Core.writeString(writerConnect, UtilContent.admin);
        id = readerConnect.readLine();
    }

    public void createConnectSystemInfo() throws IOException {
        skSystemInfo = new Socket(UtilContent.address, UtilContent.port);
        createBufferedSystemInfo();
        Action action = new Action(UtilContent.createConnectSystemInfo, id);
        String stringAction = new ObjectMapper().writeValueAsString(action);
        Core.writeString(writerSystemInfo, stringAction);
        receiveSystemInfo(writerSystemInfo, readerSystemInfo);
        Core.writeString(writerSystemInfo, UtilContent.systemReceiveReady);
    }

    private void createConnectCamera() throws IOException {
        skCamera = new Socket(UtilContent.address, UtilContent.port);
        createBufferedCamera();
        Action action = new Action(UtilContent.createConnectCamera, id);
        String stringAction = new ObjectMapper().writeValueAsString(action);
        Core.writeString(writerCamera, stringAction);
        cameraObserver(readerCamera, writerCamera);
    }

    public void receiveSystemInfo(BufferedWriter writer, BufferedReader reader) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        threadSystemInfo = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true){
                    try {
                        String dataSystem = reader.readLine();
                        SystemInfo systemInfo = mapper.readerFor(SystemInfo.class).readValue(dataSystem);

                        System.out.print(i + ": ");
                        i++;
                        System.out.println(dataSystem);

                        Core.writeString(writer, UtilContent.continues);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Disconnect to server!");
                        break;
                    }
                }
            }
        });
        threadSystemInfo.start();
    }

    public void cameraObserver(BufferedReader reader, BufferedWriter writer/*, JLabel label*/){
        camera = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Camera.observer running...");
                while (true){
                    try {
                        String dataCamera = reader.readLine();
                        StringMat stringMat = new ObjectMapper().readerFor(StringMat.class).readValue(dataCamera);
                        Mat image = stringMat.toMat();
                        System.out.println(image.cols() + ": " + image.rows());

                        //label.setIcon(new ImageIcon(HighGui.toBufferedImage(image)));
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Camera.observer stop!");
                        return;
                    }
                }
            }
        });
        camera.start();
    }

    public void sendRequest(String stringAction){
        try {
            Core.writeString(writerConnect, stringAction);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        System.out.println("Admin start...");
        Thread sendRequest = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                while (true){
                    String action = scanner.nextLine();
                    if(action.equals(UtilContent.createConnectCamera)){
                        try {
                            createConnectCamera();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(action.equals(UtilContent.stopCamera)){
                        sendRequest(UtilContent.stopCamera);
                        resetCamera();
                    }
                }
            }
        });
        sendRequest.start();
        try {
            while (true){
                String stringAction = readerConnect.readLine();
                if(stringAction.equals(UtilContent.createConnectSystemInfo)) {
                    createConnectSystemInfo();
                    System.out.println("SystemInfo running...");
                } else if(stringAction.equals(UtilContent.reset)){
                    reset();
                } else {
                    Action action = new ObjectMapper().readerFor(Action.class).readValue(stringAction);
                    switch (action.getAction()){
                        case UtilContent.newClient: {
                            System.out.println("New client: " + action.getData());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
