package core;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Core {

    public static void writeString(BufferedWriter writer, String string) throws IOException {
        writer.write(string);
        writer.newLine();
        writer.flush();
    }

    public static void resizeBasedOnWidth(Mat image, int width){
        int height = (int) (1.0 * width / image.cols() * image.rows());
        Size size = new Size(width, height);
        Imgproc.resize( image, image, size );
    }

    public static void resizeBasedOnHeight(Mat image, int height){
        int width = (int) (1.0 * height / image.rows() * image.cols());
        Size size = new Size(width, height);
        Imgproc.resize( image, image, size );
    }

    public static void resizeAuto(Mat image, int width, int height){
        float ratioContain = (float) ((1.0 * width) / height);
        float ratioImage = (float) (1.0 * image.cols()) / image.rows();
        if(ratioContain > ratioImage){
            //resize base width
            resizeBasedOnWidth(image, width);
        }else {
            //resize base height
            resizeBasedOnHeight(image, height);
        }
    }

    public static void writeMatToBuffer(Mat image, BufferedWriter writer) throws IOException {
        byte[] points = new byte[(int) (image.total() * image.channels())];
        image.get(0, 0, points);
        String imageInString = Base64.getEncoder().encodeToString(points);
        writer.write(image.rows() + "\r\n");
        writer.write(image.cols() + "\r\n");
        writer.write(image.type() + "\r\n");
        writer.write(imageInString);
        writer.newLine();
        writer.flush();
    }

    public static Mat readBufferToMat(BufferedReader reader) throws IOException {
        int rows = Integer.parseInt(reader.readLine());
        int cols = Integer.parseInt(reader.readLine());
        int types = Integer.parseInt(reader.readLine());
        byte[] points = Base64.getDecoder().decode(reader.readLine());
        Mat image = new Mat(rows, cols, types);
        image.put(0,0, points);
        return image;
    }

    public static void writeImageToBuffer(BufferedImage image, BufferedWriter writer) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] bytes = baos.toByteArray();
        String imageInString = Base64.getEncoder().encodeToString(bytes);
        writer.write(imageInString);
        writer.newLine();
        writer.flush();
    }

    public static BufferedImage readBufferToImage(BufferedReader reader) throws IOException {
        byte[] bytes = Base64.getDecoder().decode(reader.readLine());
        InputStream is = new ByteArrayInputStream(bytes);
        BufferedImage image = ImageIO.read(is);
        return image;
    }

    public static Mat bufferedImageToMat(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush();
        return Imgcodecs.imdecode(new MatOfByte(baos.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
    }

    public static String bufferedImageToString(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static BufferedImage stringToBufferedImage(String data) throws IOException {
        byte[] bytes = Base64.getDecoder().decode(data);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return ImageIO.read(bais);
    }

    public static ImageIcon scaleImage(ImageIcon icon, int w, int h)
    {
        int nw = icon.getIconWidth();
        int nh = icon.getIconHeight();
        if(icon.getIconWidth() > w)
        {
            nw = w;
            nh = (nw * icon.getIconHeight()) / icon.getIconWidth();
        }
        if(nh > h)
        {
            nh = h;
            nw = (icon.getIconWidth() * nh) / icon.getIconHeight();
        }
        return new ImageIcon(icon.getImage().getScaledInstance(nw, nh, Image.SCALE_SMOOTH));
    }

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    public static BufferedImage blurImage(BufferedImage image) {
        float ninth = 1.0f/9.0f;
        float[] blurKernel = {
                ninth, ninth, ninth,
                ninth, ninth, ninth,
                ninth, ninth, ninth
        };

        Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
        map.put(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        map.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        map.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        RenderingHints hints = new RenderingHints(map);
        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, hints);
        return op.filter(image, null);
    }

    public static void paintCursor(BufferedImage image) throws IOException {
        Image cursor = ImageIO.read(new File("image\\cursor.png"));
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int width = dimension.width;
        int height = dimension.height;
        double x = MouseInfo.getPointerInfo().getLocation().x;
        double y = MouseInfo.getPointerInfo().getLocation().y;
        double cx = x / width;
        double cy = y / height;
        Graphics2D graphics2D = image.createGraphics();
        int w = image.getWidth();
        int h = image.getHeight();
        graphics2D.drawImage(cursor, (int)(cx * w), (int)(cy * h), 16, 16, null);
    }

}
