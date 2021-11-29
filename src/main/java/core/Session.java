package core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import server.Server;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Session extends Thread {
    protected String role;
    protected Socket skConnect;
    protected Socket skSystemInfo;
    protected Socket skScreens;
    protected Socket skEvent;
    protected Socket skCamera;
    protected BufferedReader readerConnect;
    protected BufferedWriter writerConnect;
    protected BufferedReader readerSystemInfo;
    protected BufferedWriter writerSystemInfo;
    protected BufferedReader readerEvent;
    protected BufferedWriter writerEvent;
    protected BufferedReader readerScreens;
    protected BufferedWriter writerScreens;
    protected BufferedReader readerCamera;
    protected BufferedWriter writerCamera;

    public Session(Socket skConnect) throws IOException {
        this.skConnect = skConnect;
        readerConnect = new BufferedReader(new InputStreamReader(skConnect.getInputStream()));
        writerConnect = new BufferedWriter(new OutputStreamWriter(skConnect.getOutputStream()));
    }

    @Override
    public void run() {
        super.run();
    }

    public String createConnectInfo() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setIp(skConnect.getLocalAddress().getHostAddress());
        connectionInfo.setPort(skConnect.getLocalPort());
        return mapper.writeValueAsString(connectionInfo);
    }

    public String createStringConnect(ServerSocket serverSocket) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setIp(UtilContent.address);
        connectionInfo.setPort(serverSocket.getLocalPort());
        return mapper.writeValueAsString(connectionInfo);
    }

    public static Socket createSocketConnect(String stringConnect) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ConnectionInfo connectionInfo = mapper.readerFor(ConnectionInfo.class).readValue(stringConnect);
        Socket skConnect = new Socket(connectionInfo.getIp(), connectionInfo.getPort());
        return skConnect;
    }

    public void createBufferedSystemInfo() throws IOException {
        readerSystemInfo = new BufferedReader(new InputStreamReader(skSystemInfo.getInputStream()));
        writerSystemInfo = new BufferedWriter(new OutputStreamWriter(skSystemInfo.getOutputStream()));
    }

    public void createBufferedCamera() throws IOException {
        readerCamera = new BufferedReader(new InputStreamReader(skCamera.getInputStream()));
        writerCamera = new BufferedWriter(new OutputStreamWriter(skCamera.getOutputStream()));
    }

    public void createBufferedScreens() throws IOException {
        readerScreens = new BufferedReader(new InputStreamReader(skScreens.getInputStream()));
        writerScreens = new BufferedWriter(new OutputStreamWriter(skScreens.getOutputStream()));
    }

    @Override
    public void interrupt() {
        try {
            skConnect.close();
            skConnect = null;
            closeSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.interrupt();
    }

    public void closeSocket() {
        try {
            if (skSystemInfo != null && !skSystemInfo.isClosed()) {
                skSystemInfo.close();
                skSystemInfo = null;
            }
            if (skEvent != null && !skEvent.isClosed()) {
                skEvent.close();
                skEvent = null;
            }
            if (skScreens != null && !skScreens.isClosed()) {
                skScreens.close();
                skScreens = null;
            }
            if (skCamera != null && !skCamera.isClosed()) {
                skCamera.close();
                skCamera = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() throws IOException {
        if (skEvent != null && !skEvent.isClosed()) {
            skEvent.close();
            skEvent = null;
        }
        if (skScreens != null && !skScreens.isClosed()) {
            skScreens.close();
            skScreens = null;
        }
        if (skCamera != null && !skCamera.isClosed()) {
            skCamera.close();
            skCamera = null;
        }
    }

    public void resetCamera() {
        try {
            if (skCamera != null && !skCamera.isClosed()) {
                skCamera.close();
                skCamera = null;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void resetScreens() {
        try {
            if (skScreens != null && !skScreens.isClosed()) {
                skScreens.close();
                skScreens = null;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Socket getSkConnect() {
        return skConnect;
    }

    public void setSkConnect(Socket skConnect) {
        this.skConnect = skConnect;
    }

    public Socket getSkSystemInfo() {
        return skSystemInfo;
    }

    public void setSkSystemInfo(Socket skSystemInfo) {
        this.skSystemInfo = skSystemInfo;
    }

    public Socket getSkScreens() {
        return skScreens;
    }

    public void setSkScreens(Socket skScreens) {
        this.skScreens = skScreens;
    }

    public Socket getSkEvent() {
        return skEvent;
    }

    public void setSkEvent(Socket skEvent) {
        this.skEvent = skEvent;
    }

    public BufferedReader getReaderConnect() {
        return readerConnect;
    }

    public void setReaderConnect(BufferedReader readerConnect) {
        this.readerConnect = readerConnect;
    }

    public BufferedWriter getWriterConnect() {
        return writerConnect;
    }

    public void setWriterConnect(BufferedWriter writerConnect) {
        this.writerConnect = writerConnect;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public BufferedReader getReaderSystemInfo() {
        return readerSystemInfo;
    }

    public void setReaderSystemInfo(BufferedReader readerSystemInfo) {
        this.readerSystemInfo = readerSystemInfo;
    }

    public BufferedWriter getWriterSystemInfo() {
        return writerSystemInfo;
    }

    public void setWriterSystemInfo(BufferedWriter writerSystemInfo) {
        this.writerSystemInfo = writerSystemInfo;
    }

    public BufferedReader getReaderEvent() {
        return readerEvent;
    }

    public void setReaderEvent(BufferedReader readerEvent) {
        this.readerEvent = readerEvent;
    }

    public BufferedWriter getWriterEvent() {
        return writerEvent;
    }

    public void setWriterEvent(BufferedWriter writerEvent) {
        this.writerEvent = writerEvent;
    }

    public Socket getSkCamera() {
        return skCamera;
    }

    public void setSkCamera(Socket skCamera) {
        this.skCamera = skCamera;
    }

    public BufferedReader getReaderScreens() {
        return readerScreens;
    }

    public void setReaderScreens(BufferedReader readerScreens) {
        this.readerScreens = readerScreens;
    }

    public BufferedWriter getWriterScreens() {
        return writerScreens;
    }

    public void setWriterScreens(BufferedWriter writerScreens) {
        this.writerScreens = writerScreens;
    }

    public BufferedReader getReaderCamera() {
        return readerCamera;
    }

    public void setReaderCamera(BufferedReader readerCamera) {
        this.readerCamera = readerCamera;
    }

    public BufferedWriter getWriterCamera() {
        return writerCamera;
    }

    public void setWriterCamera(BufferedWriter writerCamera) {
        this.writerCamera = writerCamera;
    }
}
