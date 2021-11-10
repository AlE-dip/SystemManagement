package core.system;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

public class test {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        SystemInfo systemInfo = new SystemInfo();
        ObjectMapper mapper = new ObjectMapper();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        Memory memory1 = new Memory(memory);
        String str = mapper.writeValueAsString(memory1);
        System.out.println(str);
        System.out.println(memory1.getAvailable());
        System.out.println("Start...");
        for (int i = 0; i < 8; i++){
            System.out.println("" + i);
            Thread.sleep(1000);
        }
        Memory memory2 = mapper.readerFor(Memory.class).readValue(str);
        String str2 = mapper.writeValueAsString(memory2);
        System.out.println(str2);
        System.out.println(memory2.getAvailable() + "");
    }
}
