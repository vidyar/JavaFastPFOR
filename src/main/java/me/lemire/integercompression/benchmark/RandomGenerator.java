/**
 * This code is released under the
 * Apache License Version 2.0 http://www.apache.org/licenses/.
 */
package me.lemire.integercompression.benchmark;

import java.util.Random;

public class RandomGenerator extends DataGenerator
{
    private long seed;
    private int count;
    private int length;
    private int mean;
    private int range;

    public RandomGenerator(long seed, int count, int length,
            int mean, int range)
    {
        this.seed   = seed;
        this.count  = count;
        this.length = length;
        this.mean   = mean;
        this.range  = range;
    }

    public String getName() {
        return String.format(
                "Random(count=%3$d length=%4$d mean=%1$d range=%2$d)",
                this.mean, this.range, this.count, this.length);
    }

    public int[][] generate() {
        int offset = this.mean - this.range / 2;
        int[][] chunks = new int[this.count][];
        Random r = new Random(this.seed);
        for (int i = 0; i < this.count; ++i) {
            int[] chunk = chunks[i] = new int[this.length];
            for (int j = 0; j < this.length; ++j) {
                chunk[j] = r.nextInt(this.range) + offset;
            }
        }
        return chunks;
    }
}
