package core;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
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

}
