package core.system;

import oshi.PlatformEnum;
import oshi.SystemInfo;
import oshi.software.os.OSFileStore;

public class FileStore {
    private long available;
    private long total;
    private long use;
    private String name;
    private String label;

    public FileStore() {
        name = "";
        label = "";
        available = 0;
        total = 0;
        use = 0;
    }

    public FileStore(OSFileStore fileStore) {
        name = fileStore.getName();
        if (SystemInfo.getCurrentPlatform().equals(PlatformEnum.WINDOWS)){
            label = fileStore.getLabel();
        }
        available = fileStore.getUsableSpace();
        total = fileStore.getTotalSpace();
        use = total - available;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getUse() {
        return use;
    }

    public void setUse(long use) {
        this.use = use;
    }
}
