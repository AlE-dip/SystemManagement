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

import core.system.OperatingSystem;
import core.system.Process;
import core.system.SystemInfo;
import oshi.PlatformEnum;
import oshi.util.FormatUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Displays a process list, such as ps or task manager. This performs more like
 * Windows Task Manger with current CPU as measured between polling intervals,
 * while PS uses a cumulative CPU value.
 */
public class ProcessPanel extends OshiJPanel { // NOSONAR squid:S110

    private static final long serialVersionUID = 1L;

    private static final String PROCESSES = "Processes";
    private static final String[] COLUMNS = { "PID", "PPID", "Threads", "% CPU", "Cumulative", "VSZ", "RSS", "% Memory",
            "Process Name" };
    private static final double[] COLUMN_WIDTH_PERCENT = { 0.07, 0.07, 0.07, 0.07, 0.09, 0.1, 0.1, 0.08, 0.35 };

    //private transient Map<Integer, OSProcess> priorSnapshotMap = new HashMap<>();

    private transient ButtonGroup cpuOption = new ButtonGroup();
    private transient JRadioButton perProc = new JRadioButton("of one Processor");
    private transient JRadioButton perSystem = new JRadioButton("of System");

    private transient ButtonGroup sortOption = new ButtonGroup();
    private transient JRadioButton cpuButton = new JRadioButton("CPU %");
    private transient JRadioButton cumulativeCpuButton = new JRadioButton("Cumulative CPU");
    private transient JRadioButton memButton = new JRadioButton("Memory %");
    private transient JRadioButton nameButton = new JRadioButton("Name");

    private TableModel model;
    private JTable procTable;
    private JButton btEndTask;
    private JLabel lbPid;


    public ProcessPanel() {
        super();
        init();
    }

    private void init(/*SystemInfo si*/) {
        //OperatingSystem os = si.getOperatingSystem();
        JLabel procLabel = new JLabel(PROCESSES);
        add(procLabel, BorderLayout.NORTH);

        JPanel settings = new JPanel();

        btEndTask = new JButton("End taks");
        btEndTask.setVisible(true);
        btEndTask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!lbPid.getText().equals("0")){
                    System.out.println(lbPid.getText());
                    lbPid.setText("0");
                }
            }
        });
        settings.add(btEndTask);
        lbPid = new JLabel("0");
        settings.add(lbPid);

        JLabel cpuChoice = new JLabel("          CPU %:");
        settings.add(cpuChoice);
        cpuOption.add(perProc);
        settings.add(perProc);
        cpuOption.add(perSystem);
        settings.add(perSystem);

        perSystem.setSelected(true);
//        if (SystemInfo.getCurrentPlatform().equals(PlatformEnum.WINDOWS)) {
//            perSystem.setSelected(true);
//        } else {
//            perProc.setSelected(true);
//        }

        JLabel sortChoice = new JLabel("          Sort by:");
        settings.add(sortChoice);
        sortOption.add(cpuButton);
        settings.add(cpuButton);
        sortOption.add(cumulativeCpuButton);
        settings.add(cumulativeCpuButton);
        sortOption.add(memButton);
        settings.add(memButton);
        sortOption.add(nameButton);
        settings.add(nameButton);
        cpuButton.setSelected(true);

        //model = new DefaultTableModel(null, COLUMNS);//parseProcesses(os.getProcesses(null, null, 0), si), COLUMNS
        procTable = new JTable();
        procTable.setRowSelectionAllowed(true);
        procTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int row = procTable.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) procTable.getModel();
                int pid = (int) model.getValueAt(row, 0);
                lbPid.setText(pid + "");
            }
        });
        JScrollPane scrollV = new JScrollPane(procTable);
        scrollV.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        //resizeColumns(procTable.getColumnModel());

        add(scrollV, BorderLayout.CENTER);
        add(settings, BorderLayout.SOUTH);

