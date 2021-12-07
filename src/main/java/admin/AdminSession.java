package admin;

import admin.gui.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.Core;
import core.ProcessManager;
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
import java.awt.*;
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

    private void createConnectClipboard() throws IOException {
        skClipboard = new Socket(UtilContent.address, UtilContent.port);
        createBufferedClipboard();
        Action action = new Action(UtilContent.createConnectClipboard, id);
        String stringAction = new ObjectMapper().writeValueAsString(action);
        Core.writeString(writerClipboard, stringAction);
        clipboardObserver(readerClipboard);
    }

    private void createConnectKeyboard() throws IOException {
        skKeyboard = new Socket(UtilContent.address, UtilContent.port);
        createBufferedKeyboard();
        Action action = new Action(UtilContent.createConnectKeyboard, id);
        String stringAction = new ObjectMapper().writeValueAsString(action);
        Core.writeString(writerKeyboard, stringAction);
        keyboardObserver(readerKeyboard);
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
                boolean run = true;
                while (run){
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
                                    int height = gui.cameraPanel.lbCamera.getHeight();
                                    Core.resizeAuto(image, width, height);

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
                        gui.cameraPanel.lbCamera.setIcon(gui.iconWarn);
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
                        gui.screensPanel.lbScreens.setIcon(gui.iconWarn);
                        System.out.println("Screenshot.observer stop!");
                        return;
                    }
                }
            }
        });
        thread.start();
    }

    public void clipboardObserver(BufferedReader reader) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Clipboard.observer running...");
                while (true){
                    try {
                        String dataClipboard = reader.readLine();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ObjectMapper mapper = new ObjectMapper();
                                try {
                                    Action action = mapper.readerFor(Action.class).readValue(dataClipboard);
                                    if(action.getAction().equals(UtilContent.sendTypeString)){
                                        gui.clipKeyboardPanel.clipboardPanelAddItem(action.getData());
                                    }else if (action.getAction().equals(UtilContent.sendTypeImage)){
                                        BufferedImage image = Core.stringToBufferedImage((String) action.getData());
                                        gui.clipKeyboardPanel.clipboardPanelAddItem(image);
                                    }
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                    return;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return;
                                }
                            }
                        });
                        thread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Clipboard.observer stop!");
                        return;
                    }
                }
            }
        });
        thread.start();
    }

    public void keyboardObserver(BufferedReader reader) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Keyboard.observer running...");
                while (true){
                    try {
                        String dataKeyboard = reader.readLine();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                gui.clipKeyboardPanel.appendDataKeyEvent(dataKeyboard);
                            }
                        });
                        thread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Keyboard.observer stop!");
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
        try {
            while (true){
                String stringAction = readerConnect.readLine();
                if(stringAction.equals(UtilContent.createConnectSystemInfo)) {
                    createConnectSystemInfo();
                    System.out.println("SystemInfo running...");
                } else if(stringAction.equals(UtilContent.reset)){
                    reset();
                } else if(stringAction.equals(UtilContent.onCloseClipboard)){
                    resetClipboard();
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
                sendRequest(UtilContent.disconnect);
            }

            @Override
            public void shutdown() {
                sendRequest(UtilContent.shutdown);
            }
        });

        //set sự kiện click end task
        gui.processPanel.setEventButton(new ProcessPanel.ProcessManager() {
            @Override
            public void killProcess(String pid) {
                Action action = new Action(UtilContent.killProcess, pid);
                try {
                    String stringAction = new ObjectMapper().writeValueAsString(action);
                    sendRequest(stringAction);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        });

        //set sự kiện click button keyboard và clipboard
        gui.clipKeyboardPanel.setEventButton(new ClipKeyboardPanel.ClipKeyboard() {
            @Override
            public void runClipboard() {
                try {
                    createConnectClipboard();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void stopClipboard() {
                sendRequest(UtilContent.stopClipboard);
                resetClipboard();
            }

            @Override
            public void runKeyboard() {
                try {
                    createConnectKeyboard();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void stopKeyboard() {
                sendRequest(UtilContent.stopKeyboard);
                resetKeyboard();
            }
        });
    }
}
