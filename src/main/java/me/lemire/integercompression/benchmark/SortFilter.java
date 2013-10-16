/**
 * This code is released under the
 * Apache License Version 2.0 http://www.apache.org/licenses/.
 */
package me.lemire.integercompression.benchmark;

import java.util.Arrays;

public class SortFilter extends DataFilter
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
