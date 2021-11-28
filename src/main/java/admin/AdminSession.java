package admin;

import admin.gui.AdminGui;
import admin.gui.CameraPanel;
import admin.gui.HeaderOsHwPanel;
import admin.gui.ScreensPanel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.Core;
import core.Session;
import core.UtilContent;
import core.model.Action;
import core.model.Clients;
import core.model.StringMat;
import core.system.SystemInfo;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class AdminSession extends Session {
    private Thread threadSystemInfo;
    private String id;
    private Thread camera;
    private AdminGui gui;

    public AdminSession(Socket skConnect, AdminGui gui) throws IOException {
        super(skConnect);
        this.gui = gui;
        Core.writeString(writerConnect, UtilContent.admin);
        id = readerConnect.readLine();
    }

    public void createConnectSystemInfo() throws IOException {
        skSystemInfo = new Socket(UtilContent.address, UtilContent.port);
        createBufferedSystemInfo();

        //set action changeCurrent cho gui
        setActionButtonForGui();

        //Bắt đầu trao đổi systemInfo
        Action action = new Action(UtilContent.createConnectSystemInfo, id);
        String stringAction = new ObjectMapper().writeValueAsString(action);
        Core.writeString(writerSystemInfo, stringAction);
        receiveSystemInfo(writerSystemInfo, readerSystemInfo);
        Core.writeString(writerSystemInfo, UtilContent.systemReceiveReady);
    }

    private void setActionButtonForGui() {
        //thay đổi user khi click button user
        gui.guiAction = new AdminGui.GuiAction() {
            @Override
            public void changeCurrentUser(String id) {
                try {
                    Action action = new Action(UtilContent.changeCurrent, id);
                    String stringAction = new ObjectMapper().writeValueAsString(action);
                    Core.writeString(writerConnect, stringAction);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        //set sự kiện click button camera
        gui.cameraPanel.setEventButton(new CameraPanel.Camera() {
            @Override
            public void runCamera() {
                try {
                    createConnectCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void stopCamera() {
                sendRequest(UtilContent.stopCamera);
                resetCamera();
            }
        });

        //set sự kiện click button screens
        gui.screensPanel.setEventButton(new ScreensPanel.Screens() {
            @Override
            public void runScreens() {
                try {
                    createConnectScreens();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void stopScreens() {
                sendRequest(UtilContent.stopScreens);
                resetScreens();
            }
        });

        //set sự kiện click shutdown va disconnect
        gui.osHwTextPanel.headerOsHwPanel.setEventButton(new HeaderOsHwPanel.Control() {
            @Override
            public void disconnect() {

            }

            @Override
            public void shutdown() {

            }
        });
    }

    private void createConnectCamera() throws IOException {
        skCamera = new Socket(UtilContent.address, UtilContent.port);
        createBufferedCamera();
        Action action = new Action(UtilContent.createConnectCamera, id);
        String stringAction = new ObjectMapper().writeValueAsString(action);
        Core.writeString(writerCamera, stringAction);
        cameraObserver(readerCamera);
    }

    private void createConnectScreens() throws IOException {
        skScreens = new Socket(UtilContent.address, UtilContent.port);
        createBufferedScreens();
        Action action = new Action(UtilContent.createConnectScreens, id);
        String stringAction = new ObjectMapper().writeValueAsString(action);
        Core.writeString(writerScreens, stringAction);
        screensObserver(readerScreens);
    }

    public void receiveSystemInfo(BufferedWriter writer, BufferedReader reader) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        threadSystemInfo = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        String dataSystem = reader.readLine();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    SystemInfo systemInfo = mapper.readerFor(SystemInfo.class).readValue(dataSystem);
                                    if(gui.created){
                                        gui.refresh(systemInfo);
                                    }else {
                                        gui.create(systemInfo);
                                    }
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
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

    public void cameraObserver(BufferedReader reader){
        camera = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Camera.observer running...");
                while (true){
                    try {
                        String dataCamera = reader.readLine();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    StringMat stringMat = new ObjectMapper().readerFor(StringMat.class).readValue(dataCamera);
                                    Mat image = stringMat.toMat();
                                    //System.out.println(image.cols() + ": " + image.rows());
                                    int width = gui.cameraPanel.lbCamera.getWidth();
                                    Core.resizeBasedOnWidth(image, width);

                                    gui.cameraPanel.refresh(new ImageIcon(HighGui.toBufferedImage(image)));
                                    //label.setIcon(new ImageIcon(HighGui.toBufferedImage(image)));
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Camera.observer stop!");
                        gui.cameraPanel.lbCamera.setIcon(null);
                        return;
                    }
                }
            }
        });
        camera.start();
    }

    public void screensObserver(BufferedReader reader) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Screenshot.observer running...");
                while (true){
                    try {
                        String dataScreens = reader.readLine();
                        Thread thread1 = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    StringMat stringMat = new ObjectMapper().readerFor(StringMat.class).readValue(dataScreens);
                                    Mat image = stringMat.toMat();
                                    int width = gui.screensPanel.lbScreens.getWidth();
                                    Core.resizeBasedOnWidth(image, width);
                                    gui.screensPanel.refresh(new ImageIcon(HighGui.toBufferedImage(image)));
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread1.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        gui.screensPanel.lbScreens.setIcon(null);
                        System.out.println("Screenshot.observer stop!");
                        return;
                    }
                }
            }
        });
        thread.start();
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
                        case UtilContent.newClientConnect: {
                            LinkedHashMap<String, Object> mapDataUser = (LinkedHashMap<String, Object>) action.getData();
                            gui.addUserButtons((ArrayList<ArrayList<String>>) mapDataUser.get(UtilContent.listId));
                            gui.setCurrentButton((String) mapDataUser.get(UtilContent.current));
                            break;
                        }
                        case UtilContent.newClient: {
                            ArrayList<String> data = (ArrayList<String>) action.getData();
                            gui.addUserButton(data.get(0), data.get(1));
                            break;
                        }
                        case UtilContent.destroyClient: {
                            gui.destroyUserButton((String) action.getData());
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
