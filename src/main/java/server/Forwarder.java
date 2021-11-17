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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Forwarder {
    private String systemInfoReceiveReady = "";
    private Map<Long, ServerSession> mapWork;
    private ServerSession clientServer;
    private ServerSession adminServer;
    public Thread threadSystemInfo;

    public Forwarder() {
        adminServer = null;
        clientServer = null;
        mapWork = new HashMap<>();
    }

    public void createConnectSystemInfo() throws IOException {
        //Start admin
        adminServer.createConnectSystemInfo();
        clientServer = firstOrNonClient();
        if(clientServer != null){
            clientServer.createConnectSystemInfo();
        }
    }

    public void runSystemInfo() throws IOException {
        if(adminServer == null || clientServer == null || adminServer.getSkSystemInfo() == null || clientServer.getSkSystemInfo() == null){
            return;
        }
        if(systemInfoReceiveReady == ""){
            systemInfoReceiveReady = adminServer.getReaderSystemInfo().readLine();
        }
        Action action = new Action(UtilContent.newClient, clientServer.getId());
        String stringAction = new ObjectMapper().writeValueAsString(action);
        Core.writeString(adminServer.getWriterConnect(), stringAction);
        forwardSystemInfo();
    }

    public ServerSession firstOrNonClient(){
        if(!mapWork.isEmpty()) {
            Map.Entry<Long, ServerSession> entry = mapWork.entrySet().iterator().next();
            clientServer = entry.getValue();
            return clientServer;
        }else {
            return null;
        }
    }

    public void createConnectSystemInfoWithThisClient(ServerSession clientServer) throws IOException {
        this.clientServer = clientServer;
        clientServer.createConnectSystemInfo();
    }

    public void forwardSystemInfo() throws IOException {
        BufferedWriter writerAdmin = adminServer.getWriterSystemInfo();
        BufferedReader readerAdmin = adminServer.getReaderSystemInfo();
        BufferedWriter writerClient = clientServer.getWriterSystemInfo();
        BufferedReader readerClient = clientServer.getReaderSystemInfo();
        ObjectMapper mapper = new ObjectMapper();
        threadSystemInfo = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String dataSystem = "";
                    try {
                        dataSystem = readerClient.readLine();
                    } catch (IOException e){
                        e.printStackTrace();
                        System.out.println("SystemInfo stop!");
                        break;
                    }
                    System.out.println(dataSystem);
                    String feedback = "";
                    try {
                        Core.writeString(writerAdmin, dataSystem);
                        feedback = readerAdmin.readLine();
                    } catch (IOException e){
                        e.printStackTrace();
                        System.out.println("SystemInfo stop!");
                        break;
                    }
                    try {
                        if(feedback.equals(UtilContent.continues)){
                            Core.writeString(writerClient, UtilContent.continues);
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                        System.out.println("SystemInfo stop!");
                        break;
                    }
                }
            }
        });
        if(systemInfoReceiveReady.equals(UtilContent.systemReceiveReady)){
            threadSystemInfo.start();
            Core.writeString(writerClient, UtilContent.systemForwardReady);
        }
    }

    public void disconnectWithAdmin(){
        adminServer.interrupt();
        adminServer = null;
        systemInfoReceiveReady = "";
        if(clientServer != null){
            try {
                Core.writeString(clientServer.getWriterSystemInfo(), UtilContent.stopSystemInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            clientServer.sendRequest(UtilContent.reset);
            clientServer.reset();
            clientServer = null;
            System.out.println("Reset client.");
        }
        System.out.println("Disconnect admin!!!");
    }

    public void disconnectWithClient(long id){
        mapWork.remove(id);
        clientServer.interrupt();
        clientServer = null;
        System.out.println("Disconnect client!!!");
        System.out.println("Wait while clients connect...");
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
