package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * MultiThreaded calculator on matrix.
 */
public final class MultiThreadedMatrixSumClassic implements SumMatrix {

    private final int nThread;

    /**
     * Build a MultiThreaded calculator.
     * 
     * @param nWorker n° of threads
     */
    public MultiThreadedMatrixSumClassic(final int nWorker) {
        this.nThread = nWorker;
    }

    @Override
    public double sum(final double[][] matrix) {
        final int rows = matrix.length;
        final int size = rows % nThread + rows / nThread;

        final List<Worker> workers = new ArrayList<>(nThread);
        for (int start = 0; start < rows; start += size) {
            workers.add(new Worker(matrix, start, size));
        }

        for (final Worker w : workers) {
            w.start();
        }

        double sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }

    private final class Worker extends Thread {
        private final double[][] doubleMatrix;
        private final int startRow;
        private final int nRows;
        private double result;

        /**
         * Build a new Worker.
         * 
         * @param matrix   matrix on which calculate the sum
         * @param startRow start row of each worker
         * @param nRows    total rows
         */
        Worker(final double[][] matrix, final int startRow, final int nRows) {
            super();
            this.doubleMatrix = matrix; // NOPMD
            this.startRow = startRow;
            this.nRows = nRows;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public synchronized void run() {
            System.out.println("Working from row " + startRow + " to row " + (startRow + nRows - 1));
            for (int row = startRow; row < doubleMatrix.length && row < startRow + nRows; row++) {
                for (int col = 0; col < doubleMatrix[row].length; col++) {
                    result += doubleMatrix[row][col];
                }
            }
        }

        public synchronized double getResult() {
            return result;
        }
    }
}
