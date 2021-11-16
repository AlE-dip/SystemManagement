package core.model;

public class Action {
    private String action;
    private int port;

    public Action() {
        this.action = "";
        this.port = 0;
    }

    public Action(String action, int port) {
        this.action = action;
        this.port = port;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
