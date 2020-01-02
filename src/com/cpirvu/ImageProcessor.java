package com.cpirvu;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class ImageProcessor {
    /**
     * File opened by the given input path.
     */
    private File file;

    /**
     * If set to true and the {@link #file} is a directory, then this will use {@link #numberOfThreads} threads.
     */
    private boolean multithreaded;

    /**
     * The number of threads to use. It only works if {@link #multithreaded} is set to true. Default value is 5.
     */
    private int numberOfThreads = 5;

    /**
     * This is the value from 0 - 255 of which any pixel that with a brightness greater than the value will be converted to white,
     * and any pixel with a brightness less than this value will be converted to black. Default value is 127.
     */
    private int threshold = 127;

    /**
     * If true, the input images will also be converted to grayscale first (if needed).
     */
    private boolean force;

    /**
     * Starting point of the application. Requires a specific form of the argument(s).
     *
     * @param args the CLI arguments.
     *             usage:
     *             [-M &lt;numberOfThreads&gt;] - if present, the application will use numberOfThreads only if the given path is a directory.
     *             If only the [-M] argument is present, the default number of threads is 5. If [-M] is not specified, the application will use only the main thread.
     *             [-T &lt;staticThreshold&gt;] - if present, the binarization algorithm will use the given static threshold.
     *             The [-T] argument must be followed by the static threshold. If [-T] is not specified, the default static threshold will be set to 127.
     *             -P &lt;path&gt; - mandatory argument, the path can be either an image file or a directory containing image files.
     *             The path can be relative or absolute (unix style). If containing spaces, it must be enclosed in double quotes.
     *             !!! The files must have the extension BMP and contain 24bit images!
     *             [-F] if present, the input images will also be converted to grayscale first (if needed).
     *             If the input image (or any image from the directory) is not grayscale and this flag is not present, then the program will fail.
     *             <p>
     *             If the first argument is "help", the application will only print CLI usage info.
     */
    public static void main(String[] args) {
        ImageProcessor imageProcessor = new ImageProcessor();

        // decode arguments and update the attributes of imageProcessor
        boolean continueExecution = imageProcessor.decodeArgs(args);
        if (!continueExecution) {
            return;
        }

        // set the global static threshold to use
        ImageBinarization.THRESHOLD = imageProcessor.threshold;

        // set the 'convert to grayscale if needed' flag
        ImageBinarization.FORCE = imageProcessor.force;

        if (imageProcessor.file.isFile()) {
            if (imageProcessor.multithreaded) {
                System.err.println("Argument path is a single file, rolling back to single-threaded version.");
            }
            new ExecutionCycle(imageProcessor.file).run();
        } else if (imageProcessor.file.isDirectory()) {
            File[] listOfFiles = imageProcessor.file.listFiles(file -> !file.isHidden()); //lambda filter used to ignore hidden files
            if (listOfFiles == null || listOfFiles.length == 0) {
                System.err.println("Directory is empty!");
                printError(args);
                return;
            }
            if (!imageProcessor.multithreaded) {
                System.err.println("Argument path is a directory. We recommend using [-M] argument for running this in multi-threading when processing multiple files.");
                for (File file : listOfFiles) {
                    new ExecutionCycle(file).run();
                }
            } else {
                ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(imageProcessor.numberOfThreads);
                for (File file : listOfFiles) {
                    executor.execute(new ExecutionCycle(file)); //could use submit() method, but we're not validating any result or care about the execution status
                }
                executor.shutdown(); //asking for executor shutdown after all tasks are done
            }
        }
    }

    /**
     * Basic method that prints an error message if the input arguments are invalid.
     *
     * @param args the command line arguments
     */
    private static void printError(String[] args) {
        System.err.println("Wrong input arguments: '" + Arrays.toString(args) + "' !");
        System.err.println("Type 'java com.cpirvu.ImageProcessor help' for more information.");
    }

    private void printHelp() {
        System.out.println("usage:");
        System.out.println(
                "   [-M <numberOfThreads>] - if present, the application will use numberOfThreads only if the given path is a directory.\n" +
                        "       If only the [-M] argument is present, the default number of threads is 5.\n" +
                        "       If [-M] is not specified, the application will use only the main thread.\n" +
                        "   [-T <staticThreshold>] - if present, the binarization algorithm will use the given static threshold.\n" +
                        "       The [-T] argument must be followed by the static threshold.\n" +
                        "       If [-T] is not specified, the default static threshold will be set to 127.\n" +
                        "   -P <path> - mandatory argument, the path can be either an image file or a directory containing image files.\n" +
                        "       The path can be relative or absolute (unix style). If containing spaces, it must be enclosed in double quotes.\n" +
                        "       !!! The files must have the extension BMP and contain 24bit images!\n" +
                        "   [-F] - if present, the input images will also be converted to grayscale first (if needed).\n" +
                        "       If the input image (or any image from the directory) is not grayscale and this flag is not present, then the program will fail.\n\n" +

                        "   If the first argument is 'help', the application will only print CLI usage info.");
    }

    /**
     * Method that decodes the given CLI arguments. If this methods returns false, the execution of the program should end,
     * as the input arguments were invalid.
     *
     * @param args the CLI arguments.
     * @return boolean stating whether or not the program should continue execution.
     */
    private boolean decodeArgs(String[] args) {
        if (args == null || args.length == 0) {
            printError(args);
            return false;
        }
        if (args[0].equals("help")) {
            printHelp();
            return false;
        }
        int numberOfMParameters = 0;
        int numberOfTParameters = 0;
        int numberOfFParameters = 0;
        int numberOfPParameters = 0;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-M":
                    numberOfMParameters++;
                    this.multithreaded = true;
                    if (i + 1 < args.length && args[i + 1].charAt(0) != '-') {
                        int numberOfThreads;
                        try {
                            numberOfThreads = Integer.parseInt(args[i + 1]);
                        } catch (NumberFormatException e) {
                            System.err.println("The number of threads must be a number between 1 and 255!");
                            printError(args);
                            return false;
                        }
                        if (numberOfThreads > 0 && numberOfThreads < 256) {
                            this.numberOfThreads = numberOfThreads;
                        } else {
                            System.err.println("The number of threads must be between 1 and 255. Rolling back to the default number of 5 threads.");
                        }
                        i++;
                    }
                    break;
                case "-T":
                    numberOfTParameters++;
                    if (i + 1 < args.length && args[i + 1].charAt(0) != '-') {
                        int threshold;
                        try {
                            threshold = Integer.parseInt(args[i + 1]);
                        } catch (NumberFormatException e) {
                            System.err.println("The static threshold must be a number between 0 and 255.");
                            printError(args);
                            return false;
                        }
                        if (threshold >= 0 && threshold <= 255) {
                            this.threshold = threshold;
                        } else {
                            System.err.println("The static threshold must be between 0 and 255. Rolling back to the default threshold of 127.");
                        }
                        i++;
                    } else {
                        System.err.println("If the [-T] argument is present, it must be followed by the static threshold!");
                        printError(args);
                        return false;
                    }
                    break;
                case "-F":
                    numberOfFParameters++;
                    this.force = true;
                    break;
                case "-P":
                    numberOfPParameters++;
                    if (i + 1 < args.length && args[i + 1].charAt(0) != '-') {
                        File file = new File(args[i + 1]);
                        if (!file.exists()) {
                            System.err.println("The given path doesn't exist! Make sure to escape special characters!");
                            printError(args);
                            return false;
                        }
                        this.file = file;

                        i++;
                    } else {
                        System.err.println("The [-P] argument must be followed by the file path (relative or absolute, unix style)!");
                        printError(args);
                        return false;
                    }
                    break;
                default:
                    System.err.println("Couldn't recognize parameter: '" + args[i] + "' !");
                    printError(args);
                    return false;
            }
        }

        if (numberOfMParameters > 1 ||
                numberOfTParameters > 1 ||
                numberOfFParameters > 1 ||
                numberOfPParameters > 1) {
            // error in case of duplicate parameters
            printError(args);
            return false;
        }

        if (numberOfPParameters != 1) {
            //error if mandatory parameter is not present
            System.err.println("Mandatory parameter [-P] is not present!");
            printError(args);
            return false;
        }

        return true;
    }
}