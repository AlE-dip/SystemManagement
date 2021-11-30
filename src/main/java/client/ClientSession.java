package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.*;
import core.model.Action;
import core.model.StringMat;
import core.system.SystemInfo;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ClientSession extends Session {
    private Thread threadSystemInfo;
    private String id;
    ClipboardListener clipboardListener;

    public ClientSession(Socket skConnect) throws IOException {
        super(skConnect);
        Core.writeString(writerConnect, UtilContent.client);
        //send name PC
        oshi.SystemInfo si = new oshi.SystemInfo();
        String hostName = si.getOperatingSystem().getNetworkParams().getHostName();
        Core.writeString(writerConnect, hostName);
        //Nhận id từ server
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

    private void createConnectScreens() throws IOException {
        skScreens = new Socket(UtilContent.address, UtilContent.port);
        createBufferedScreens();
        Action action = new Action(UtilContent.createConnectScreens, id);
        String stringAction = new ObjectMapper().writeValueAsString(action);
        Core.writeString(writerScreens, stringAction);
    }

    private void createConnectClipboard() throws IOException {
        skClipboard = new Socket(UtilContent.address, UtilContent.port);
        createBufferedClipboard();
        Action action = new Action(UtilContent.createConnectClipboard, id);
        String stringAction = new ObjectMapper().writeValueAsString(action);
        Core.writeString(writerClipboard, stringAction);
    }

    private void disconnect() {
        interrupt();
    }

    private void shutdown() {
        interrupt();
        System.out.println("Shutdown");
        /*try {
            ProcessManager.shutDown();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void killProcess(String pid) {
        System.out.println("Kill" + pid);
        /*try {
            ProcessManager.killProcess(Integer.parseInt(pid));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void sendRequest(String stringAction){
        try {
            Core.writeString(writerConnect, stringAction);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopClipboard() {
        //Gắn false để dừng theo dõi clipboard
        if(clipboardListener != null){
            clipboardListener.run = false;
            clipboardListener = null;
            resetClipboard();
            System.out.println("Stopped clipboard.");
        }
    }

    public void sendSystemInfo(BufferedWriter writer, BufferedReader reader) throws IOException {
        oshi.SystemInfo si = new oshi.SystemInfo();
        SystemInfo systemInfo = new SystemInfo(si);
        ObjectMapper mapper = new ObjectMapper();
        threadSystemInfo = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        systemInfo.refresh();
                        String dataSystem = mapper.writeValueAsString(systemInfo);
                        Core.writeString(writer, dataSystem);

                        String feedback = reader.readLine();
                        if (feedback.equals(UtilContent.continues)) {
                            Thread.sleep(UtilContent.timeSystemInfo);
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("SystemInfo stopped.");
                        return;
                    }
                }
            }
        });

        String wait = reader.readLine();
        if (wait.equals(UtilContent.systemForwardReady)) {
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
                while (true) {
                    try {
                        videoCapture.read(frame);
                        StringMat stringMat = new StringMat(frame);
                        String dataCamera = new ObjectMapper().writeValueAsString(stringMat);
                        Core.writeString(writer, dataCamera);

                        Thread.sleep(UtilContent.timeCamera);

                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                        if (videoCapture.isOpened()) {
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

    public void screensStart(BufferedWriter writer) {
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Record start...");
                    while (true) {
                        BufferedImage screenshot = robot.createScreenCapture(screenRect);
                        try {
                            Core.paintCursor(screenshot);
                            Mat mat = Core.bufferedImageToMat(screenshot);
                            StringMat stringMat = new StringMat(mat);
                            String dataScreens = new ObjectMapper().writeValueAsString(stringMat);
                            Core.writeString(writer, dataScreens);

                            TimeUnit.MICROSECONDS.sleep(10);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                            System.out.println("Screens record stop.");
                            return;
                        }
                    }
                }
            });
            thread.start();
        } catch (AWTException e) {
            e.printStackTrace();
            System.out.println("Can not create robot!");
        }
    }

    private void clipboardListen(BufferedWriter writerClipboard) {
        clipboardListener = new ClipboardListener(new ClipboardListener.ClipboardChange() {
            @Override
            public void onChange(Transferable transferable) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String data = (String) (transferable.getTransferData(DataFlavor.stringFlavor));
                    Action action = new Action(UtilContent.sendTypeString, data);
                    String stringAction = mapper.writeValueAsString(action);
                    Core.writeString(writerClipboard, stringAction);
                    System.out.println("Send string");
                } catch (Exception e) {
                    try {
                        BufferedImage bufferedImage = (BufferedImage) transferable.getTransferData(DataFlavor.imageFlavor);
                        String data = Core.bufferedImageToString(bufferedImage);
                        Action action = new Action(UtilContent.sendTypeImage, data);
                        String stringAction = mapper.writeValueAsString(action);
                        Core.writeString(writerClipboard, stringAction);
                        System.out.println("Send image");
                    } catch (UnsupportedFlavorException ex) {
                        System.out.println("Not string or image UnsupportedFlavorException");
                    } catch (IOException ex) {
                        System.out.println("Not string or image IOException");
                    }
                }
            }

            @Override
            public void onClose() {
                sendRequest(UtilContent.onCloseClipboard);
                System.out.println("Lost clipboard");
            }
        });

    }

    @Override
    public void run() {
        super.run();
        System.out.println("Client start...");
        try {
            while (true) {
                String stringAction = readerConnect.readLine();
                if (stringAction.equals(UtilContent.createConnectSystemInfo)) {
                    createConnectSystemInfo();
                    System.out.println("SystemInfo running...");
                } else if (stringAction.equals(UtilContent.reset)) {
                    //Yêu cầu reset từ server
                    stopClipboard();
                    threadSystemInfo.interrupt();
                    closeSocket();
                    System.out.println("Wait...");
                } else if (stringAction.equals(UtilContent.createConnectCamera)) {
                    createConnectCamera();
                    cameraStart(writerCamera);
                } else if (stringAction.equals(UtilContent.stopCamera)) {
                    resetCamera();
                } else if (stringAction.equals(UtilContent.createConnectScreens)) {
                    createConnectScreens();
                    screensStart(writerScreens);
                } else if (stringAction.equals(UtilContent.stopScreens)) {
                    resetScreens();
                } else if (stringAction.equals(UtilContent.disconnect)) {
                    disconnect();
                } else if (stringAction.equals(UtilContent.shutdown)) {
                    shutdown();
                } else if (stringAction.equals(UtilContent.createConnectClipboard)) {
                    createConnectClipboard();
                    clipboardListen(writerClipboard);
                } else if (stringAction.equals(UtilContent.stopClipboard)) {
                    stopClipboard();
                } else {
                    Action action = new ObjectMapper().readerFor(Action.class).readValue(stringAction);
                    switch (action.getAction()) {
                        case UtilContent.killProcess: {
                            killProcess((String) action.getData());
                        }
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
