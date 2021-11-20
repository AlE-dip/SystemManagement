package core.model;

import java.util.ArrayList;

public class Clients {
    private ArrayList<Long> ids;
    private long current;

    public Clients() {
        ids = null;
        current = 0;
    }

    public Clients(ArrayList<Long> ids, long current) {
        this.ids = ids;
        this.current = current;
    }

    public ArrayList<Long> getIds() {
        return ids;
    }

    public void setIds(ArrayList<Long> ids) {
        this.ids = ids;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }
}
