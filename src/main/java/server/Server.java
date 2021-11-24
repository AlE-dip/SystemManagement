package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.Core;
import core.UtilContent;
import core.Session;
import core.model.Action;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Server {

    public static Forwarder forwarder;

    public Server() throws IOException {
        forwarder = new Forwarder();
        ServerSocket serverSocket = new ServerSocket();
        SocketAddress http = new InetSocketAddress(UtilContent.port);
        serverSocket.bind(http);
        System.out.println("Server running...");
        while (true){
            try {
                Socket socket = serverSocket.accept();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String stringAction = reader.readLine();
                if(stringAction.equals(UtilContent.admin)){
                    //Trường hợp admin connect
                    ServerSession session = new ServerSession(socket);
                    session.setWriterConnect(writer);
                    session.setReaderConnect(reader);
                    session.setRole(UtilContent.admin);
                    session.start();
                    System.out.println("Admin connecting...");
                    forwarder.setAdminServer(session);
                    forwarder.createConnectSystemInfo();
                }else if (stringAction.equals(UtilContent.client)){
                    //Trường hợp client connect
                    String hostName = reader.readLine();
                    ServerSession session = new ServerSession(socket);
                    session.clientHostName = hostName;
                    session.setWriterConnect(writer);
                    session.setReaderConnect(reader);
                    session.setRole(UtilContent.client);
                    session.start();
                    System.out.println("Client connecting...");
                    forwarder.getMapWork().put(session.getId(), session);
                    forwarder.newClient(session.getId() + "", session.clientHostName);
                    if(forwarder.getAdminServer() != null && forwarder.getClientServer() == null){
                        forwarder.createConnectSystemInfoWithThisClient(session);
                    }
                }else {
                    //Trường hợp tạo các socket truyền dữ liệu
                    Action action = new ObjectMapper().readerFor(Action.class).readValue(stringAction);
                    if(forwarder.getAdminServer().getId() == Long.parseLong((String) action.getData())){
                        setSocketAndRun(socket, writer, reader, forwarder.getAdminServer(), action.getAction());
                    }else {
                        for (ServerSession session: forwarder.getMapWork().values()){
                            if(session.getId() == Long.parseLong((String) action.getData())){
                                setSocketAndRun(socket, writer, reader, session, action.getAction());
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Can not create connection!");
            }
        }
    }

    private void setSocketAndRun(Socket socket, BufferedWriter writer, BufferedReader reader, ServerSession serverSession,
                                 String action) throws IOException {
        switch (action){
            case UtilContent.createConnectSystemInfo: {
                serverSession.setSkSystemInfo(socket);
                serverSession.setWriterSystemInfo(writer);
                serverSession.setReaderSystemInfo(reader);
                forwarder.runSystemInfo();
                break;
            }
            case UtilContent.createConnectCamera: {
                serverSession.setSkCamera(socket);
                serverSession.setWriterCamera(writer);
                serverSession.setReaderCamera(reader);
                forwarder.createConnectCamera();
                forwarder.runCamera();
                break;
            }
            case UtilContent.createConnectScreens: {
                serverSession.setSkScreens(socket);
                serverSession.setWriterScreens(writer);
                serverSession.setReaderScreens(reader);
                forwarder.createConnectScreens();
                forwarder.runScreens();
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
