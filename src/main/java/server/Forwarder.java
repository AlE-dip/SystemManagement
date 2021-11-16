package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.Core;
import core.Session;
import core.SystemSR;
import core.UtilContent;
import core.model.ClientInfo;
import core.model.Clients;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Forwarder {
    private String systemReceiveReady = "";
    private Map<Long, ServerSession> mapWork;
    private ServerSession clientServer;
    private ServerSession adminServer;

    public Forwarder() {
        adminServer = null;
        clientServer = null;
        mapWork = new HashMap<>();
    }

    public void startWithFirstClientOrNon() throws IOException {
        //Start admin
        adminServer.createConnectSystemInfo();
        systemReceiveReady = adminServer.getReaderSystemInfo().readLine();
        clientServer = firstOrNonClient();
        if(clientServer != null){
            clientServer.createConnectSystemInfo();
            forwardSystemInfo();
        }
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

    public void continueWithThisClient(ServerSession clientServer) throws IOException {
        this.clientServer = clientServer;
        clientServer.createConnectSystemInfo();
        forwardSystemInfo();
    }

    public void forwardSystemInfo() throws IOException {
        BufferedWriter writerAdmin = adminServer.getWriterSystemInfo();
        BufferedReader readerAdmin = adminServer.getReaderSystemInfo();
        BufferedWriter writerClient = clientServer.getWriterSystemInfo();
        BufferedReader readerClient = clientServer.getReaderSystemInfo();
        ObjectMapper mapper = new ObjectMapper();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String dataSystem = "";
                    try {
                        dataSystem = readerClient.readLine();
                    } catch (IOException e){
                        e.printStackTrace();
                        disconnectWithClientAndStartWithClientDefault();
                        break;
                    }
                    System.out.println(dataSystem);
                    String feedback = "";
                    try {
                        Core.writeString(writerAdmin, dataSystem);
                        feedback = readerAdmin.readLine();
                    } catch (IOException e){
                        e.printStackTrace();
                        disconnectWithAdmin();
                        break;
                    }
                    try {
                        if(feedback.equals(UtilContent.continues)){
                            Core.writeString(writerClient, UtilContent.continues);
                        }else {
                            Core.writeString(writerClient, UtilContent.stop);
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                        disconnectWithClientAndStartWithClientDefault();
                        break;
                    }
                }
            }
        });
        if(systemReceiveReady.equals(UtilContent.systemReceiveReady)){
            thread.start();
            Core.writeString(writerClient, UtilContent.systemForwardReady);
        }
    }

    public void disconnectWithAdmin(){
        adminServer.interrupt();
        adminServer = null;
        try {
            Core.writeString(clientServer.getWriterSystemInfo(), UtilContent.stopSystemInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientServer.sendRequest(UtilContent.reset);
        clientServer.reset();
        System.out.println("Disconnect admin!!!");
        System.out.println("Reset client.");
    }

    public void disconnectWithClientAndStartWithClientDefault(){
        mapWork.remove(clientServer.getId());
        clientServer.interrupt();
        clientServer = firstOrNonClient();
        System.out.println("Disconnect client!!!");
        if(clientServer != null){
            try {
                clientServer.createConnectSystemInfo();
                forwardSystemInfo();
                System.out.println("Start default client.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
