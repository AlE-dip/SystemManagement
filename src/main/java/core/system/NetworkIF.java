package core.system;

import oshi.util.Constants;

import java.util.List;

public class NetworkIF {
    private String name;
    private int index;
    private long speed;
    private String ipV4Address;
    private String ipV6Addresses;
    private String macAddress;

    public NetworkIF() {
        this.name = "";
        this.index = 0;
        this.speed = 0;
        this.ipV4Address = "";
        this.ipV6Addresses = "";
        this.macAddress = "";
    }

    public NetworkIF(oshi.hardware.NetworkIF networkIF) {
        this.name = networkIF.getName();
        this.index = networkIF.getIndex();
        this.speed = networkIF.getSpeed();
        this.ipV4Address = getIPAddressesString(networkIF.getIPv4addr());
        this.ipV6Addresses = getIPAddressesString(networkIF.getIPv6addr());
        this.macAddress = Constants.UNKNOWN.equals(networkIF.getMacaddr()) ? "" : networkIF.getMacaddr();
    }

    public static String getIPAddressesString(String[] ipAddressArr) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (String ipAddress : ipAddressArr) {
            if (first) {
                first = false;
            } else {
                sb.append("; ");
            }
            sb.append(ipAddress);
        }

        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public String getIpV4Address() {
        return ipV4Address;
    }

    public void setIpV4Address(String ipV4Address) {
        this.ipV4Address = ipV4Address;
    }

    public String getIpV6Addresses() {
        return ipV6Addresses;
    }

    public void setIpV6Addresses(String ipV6Addresses) {
        this.ipV6Addresses = ipV6Addresses;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
