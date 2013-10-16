/**
 * This code is released under the
 * Apache License Version 2.0 http://www.apache.org/licenses/.
 */
package me.lemire.integercompression.benchmark;

public abstract class DataFilter extends DataGenerator {

    protected DataGenerator baseGenerator;

    protected DataFilter(DataGenerator baseGenerator) {
        this.baseGenerator = baseGenerator;
    }

    public final int[][] generate() {
        int[][] data = filter(this.baseGenerator.generate());
        this.baseGenerator.reset();
        return data;
    }

    public abstract int[][] filter(int[][] src);
}
