/**
 * This code is released under the
 * Apache License Version 2.0 http://www.apache.org/licenses/.
 */
package me.lemire.integercompression.benchmark;

import me.lemire.integercompression.DeltaZigzagBinaryPacking2;
import me.lemire.integercompression.IntegerCODEC;
import me.lemire.integercompression.IntegratedBinaryPacking;
import me.lemire.integercompression.IntegratedComposition;
import me.lemire.integercompression.IntegratedVariableByte;
import me.lemire.integercompression.JustCopy;

public class BenchmarkComposed extends BenchmarkBase
{
    public BenchmarkComposed() {
        super("composed");
    }

    public DataGenerator[] getDataGenerators() {
        return new DataGenerator[] {
            new SortFilter(new RandomGenerator(
                        0, 20000, 1000, 1 << 20, 1 << 10)),
            new SortFilter(new RandomGenerator(
                        0, 20, 1000000, 1 << 20, 1 << 10)),
        };
    }

    public IntegerCODEC[] getCODECs() {
        return new IntegerCODEC[] {
            new JustCopy(),
            new DeltaZigzagBinaryPacking2(),
            new IntegratedComposition(
                    new IntegratedBinaryPacking(),
                    new IntegratedVariableByte()),
        };
    }


    public static void main(String[] args) throws Exception {
        new BenchmarkComposed().run();
    }
}
