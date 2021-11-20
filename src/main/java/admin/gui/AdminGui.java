package admin.gui;

import core.system.SystemInfo;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminGui extends JFrame {
    private JPanel pnMain;
    private JTabbedPane tpUser;
    private JPanel pnListUser;
    //OshiGui
    private OsHwTextPanel osHwTextPanel;
    public boolean created;
    private SystemInfo systemInfo;
    private oshi.SystemInfo si;
    private Map<Integer, JButton> listUser;
    public int currentUser;
    private JButton currentButton;

    public AdminGui() {
        super("System Management");
        setContentPane(pnMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(Config.GUI_WIDTH, Config.GUI_HEIGHT);
        setVisible(true);
        si = new oshi.SystemInfo();
        systemInfo = new SystemInfo(si);
        init();
    }

    private void init() {
        created = false;
        currentUser = -111;
        listUser = new LinkedHashMap<>();
        osHwTextPanel = new OsHwTextPanel();
        tpUser.addTab("OS & HW Info", null, osHwTextPanel, "O");
        tpUser.addTab("Memory", null, new MemoryPanel(si), "click to show panel 2");

        pnListUser = new JPanel(new GridLayout(10, 1, 1, 5));
        pnListUser.setVisible(true);
        add(pnListUser, BorderLayout.WEST);
    }

    public void addUserButtons(ArrayList<Integer> ids) {
        listUser.clear();
        pnListUser.removeAll();
        for (int id : ids) {
            JButton button = new JButton("user - " + id);
            button.setVisible(true);
            listUser.put(id, button);
            pnListUser.add(button);
        }
        pnListUser.revalidate();
        pnListUser.repaint();
    }

    public void addUserButton(int id) {
        JButton button = new JButton("user - " + id);
        button.setVisible(true);
        listUser.put(id, button);
        pnListUser.add(button);
        pnListUser.revalidate();
        pnListUser.repaint();
    }

    public void destroyUserButton(int id) {
        listUser.remove(id);
        pnListUser.removeAll();
        for (Integer i : listUser.keySet()) {
            JButton button = new JButton("user - " + i);
            button.setVisible(true);
            if (currentUser == i) {
                currentButton = button;
                currentButton.setBackground(Color.ORANGE);
            }
            pnListUser.add(button);
        }
        pnListUser.revalidate();
        pnListUser.repaint();
    }

    public void setCurrentButton(int id) {
        if (currentUser == id) {
            currentButton.setBackground(Color.ORANGE);
        } else {
            currentButton = listUser.get(id);
            currentButton.setBackground(Color.ORANGE);
            currentUser = id;
            reset();
        }
    }

    public void create(SystemInfo systemInfo) {
        osHwTextPanel.create(systemInfo);
        created = true;
    }

    public void reset() {
        osHwTextPanel.reset();
        created = false;
    }

    public void refresh(SystemInfo systemInfo) {
        osHwTextPanel.refresh(systemInfo);
    }
}
