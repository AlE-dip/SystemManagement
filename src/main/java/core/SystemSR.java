package core;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.system.SystemInfo;
import server.ServerSession;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;

public class SystemSR {
    public static void send(BufferedWriter writer, BufferedReader reader) throws IOException {
        oshi.SystemInfo si = new oshi.SystemInfo();
        SystemInfo systemInfo = new SystemInfo(si);
        ObjectMapper mapper = new ObjectMapper();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        systemInfo.refresh();
                        String dataSystem = mapper.writeValueAsString(systemInfo);
                        Core.writeString(writer, dataSystem);

                        String feedback = reader.readLine();
                        if(feedback.equals(UtilContent.stop)){
                            break;
                        }else if (feedback.equals(UtilContent.continues)){
                            Thread.sleep(1000);
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        String wait = reader.readLine();
        if(wait.equals(UtilContent.systemForwardReady)){
            thread.start();
        }
    }

    public static void forward(ServerSession adminServer, ServerSession clientServer, Map<Long, ServerSession> mapwork) throws IOException {
        BufferedWriter writerAdmin = adminServer.getWriterSystemInfo();
        BufferedReader readerAdmin = adminServer.getReaderSystemInfo();
        BufferedWriter writerClient = clientServer.getWriterSystemInfo();
        BufferedReader readerClient = clientServer.getReaderSystemInfo();
        ObjectMapper mapper = new ObjectMapper();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        String dataSystem = readerClient.readLine();
                        //SystemInfo systemInfo = mapper.readerFor(SystemInfo.class).readValue(dataSystem);
                        System.out.println(dataSystem);
                        Core.writeString(writerAdmin, dataSystem);
                        String feedback = readerAdmin.readLine();
                        if(feedback.equals(UtilContent.continues)){
                            Core.writeString(writerClient, UtilContent.continues);
                        }else {
                            Core.writeString(writerClient, UtilContent.stop);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        mapwork.remove(clientServer.getId());
                        clientServer.interrupt();
                        break;
                    }
                }
            }
        });
        ///////
        String wait = readerAdmin.readLine();
        if(wait.equals(UtilContent.systemReceiveReady)){
            thread.start();
            Core.writeString(writerClient, UtilContent.systemForwardReady);
        }

    }

    public static void receive(BufferedWriter writer, BufferedReader reader) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        String dataSystem = reader.readLine();
                        SystemInfo systemInfo = mapper.readerFor(SystemInfo.class).readValue(dataSystem);

                        System.out.println(dataSystem);
                        System.out.println(systemInfo.getBaseboard().getManufacturer());

                        Core.writeString(writer, UtilContent.continues);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        Core.writeString(writer, UtilContent.systemReceiveReady);
    }
}
