package client;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessManager {

    private static final String TASKLIST = "tasklist";
    private static final String KILL = "taskkill /F /PID ";

    public static BufferedReader listProcess() throws Exception {

        Process process = Runtime.getRuntime().exec(TASKLIST);
        BufferedReader listProcess = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = listProcess.readLine()) != null) {
            System.out.println(line);
        }
        return listProcess;
    }

    public static void killProcess(int PID) throws Exception {
        Runtime.getRuntime().exec(KILL + PID);
    }

    public static void shutDown() throws Exception {
        Runtime.getRuntime().exec("shutdown -s -t 0");
    }

    public static void main(String[] args) throws Exception{
        // Đọc danh sách tiến trình
        listProcess();
       /*String line;
        while ((line = listProcess().readLine()) != null) {
            System.out.println(line);
        }*/

        //truyền vào tiến trình muốn kill
        //killProcess(PID);
        //shutDown();
    }
}
