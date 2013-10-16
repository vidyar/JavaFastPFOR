/**
 * This code is released under the
 * Apache License Version 2.0 http://www.apache.org/licenses/.
 */
package me.lemire.integercompression.benchmark;

import java.util.Random;

public class SineGenerator extends DataGenerator
{
    private long seed;
    private int count;
    private int length;
    private int mean;
    private int range;
    private int freq;

    public SineGenerator(long seed, int count, int length,
            int mean, int range, int freq)
    {
        this.seed   = seed;
        this.count  = count;
        this.length = length;
        this.mean   = mean;
        this.range  = range;
        this.freq  = freq;
    }

    public String getName() {
        return String.format(
                "Sine(count=%3$d length=%4$d mean=%1$d range=%2$d freq=%5$d)",
                this.mean, this.range, this.count, this.length, this.freq);
    }

    public int[][] generate() {
        int[][] chunks = new int[this.count][];
        Random r = new Random(this.seed);
        for (int i = 0; i < this.count; ++i) {
            int[] chunk = chunks[i] = new int[this.length];
            int phase = r.nextInt(2 * this.freq);
            for (int j = 0; j < this.length; ++j) {
                double angle = 2.0 * Math.PI * (j + phase) / this.freq;
                chunk[j] = (int)(this.mean + Math.sin(angle) * this.range);
            }
        }
        return chunks;
    }
}
