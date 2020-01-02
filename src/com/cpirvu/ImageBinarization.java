package com.cpirvu;

import java.awt.image.BufferedImage;

public class ImageBinarization extends ImageExecutionStep {
    public static String FORMAT = "This image binarization step took %d milliseconds.";
    public static int THRESHOLD;
    public static boolean FORCE;
    private BufferedImage binaryImage;
    private BufferedImage originalImage;

    /**
     * @param originalImage the image that is going to be binarized.
     * @throws IllegalArgumentException if the input parameter is null.
     */
    ImageBinarization(BufferedImage originalImage) {
        if (originalImage == null) {
            throw new IllegalArgumentException("Image cannot be null!");
        }
        this.originalImage = originalImage;
    }

    @Override
    public BufferedImage getImage() {
        return binaryImage;
    }

    @Override
    public void printProcessingTime() {
        printProcessingTime(FORMAT);
    }

    /**
     * Method that converts the input image to binary.
     * If the input image is not grayscale and the {@link #FORCE} flag is not set to true, then this will fail to convert.
     * If the {@link #FORCE} flag is set to true, this will also convert the input image to grayscale if needed.
     */
    @Override
    public void execute() {
        boolean grayscale = isGrayscale(originalImage);

        if (!grayscale && !FORCE) {
            System.err.println("Input image is not grayscale! Consider using -F argument to also convert to grayscale if needed!");
            setFinishedSuccessfully(false);
            return;
        }

        if (!grayscale) {
            convertToGrayscale(originalImage);
        }

        binaryImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

        int red;
        short pixel;
        for (int i = 0; i < originalImage.getWidth(); i++) {
            for (int j = 0; j < originalImage.getHeight(); j++) {
                red = originalImage.getRGB(i, j) & 0xFF; // get red value from rgb integer value
                if (red > THRESHOLD) { // can use any of the r/g/b elements, as the image is grayscale!
                    pixel = 0;
                } else {
                    pixel = 255;
                }
                int rgbAsInt = rgbToInt(pixel, pixel, pixel);
                binaryImage.setRGB(i, j, rgbAsInt);
            }
        }
        setFinishedSuccessfully(true);
    }

    /**
     * Method that converts a color from red, green, blue scheme to int.
     * Assuming the image is a 24bit channel image, the alpha component is therefore null.
     *
     * @param red   the red value
     * @param green the green value
     * @param blue  the blue value
     * @return int containing the all three elements (r, g, b) - i.e. rgb converted to integer
     */
    private int rgbToInt(short red, short green, short blue) {
        int colorAsInt = 0; // the alpha element is empty (24 bit)
        colorAsInt = (colorAsInt << 8) + red; // shift 8 bits then add the red element
        colorAsInt = (colorAsInt << 8) + green; // shift 8 bits then add the green element
        colorAsInt = (colorAsInt << 8) + blue; // shift 8 bits then add the blue element
        return colorAsInt;
    }

    /**
     * Method that checks if the input image is grayscale.
     *
     * @param image the image to check for grayscale
     * @return boolean stating whether or not the image is grayscale
     */
    private boolean isGrayscale(BufferedImage image) {
        int pixel, red, green, blue;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                pixel = image.getRGB(i, j);
                red = (pixel >> 16) & 0xff;
                green = (pixel >> 8) & 0xff;
                blue = (pixel) & 0xff;
                if (red != green || green != blue) return false;
            }
        }
        return true;
    }

    /**
     * Method that converts the input image to grayscale.
     * This uses the luminosity method - weighted average to account for human eye perception.
     *
     * @param image the image to convert to grayscale
     */
    private void convertToGrayscale(BufferedImage image) {
        int color, red, green, blue;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                color = image.getRGB(i, j);

                // ignore the alpha element as it is empty (24bit channel)
                red = (color >> 16) & 255;
                green = (color >> 8) & 255;
                blue = (color) & 255;

                final int lum = (int) (0.2126 * red + 0.7152 * green + 0.0722 * blue);

                color = (lum << 16) + (lum << 8) + lum; // red | green | blue

                image.setRGB(i, j, color);
            }
        }
    }
}
