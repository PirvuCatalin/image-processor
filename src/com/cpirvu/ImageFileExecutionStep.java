package com.cpirvu;

import java.awt.image.BufferedImage;

/**
 * This class stand only as a template for working with image files.
 */
abstract class ImageFileExecutionStep extends ExecutionStep {
    public static String FORMAT = "This file processing step took %d milliseconds.";

    ImageFileExecutionStep() {
        super(System.currentTimeMillis());
    }

    public abstract BufferedImage getImage();
}
