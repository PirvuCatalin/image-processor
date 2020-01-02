package com.cpirvu;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Class that deals with writing images to the local file system.
 */
public class ImageFileWriting extends ImageFileExecutionStep {
    public static String FORMAT = "This image file writing step took %d milliseconds.";
    private BufferedImage image;
    private File file;

    /**
     * @param image the image to be written.
     * @param file  the file containing the location to save the image at.
     */
    ImageFileWriting(BufferedImage image, File file) {
        this.image = image;
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
     * This method will write the image {@link #image} at the given location of file {@link #file}.
     */
    @Override
    public void execute() {
        try {
            ImageIO.write(image, "bmp", file);
            setFinishedSuccessfully(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
