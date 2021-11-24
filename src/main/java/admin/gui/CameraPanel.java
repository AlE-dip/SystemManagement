package admin.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CameraPanel extends Component {
    private JPanel pnMain;
    public JButton btCamera;
    public JLabel lbCamera;

    public CameraPanel(){
    }

    public JPanel createPanel(){
        return pnMain;
    }

    public void refresh(ImageIcon icon){
        lbCamera.setIcon(icon);
    }

    public void setEventButton(Camera camera){
        btCamera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(btCamera.getText().equals("Run")){
                    camera.runCamera();
                    btCamera.setText("Stop");
                    lbCamera.setText("");
                }else {
                    camera.stopCamera();
                    btCamera.setText("Run");
                    lbCamera.setText("Close");
                }
            }
        });
    }

    public interface Camera{
        public void runCamera();
        public void stopCamera();
    }
}
