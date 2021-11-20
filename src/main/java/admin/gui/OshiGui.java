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

import core.system.SystemInfo;

import javax.swing.*;
import java.awt.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 * Basic Swing class to demonstrate potential uses for OSHI in a monitoring GUI.
 * Not ready for production use and intended as inspiration/examples.
 */
public class OshiGui {

    private JFrame mainFrame;
    private JButton jMenu;
    OsHwTextPanel osHwTextPanel;

    private oshi.SystemInfo si = new oshi.SystemInfo();
    private SystemInfo systemInfo;

    public static void main(String[] args) {
        OshiGui gui = new OshiGui();
        gui.init();
        SwingUtilities.invokeLater(gui::setVisible);

    }

    private void setVisible() {
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jMenu.doClick();
    }

    private void init() {
        osHwTextPanel = new OsHwTextPanel();
        // Create the external frame
        mainFrame = new JFrame(Config.GUI_TITLE);
        mainFrame.setSize(Config.GUI_WIDTH, Config.GUI_HEIGHT);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setResizable(true);
        mainFrame.setLocationByPlatform(true);
        mainFrame.setLayout(new BorderLayout());
        // Add a menu bar
        JMenuBar menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);
        // Assign the first menu option to be clicked on visibility
        jMenu = getJMenu("OS & HW Info", 'O', "Hardware & OS Summary", osHwTextPanel);
        menuBar.add(jMenu);
        // Add later menu items
        menuBar.add(getJMenu("Memory", 'M', "Memory Summary", new MemoryPanel(si)));
        menuBar.add(getJMenu("CPU", 'C', "CPU Usage", new ProcessorPanel(si)));
        menuBar.add(getJMenu("FileStores", 'F', "FileStore Usage", new FileStorePanel(si)));
        menuBar.add(getJMenu("Processes", 'P', "Processes", new ProcessPanel(si)));
        menuBar.add(getJMenu("USB Devices", 'U', "USB Device list", new UsbPanel(si)));
        menuBar.add(getJMenu("Network", 'N', "Network Params and Interfaces", new InterfacePanel(si)));
    }

    private JButton getJMenu(String title, char mnemonic, String toolTip, OshiJPanel panel) {
        JButton button = new JButton(title);
        button.setMnemonic(mnemonic);
        button.setToolTipText(toolTip);
        button.addActionListener(e -> {
            Container contentPane = this.mainFrame.getContentPane();
            if (contentPane.getComponents().length <= 0 || contentPane.getComponent(0) != panel) {
                resetMainGui();
                this.mainFrame.getContentPane().add(panel);
                refreshMainGui();
            }
        });

        return button;
    }

    public void refresh(SystemInfo systemInfo){
        if(this.systemInfo == null){
            this.systemInfo = systemInfo;
            init();
            SwingUtilities.invokeLater(this::setVisible);
        }else {
            osHwTextPanel.refresh(systemInfo);
        }
    }

    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    private void resetMainGui() {
        this.mainFrame.getContentPane().removeAll();
    }

    private void refreshMainGui() {
        this.mainFrame.revalidate();
        this.mainFrame.repaint();
    }
}
