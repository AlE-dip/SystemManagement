package ClipKeyboard;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.ImageIO;
 
public class ImageToClipboard {
	public static BufferedImage pasteImageFromClipboard() {
        BufferedImage result;
        Image img;
        int width;
        int height;
        Graphics g;

        result = null;
        Image transferableImage = null;
        String transferableString = null;
        img = (Image) pasteFromClipboard(DataFlavor.imageFlavor);
        if (img != null) {
            width = img.getWidth(null);
            height = img.getHeight(null);
            result = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            g = result.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
        }

        return result;
    }

	
    public static Object pasteFromClipboard(DataFlavor flavor) {
        Clipboard clipboard;
        Object result;
        Transferable content;

        result = null;

        try {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            content = clipboard.getContents(null);
            if ((content != null)
                    && (content.isDataFlavorSupported(flavor)))
                result = content.getTransferData(flavor);
        } catch (Exception e) {
            result = null;
        }

        return result;
    }
  public static void main(String[] arguments) throws IOException {
 
    JPanel panel = new JPanel();
 
    BufferedImage image = pasteImageFromClipboard();
    JLabel label = new JLabel(new ImageIcon(image));
    panel.add(label);
 
    // main window
    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame frame = new JFrame("JPanel Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
    // add the Jpanel to the main window
    frame.add(panel); 
 
    frame.pack();
    frame.setVisible(true);
 
  }
}