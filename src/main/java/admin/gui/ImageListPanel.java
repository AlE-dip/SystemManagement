package admin.gui;
import javax.swing.*;
import  java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ImageListPanel extends OshiJPanel {
    ArrayList<String> lstImg = new ArrayList<>();
    List list;
    public ImageListPanel (ArrayList<String> lstImg) {
        this.lstImg = lstImg;
        list = new List();
        addItem();
        add(list);
        list.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Do class ScreenshotPanel có mở hình bằng default image viewer nên không cần tạo frame
                new ScreenshotPanel(list.getSelectedItem());
            }
        });
    }

    private void addItem() {
        list.removeAll();
        for (String item : lstImg) {
            list.add(item);
        }
    }

    public static void main (String[] args) {
        JFrame frame = new JFrame();

        // tạo ArrayList test ---------------------------------------------
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("img.png");
        for (int i=1; i<=7; i++) {
            arrayList.add("img_"+ i +".png");
        }
        //------------------------------------------------------------------

        ImageListPanel imageListPanel = new ImageListPanel(arrayList);
        frame.setSize(1100,1100);
        frame.add(imageListPanel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
