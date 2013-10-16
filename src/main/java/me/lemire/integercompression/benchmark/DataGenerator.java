/**
 * This code is released under the
 * Apache License Version 2.0 http://www.apache.org/licenses/.
 */
package me.lemire.integercompression.benchmark;

public abstract class DataGenerator {

    private int[][] data;

    public final int[][] getData() {
        if (this.data == null) {
            this.data = generate();
        }
        return this.data;
    }

    public final void reset() {
        this.data = null;
    }

    public abstract String getName();

    public abstract int[][] generate();

    public int getRepeat() {
        return BenchmarkBase.DEFAULT_REPEAT;
    }
}
