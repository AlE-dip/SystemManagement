package core.system;

import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.NetworkParams;
import oshi.software.os.OperatingSystem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Network {
    //network
    private String network;
    private ArrayList<NetworkIF> networkIfs;

    public Network() {
        network = null;
        networkIfs = null;
    }

    public Network(NetworkParams networkParams, List<oshi.hardware.NetworkIF> list) {
        StringBuilder stringBuilder = new StringBuilder("Host Name: ")
                .append(networkParams.getHostName());
        String domainName = networkParams.getDomainName();
        if (!domainName.isEmpty()) {
            stringBuilder.append("\nDomain Name: ").append(domainName);
        }
        stringBuilder.append("\nIPv4 Default Gateway: ").append(networkParams.getIpv4DefaultGateway());
        String ipv6DefaultGateway = networkParams.getIpv6DefaultGateway();
        if (!ipv6DefaultGateway.isEmpty()) {
            stringBuilder.append("\nIPv6 Default Gateway: ").append(ipv6DefaultGateway);
        }
        stringBuilder.append("\nDNS Servers: ").append(NetworkIF.getIPAddressesString(networkParams.getDnsServers()));
        network = stringBuilder.toString();
        networkIfs = new ArrayList<>();
        for (oshi.hardware.NetworkIF networkIf: list){
            networkIfs.add(new NetworkIF(networkIf));
        }
        networkIfs.sort(new Comparator<NetworkIF>() {
            @Override
            public int compare(NetworkIF o1, NetworkIF o2) {
                return o1.getIndex() > o2.getIndex() ? 1 :
                        o1.getIndex() < o2.getIndex() ? -1 : 0;
            }
        });
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public ArrayList<NetworkIF> getNetworkIfs() {
        return networkIfs;
    }

    public void setNetworkIfs(ArrayList<NetworkIF> networkIfs) {
        this.networkIfs = networkIfs;
    }
}
