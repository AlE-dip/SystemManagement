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
                } else if (stringAction.equals(UtilContent.stopClipboard)) {
                    Server.forwarder.resetClipboard();
                } else if (stringAction.equals(UtilContent.onCloseClipboard)) {
                    Server.forwarder.onCloseClipboard();
                } else if (stringAction.equals(UtilContent.stopKeyboard)) {
                    Server.forwarder.resetKeyboard();
                } else if (stringAction.equals(UtilContent.writeLogScreens)) {
                    Server.forwarder.writeLogScreens();
                } else if (stringAction.equals(UtilContent.stopLogScreens)) {
                    Server.forwarder.stopLogScreens();
                } else if (stringAction.equals(UtilContent.showListLogScreens)) {
                    Server.forwarder.getListLogScreens();
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
                        case UtilContent.getLogImage: {
                            Server.forwarder.getLogImage((String) action.getData());
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
