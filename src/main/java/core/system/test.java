package core.system;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.FileSystem;
import oshi.software.os.OSProcess;

import java.util.Base64;
import java.util.List;

public class test {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        SystemInfo systemInfo = new SystemInfo();
        ObjectMapper mapper = new ObjectMapper();
//        GlobalMemory memory = systemInfo.getHardware().getMemory();
//        Memory memory1 = new Memory(memory);
//        CentralProcessor cpu = systemInfo.getHardware().getProcessor();
//        Processor processor = new Processor(cpu);
        //Thread.sleep(1000);
        //memory1.refresh(memory);
//        FileSystem fileSystem = systemInfo.getOperatingSystem().getFileSystem();
//        core.system.FileSystem fileSystem1 = new core.system.FileSystem(fileSystem);

//        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
//        List<OSProcess> list = operatingSystem.getProcesses(null, null, 0);
//        Thread.sleep(5000);
//        List<OSProcess> list2 = operatingSystem.getProcesses(null, null, 0);
//        OSProcess process = list.get(0);
//        OSProcess process2 = list2.get(0);
//        Process process3 = new Process(process);
//        System.out.println(process.getProcessID() + " " + process.getName() + " " + process.getProcessCpuLoadCumulative());
//        System.out.println(process2.getProcessID() + " " + process2.getName() + " " + process2.getProcessCpuLoadCumulative());
//        OperatingSystem operatingSystem = new OperatingSystem(systemInfo.getOperatingSystem());
//        Thread.sleep(4000);
//        operatingSystem.refresh(systemInfo.getOperatingSystem());
 //       Display display = new Display(systemInfo.getHardware().getDisplays());
        core.system.SystemInfo si = new core.system.SystemInfo(systemInfo);
        String str = mapper.writeValueAsString(si);
        System.out.println(str);
        System.out.println("Start...");
        for (int i = 0; i < 2; i++){
            System.out.println("" + i);
            Thread.sleep(1000);
        }
        //Memory memory2 = mapper.readerFor(Memory.class).readValue(str);
     //   Processor processor1 = mapper.readerFor(Processor.class).readValue(str);
        //core.system.FileSystem fileSystem2 = mapper.readerFor(core.system.FileSystem.class).readValue(str);

       // Process process4 = mapper.readerFor(Process.class).readValue(str);
//        String str2 = mapper.writeValueAsString(process4);
//        System.out.println(process.getProcessID() + " " + process.getName() + " " + process.getProcessCpuLoadCumulative());
//        System.out.println(process2.getProcessID() + " " + process2.getName() + " " + process2.getProcessCpuLoadCumulative());
//        OperatingSystem operatingSystem1 = mapper.readerFor(OperatingSystem.class).readValue(str);
//        operatingSystem1.sort(OperatingSystem.sortByCumulative);
//        for(Process process: operatingSystem1.getProcesses()){
//            System.out.println(mapper.writeValueAsString(process));
//        }
       // Display display1 = mapper.readerFor(Display.class).readValue(str);
        core.system.SystemInfo si1 = mapper.readerFor(core.system.SystemInfo.class).readValue(str);
        System.out.println(str.length() + "");
    }
}
