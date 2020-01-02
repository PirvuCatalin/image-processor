package com.cpirvu;

import java.awt.image.BufferedImage;

/**
 * This class stand only as a template for working with image processing.
 */
abstract class ImageExecutionStep extends ExecutionStep {
    public static String FORMAT = "This image processing step took %d milliseconds.";

    ImageExecutionStep() {
        super(System.currentTimeMillis());
    }

    public abstract BufferedImage getImage();
}
