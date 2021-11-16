package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.Core;
import core.UtilContent;
import core.Session;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Server {

    public Server() throws IOException {
        Forwarder forwarder = new Forwarder();
        ServerSocket serverSocket = new ServerSocket();
        SocketAddress http = new InetSocketAddress(UtilContent.port);
        serverSocket.bind(http);
        System.out.println("Server running...");
        while (true){
            try {
                Socket socket = serverSocket.accept();
                ServerSession session = new ServerSession(socket);

                if(session.getRole().equals(UtilContent.admin)){
                    forwarder.setAdminServer(session);
                    forwarder.startWithFirstClientOrNon();
                }else if (session.getRole().equals(UtilContent.client)){
                    forwarder.getMapWork().put(session.getId(), session);
                    if(forwarder.getAdminServer() != null && forwarder.getClientServer() == null){
                        forwarder.continueWithThisClient(session);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Can not create connection!");
            }
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't not create socketServer.");
        }
    }
}
