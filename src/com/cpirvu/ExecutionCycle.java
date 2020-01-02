package com.cpirvu;

import java.io.File;

/**
 * This class is a single execution cycle that completely processes a single file.
 * It also implements the {@link Runnable} interface to help in multi-thread scenarios.
 */
class ExecutionCycle implements PrintableExecution, Runnable {
    /**
     * Single shared lock of ExecutionCycle calls.
     * This is used so that the output (calls to {@link System#out} print()) is synchronized and human intelligible.
     */
    private static final Object LOCK = new Object();

    /**
     * This will count all the cycles in the program.
     */
    private static int COUNT = 0;

    /**
     * This states the number of the current cycle.
     */
    private int currentCycle;

    /**
     * The start of this execution cycle.
     */
    private long executionStartingTimeInMillis;

    /**
     * The end of this execution cycle.
     */
    private long executionEndingTimeInMillis;

    /**
     * Boolean stating whether this cycle is running or not.
     */
    private boolean running;

    /**
     * Boolean marking if this cycle has ever been called.
     */
    private boolean neverCalled = true;

    private long fileReadDurationInMillis;

    private long imageBinarizationDurationInMillis;

    private long fileWriteDurationInMillis;

    private String filePath;
    private File file;

    ExecutionCycle(String filePath) {
        currentCycle = COUNT++;
        this.filePath = filePath;
    }

    ExecutionCycle(File file) {
        currentCycle = COUNT++;
        this.file = file;
    }

    public long getExecutionStartingTimeInMillis() {
        return executionStartingTimeInMillis;
    }

    public long getExecutionEndingTimeInMillis() {
        return executionEndingTimeInMillis;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isNeverCalled() {
        return neverCalled;
    }

    public long getFileReadDurationInMillis() {
        return fileReadDurationInMillis;
    }

    public void setFileReadDurationInMillis(long fileReadDurationInMillis) {
        this.fileReadDurationInMillis = fileReadDurationInMillis;
    }

    public long getImageBinarizationDurationInMillis() {
        return imageBinarizationDurationInMillis;
    }

    public void setImageBinarizationDurationInMillis(long imageBinarizationDurationInMillis) {
        this.imageBinarizationDurationInMillis = imageBinarizationDurationInMillis;
    }

    public long getFileWriteDurationInMillis() {
        return fileWriteDurationInMillis;
    }

    public void setFileWriteDurationInMillis(long fileWriteDurationInMillis) {
        this.fileWriteDurationInMillis = fileWriteDurationInMillis;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Method that prints info about the current execution cycle.
     * @throws IllegalStateException if this cycle never been called
     */
    @Override
    public void printProcessingTime() {
        if (neverCalled) {
            throw new IllegalStateException("This cycle has never run!");
        }
        System.out.printf("[Execution Cycle: " + currentCycle + "] Finished. This execution cycle took %d ms.", executionEndingTimeInMillis - executionStartingTimeInMillis);
        System.out.println();
    }

    /**
     * @return the total processing time of this cycle.
     * @throws IllegalStateException if this cycle has never been called or it is still running.
     */
    @Override
    public long getProcessingTime() {
        if (running) {
            throw new IllegalStateException("This cycle is still running!");
        }
        if (neverCalled) {
            throw new IllegalStateException("This cycle has never run!");
        }
        return executionEndingTimeInMillis - executionStartingTimeInMillis;
    }

    /**
     * When this cycle is not running, it will call {@link ExecutionCycle#getProcessingTime()}.
     *
     * @return the current processing time of this cycle.
     */
    public long getProcessingTimeUntilNow() {
        if (running) {
            return System.currentTimeMillis() - executionStartingTimeInMillis;
        } else {
            return getProcessingTime();
        }
    }

    /**
     * This method will chain the executions to completely transform a file from Grayscale to Binary.
     * The steps consist of calling the following methods in order:
     * - {@link ImageFileReading#execute()}
     * - {@link ImageBinarization#execute()}
     * - {@link ImageFileWriting#execute()}
     * If any of them fail to complete successfully, then an error message will be printed that will point at the exact
     * failed step.
     */
    @Override
    public void run() {
        printExecutionCycle("Started.");
        running = true;
        this.executionStartingTimeInMillis = System.currentTimeMillis();

        if (neverCalled) {
            // the first time we run this cycle, we update the neverCalled variable
            neverCalled = false;
        }

        ImageExecutionStep imageProcessing;
        ImageFileExecutionStep write;
        ImageFileExecutionStep read;

        if (file == null) {
            read = new ImageFileReading(filePath);
        } else {
            read = new ImageFileReading(file);
        }

        this.fileReadDurationInMillis = executeStepAndReturnProcessingTime(read);

        if (read.isFinishedSuccessfully()) {
            imageProcessing = new ImageBinarization(read.getImage());
            this.imageBinarizationDurationInMillis = executeStepAndReturnProcessingTime(imageProcessing);
            if (imageProcessing.isFinishedSuccessfully()) {
                // path is validated as '.bmp', we're going to remove it now
                if (file == null) {
                    write = new ImageFileWriting(
                            imageProcessing.getImage(),
                            new File(filePath.substring(0, filePath.length() - 4) + "_BINARIZED.bmp"));
                } else {
                    write = new ImageFileWriting(
                            imageProcessing.getImage(),
                            new File(file.getPath().substring(0, file.getPath().length() - 4) + "_BINARIZED.bmp"));
                }

                this.fileWriteDurationInMillis = executeStepAndReturnProcessingTime(write);
                if (!write.isFinishedSuccessfully()) {
                    printExecutionCycle("Failed in writing image file!");
                    return;
                }
            } else {
                printExecutionCycle("Failed in processing image!");
                return;
            }
        } else {
            printExecutionCycle("Failed in reading image file!");
            return;
        }

        this.executionEndingTimeInMillis = System.currentTimeMillis();
        running = false;

        synchronized (LOCK) {
            printProcessingTime();
        }
    }

    /**
     * Method that helps in printing the current cycle number followed by the given string.
     *
     * @param string the string in front of which the current execution cycle number must be printed
     */
    private void printExecutionCycle(String string) {
        System.out.println("[Execution Cycle: " + currentCycle + "] " + string);
    }

    /**
     * Method that prints the current execution cycle number.
     * To be used followed by specific events.
     */
    private void printExecutionCycle() {
        System.out.print("[Execution Cycle: " + currentCycle + "] ");
    }

    /**
     * This method will execute the given {@link ExecutionStep}, print and return it's processing time.
     *
     * @param step the {@link ExecutionStep} to execute.
     * @return long containing the processing time of this step.
     */
    private long executeStepAndReturnProcessingTime(ExecutionStep step) {
        step.execute();

        if (step.isFinishedSuccessfully()) {
            synchronized (LOCK) {
                printExecutionCycle();
                step.printProcessingTime();
            }
        }

        return step.getProcessingTime();
    }
}
