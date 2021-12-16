package core;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageHandle {
    private static final String imgLocation = "image\\";

    public static void storeImage (Image image, String folderName) throws IOException {
        File folder = new File(imgLocation + folderName);
        if (!folder.exists())
            new File(folder.getPath()).mkdirs();
        File outputfile = new File(folder.getPath() + "\\" + System.currentTimeMillis() + ".png");
        ImageIO.write((BufferedImage) image, "png", outputfile);
    }

    public static ArrayList<String> scanImage (String folderName) {
        Set<String> set = Stream.of(new File(imgLocation + folderName).listFiles())
                .filter(file -> !file.isDirectory())
                .filter(file -> (new MimetypesFileTypeMap().getContentType(file)).contains("image"))
                .map(File::getName)
                .collect(Collectors.toSet());
        ArrayList<String> imgList = new ArrayList(set);
        imgList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1) > 0 ? 1 :
                        o2.compareTo(o1) <= 0 ? -1 : 0;
            }
        });
        return imgList;
    }

    /*public static void main (String[] args) throws Exception{
        ImageHandle imageHandle = new ImageHandle();
        Image image  = ImageIO.read(new File("image\\camera.png"));
        imageHandle.storeImage(image, "Hello");
        for (String img : imageHandle.scanImage("Hello")) {
            System.out.println(img);
        }
    }*/
}
