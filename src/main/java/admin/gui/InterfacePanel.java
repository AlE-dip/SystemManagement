/*
 * MIT License
 *
 * Copyright (c) 2010 - 2021 The OSHI Project Contributors: https://github.com/oshi/oshi/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package admin.gui;

import core.system.Network;
import core.system.NetworkIF;
import core.system.OperatingSystem;
import core.system.SystemInfo;
import oshi.util.Constants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Displays a interface list, such as ifconfig.
 */
public class InterfacePanel extends OshiJPanel { // NOSONAR squid:S110

    private static final long serialVersionUID = 1L;

    private static final int INIT_HASH_SIZE = 100;
    private static final String IP_ADDRESS_SEPARATOR = "; ";

    private static final String PARAMS = "Network Parameters";
    private static final String INTERFACES = "Network Interfaces";
    private static final String[] COLUMNS = { "Name", "Index", "Speed", "IPv4 Address", "IPv6 address", "MAC address" };
    private static final double[] COLUMN_WIDTH_PERCENT = { 0.02, 0.02, 0.1, 0.25, 0.45, 0.15 };

    private JTextArea paramsArea;
    private TableModel model;
    private JTable intfTable;

    public InterfacePanel() {
        super();
        init();
    }

    private void init(/*SystemInfo si*/) {
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel paramsLabel = new JLabel(PARAMS);
        paramsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(paramsLabel);

        paramsArea = new JTextArea(0, 0);
        paramsArea.setText("");//buildParamsText(si.getOperatingSystem())
        add(paramsArea);

        JLabel interfaceLabel = new JLabel(INTERFACES);
        interfaceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(interfaceLabel);

//        List<NetworkIF> networkIfList = si.getHardware().getNetworkIFs(true);
//
//        TableModel model = new DefaultTableModel(parseInterfaces(networkIfList), COLUMNS);
        intfTable = new JTable();
        JScrollPane scrollV = new JScrollPane(intfTable);
        scrollV.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        resizeColumns(intfTable.getColumnModel());
        add(scrollV);
    }

//    private static String buildParamsText(Network network) {
//        NetworkParams params = os.getNetworkParams();
//        StringBuilder sb = new StringBuilder("Host Name: ").append(params.getHostName());
//        if (!params.getDomainName().isEmpty()) {
//            sb.append("\nDomain Name: ").append(params.getDomainName());
//        }
//        sb.append("\nIPv4 Default Gateway: ").append(params.getIpv4DefaultGateway());
//        if (!params.getIpv6DefaultGateway().isEmpty()) {
//            sb.append("\nIPv6 Default Gateway: ").append(params.getIpv6DefaultGateway());
//        }
//        sb.append("\nDNS Servers: ").append(getIPAddressesString(params.getDnsServers()));
//        return sb.toString();
//    }

    private static Object[][] parseInterfaces(Network network) {
        ArrayList<NetworkIF> networkIfList = network.getNetworkIfs();
//        Map<NetworkIF, Integer> intfSortValueMap = new HashMap<>(INIT_HASH_SIZE);
//        for (NetworkIF intf : list) {
//            intfSortValueMap.put(intf, intf.getIndex());
//        }
//        List<Entry<NetworkIF, Integer>> intfList = new ArrayList<>(intfSortValueMap.entrySet());
//        intfList.sort(Entry.comparingByValue());

//        int i = 0;

        Object[][] intfArr = new Object[networkIfList.size()][COLUMNS.length];

        for (int i = 0; i < networkIfList.size(); i++) {
            NetworkIF networkIF = networkIfList.get(i);
            intfArr[i][0] = networkIF.getName();
            intfArr[i][1] = networkIF.getIndex();
            intfArr[i][2] = networkIF.getSpeed();
            intfArr[i][3] = networkIF.getIpV4Address();
            intfArr[i][4] = networkIF.getIpV6Addresses();
            intfArr[i][5] = networkIF.getMacAddress();
        }
        return intfArr;
    }

    private static void resizeColumns(TableColumnModel tableColumnModel) {
        TableColumn column;
        int tW = tableColumnModel.getTotalColumnWidth();
        int cantCols = tableColumnModel.getColumnCount();
        for (int i = 0; i < cantCols; i++) {
            column = tableColumnModel.getColumn(i);
            int pWidth = (int) Math.round(COLUMN_WIDTH_PERCENT[i] * tW);
            column.setPreferredWidth(pWidth);
        }
    }

    private static String getIPAddressesString(String[] ipAddressArr) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (String ipAddress : ipAddressArr) {
            if (first) {
                first = false;
            } else {
                sb.append(IP_ADDRESS_SEPARATOR);
            }
            sb.append(ipAddress);
        }

        return sb.toString();
    }

    public void create(SystemInfo systemInfo){
        paramsArea.setText(systemInfo.getNetwork().getNetwork());

        TableModel model = new DefaultTableModel(parseInterfaces(systemInfo.getNetwork()), COLUMNS);
        intfTable.setModel(model);
    }

    public void reset(){
        intfTable.removeAll();
    }
}
