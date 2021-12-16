package admin.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScreensPanel {
    private JPanel pnMain;
    public JButton btScreens;
    public JButton btWriteLog;
    public JLabel lbScreens;
    private JButton btShowLog;

    public ScreensPanel() {
    }

    public JPanel createPanel(){
        btScreens.setEnabled(false);
        btWriteLog.setEnabled(false);
        btShowLog.setEnabled(false);
        return pnMain;
    }

    public void refresh(ImageIcon icon){
        lbScreens.setIcon(icon);
    }

    public void setEventButton(Screens screens){
        btScreens.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(btScreens.getText().equals("Run")){
                    screens.runScreens();
                    btScreens.setText("Stop");
                    btWriteLog.setEnabled(true);
                }else {
                    int result = JOptionPane.showConfirmDialog(
                            JOptionPane.getFrameForComponent(pnMain),
                            "Stop???",
                            "Screens",
                            JOptionPane.YES_NO_OPTION);
                    if(result == 0){
                        screens.stopScreens();
                        btScreens.setText("Run");
                        btWriteLog.setText("Write Log");
                        btWriteLog.setEnabled(false);
                    }
                }
            }
        });
        btWriteLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(btWriteLog.getText().equals("Write Log")){
                    screens.writeLog();
                    btWriteLog.setText("Stop");
                }else {
                    int result = JOptionPane.showConfirmDialog(
                            JOptionPane.getFrameForComponent(pnMain),
                            "Stop???",
                            "Screens Log",
                            JOptionPane.YES_NO_OPTION);
                    if(result == 0){
                        screens.stopLog();
                        btWriteLog.setText("Write Log");
                    }
                }
            }
        });
        btShowLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                screens.showLog();
            }
        });
    }

    public void reset(){
        lbScreens.setIcon(null);
        lbScreens.setIcon(AdminGui.iconWarn);
        btScreens.setEnabled(false);
        btScreens.setText("Run");
        btWriteLog.setEnabled(false);
        btWriteLog.setText("Write Log");
        btShowLog.setEnabled(false);
    }

    public void create(){
        lbScreens.setIcon(AdminGui.iconWarn);
        btScreens.setEnabled(true);
        btShowLog.setEnabled(true);
    }

    public interface Screens{
        public void runScreens();
        public void stopScreens();
        public void writeLog();
        public void stopLog();
        public void showLog();
    }
}
