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

import core.system.Memory;
import core.system.VirtualMemory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

/**
 * Displays physical and virtual (swap) memory stats.
 */
public class MemoryPanel extends OshiJPanel { // NOSONAR squid:S110

    private static final long serialVersionUID = 1L;

    private static final String PHYSICAL_MEMORY = "Physical Memory";
    private static final String VIRTUAL_MEMORY = "Virtual Memory (Swap)";

    private static final String USED = "Used";
    private static final String AVAILABLE = "Available";

    private DefaultPieDataset<String> physMemData;
    private DefaultPieDataset<String> virtMemData;
    private JFreeChart physMem;
    private JFreeChart virtMem;
    private JTextArea textArea;

    public MemoryPanel() {
        super();
        init();//si.getHardware().getMemory()
    }

    private void init(/*GlobalMemory memory*/) {
        physMemData = new DefaultPieDataset<>();
        virtMemData = new DefaultPieDataset<>();
        //updateDatasets(memory, physMemData, virtMemData);

        physMem = ChartFactory.createPieChart(PHYSICAL_MEMORY, physMemData, true, true, false);
        virtMem = ChartFactory.createPieChart(VIRTUAL_MEMORY, virtMemData, true, true, false);
        configurePlot(physMem);
        configurePlot(virtMem);
        physMem.setSubtitles(Collections.singletonList(new TextTitle("")));//updatePhysTitle(memory)
        virtMem.setSubtitles(Collections.singletonList(new TextTitle("")));//updateVirtTitle(memory)

        GridBagConstraints pmConstraints = new GridBagConstraints();
        pmConstraints.weightx = 1d;
        pmConstraints.weighty = 1d;
        pmConstraints.fill = GridBagConstraints.BOTH;
        GridBagConstraints vmConstraints = (GridBagConstraints) pmConstraints.clone();
        vmConstraints.gridx = 1;
        GridBagConstraints textConstraints = new GridBagConstraints();
        textConstraints.gridy = 1;
        textConstraints.gridwidth = 2;
        textConstraints.fill = GridBagConstraints.BOTH;

        JPanel memoryPanel = new JPanel();
        memoryPanel.setLayout(new GridBagLayout());
        memoryPanel.add(new ChartPanel(physMem), pmConstraints);
        memoryPanel.add(new ChartPanel(virtMem), vmConstraints);

        textArea = new JTextArea(60, 20);
        textArea.setText("");//updateMemoryText(memory)
        memoryPanel.add(textArea, textConstraints);

        add(memoryPanel, BorderLayout.CENTER);

//        Timer timer = new Timer(Config.REFRESH_SLOW, e -> {
//            updateDatasets(memory, physMemData, virtMemData);
//            physMem.setSubtitles(Collections.singletonList(new TextTitle(updatePhysTitle(memory))));
//            virtMem.setSubtitles(Collections.singletonList(new TextTitle(updateVirtTitle(memory))));
//            textArea.setText(updateMemoryText(memory));
//        });
//        timer.start();
    }

    private static String updatePhysTitle(Memory memory) {
        return memory.getPhysicalTitle();
    }

    private static String updateVirtTitle(Memory memory) {
        return memory.getVirtualMemory().getVirtualTitle();
    }

    private static String updateMemoryText(Memory memory) {
        return memory.getPhysicalMemory();
    }

    private static void updateDatasets(Memory memory, DefaultPieDataset<String> physMemData, DefaultPieDataset<String> virtMemData) {
        physMemData.setValue(USED, (double) memory.getUse());
        physMemData.setValue(AVAILABLE, memory.getAvailable());

        VirtualMemory virtualMemory = memory.getVirtualMemory();
        virtMemData.setValue(USED, virtualMemory.getSwapUsed());
        virtMemData.setValue(AVAILABLE, (double) virtualMemory.getSwapavAilable());
    }

    private static void configurePlot(JFreeChart chart) {
        @SuppressWarnings("unchecked")
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setSectionPaint(USED, Color.red);
        plot.setSectionPaint(AVAILABLE, Color.green);
        plot.setExplodePercent(USED, 0.10);
        plot.setSimpleLabels(true);

        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator("{0}: {1} ({2})",
                new DecimalFormat("0"), new DecimalFormat("0%"));
        plot.setLabelGenerator(labelGenerator);
    }

    public void create(Memory memory) {
        updateDatasets(memory, physMemData, virtMemData);
        physMem.setSubtitles(Collections.singletonList(new TextTitle(updatePhysTitle(memory))));
        virtMem.setSubtitles(Collections.singletonList(new TextTitle(updateVirtTitle(memory))));
        textArea.setText(updateMemoryText(memory));
    }

    public void reset() {
        physMemData.setValue(USED, (double) 0);
        physMemData.setValue(AVAILABLE, 100);
        virtMemData.setValue(USED, 0);
        virtMemData.setValue(AVAILABLE, (double) 100);
        physMem.setSubtitles(Collections.singletonList(new TextTitle("")));
        virtMem.setSubtitles(Collections.singletonList(new TextTitle("")));
        textArea.setText("");
    }

    public void refresh(Memory memory) {
        updateDatasets(memory, physMemData, virtMemData);
        physMem.setSubtitles(Collections.singletonList(new TextTitle(updatePhysTitle(memory))));
        virtMem.setSubtitles(Collections.singletonList(new TextTitle(updateVirtTitle(memory))));
    }
}
