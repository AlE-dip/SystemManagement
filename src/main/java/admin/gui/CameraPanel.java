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
        btCamera.setEnabled(false);
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
                }else {
                    int result = JOptionPane.showConfirmDialog(
                            JOptionPane.getFrameForComponent(pnMain),
                            "Stop???",
                            "Camera",
                            JOptionPane.YES_NO_OPTION);
                    if(result == 0){
                        camera.stopCamera();
                        btCamera.setText("Run");
                    }
                }
            }
        });
    }

    public void reset(){
        lbCamera.setIcon(null);
        lbCamera.setIcon(AdminGui.iconWarn);
        btCamera.setEnabled(false);
        btCamera.setText("Run");
    }

    public void create(){
        lbCamera.setIcon(AdminGui.iconWarn);
        btCamera.setEnabled(true);
    }

    public interface Camera{
        public void runCamera();
        public void stopCamera();
    }
}
