package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public final class MultiThreadedMatrixSumClassic implements SumMatrix {

    private final int nThread;

    public MultiThreadedMatrixSumClassic(final int nWorker) {
        this.nThread = nWorker;
    }

    @Override
    public double sum(double[][] matrix) {
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
         * @param matrix
         * @param startRow
         * @param nRows
         */
        public Worker(final double[][] matrix, final int startRow, final int nRows) {
            super();
            this.doubleMatrix = matrix;
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
