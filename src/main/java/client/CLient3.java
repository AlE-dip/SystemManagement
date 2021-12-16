package client;

import org.opencv.core.Core;

public class CLient3 {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Client client = new Client();
    }
}
