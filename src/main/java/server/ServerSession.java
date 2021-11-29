package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.Core;
import core.Session;
import core.UtilContent;
import core.model.Action;

import java.io.*;
import java.net.Socket;

public class ServerSession extends Session {

    String clientHostName;

    public ServerSession(Socket skConnect) throws IOException {
        super(skConnect);
        //send id
        Core.writeString(writerConnect, getId() + "");
    }

    public void sendRequest(String stringAction) {
        try {
            Core.writeString(writerConnect, stringAction);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            try {
                String stringAction = readerConnect.readLine(); ///{chat:{nguoiGui,nguoiNhan,tinNhan}}
                if (stringAction.equals(UtilContent.stopCamera)) {
                    Server.forwarder.resetCamera();
                } else if (stringAction.equals(UtilContent.stopScreens)) {
                    Server.forwarder.resetScreens();
                } else if(stringAction.equals(UtilContent.disconnect)){
                    Server.forwarder.disconnectClient();
                } else if (stringAction.equals(UtilContent.shutdown)) {
                    Server.forwarder.shutDownClient();
                } else {
                    Action action = new ObjectMapper().readerFor(Action.class).readValue(stringAction);
                    switch (action.getAction()) {
                        case UtilContent.disconnect: {
                            System.out.println("Disconnect " + role + "!");
                            break;
                        }
                        case UtilContent.changeCurrent: {
                            Server.forwarder.changeCurrentClient((String) action.getData());
                            break;
                        }
                        case UtilContent.killProcess: {
                            Server.forwarder.killProcessClient(stringAction);
                            break;
                        }
                    }
                }
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                System.out.println("Disconnect " + role + "... error!");
                if (role.equals(UtilContent.admin)) {
                    Server.forwarder.disconnectWithAdmin();
                } else {
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
