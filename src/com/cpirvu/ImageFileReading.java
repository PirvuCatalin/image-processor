package com.cpirvu;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Class that deals with reading images from the local file system.
 */
public class ImageFileReading extends ImageFileExecutionStep {
    public static String FORMAT = "This image file reading step took %d milliseconds.";
    private String filePath;
    private File file;
    private BufferedImage image;

    /**
     * @param path the path of the file containing a 24bit BMP picture.
     * @throws IllegalArgumentException if the path is empty or null.
     */
    ImageFileReading(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be empty or null!");
        }
        filePath = path;
    }

    /**
     * @param file the file containing a 24bit BMP picture.
     * @throws IllegalArgumentException if the file is null.
     */
    ImageFileReading(File file) {
        if (file == null) {
            throw new IllegalArgumentException("Input file is null!");
        }
        this.file = file;
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

    @Override
    public void printProcessingTime() {
        printProcessingTime(FORMAT);
    }

    /**
     * This method opens the file at the given path {@link #filePath} or uses the given {@link #file} and tries to
     * read an image into the BufferedImage {@link #image}.
     * The input file must have the extension bmp, otherwise this will fail.
     */
    @Override
    public void execute() {
        File file;
        if (this.file == null) {
            file = new File(filePath);
        } else {
            file = this.file;
        }

        String extension = "";
        int i = file.getPath().lastIndexOf('.');
        if (i > 0) { // get the extension
            extension = file.getPath().substring(i + 1);
        }
        if (extension.equals("bmp")) {
            setFinishedSuccessfully(true);
        } else {
            System.err.println("The file at " + file.getPath() + " does not have the extension bmp!");
            setFinishedSuccessfully(false);
            return;
        }

        try {
            image = ImageIO.read(file);
            if (image.getColorModel().getPixelSize() != 24) {
                System.err.println("The file at " + file.getPath() + " is not using a 24 bit channel!");
                setFinishedSuccessfully(false);
                return;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
