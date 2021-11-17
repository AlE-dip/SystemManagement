package core.model;

public class Action {
    private String action;
    private Object data;

    public Action() {
        this.action = "";
        this.data = 0;
    }

    public Action(String action, Object data) {
        this.action = action;
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
