/**
 * This code is released under the
 * Apache License Version 2.0 http://www.apache.org/licenses/.
 */
package me.lemire.integercompression.benchmark;

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

    public static void main(String[] args) throws Exception {
        new BenchmarkOffsettedSeries2().run();
    }
}
