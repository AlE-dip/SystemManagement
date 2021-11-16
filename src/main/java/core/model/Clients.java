package core.model;

import java.util.ArrayList;

public class Clients {
    ArrayList<ClientInfo> clientInfos;

    public Clients() {
        this.clientInfos = null;
    }

    public Clients(ArrayList<ClientInfo> clientInfos) {
        this.clientInfos = clientInfos;
    }

    public ArrayList<ClientInfo> getClientInfos() {
        return clientInfos;
    }

    public void setClientInfos(ArrayList<ClientInfo> clientInfos) {
        this.clientInfos = clientInfos;
    }
}
