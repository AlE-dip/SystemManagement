package core.system;

import oshi.hardware.CentralProcessor;

public class Processor {
    private long[] oldTicks;
    private double ticks;
    private long[][] oldProcTicks;
    private double[] procTicks;

    public Processor() {
        this.oldTicks = null;
        ticks = 0;
        this.oldProcTicks = null;
        procTicks = null;
    }

    public Processor(CentralProcessor cpu){
        oldTicks = cpu.getSystemCpuLoadTicks();
        ticks = 0;
        oldProcTicks = cpu.getProcessorCpuLoadTicks();
        procTicks = null;
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
}
