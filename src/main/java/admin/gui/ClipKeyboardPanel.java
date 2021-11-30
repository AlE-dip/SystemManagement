package admin.gui;

import core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ClipKeyboardPanel {
    private JPanel pnMain;
    private JButton btRunKey;
    private JButton btRunClip;
    private JPanel pnBody;
    private JTextArea taKey;
    private JPanel pnClip;
    private ArrayList<JPanel> listItem;
    private JLabel lbImage;

    public ClipKeyboardPanel() {
        listItem = new ArrayList<>();
        btRunClip.setEnabled(false);
        btRunKey.setEnabled(false);
        pnBody = new JPanel(new GridLayout(1,2));
        JPanel pnKey = new JPanel(new BorderLayout());
        pnClip = new JPanel(new GridLayout(10, 1, 10, 5));
        JScrollPane srKey = new JScrollPane(pnKey,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane srClip = new JScrollPane(pnClip,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        taKey = new JTextArea();
        taKey.setWrapStyleWord(true);
        taKey.setLineWrap(true);
        pnKey.add(taKey, BorderLayout.CENTER);

        pnBody.add(srKey);
        pnBody.add(srClip);
        //pnKey.add(taKey, BorderLayout.CENTER);
        pnMain.add(pnBody, BorderLayout.CENTER);
        pnMain.setVisible(true);
        pnMain.revalidate();
        pnMain.repaint();
    }

    public JPanel createPanel(){
        return pnMain;
    }

    public void setEventButton(ClipKeyboard clipKeyboard){
        btRunClip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(btRunClip.getText().equals("Run")){
                    clipKeyboard.runClipboard();
                    btRunClip.setText("Stop");
                }else {
                    clipKeyboard.stopClipboard();
                    btRunClip.setText("Run");
                }
            }
        });
        btRunKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(btRunKey.getText().equals("Run")){
                    clipKeyboard.runKeyboard();
                    btRunKey.setText("Stop");
                }else {
                    clipKeyboard.stopKeyboard();
                    btRunKey.setText("Run");
                }
            }
        });
    }

    public void create(){
        btRunClip.setEnabled(true);
        btRunKey.setEnabled(true);
    }

    public void reset(){
        btRunClip.setText("Run");
        btRunKey.setText("Run");
        btRunClip.setEnabled(false);
        btRunKey.setEnabled(false);
        taKey.setText("");
        listItem.clear();
    }

    public void clipboardPanelAddItem(Object object){
        JPanel pnItemClip = new JPanel(new BorderLayout());
        pnItemClip.setMinimumSize(new Dimension(80,80));
        pnItemClip.setPreferredSize(new Dimension(80,80));
        pnItemClip.setMaximumSize(new Dimension(80,80));
        Border blackline = BorderFactory.createLineBorder(Color.black);
        pnItemClip.setBorder(blackline);
        //List 10 phần tử
        if(listItem.size() < 10){
            listItem.add(pnItemClip);
        }else {
            listItem.remove(0);
            listItem.add(pnItemClip);
        }
        pnClip.removeAll();
        for (int i = listItem.size() - 1; i >= 0; i--){
            pnClip.add(listItem.get(i));
        }
        //Gắn giá trị
        if(object instanceof String){
            JTextArea taItem = new JTextArea();
            taItem.setWrapStyleWord(true);
            taItem.setLineWrap(true);
            taItem.setText((String) object);
            pnItemClip.add(taItem, BorderLayout.CENTER);
        }else if (object instanceof BufferedImage){
            BufferedImage image = (BufferedImage) object;
            lbImage = new JLabel();
            lbImage.setHorizontalAlignment(JLabel.CENTER);
            ImageIcon icon = Core.scaleImage(new ImageIcon(image), 400, 80);
            lbImage.setIcon(icon);
            pnItemClip.add(lbImage, BorderLayout.CENTER);
        }
    }

    public interface ClipKeyboard {
        public void runClipboard();
        public void stopClipboard();
        public void runKeyboard();
        public void stopKeyboard();
    }
}
