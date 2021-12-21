package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.Core;
import core.ImageHandle;
import core.UtilContent;
import core.model.Action;
import core.model.StringMat;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class Forwarder {
    private String systemInfoReceiveReady = "";
    private Map<Long, ServerSession> mapWork;
    private ServerSession clientServer;
    private ServerSession adminServer;
    public Thread threadSystemInfo;
    private Thread threadScreens;
    public int timeSave;

    public Forwarder() {
        adminServer = null;
        clientServer = null;
        mapWork = new HashMap<>();
        timeSave = UtilContent.onceMinute;
    }

    public void createConnectSystemInfo() throws IOException {
        //Start admin
        adminServer.sendRequest(UtilContent.createConnectSystemInfo);
        clientServer = firstOrNonClient();
        if (clientServer != null) {
            clientServer.sendRequest(UtilContent.createConnectSystemInfo);
        }
    }

    public void createConnectCamera() throws IOException {
        if(clientServer != null && clientServer.getSkCamera() == null){
            clientServer.sendRequest(UtilContent.createConnectCamera);
        }
    }

    public void createConnectScreens() throws IOException {
        if(clientServer != null && clientServer.getSkScreens() == null){
            clientServer.sendRequest(UtilContent.createConnectScreens);
        }
    }

    public void createConnectClipboard() throws IOException {
        if(clientServer != null && clientServer.getSkClipboard() == null){
            clientServer.sendRequest(UtilContent.createConnectClipboard);
        }
    }

    public void createConnectKeyboard() throws IOException {
        if(clientServer != null && clientServer.getSkKeyboard() == null){
            clientServer.sendRequest(UtilContent.createConnectKeyboard);
        }
    }

    public void runSystemInfo() throws IOException {
        if (adminServer == null || clientServer == null || adminServer.getSkSystemInfo() == null || clientServer.getSkSystemInfo() == null) {
            return;
        }
        if (systemInfoReceiveReady == "") {
            systemInfoReceiveReady = adminServer.getReaderSystemInfo().readLine();
        }
        //gửi danh sách client
        Map<String, Object> mapDataUser = new LinkedHashMap<>();
        mapDataUser.put(UtilContent.listId, getAllIdClient());
        mapDataUser.put(UtilContent.current, clientServer.getId() + "");
        Action action = new Action(UtilContent.newClientConnect, mapDataUser);
        String stringAction = new ObjectMapper().writeValueAsString(action);
        Core.writeString(adminServer.getWriterConnect(), stringAction);
        //start
        if (systemInfoReceiveReady.equals(UtilContent.systemReceiveReady)) {
            forwardSystemInfo(adminServer.getWriterSystemInfo(), adminServer.getReaderSystemInfo(),
                    clientServer.getWriterSystemInfo(), clientServer.getReaderSystemInfo());
            Core.writeString(clientServer.getWriterSystemInfo(), UtilContent.systemForwardReady);
        }
    }

    public void newClient(String id, String clientHostName) throws IOException {
        if(adminServer != null){
            Action action = new Action(UtilContent.newClient, new String[] {id, clientHostName});
            String stringAction = new ObjectMapper().writeValueAsString(action);
            Core.writeString(adminServer.getWriterConnect(), stringAction);
        }
    }

    public void destroyClient(String id) throws IOException {
        if(adminServer != null){
            Action action = new Action(UtilContent.destroyClient, id);
            String stringAction = new ObjectMapper().writeValueAsString(action);
            Core.writeString(adminServer.getWriterConnect(), stringAction);
        }
    }

    public void changeCurrentClient(String id) throws IOException {
        if(clientServer != null){
            try {
                Core.writeString(clientServer.getWriterSystemInfo(), UtilContent.stopSystemInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stopLogScreens();
            clientServer.sendRequest(UtilContent.reset);
            clientServer.closeSocket();
            clientServer = null;
        }
        clientServer = mapWork.get(Long.parseLong(id));
        if(clientServer != null){
            adminServer.sendRequest(UtilContent.reset);
            clientServer.sendRequest(UtilContent.createConnectSystemInfo);
            runSystemInfo();
        }
    }

    private ArrayList<String[]> getAllIdClient(){
        ArrayList<String[]> ids = new ArrayList<>();
        for(Map.Entry<Long, ServerSession> entry: mapWork.entrySet()){
            ids.add(new String[] {entry.getValue().getId() + "", entry.getValue().clientHostName});
        }
        return ids;
    }

    public void runCamera() throws IOException {
        if (adminServer == null || clientServer == null || adminServer.getSkCamera() == null || clientServer.getSkCamera() == null) {
            return;
        }
        forwardUtil(adminServer.getWriterCamera(), clientServer.getReaderCamera(), "Camera");
    }

    public void runScreens() throws IOException {
        if (adminServer == null || clientServer == null || adminServer.getSkScreens() == null || clientServer.getSkScreens() == null) {
            return;
        }
        forwardScreens(adminServer.getWriterScreens(), clientServer.getReaderScreens(), "Screens");
    }

    public void runClipboard() throws IOException {
        if (adminServer == null || clientServer == null || adminServer.getSkClipboard() == null || clientServer.getSkClipboard() == null) {
            return;
        }
        forwardUtil(adminServer.getWriterClipboard(), clientServer.getReaderClipboard(), "Clipboard");
    }

    public void runKeyboard() throws IOException {
        if (adminServer == null || clientServer == null || adminServer.getSkKeyboard() == null || clientServer.getSkKeyboard() == null) {
            return;
        }
        forwardUtil(adminServer.getWriterKeyboard(), clientServer.getReaderKeyboard(), "Keyboard");
    }

    public void resetCamera(){
        clientServer.sendRequest(UtilContent.stopCamera);
        clientServer.resetCamera();
        adminServer.resetCamera();
    }

    public void resetScreens(){
        stopLogScreens();
        clientServer.sendRequest(UtilContent.stopScreens);
        clientServer.resetScreens();
        adminServer.resetScreens();
    }

    public void stopLogScreens(){
        if(threadScreens != null && !threadScreens.isInterrupted()){
            threadScreens.interrupt();
        }
    }

    public void getListLogScreens(){
        try {
            Action action = new Action(UtilContent.sendTypeScreensLog, ImageHandle.scanImage(clientServer.clientHostName));
            String stringAction = new ObjectMapper().writeValueAsString(action);
            adminServer.sendRequest(stringAction);
        } catch (Exception e) {
            e.printStackTrace();
            adminServer.sendRequest(UtilContent.getLogImageNull);
            System.out.println("Send null data.");
        }
    }

    public void getLogImage(String path){
        try {
            Mat image = Imgcodecs.imread("image\\" + path);
            StringMat stringMat = new StringMat(image);
            Action action = new Action(UtilContent.getLogImage, stringMat);
            String stringAction = new ObjectMapper().writeValueAsString(action);
            adminServer.sendRequest(stringAction);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public synchronized void writeLogScreens(){
        int delay = 1000;
        threadScreens = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(delay);
                        timeSave -= delay;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("End log screens.");
                        break;
                    }
                }
            }
        });
        threadScreens.start();
    }

    public void disconnectClient(){
        stopLogScreens();
        clientServer.sendRequest(UtilContent.disconnect);
    }

    public void shutDownClient(){
        stopLogScreens();
        clientServer.sendRequest(UtilContent.shutdown);
    }

    public void resetClipboard(){
        clientServer.sendRequest(UtilContent.stopClipboard);
        clientServer.resetClipboard();
        adminServer.resetClipboard();
    }

    public void onCloseClipboard(){
        adminServer.sendRequest(UtilContent.onCloseClipboard);
        clientServer.resetClipboard();
        adminServer.resetClipboard();
    }

    public void resetKeyboard(){
        clientServer.sendRequest(UtilContent.stopKeyboard);
        clientServer.resetKeyboard();
        adminServer.resetKeyboard();
    }

    public void killProcessClient(String stringAction){
        clientServer.sendRequest(stringAction);
    }

    public ServerSession firstOrNonClient() {
        if (!mapWork.isEmpty()) {
            Map.Entry<Long, ServerSession> entry = mapWork.entrySet().iterator().next();
            clientServer = entry.getValue();
            return clientServer;
        } else {
            return null;
        }
    }

    public void createConnectSystemInfoWithThisClient(ServerSession clientServer) throws IOException {
        this.clientServer = clientServer;
        clientServer.sendRequest(UtilContent.createConnectSystemInfo);
    }

    public void forwardSystemInfo(BufferedWriter writerAdmin, BufferedReader readerAdmin, BufferedWriter writerClient,
                                  BufferedReader readerClient) throws IOException {
        threadSystemInfo = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String dataSystem = "";
                    try {
                        dataSystem = readerClient.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("SystemInfo stop!");
                        break;
                    }
                    //System.out.println(dataSystem.length());
                    String feedback = "";
                    try {
                        Core.writeString(writerAdmin, dataSystem);
                        feedback = readerAdmin.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("SystemInfo stop!");
                        break;
                    }
                    try {
                        if (feedback.equals(UtilContent.continues)) {
                            Core.writeString(writerClient, UtilContent.continues);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("SystemInfo stop!");
                        break;
                    }
                }
            }
        });
        threadSystemInfo.start();
    }

    public void forwardUtil(BufferedWriter writerAdmin, BufferedReader readerClient, String type) throws IOException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String data = "";
                    try {
                        data = readerClient.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println(type + " stop!");
                        break;
                    }
                    //System.out.println(dataSystem.length());
                    try {
                        Core.writeString(writerAdmin, data);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println(type + "stop!");
                        break;
                    }
                }
            }
        });
        thread.start();
    }

    public synchronized void forwardScreens(BufferedWriter writerAdmin, BufferedReader readerClient, String type) throws IOException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String data = "";
                    try {
                        data = readerClient.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println(type + " stop!");
                        break;
                    }
                    //System.out.println(dataSystem.length());
                    try {
                        Core.writeString(writerAdmin, data);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println(type + "stop!");
                        break;
                    }
                    //Sava log image
                    if(timeSave <= 0){
                        String finalData = data;
                        Thread thread1 = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    System.out.println("Save image.");
                                    StringMat stringMat = new ObjectMapper().readerFor(StringMat.class).readValue(finalData);
                                    Mat image = stringMat.toMat();
                                    ImageHandle.storeImage(HighGui.toBufferedImage(image), clientServer.clientHostName);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.out.println("Can not save!");
                                }
                            }
                        });
                        thread1.start();
                        timeSave = UtilContent.onceMinute;
                    }
                }
            }
        });
        thread.start();
    }

    public void forwardDebug(BufferedWriter writerAdmin, BufferedReader readerClient, String type) throws IOException {
        threadSystemInfo = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String dataSystem = "";
                    try {
                        dataSystem = readerClient.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println(type + " stop!");
                        break;
                    }
                    System.out.println(dataSystem.length());
                    try {
                        Core.writeString(writerAdmin, dataSystem);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println(type + "stop!");
                        return;
                    }
                }
            }
        });
        threadSystemInfo.start();
    }

    //xóa admin, tạm dùng client
    public void disconnectWithAdmin() {
        adminServer.interrupt();
        adminServer = null;
        systemInfoReceiveReady = "";
        stopLogScreens();
        if (clientServer != null) {
            try {
                Core.writeString(clientServer.getWriterSystemInfo(), UtilContent.stopSystemInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            clientServer.sendRequest(UtilContent.reset);
            clientServer.closeSocket();
            clientServer = null;
            System.out.println("Reset client.");
        }
        System.out.println("Disconnect admin!!!");
    }

    //Xóa client, reset admin
    public void disconnectWithClient(long id) {
        mapWork.remove(id);
        System.out.println("Disconnect client " + id + "!!!");
        //trường hợp mất connect với current client
        if(clientServer.getId() == id){
            stopLogScreens();
            adminServer.sendRequest(UtilContent.reset);
            clientServer = firstOrNonClient();
            if (clientServer != null) {
                try {
                    clientServer.sendRequest(UtilContent.createConnectSystemInfo);
                    runSystemInfo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Wait while client connect...");
            }
        }
        try {
            destroyClient(id + "");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can not send destroy action!");
        }
    }

    public ServerSession getClientServer() {
        return clientServer;
    }

    public void setClientServer(ServerSession clientServer) {
        this.clientServer = clientServer;
    }

    public ServerSession getAdminServer() {
        return adminServer;
    }

    public void setAdminServer(ServerSession adminServer) {
        this.adminServer = adminServer;
    }

    public Map<Long, ServerSession> getMapWork() {
        return mapWork;
    }

    public void setMapWork(Map<Long, ServerSession> mapWork) {
        this.mapWork = mapWork;
    }
}
