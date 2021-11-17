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

    int port;

    public ServerSession(Socket skConnect) throws IOException {
        super(skConnect);
        //send id
        Core.writeString(writerConnect, getId() + "");
    }

    public void createConnectSystemInfo() throws IOException {
        sendRequest(UtilContent.createConnectSystemInfo);
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
            Core.writeString(writerConnect, stringAction);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        while (true){
            try {
                String stringAction = readerConnect.readLine();
                Action action = new ObjectMapper().readerFor(Action.class).readValue(stringAction);
                switch (action.getAction()){
                    case UtilContent.disconnect: {
                        System.out.println("Disconnect " + role + "!");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Disconnect " + role + "... error!");
                Server.forwarder.threadSystemInfo.interrupt();
                if (role.equals(UtilContent.admin)){
                    Server.forwarder.disconnectWithAdmin();
                }else {
                    Server.forwarder.disconnectWithClient(getId());
                }

                break;
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }
}
