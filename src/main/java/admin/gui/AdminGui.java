package admin.gui;

import core.system.SystemInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private ArrayList<String> listUser;
    public String currentUser;
    private JButton currentButton;
    public GuiAction guiAction;

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
        currentUser = "";
        listUser = new ArrayList<>();
        osHwTextPanel = new OsHwTextPanel();
        tpUser.addTab("OS & HW Info", null, osHwTextPanel, "O");
        tpUser.addTab("Memory", null, new MemoryPanel(si), "click to show panel 2");

        pnListUser = new JPanel(new GridLayout(10, 1, 1, 5));
        pnListUser.setVisible(true);
        add(pnListUser, BorderLayout.WEST);
    }

    public void addUserButtons(ArrayList<String> ids) {
        listUser.clear();
        pnListUser.removeAll();
        for (String id : ids) {
            JButton button = new JButton("user - " + id);
            button.setVisible(true);
            setEventButton(button, id);
            listUser.add(id);
            pnListUser.add(button);
        }
        pnListUser.revalidate();
        pnListUser.repaint();
    }

    public void addUserButton(String id) {
        JButton button = new JButton("user - " + id);
        button.setVisible(true);
        setEventButton(button, id);
        listUser.add(id);
        pnListUser.add(button);
        pnListUser.revalidate();
        pnListUser.repaint();
    }

    public void setEventButton(JButton jButton, String id){
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiAction.changeCurrentUser(id);
            }
        });
    }

    public void destroyUserButton(String id) {
        int index = listUser.indexOf(id);
        listUser.remove(id);
        pnListUser.remove(index);
        pnListUser.revalidate();
        pnListUser.repaint();
    }

    public void setCurrentButton(String id) {
        if (currentUser.equals(id)) {
            currentButton.setBackground(Color.ORANGE);
        } else {
            Component[] components = pnListUser.getComponents();
            if(components[listUser.indexOf(id)] instanceof JButton){
                currentButton = (JButton) components[listUser.indexOf(id)];
                currentButton.setBackground(Color.ORANGE);
                currentUser = id;
                reset();
            }
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

    public interface GuiAction {
        public void changeCurrentUser(String id);
    }
}
