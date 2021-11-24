package core.system;

import oshi.hardware.CentralProcessor;

public class Processor {
    private long[] oldTicks;
    private double ticks;
    private long[][] oldProcTicks;
    private double[] procTicks;
    // os and hw panel
    private String infoProcessor;
    private int logicalProcessorCount;

    public Processor() {
        this.oldTicks = null;
        ticks = 0;
        this.oldProcTicks = null;
        procTicks = null;
        infoProcessor = "";
        logicalProcessorCount = 0;
    }

    public Processor(CentralProcessor cpu){
        oldTicks = cpu.getSystemCpuLoadTicks();
        ticks = 0;
        oldProcTicks = cpu.getProcessorCpuLoadTicks();
        procTicks = null;
        infoProcessor = cpu.toString();
        logicalProcessorCount = cpu.getLogicalProcessorCount();
    }

    public void refresh(CentralProcessor cpu){
        ticks = cpu.getSystemCpuLoadBetweenTicks(oldTicks);
        oldTicks = cpu.getSystemCpuLoadTicks();
        procTicks = cpu.getProcessorCpuLoadBetweenTicks(oldProcTicks);
        oldProcTicks = cpu.getProcessorCpuLoadTicks();
    }

    public double getTicks() {
        return ticks;
    }

    public void setTicks(double ticks) {
        this.ticks = ticks;
    }

    public double[] getProcTicks() {
        return procTicks;
    }

    public void setProcTicks(double[] procTicks) {
        this.procTicks = procTicks;
    }

    public long[] getOldTicks() {
        return oldTicks;
    }

    public void setOldTicks(long[] oldTicks) {
        this.oldTicks = oldTicks;
    }

    public long[][] getOldProcTicks() {
        return oldProcTicks;
    }

    public void setOldProcTicks(long[][] oldProcTicks) {
        this.oldProcTicks = oldProcTicks;
    }

    public String getInfoProcessor() {
        return infoProcessor;
    }

    public void setInfoProcessor(String infoProcessor) {
        this.infoProcessor = infoProcessor;
    }

    public int getLogicalProcessorCount() {
        return logicalProcessorCount;
    }

    public void setLogicalProcessorCount(int logicalProcessorCount) {
        this.logicalProcessorCount = logicalProcessorCount;
    }
}
