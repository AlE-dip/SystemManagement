package core.system;

import oshi.software.os.OSProcess;

import java.util.Comparator;

public class Process {
    private int pid;
    private int parentProcessId;
    private int threadCount;
    private double processCpuLoad;
    private double processCumulative;
    private long virtualSize;
    private long residentSetSize;
    private String name;
    private OSProcess process;

    public Process() {
        this.pid = 0;
        this.parentProcessId = 0;
        this.threadCount = 0;
        this.processCpuLoad = 0;
        this.processCumulative = 0;
        this.virtualSize = 0;
        this.residentSetSize = 0;
        this.name = "";
    }

    public Process(OSProcess process) {
        this.pid = process.getProcessID();
        this.parentProcessId = process.getParentProcessID();
        this.threadCount = process.getThreadCount();
        this.processCumulative = 100d * process.getProcessCpuLoadCumulative();
        this.virtualSize = process.getVirtualSize();
        this.residentSetSize = process.getResidentSetSize();
        this.name = process.getName();
        this.process = process;
    }

    public void caculateCpuTicks(OSProcess oldProcess) {
        this.processCpuLoad = 100d * process.getProcessCpuLoadBetweenTicks(oldProcess);
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getParentProcessId() {
        return parentProcessId;
    }

    public void setParentProcessId(int parentProcessId) {
        this.parentProcessId = parentProcessId;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public double getProcessCpuLoad() {
        return processCpuLoad;
    }

    public void setProcessCpuLoad(double processCpuLoad) {
        this.processCpuLoad = processCpuLoad;
    }

    public double getProcessCumulative() {
        return processCumulative;
    }

    public void setProcessCumulative(double processCumulative) {
        this.processCumulative = processCumulative;
    }

    public long getVirtualSize() {
        return virtualSize;
    }

    public void setVirtualSize(long virtualSize) {
        this.virtualSize = virtualSize;
    }

    public long getResidentSetSize() {
        return residentSetSize;
    }

    public void setResidentSetSize(long residentSetSize) {
        this.residentSetSize = residentSetSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
