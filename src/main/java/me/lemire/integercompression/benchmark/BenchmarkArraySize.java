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

public class BenchmarkArraySize extends BenchmarkBase
{
    public BenchmarkArraySize() {
        super("arraysize");
    }

    public DataGenerator[] getDataGenerators() {
        return new DataGenerator[] {
            new RandomGenerator(0, 8 * 1024, 1280, 1 << 20, 1 << 10),
            new RandomGenerator(0, 20, 1 << 20, 1 << 20, 1 << 10),
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
        new BenchmarkArraySize().run();
    }
}
