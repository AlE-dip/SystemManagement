package core.model;

import org.opencv.core.Mat;

import java.util.Base64;

public class StringMat {
    private int cols;
    private int rows;
    private int type;
    private String pixels;

    public StringMat() {
        this.cols = 0;
        this.rows = 0;
        this.type = 0;
        this.pixels = "";
    }

    public StringMat(Mat image) {
        this.cols = image.cols();
        this.rows = image.rows();
        this.type = image.type();
        byte[] data = new byte[(int) (image.total() * image.channels())];
        image.get(0, 0, data);
        String imageInString = Base64.getEncoder().encodeToString(data);
        this.pixels = imageInString;
    }

    public Mat toMat(){
        byte[] points = Base64.getDecoder().decode(pixels);
        Mat image = new Mat(rows, cols, type);
        image.put(0,0, points);
        return image;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPixels() {
        return pixels;
    }

    public void setPixels(String pixels) {
        this.pixels = pixels;
    }
}