//        Timer timer = new Timer(Config.REFRESH_SLOW, e -> {
//            DefaultTableModel tableModel = (DefaultTableModel) procTable.getModel();
//            Object[][] newData = parseProcesses(os.getProcesses(null, null, 0), si);
//            int rowCount = tableModel.getRowCount();
//            for (int row = 0; row < newData.length; row++) {
//                if (row < rowCount) {
//                    // Overwrite row
//                    for (int col = 0; col < newData[row].length; col++) {
//                        tableModel.setValueAt(newData[row][col], row, col);
//                    }
//                } else {
//                    // Add row
//                    tableModel.addRow(newData[row]);
//                }
//            }
//            // Delete any extra rows
//            for (int row = rowCount - 1; row >= newData.length; row--) {
//                tableModel.removeRow(row);
//            }
//        });
//        timer.start();
    }

    private Object[][] parseProcesses(OperatingSystem operatingSystem, SystemInfo systemInfo) {
        long totalMem = systemInfo.getMemory().getTotal();
        int cpuCount = systemInfo.getProcessor().getLogicalProcessorCount();
        // Build a map with a value for each process to control the sort
//        Map<OSProcess, Double> processSortValueMap = new HashMap<>();
//        for (OSProcess p : list) {
//            int pid = p.getProcessID();
//            // Ignore the Idle process on Windows
//            if (pid > 0 || !SystemInfo.getCurrentPlatform().equals(PlatformEnum.WINDOWS)) {
//                // Set up for appropriate sort
//                if (cpuButton.isSelected()) {
//                    processSortValueMap.put(p, p.getProcessCpuLoadBetweenTicks(priorSnapshotMap.get(pid)));
//                } else if (cumulativeCpuButton.isSelected()) {
//                    processSortValueMap.put(p, p.getProcessCpuLoadCumulative());
//                } else {
//                    processSortValueMap.put(p, (double) p.getResidentSetSize());
//                }
//            }
//        }
        if (cpuButton.isSelected()) {
            operatingSystem.sort(OperatingSystem.sortByCpu);
        } else if (cumulativeCpuButton.isSelected()) {
            operatingSystem.sort(OperatingSystem.sortByCumulative);
        } else if(memButton.isSelected()){
            operatingSystem.sort(OperatingSystem.sortByMemory);
        }else {
            operatingSystem.sort(OperatingSystem.sortByName);
        }
        // Now sort the list by the values
//        List<Entry<OSProcess, Double>> procList = new ArrayList<>(processSortValueMap.entrySet());
//        procList.sort(Entry.comparingByValue());
        // Insert into array in reverse order (lowest sort value last)
        ArrayList<Process> processes = operatingSystem.getProcesses();
        Object[][] procArr = new Object[processes.size()][COLUMNS.length];
        // These are in descending CPU order
        for (int i = 0; i < processes.size(); i++) {
            Process p = processes.get(i);
            // Matches order of COLUMNS field
            int pid = p.getPid();
            procArr[i][0] = pid;
            procArr[i][1] = p.getParentProcessId();
            procArr[i][2] = p.getThreadCount();
            if (perProc.isSelected()) {
                procArr[i][3] = String.format("%.1f", p.getProcessCpuLoad() / cpuCount);
                procArr[i][4] = String.format("%.1f", p.getProcessCumulative() / cpuCount);
            } else {
                procArr[i][3] = String.format("%.1f", p.getProcessCpuLoad());
                procArr[i][4] = String.format("%.1f", p.getProcessCumulative());
            }
            procArr[i][5] = FormatUtil.formatBytes(p.getVirtualSize());
            procArr[i][6] = FormatUtil.formatBytes(p.getResidentSetSize());
            procArr[i][7] = String.format("%.1f", 100d * p.getResidentSetSize() / totalMem);
            procArr[i][8] = p.getName();
        }
        // Re-populate snapshot map
//        priorSnapshotMap.clear();
//        for (OSProcess p : list) {
//            priorSnapshotMap.put(p.getProcessID(), p);
//        }
        return procArr;
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

    public void create(SystemInfo systemInfo){
        model = new DefaultTableModel(parseProcesses(systemInfo.getOperatingSystem(), systemInfo), COLUMNS);
        procTable.setModel(model);
        resizeColumns(procTable.getColumnModel());
    }

    public void refresh(SystemInfo systemInfo){
        DefaultTableModel tableModel = (DefaultTableModel) procTable.getModel();
        Object[][] newData = parseProcesses(systemInfo.getOperatingSystem(), systemInfo);
        int rowCount = tableModel.getRowCount();
        for (int row = 0; row < newData.length; row++) {
            if (row < rowCount) {
                // Overwrite row
                for (int col = 0; col < newData[row].length; col++) {
                    tableModel.setValueAt(newData[row][col], row, col);
                }
            } else {
                // Add row
                tableModel.addRow(newData[row]);
            }
        }
        // Delete any extra rows
        for (int row = rowCount - 1; row >= newData.length; row--) {
            tableModel.removeRow(row);
        }
    }

    public void reset(){
        procTable.removeAll();
    }
}
