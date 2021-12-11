package core.system;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageHandle {
    final private String imgLocation = "image\\";

    public void storeImage (Image image, String folderName) throws IOException {
        File folder = new File(imgLocation + folderName);
        if (!folder.exists())
            new File(folder.getPath()).mkdirs();
        File outputfile = new File(folder.getPath() + "\\" + System.currentTimeMillis() + ".png");
        ImageIO.write((BufferedImage) image, "png", outputfile);
    }

    public ArrayList<String> scanImage (String folderName) {
        ArrayList<String> imgList = null;
        Set<String> set = Stream.of(new File(imgLocation + folderName).listFiles())
                .filter(file -> !file.isDirectory())
                .filter(file -> (new MimetypesFileTypeMap().getContentType(file)).contains("image"))
                .map(File::getName)
                .collect(Collectors.toSet());
        imgList = new ArrayList(set);
        return imgList;
    }

//    public static void main (String[] args) throws Exception{
//        ImageHandle imageHandle = new ImageHandle();
//        Image image  = ImageIO.read(new File("image\\camera.png"));
//        imageHandle.storeImage(image, "Hello");
//        for (String img : imageHandle.scanImage("Hello")) {
//            System.out.println(img);
//        }
//    }
}
