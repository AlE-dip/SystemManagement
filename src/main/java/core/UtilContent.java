package core;

public class UtilContent {
    public static final String admin = "admin";//role admin
    public static final String client = "client";//role admin
    public static final String systemForwardReady = "systemForwardReady";
    public static final String systemReceiveReady = "systemReceiveReady";
    public static final String stop = "stop";
    public static final String disconnect = "disconnect";
    public static final String stopSystemInfo = "stopSystemInfo";
    public static final String continues = "continues";
    public static final String createConnectSystemInfo = "createConnectSystemInfo";
    public static final String createConnectCamera = "createConnectCamera";
    public static final String createConnectScreens = "createConnectScreens";
    public static final String createConnectClipboard = "createConnectClipboard";
    public static final String createConnectKeyboard = "createConnectKeyboard";
    public static final String reset = "reset";
    public static final int timeCamera = 10;
    public static final int timeSystemInfo = 1000;
    public static final String address = "localhost";//address server
    public static final int port = 50000;//port server
    public static final String newClient = "newClient";//trường hợp 1 client kết nối
    public static final String destroyClient = "destroyClient";//trường hợp 1 client kết nối
    public static final String changeCurrent = "changeCurrent";//trường hợp thay đổi client từ admin
    public static final String newClientConnect = "newClientConnect";//trường hợp client và admin gửi dữ liệu
    public static final String stopCamera = "stopCamera";
    public static final String stopScreens = "stopScreens";
    public static final String listId = "listId";
    public static final String current = "current";
    public static final String killProcess = "killProcess";
    public static final String shutdown = "shutdown";
    public static final String stopClipboard = "stopClipboard";
    public static final String stopKeyboard = "stopKeyboard";
    public static final String sendTypeString = "sendTypeString";//Clipboard send string
    public static final String sendTypeImage = "sendTypeImage";//Clipboard send image
    public static final String sendTypeEventKey = "sendTypeEventKey";//Keyboard send key press
    public static final String onCloseClipboard = "onCloseClipboard";//error clipboard client
}
