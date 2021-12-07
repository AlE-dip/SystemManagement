package admin.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HeaderOsHwPanel {
    public JButton btDisconnect;
    public JButton btShutdown;
    private JPanel pnMain;

    public HeaderOsHwPanel(){
    }

    public JPanel createPanel(){
        btDisconnect.setEnabled(false);
        btShutdown.setEnabled(false);
        return pnMain;
    }

    public void setEventButton(Control control){
        btShutdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        JOptionPane.getFrameForComponent(pnMain),
                        "Are you sure?",
                        "Shutdown",
                        JOptionPane.YES_NO_OPTION);
                if(result == 0){
                    control.shutdown();
                }
            }
        });
        btDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        JOptionPane.getFrameForComponent(pnMain),
                        "Are you sure?",
                        "Disconnect",
                        JOptionPane.YES_NO_OPTION);
                if(result == 0){
                    control.disconnect();
                }
            }
        });
    }

    public void reset(){
        btDisconnect.setEnabled(false);
        btShutdown.setEnabled(false);
    }

    public void create(){
        btDisconnect.setEnabled(true);
        btShutdown.setEnabled(true);
    }

    public interface Control{
        public void disconnect();
        public void shutdown();
    }
}
