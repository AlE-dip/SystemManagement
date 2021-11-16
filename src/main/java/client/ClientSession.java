package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.Core;
import core.Session;
import core.SystemSR;
import core.UtilContent;
import core.model.Action;
import core.system.SystemInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientSession extends Session {
    private Thread threadSystemInfo;

    public ClientSession(Socket skConnect) throws IOException {
        super(skConnect);
        Core.writeString(writerConnect, UtilContent.client);
    }

    public void createConnectSystemInfo(int port) throws IOException {
        skSystemInfo = new Socket(UtilContent.address, port);
        createBufferedSystemInfo();
        sendSystemInfo(writerSystemInfo, readerSystemInfo);
    }

    public void sendSystemInfo(BufferedWriter writer, BufferedReader reader) throws IOException {
        oshi.SystemInfo si = new oshi.SystemInfo();
        SystemInfo systemInfo = new SystemInfo(si);
        ObjectMapper mapper = new ObjectMapper();
        threadSystemInfo = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        systemInfo.refresh();
                        String dataSystem = mapper.writeValueAsString(systemInfo);
                        Core.writeString(writer, dataSystem);

                        String feedback = reader.readLine();
                        if(feedback.equals(UtilContent.stopSystemInfo)){
                            System.out.println("Wait...");
                            break;
                        }else if (feedback.equals(UtilContent.continues)){
                            Thread.sleep(1000);
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });

        String wait = reader.readLine();
        if(wait.equals(UtilContent.systemForwardReady)){
            threadSystemInfo.start();
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            while (true){
                String stringAction = readerConnect.readLine();
                Action action = new ObjectMapper().readerFor(Action.class).readValue(stringAction);
                switch (action.getAction()){
                    case UtilContent.reset: {
                        threadSystemInfo.interrupt();
                        closeSocket();
                        break;
                    }
                    case UtilContent.createConnectSystemInfo: {
                        createConnectSystemInfo(action.getPort());
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
