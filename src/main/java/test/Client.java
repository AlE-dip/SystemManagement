package test;


import org.jfree.data.json.impl.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 50000);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true){
                String strNum = new Scanner(System.in).next();
                writer.write(strNum);
                writer.newLine();
                writer.flush();

                String number = reader.readLine();
                System.out.println(number);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
