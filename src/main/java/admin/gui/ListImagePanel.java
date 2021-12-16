package admin.gui;

import admin.AdminSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ListImagePanel extends JFrame{
    private JPanel pnMain;
    private JPanel pnList;
    private JButton btClose;
    List list;

    public ListImagePanel(AdminSession session, ArrayList<String> lstImg, String hostNameClient, ScreensLog screensLog){
        super("Screens Log " + hostNameClient);
        setContentPane(pnMain);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setSize(Config.MINI_WIDTH, Config.GUI_HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
        addItem(lstImg);
        pnList.add(list);
        list.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Do class ScreenshotPanel có mở hình bằng default image viewer nên không cần tạo frame
                screensLog.get(list.getSelectedItem(), hostNameClient);
            }
        });
        btClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                session.disposeImagePanel();
            }
        });
    }

    private void addItem(ArrayList<String> lstImg) {
        list = new List();
        for (String item : lstImg) {
            list.add(item);
        }
    }

    public interface ScreensLog{
        public void get(String nameFile, String folder);
    }
}
