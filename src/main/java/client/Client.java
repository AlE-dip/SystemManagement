package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.UtilContent;
import core.Session;
import org.opencv.core.Core;

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
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Client client = new Client();
    }
}
