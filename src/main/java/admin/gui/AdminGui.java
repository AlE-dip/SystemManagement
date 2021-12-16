package admin.gui;

import core.system.SystemInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
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
    private ArrayList<Image> listIcon;
    private boolean loadIcon;
    private ImageIcon iconUser;
    public static ImageIcon iconWarn;
    public OsHwTextPanel osHwTextPanel;
    private MemoryPanel memoryPanel;
    private ProcessorPanel processorPanel;
    private FileStorePanel fileStorePanel;
    public ProcessPanel processPanel;
    private InterfacePanel interfacePanel;
    public CameraPanel cameraPanel;
    public ScreensPanel screensPanel;
    public ClipKeyboardPanel clipKeyboardPanel;
    public boolean created;
    private SystemInfo systemInfo;
    private oshi.SystemInfo si;
    private ArrayList<String> listUser;
    public String currentUser;
    private JButton currentButton;
    public GuiAction guiAction;
    public String currentHostNameClient;

    public AdminGui() {
        super("System Management");
        setContentPane(pnMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(Config.GUI_WIDTH, Config.GUI_HEIGHT);
        setLocationRelativeTo(null);
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
        memoryPanel = new MemoryPanel();
        processorPanel = new ProcessorPanel();
        fileStorePanel = new FileStorePanel();
        processPanel = new ProcessPanel();
        interfacePanel = new InterfacePanel();
        cameraPanel = new CameraPanel();
        screensPanel = new ScreensPanel();
        clipKeyboardPanel = new ClipKeyboardPanel();

        loadIcon();
        initTab();

        pnListUser = new JPanel(new GridLayout(10, 1, 1, 5));
        pnListUser.setVisible(true);
        Border line = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        pnListUser.setBorder(line);
        add(pnListUser, BorderLayout.WEST);
    }

    public void loadIcon(){
        listIcon = new ArrayList<>();
        loadIcon = true;
        try {
            listIcon.add(ImageIO.read(new File("image\\hardware.png")));
            listIcon.add(ImageIO.read(new File("image\\ram.png")));
            listIcon.add(ImageIO.read(new File("image\\cpu.png")));
            listIcon.add(ImageIO.read(new File("image\\cd.png")));
            listIcon.add(ImageIO.read(new File("image\\process.png")));
            listIcon.add(ImageIO.read(new File("image\\network.png")));
            listIcon.add(ImageIO.read(new File("image\\camera.png")));
            listIcon.add(ImageIO.read(new File("image\\monitor.png")));
            listIcon.add(ImageIO.read(new File("image\\keyboard.png")));
            iconUser = new ImageIcon(ImageIO.read(new File("image\\programmer.png")));
            iconWarn = new ImageIcon(ImageIO.read(new File("image\\warn.png")));
        } catch (IOException e) {
            e.printStackTrace();
            loadIcon = false;
        }
    }

    public void initTab(){
        if (loadIcon){
            tpUser.addTab("OS & HW Info", new ImageIcon(listIcon.get(0)), osHwTextPanel, "O");
            tpUser.addTab("Memory", new ImageIcon(listIcon.get(1)), memoryPanel, "M");
            tpUser.addTab("CPU", new ImageIcon(listIcon.get(2)), processorPanel, "P");
            tpUser.addTab("FileStores", new ImageIcon(listIcon.get(3)), fileStorePanel, "F");
            tpUser.addTab("Processes", new ImageIcon(listIcon.get(4)), processPanel, "P");
            tpUser.addTab("Network", new ImageIcon(listIcon.get(5)), interfacePanel, "I");
            tpUser.addTab("Camera", new ImageIcon(listIcon.get(6)), cameraPanel.createPanel(), "C");
            tpUser.addTab("Screens", new ImageIcon(listIcon.get(7)), screensPanel.createPanel(), "S");
            tpUser.addTab("Clipboard & Keyboard", new ImageIcon(listIcon.get(8)), clipKeyboardPanel.createPanel(), "C");
        }else {
            tpUser.addTab("OS & HW Info", null, osHwTextPanel, "O");
            tpUser.addTab("Memory", null, memoryPanel, "M");
            tpUser.addTab("CPU", null, processorPanel, "P");
            tpUser.addTab("FileStores", null, fileStorePanel, "F");
            tpUser.addTab("Processes", null, processPanel, "P");
            tpUser.addTab("Network", null, interfacePanel, "I");
            tpUser.addTab("Camera", null, cameraPanel.createPanel(), "C");
            tpUser.addTab("Screens", null, screensPanel.createPanel(), "S");
            tpUser.addTab("Clipboard & Keyboard", null, clipKeyboardPanel.createPanel(), "C");
        }
    }

    public void addUserButtons(ArrayList<ArrayList<String>> ids) {
        listUser.clear();
        pnListUser.removeAll();
        for (ArrayList<String> id : ids) {
            JButton button = new JButton(id.get(1) + " - " + id.get(0), iconUser);
            button.setPreferredSize(new Dimension(140, 25));
            button.setVisible(true);
            setEventButton(button, id.get(0));
            listUser.add(id.get(0));
            pnListUser.add(button);
        }
        pnListUser.revalidate();
        pnListUser.repaint();
    }

    public void addUserButton(String id, String clientHostName) {
        JButton button = new JButton(clientHostName + " - " + id, iconUser);
        button.setPreferredSize(new Dimension(140, 25));
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
                if(!id.equals(currentUser)){
                    guiAction.changeCurrentUser(id);
                }
            }
        });
    }

    public void destroyUserButton(String id) {
        int index = listUser.indexOf(id);
        listUser.remove(id);
        pnListUser.remove(index);
        pnListUser.revalidate();
        pnListUser.repaint();
        //reset when destroy current
        if(currentUser.equals(id)){
            reset();
        }
    }

    public void setCurrentButton(String id) {
        if (currentUser.equals(id)) {
            currentButton.setBackground(Color.ORANGE);
        } else {
            Component[] components = pnListUser.getComponents();
            if(components[listUser.indexOf(id)] instanceof JButton){
                currentButton = (JButton) components[listUser.indexOf(id)];
                currentButton.setBackground(Color.ORANGE);
                currentHostNameClient = currentButton.getText().split(" - ")[0];
                currentUser = id;
                reset();
            }
        }
    }

    public void create(SystemInfo systemInfo) {
        osHwTextPanel.create(systemInfo);
        memoryPanel.create(systemInfo.getMemory());
        processorPanel.create(systemInfo);
        fileStorePanel.create(systemInfo);
        processPanel.create(systemInfo);
        interfacePanel.create(systemInfo);
        cameraPanel.create();
        screensPanel.create();
        osHwTextPanel.headerOsHwPanel.create();
        clipKeyboardPanel.create();
        created = true;
    }

    public void reset() {
        osHwTextPanel.reset();
        memoryPanel.reset();
        processorPanel.reset();
        fileStorePanel.reset();
        processPanel.reset();
        interfacePanel.reset();
        cameraPanel.reset();
        screensPanel.reset();
        osHwTextPanel.headerOsHwPanel.reset();
        clipKeyboardPanel.reset();
        created = false;
    }

    public void refresh(SystemInfo systemInfo) {
        osHwTextPanel.refresh(systemInfo);
        memoryPanel.refresh(systemInfo.getMemory());
        processorPanel.refresh(systemInfo.getProcessor());
        fileStorePanel.refresh(systemInfo);
        processPanel.refresh(systemInfo);
    }

    public interface GuiAction {
        public void changeCurrentUser(String id);
    }
}
