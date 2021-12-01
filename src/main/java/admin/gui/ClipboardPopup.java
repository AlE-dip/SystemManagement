package admin.gui;

import core.Core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ClipboardPopup extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel lbContent;
    private JTextArea taContent;
    private JPanel pnContent;

    public ClipboardPopup(Object object, ClipKeyboardPanel parent) {
        super("Item clipboard");
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setSize(Config.GUI_WIDTH, Config.GUI_HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
        pnContent = new JPanel(new GridLayout(1,1));
        pnContent.setBackground(Color.white);
        JScrollPane srKey = new JScrollPane(pnContent,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPane.add(srKey, BorderLayout.CENTER);
        if (object instanceof String){
            taContent = new JTextArea();
            taContent.setText((String) object);
            pnContent.add(taContent);
        }else if (object instanceof BufferedImage){
            lbContent = new JLabel();
            lbContent.setHorizontalAlignment(JLabel.CENTER);
            ImageIcon icon = Core.scaleImage(new ImageIcon((BufferedImage) object), 950, 600);
            lbContent.setIcon(icon);
            pnContent.add(lbContent);
            pnContent.setVisible(true);
            System.out.println(pnContent.getWidth());
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parent.disposeChildrenFrame();
            }
        });
    }
}
