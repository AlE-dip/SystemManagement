package core.system;

public class VirtualMemory {
    private String virtualTitle;
    private long swapUsed;
    private long swapTotal;
    private long swapavAilable;

    public VirtualMemory() {
        this.virtualTitle = "";
        this.swapUsed = 0;
        this.swapTotal = 0;
        this.swapavAilable = 0;
    }

    public VirtualMemory(oshi.hardware.VirtualMemory virtualMemory) {
        virtualTitle = virtualMemory.toString();
        swapUsed = virtualMemory.getSwapUsed();
        swapTotal = virtualMemory.getSwapTotal();
        swapavAilable = swapTotal - swapUsed;
    }

    public void refresh(oshi.hardware.VirtualMemory virtualMemory) {
        virtualTitle = virtualMemory.toString();
        swapUsed = virtualMemory.getSwapUsed();
        swapTotal = virtualMemory.getSwapTotal();
        swapavAilable = swapTotal - swapUsed;
    }

    public String getVirtualTitle() {
        return virtualTitle;
    }

    public void setVirtualTitle(String virtualTitle) {
        this.virtualTitle = virtualTitle;
    }

    public long getSwapUsed() {
        return swapUsed;
    }

    public void setSwapUsed(long swapUsed) {
        this.swapUsed = swapUsed;
    }

    public long getSwapTotal() {
        return swapTotal;
    }

    public void setSwapTotal(long swapTotal) {
        this.swapTotal = swapTotal;
    }

    public long getSwapavAilable() {
        return swapavAilable;
    }

    public void setSwapavAilable(long swapavAilable) {
        this.swapavAilable = swapavAilable;
    }
}
