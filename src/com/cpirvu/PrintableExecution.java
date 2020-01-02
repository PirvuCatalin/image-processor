package com.cpirvu;

/**
 * The PrintableExecution marks a class that have it's processing time printable.
 */
interface PrintableExecution {
    /**
     * Method that prints how long the implementing class has been processing a request.
     */
    void printProcessingTime();

    /**
     * Method that returns a long containing the time of how long the implementing class has been processing a request.
     *
     * @return long containing the processing time
     */
    long getProcessingTime();
}
