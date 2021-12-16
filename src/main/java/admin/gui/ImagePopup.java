package admin.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImagePopup extends JFrame{
    private JPanel pnMain;
    private JLabel lbImage;
    private JButton btClose;

    public ImagePopup(Image image){
        super("Screens Log");
        setContentPane(pnMain);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setSize(Config.GUI_WIDTH, Config.GUI_HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
        lbImage.setIcon(new ImageIcon(image));
        btClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}
