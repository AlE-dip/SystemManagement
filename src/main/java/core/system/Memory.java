package core.system;

import oshi.hardware.GlobalMemory;
import oshi.hardware.PhysicalMemory;

import java.util.List;

public class Memory {

    private String physicalTitle;
    private long total;
    private long available;
    private long use;
    private VirtualMemory virtualMemory;
    private String physicalMemory;

    public Memory() {
        this.physicalTitle = "";
        this.total = 0;
        this.available = 0;
        this.use = 0;
        this.virtualMemory = null;
        this.physicalMemory = "";
    }

    public Memory(GlobalMemory memory) {
        physicalTitle = memory.toString();
        total = memory.getTotal();
        available = memory.getAvailable();
        use = total - available;
        virtualMemory = new VirtualMemory(memory.getVirtualMemory());
        StringBuilder sb = new StringBuilder();
        List<PhysicalMemory> pmList = memory.getPhysicalMemory();
        for (PhysicalMemory pm : pmList) {
            sb.append('\n').append(pm.toString());
        }
        physicalMemory = sb.toString();
    }

    public void refresh(GlobalMemory memory) {
        physicalTitle = memory.toString();
        total = memory.getTotal();
        available = memory.getAvailable();
        use = total - available;
        virtualMemory.refresh(memory.getVirtualMemory());
    }

    public String getPhysicalTitle() {
        return physicalTitle;
    }

    public void setPhysicalTitle(String physicalTitle) {
        this.physicalTitle = physicalTitle;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    public long getUse() {
        return use;
    }

    public void setUse(long use) {
        this.use = use;
    }

    public VirtualMemory getVirtualMemory() {
        return virtualMemory;
    }

    public void setVirtualMemory(VirtualMemory virtualMemory) {
        this.virtualMemory = virtualMemory;
    }

    public String getPhysicalMemory() {
        return physicalMemory;
    }

    public void setPhysicalMemory(String physicalMemory) {
        this.physicalMemory = physicalMemory;
    }
}
