package admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.Core;
import core.Session;
import core.SystemSR;
import core.UtilContent;
import core.model.Action;
import core.model.ClientInfo;
import core.model.Clients;
import core.system.SystemInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class AdminSession extends Session {
    private Thread threadSystemInfo;

    public AdminSession(Socket skConnect) throws IOException {
        super(skConnect);
        Core.writeString(writerConnect, UtilContent.admin);
    }

    public void createConnectSystemInfo(int port) throws IOException {
        skSystemInfo = new Socket(UtilContent.address, port);
        createBufferedSystemInfo();
        receiveSystemInfo(writerSystemInfo, readerSystemInfo);
        Core.writeString(writerSystemInfo, UtilContent.systemReceiveReady);
    }

    public void receiveSystemInfo(BufferedWriter writer, BufferedReader reader) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        threadSystemInfo = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true){
                    try {
                        String dataSystem = reader.readLine();
                        SystemInfo systemInfo = mapper.readerFor(SystemInfo.class).readValue(dataSystem);

                        System.out.print(i + ": ");
                        i++;
                        System.out.println(dataSystem);

                        Core.writeString(writer, UtilContent.continues);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Disconnect to server!");
                        break;
                    }
                }
            }
        });
        threadSystemInfo.start();
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
