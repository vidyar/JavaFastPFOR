/**
 * This code is released under the
 * Apache License Version 2.0 http://www.apache.org/licenses/.
 */
package me.lemire.integercompression.benchmark;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

import me.lemire.integercompression.BinaryPacking;
import me.lemire.integercompression.DeltaZigzagBinaryPacking;
import me.lemire.integercompression.DeltaZigzagVariableByte;
import me.lemire.integercompression.FastPFOR;
import me.lemire.integercompression.IntegerCODEC;
import me.lemire.integercompression.IntegratedBinaryPacking;
import me.lemire.integercompression.JustCopy;
import me.lemire.integercompression.XorBinaryPacking;

public class BenchmarkOffsettedSeries2 extends BenchmarkBase
{
    public BenchmarkOffsettedSeries2() {
        super("offsetted2");
    }

    public DataGenerator[] getDataGenerators() {
        RandomGenerator[] r = {
            new RandomGenerator(0, 8 * 1024, 1280, 1 << 20, 1 << 10),
            new RandomGenerator(0, 8 * 1024, 1280, 1 << 20, 1 <<  5),
        };
        return new DataGenerator[] {
            r[0],
            new DeltaFilter(r[0]),
            new SortFilter(r[0]),
            new DeltaFilter(new SortFilter(r[0])),
            r[1],
            new DeltaFilter(r[1]),
            new SortFilter(r[1]),
            new DeltaFilter(new SortFilter(r[1])),
        };
    }

    public IntegerCODEC[] getCODECs() {
        return new IntegerCODEC[] {
            new JustCopy(),
            new BinaryPacking(),
            new DeltaZigzagBinaryPacking(),
            new DeltaZigzagVariableByte(),
            new IntegratedBinaryPacking(),
            new XorBinaryPacking(),
            new FastPFOR(),
        };
    }

    public static class RandomGenerator extends DataGenerator
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
            return String.format("Random(mean=%1$d range=%2$d)", this.mean,
                    this.range);
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

    public static class DeltaFilter extends DataFilter
    {
        public DeltaFilter(DataGenerator generator) {
            super(generator);
        }

        public String getName() {
            return String.format("Delta(%1$s)", this.baseGenerator.getName());
        }

        public int[][] filter(int[][] src) {
            int[][] dst = new int[src.length][];
            for (int i = 0; i < src.length; ++i) {
                int[] s = src[i];
                int[] d = dst[i] = new int[s.length];
                int prev = 0;
                for (int j = 0; j < s.length; ++j) {
                    d[j] = s[j] - prev;
                    prev = s[j];
                }
            }
            return dst;
        }
    }

    public static class SortFilter extends DataFilter
    {
        public SortFilter(DataGenerator generator) {
            super(generator);
        }

        public String getName() {
            return String.format("Sort(%1$s)", this.baseGenerator.getName());
        }

        public int[][] filter(int[][] src) {
            int[][] dst = new int[src.length][];
            for (int i = 0; i < src.length; ++i) {
                dst[i] = Arrays.copyOf(src[i], src[i].length);
                Arrays.sort(dst[i]);
            }
            return dst;
        }
    }

    public static void main(String[] args) throws Exception {
        new BenchmarkOffsettedSeries2().run();
    }
}
