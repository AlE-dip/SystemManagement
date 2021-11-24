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

import core.system.Processor;
import core.system.SystemInfo;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Shows system and per-processor CPU usage every second in a time series chart.
 */
public class ProcessorPanel extends OshiJPanel { // NOSONAR squid:S110

    private static final long serialVersionUID = 1L;

    private DynamicTimeSeriesCollection sysData;
    private DynamicTimeSeriesCollection procData;
    private JFreeChart procCpu;
    private Date date;
    private JPanel cpuPanel;
    private GridBagConstraints procConstraints;
    private GridBagConstraints sysConstraints;

    public ProcessorPanel() {
        super();
        //CentralProcessor cpu = si.getHardware().getProcessor();
//        oldTicks = new long[TickType.values().length];
//        oldProcTicks = new long[cpu.getLogicalProcessorCount()][TickType.values().length];
        init();
    }

    private void init(/*CentralProcessor processor*/) {

        sysConstraints = new GridBagConstraints();
        sysConstraints.weightx = 1d;
        sysConstraints.weighty = 1d;
        sysConstraints.fill = GridBagConstraints.BOTH;

        procConstraints = (GridBagConstraints) sysConstraints.clone();
        procConstraints.gridx = 1;

//        date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
//        sysData = new DynamicTimeSeriesCollection(1, 60, new Second());
//        sysData.setTimeBase(new Second(date));
//        //sysData.addSeries(floatArrayPercent(0), 0, "All cpus");//cpuData(processor)
//        JFreeChart systemCpu = ChartFactory.createTimeSeriesChart("System CPU Usage", "Time", "% CPU", sysData, true,
//                true, false);
//
//        //double[] procUsage = procData(processor);
//        procData = new DynamicTimeSeriesCollection(0, 60, new Second());//procUsage.length
//        procData.setTimeBase(new Second(date));
////        for (int i = 0; i < procUsage.length; i++) {
////            procData.addSeries(floatArrayPercent(procUsage[i]), i, "cpu" + i);
////        }
//        procCpu = ChartFactory.createTimeSeriesChart("Processor CPU Usage", "Time", "% CPU", procData, true,
//                true, false);

        cpuPanel = new JPanel();
        cpuPanel.setLayout(new GridBagLayout());
//        cpuPanel.add(new ChartPanel(systemCpu), sysConstraints);
//        cpuPanel.add(new ChartPanel(procCpu), procConstraints);

        add(cpuPanel, BorderLayout.CENTER);

//        Timer timer = new Timer(Config.REFRESH_FAST, e -> {
//            sysData.advanceTime();
//            sysData.appendData(floatArrayPercent(cpuData(processor)));
//            procData.advanceTime();
//            int newest = procData.getNewestIndex();
//            double[] procUsageData = procData(processor);
//            for (int i = 0; i < procUsageData.length; i++) {
//                procData.addValue(i, newest, (float) (100 * procUsageData[i]));
//            }
//        });
//        timer.start();
    }

    private static float[] floatArrayPercent(double d) {
        float[] f = new float[1];
        f[0] = (float) (100d * d);
        return f;
    }

    private double cpuData(Processor proc) {
        return proc.getTicks();
    }

    private double[] procData(Processor proc) {
        return proc.getProcTicks();
    }

    public void create(SystemInfo systemInfo){
        Processor processor = systemInfo.getProcessor();
        date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        sysData = new DynamicTimeSeriesCollection(1, 60, new Second());
        sysData.setTimeBase(new Second(date));
        sysData.addSeries(floatArrayPercent(cpuData(processor)), 0, "All cpus");
        JFreeChart systemCpu = ChartFactory.createTimeSeriesChart("System CPU Usage", "Time", "% CPU", sysData, true,
                true, false);

        double[] procUsage = procData(processor);
        procData = new DynamicTimeSeriesCollection(procUsage.length, 60, new Second());
        procData.setTimeBase(new Second(date));
        for (int i = 0; i < procUsage.length; i++) {
            procData.addSeries(floatArrayPercent(procUsage[i]), i, "cpu" + i);
        }
        JFreeChart procCpu = ChartFactory.createTimeSeriesChart("Processor CPU Usage", "Time", "% CPU", procData, true,
                true, false);
        cpuPanel.add(new ChartPanel(systemCpu), sysConstraints);
        cpuPanel.add(new ChartPanel(procCpu), procConstraints);
    }

    public void refresh(Processor processor){
        sysData.advanceTime();
        sysData.appendData(floatArrayPercent(cpuData(processor)));
        procData.advanceTime();
        int newest = procData.getNewestIndex();
        double[] procUsageData = procData(processor);
        for (int i = 0; i < procUsageData.length; i++) {
            procData.addValue(i, newest, (float) (100 * procUsageData[i]));
        }
    }

    public void reset(){
        cpuPanel.removeAll();
    }
}
