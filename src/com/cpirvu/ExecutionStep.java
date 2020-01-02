package com.cpirvu;

/**
 * An ExecutionStep is the smallest unit of processing power that can be ran at a specific time.
 */
abstract class ExecutionStep implements PrintableExecution {
    /**
     * The time in millis when the processor started executing this.
     */
    private long executionStartingTimeInMillis;

    /**
     * Boolean stating whether or not this step finished successfully.
     * If the execution did not end, this will be set to false.
     */
    private boolean finishedSuccessfully;

    /**
     * The call to this constructor should be using {@link System#currentTimeMillis()} as parameter, at
     * the moment of execution start.
     *
     * @param executionStartingTimeInMillis the execution time start.
     */
    ExecutionStep(long executionStartingTimeInMillis) {
        if (executionStartingTimeInMillis <= 0) {
            throw new IllegalArgumentException("The argument cannot be lesser than or equal to zero!");
        }
        this.executionStartingTimeInMillis = executionStartingTimeInMillis;
    }

    public long getExecutionStartingTimeInMillis() {
        return executionStartingTimeInMillis;
    }

    public boolean isFinishedSuccessfully() {
        return finishedSuccessfully;
    }

    public void setFinishedSuccessfully(boolean finishedSuccessfully) {
        this.finishedSuccessfully = finishedSuccessfully;
    }

    /**
     * Method that prints info about this execution step, following the given format.
     *
     * @param format the format containing at least one %d tag, denoting the place in the string where to
     *               place the processing time of this step
     */
    public void printProcessingTime(String format) {
        if (!format.contains("%d")) {
            throw new IllegalArgumentException("The format must contain at least a %d tag!");
        }
        System.out.printf(format, System.currentTimeMillis() - executionStartingTimeInMillis);
        System.out.println();
    }

    /**
     * The smallest unit of processing that can be done in a step.
     */
    public abstract void execute();


    @Override
    public long getProcessingTime() {
        return System.currentTimeMillis() - this.executionStartingTimeInMillis;
    }
}
