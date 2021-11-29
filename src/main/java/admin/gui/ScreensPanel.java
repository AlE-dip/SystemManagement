package admin.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScreensPanel {
    private JPanel pnMain;
    public JButton btScreens;
    public JButton btControl;
    public JLabel lbScreens;

    public ScreensPanel() {
    }

    public JPanel createPanel(){
        btScreens.setEnabled(false);
        btControl.setEnabled(false);
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
                    lbScreens.setText("");
                }else {
                    screens.stopScreens();
                    btScreens.setText("Run");
                    lbScreens.setText("Close");
                }
            }
        });
    }

    public void reset(){
        lbScreens.setIcon(null);
        lbScreens.setText("Close");
        btScreens.setEnabled(false);
        btScreens.setText("Run");
        btControl.setEnabled(false);
    }

    public void create(){
        btScreens.setEnabled(true);
        btControl.setEnabled(true);
    }

    public interface Screens{
        public void runScreens();
        public void stopScreens();
    }
}
