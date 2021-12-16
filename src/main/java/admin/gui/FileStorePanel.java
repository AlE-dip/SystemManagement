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

import core.system.FileStore;
import core.system.FileSystem;
import core.system.SystemInfo;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import oshi.PlatformEnum;
import oshi.util.FormatUtil;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays used and free space on all filesystems.
 */
public class FileStorePanel extends OshiJPanel { // NOSONAR squid:S110

    private static final long serialVersionUID = 1L;

    private static final String USED = "Used";
    private static final String AVAILABLE = "Available";

    private GridBagConstraints fsConstraints;
    private DefaultPieDataset<String>[] fsData;
    private JFreeChart[] fsCharts;
    private JPanel fsPanel;
    private JTextArea taDiskInfo;

    public FileStorePanel() {
        super();
        init(/*si.getOperatingSystem().getFileSystem()*/);
    }

    private void init() {
        //List<OSFileStore> fileStores = fs.getFileStores();
//        DefaultPieDataset<String>[] fsData = new DefaultPieDataset[fileStores.size()];
//        JFreeChart[] fsCharts = new JFreeChart[fsData.length];

        fsPanel = new JPanel();
        fsPanel.setLayout(new GridBagLayout());
        fsConstraints = new GridBagConstraints();
        fsConstraints.weightx = 1d;
        fsConstraints.weighty = 1d;
        fsConstraints.fill = GridBagConstraints.BOTH;

//        int modBase = (int) (fileStores.size() * (Config.GUI_HEIGHT + Config.GUI_WIDTH)
//                / (Config.GUI_WIDTH * Math.sqrt(fileStores.size())));
//        for (int i = 0; i < fileStores.size(); i++) {
//            fsData[i] = new DefaultPieDataset<>();
//            fsCharts[i] = ChartFactory.createPieChart(null, fsData[i], true, true, false);
//            configurePlot(fsCharts[i]);
//            fsConstraints.gridx = i % modBase;
//            fsConstraints.gridy = i / modBase;
//            fsPanel.add(new ChartPanel(fsCharts[i]), fsConstraints);
//        }
//        updateDatasets(fs, fsData, fsCharts);

        add(fsPanel, BorderLayout.CENTER);

//        Timer timer = new Timer(Config.REFRESH_SLOWER, e -> {
//            if (!updateDatasets(fs, fsData, fsCharts)) {
//                ((Timer) e.getSource()).stop();
//                fsPanel.removeAll();
//                init(fs);
//                fsPanel.revalidate();
//                fsPanel.repaint();
//            }
//        });
//        timer.start();
        taDiskInfo = new JTextArea();
        add(taDiskInfo, BorderLayout.SOUTH);
    }

    private static boolean updateDatasets(FileSystem fs, DefaultPieDataset<String>[] fsData, JFreeChart[] fsCharts) {
        List<FileStore> fileStores = fs.getFileStores();
        if (fileStores.size() != fsData.length) {
            return false;
        }
        int i = 0;
        for (FileStore store : fileStores) {
            fsCharts[i].setTitle(store.getName());
            List<TextTitle> subtitles = new ArrayList<>();
            if(store.getLabel() != null) {
                subtitles.add(new TextTitle(store.getLabel()));
            }
            long usable = store.getAvailable();
            long total = store.getTotal();
            subtitles.add(new TextTitle(
                    "Available: " + FormatUtil.formatBytes(usable) + "/" + FormatUtil.formatBytes(total)));
            fsCharts[i].setSubtitles(subtitles);
            fsData[i].setValue(USED, (double) store.getUse());
            fsData[i].setValue(AVAILABLE, usable);
            i++;
        }
        return true;
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

    public void create(SystemInfo systemInfo){
        FileSystem fileSystem = systemInfo.getFileSystem();
        ArrayList<FileStore> fileStores = fileSystem.getFileStores();
        fsData = new DefaultPieDataset[fileStores.size()];
        fsCharts = new JFreeChart[fsData.length];

        int modBase = (int) (fileStores.size() * (Config.GUI_HEIGHT + Config.GUI_WIDTH)
                / (Config.GUI_WIDTH * Math.sqrt(fileStores.size())));
        for (int i = 0; i < fileStores.size(); i++) {
            fsData[i] = new DefaultPieDataset<>();
            fsCharts[i] = ChartFactory.createPieChart(null, fsData[i], true, true, false);
            configurePlot(fsCharts[i]);
            fsConstraints.gridx = i % modBase;
            fsConstraints.gridy = i / modBase;
            fsPanel.add(new ChartPanel(fsCharts[i]), fsConstraints);
        }
        updateDatasets(fileSystem, fsData, fsCharts);
        taDiskInfo.setText(systemInfo.getDiskInfo());
    }

    public void refresh(SystemInfo systemInfo){
        FileSystem fileSystem = systemInfo.getFileSystem();
        if (!updateDatasets(fileSystem, fsData, fsCharts)) {
            fsPanel.removeAll();
            init();
            create(systemInfo);
            fsPanel.revalidate();
            fsPanel.repaint();
        }
    }

    public void reset(){
        fsPanel.removeAll();
        fsPanel.revalidate();
        fsPanel.repaint();
    }

}
