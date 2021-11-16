package core.model;

public class ClientInfo {
    long id;

    public ClientInfo() {
        this.id = 0;
    }

    public ClientInfo(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
