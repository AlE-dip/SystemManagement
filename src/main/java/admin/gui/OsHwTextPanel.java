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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.system.OperatingSystem;
import core.system.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Display;
import oshi.util.EdidUtil;
import oshi.util.FormatUtil;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.util.List;

/**
 * Displays text in panes covering mostly-static information. Uptime is
 * refreshed every second.
 */
public class OsHwTextPanel extends OshiJPanel { // NOSONAR squid:S110

    private static final long serialVersionUID = 1L;

    private static final String OPERATING_SYSTEM = "Operating System";
    private static final String HARDWARE_INFORMATION = "Hardware Information";
    private static final String PROCESSOR = "Processor";
    private static final String DISPLAYS = "Displays";
    private String osPrefix;
    private JTextArea osArea;

    public OsHwTextPanel(SystemInfo si) {
        super();
        init(si);
    }

    private void init(SystemInfo si) {
        osPrefix = getOsPrefix(si);

        GridBagConstraints osLabel = new GridBagConstraints();
        GridBagConstraints osConstraints = new GridBagConstraints();
        osConstraints.gridy = 1;
        osConstraints.fill = GridBagConstraints.BOTH;
        osConstraints.insets = new Insets(0, 0, 15, 15); // T,L,B,R

        GridBagConstraints procLabel = (GridBagConstraints) osLabel.clone();
        procLabel.gridy = 2;
        GridBagConstraints procConstraints = (GridBagConstraints) osConstraints.clone();
        procConstraints.gridy = 3;

        GridBagConstraints displayLabel = (GridBagConstraints) procLabel.clone();
        displayLabel.gridy = 4;
        GridBagConstraints displayConstraints = (GridBagConstraints) osConstraints.clone();
        displayConstraints.gridy = 5;
        displayConstraints.insets = new Insets(0, 0, 0, 15); // T,L,B,R

        GridBagConstraints csLabel = (GridBagConstraints) osLabel.clone();
        csLabel.gridx = 1;
        GridBagConstraints csConstraints = new GridBagConstraints();
        csConstraints.gridx = 1;
        csConstraints.gridheight = 6;
        csConstraints.fill = GridBagConstraints.BOTH;

        JPanel oshwPanel = new JPanel();
        oshwPanel.setLayout(new GridBagLayout());

        osArea = new JTextArea(0, 0);
        osArea.setText(osPrefix + si.getOperatingSystem().getUpTime());
        oshwPanel.add(new JLabel(OPERATING_SYSTEM), osLabel);
        oshwPanel.add(osArea, osConstraints);

        JTextArea procArea = new JTextArea(0, 0);
        procArea.setText(getProc(si));
        oshwPanel.add(new JLabel(PROCESSOR), procLabel);
        oshwPanel.add(procArea, procConstraints);

        JTextArea displayArea = new JTextArea(0, 0);
        displayArea.setText(getDisplay(si));
        oshwPanel.add(new JLabel(DISPLAYS), displayLabel);
        oshwPanel.add(displayArea, displayConstraints);

        JTextArea csArea = new JTextArea(0, 0);
        csArea.setText(getHw(si));
        oshwPanel.add(new JLabel(HARDWARE_INFORMATION), csLabel);
        oshwPanel.add(csArea, csConstraints);

        add(oshwPanel, BorderLayout.CENTER);
    }

    private static String getOsPrefix(SystemInfo si) {
        StringBuilder sb = new StringBuilder(OPERATING_SYSTEM);

        OperatingSystem os = si.getOperatingSystem();
        sb.append(os.getOsPrefix());
        return sb.toString();
    }

    private static String getHw(SystemInfo si) {
        StringBuilder sb = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        try {
            sb.append(mapper.writeValueAsString(si.getFirmware()).substring(0, 8) + "\n"); //con
            sb.append(mapper.writeValueAsString(si.getBaseboard()).substring(0, 8) + "\n"); //con
            sb.append(si.getModel() + "\n");
            sb.append(si.getManufacturer() + "\n");
            sb.append(si.getSerialNumber() + "\n");
            sb.append(si.getHardwareUUID() + "\n");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static String getProc(SystemInfo si) {
        StringBuilder sb = new StringBuilder();
        sb.append(si.getProcessor().getInfoProcessor());
        return sb.toString();
    }

    private static String getDisplay(SystemInfo si) {
        StringBuilder sb = new StringBuilder();
        sb.append(si.getDisplay().getDisplay());
        return sb.toString();
    }

    private void updateOsData(SystemInfo si) {
        osArea.setText(osPrefix + si.getOperatingSystem().getUpTime());
    }
}
