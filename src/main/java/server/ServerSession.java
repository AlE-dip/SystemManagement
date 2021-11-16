package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.Core;
import core.Session;
import core.SystemSR;
import core.UtilContent;
import core.model.Action;
import core.model.ClientInfo;
import core.model.Clients;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class ServerSession extends Session {

    ServerSocket serverSocket;
    int port;

    public ServerSession(Socket skConnect) throws IOException {
        super(skConnect);
        //Stay connect
        serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
        //read socket
        String role = readerConnect.readLine();
        if(role.equals(UtilContent.client)){
            this.role = UtilContent.client;
            System.out.println("Client connecting...");
        }else {
            this.role = UtilContent.admin;
            System.out.println("Admin connecting...");
        }
    }

    public void createConnectSystemInfo() throws IOException {
        sendRequest(UtilContent.createConnectSystemInfo, port);
        skSystemInfo = serverSocket.accept();
        createBufferedSystemInfo();
    }

    public void reset(){
        try {
            closeSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(String stringAction){
        try {
            Action action = new Action();
            action.setAction(stringAction);
            stringAction = new ObjectMapper().writeValueAsString(action);
            Core.writeString(writerConnect, stringAction);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(String stringAction, int port){
        try {
            Action action = new Action();
            action.setAction(stringAction);
            action.setPort(port);
            stringAction = new ObjectMapper().writeValueAsString(action);
            Core.writeString(writerConnect, stringAction);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            if(role.equals(UtilContent.admin)) {
                createConnectSystemInfo();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
