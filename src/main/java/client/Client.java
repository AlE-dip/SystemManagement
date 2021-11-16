package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.Core;
import core.UtilContent;
import core.ConnectionInfo;
import core.Session;

import java.io.*;
import java.net.Socket;

public class Client {
    public Client(){
        try {
            Socket socket = new Socket(UtilContent.address, UtilContent.port);
            ClientSession session = new ClientSession(socket);
            session.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("client stop!");
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
