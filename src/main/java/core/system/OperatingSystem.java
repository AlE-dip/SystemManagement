package core.system;

import oshi.software.os.OSProcess;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class OperatingSystem {
    public static final int sortByCpu = 1;
    public static final int sortByCumulative = 2;
    public static final int sortByMemory = 3;
    public static final int processor = 4;
    public static final int system = 5;
    private ArrayList<Process> processes;
    private Map<Integer, OSProcess> oldOSProcess;

    public OperatingSystem() {
        this.processes = null;
    }

    public OperatingSystem(oshi.software.os.OperatingSystem operatingSystem) {
        processes = new ArrayList<>();
        oldOSProcess = new HashMap<>();
        for (OSProcess osProcess: operatingSystem.getProcesses(null, null, 0)){
            processes.add(new Process(osProcess));
            oldOSProcess.put(osProcess.getProcessID(), osProcess);
        }
    }

    public void refresh(oshi.software.os.OperatingSystem operatingSystem){
        processes.clear();
        Map<Integer, OSProcess> osProcessMap = new HashMap<>();
        for (OSProcess osProcess: operatingSystem.getProcesses(null, null, 0)){
            Process process = new Process(osProcess);
            OSProcess proc = oldOSProcess.get(osProcess.getProcessID());
            process.caculateCpuTicks(proc);
            processes.add(process);
            osProcessMap.put(osProcess.getProcessID(), osProcess);
        }
        oldOSProcess.clear();
        oldOSProcess.putAll(osProcessMap);
    }

    public void sort(int sortBy){
        processes.sort(new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) {
                if(sortBy == sortByCpu){
                    return o2.getProcessCpuLoad() > o1.getProcessCpuLoad() ? 1 :
                            o2.getProcessCpuLoad() < o1.getProcessCpuLoad() ? -1 : 0;
                }
                if(sortBy == sortByCumulative){
                    return o2.getProcessCumulative() > o1.getProcessCumulative() ? 1 :
                            o2.getProcessCumulative() < o1.getProcessCumulative() ? -1 : 0;
                }
                return o2.getResidentSetSize() > o1.getResidentSetSize() ? 1 :
                        o2.getResidentSetSize() < o1.getResidentSetSize() ? -1 : 0;
            }
        });
    }

    public ArrayList<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(ArrayList<Process> processes) {
        this.processes = processes;
    }
}
