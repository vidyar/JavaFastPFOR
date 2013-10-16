/**
 * This code is released under the
 * Apache License Version 2.0 http://www.apache.org/licenses/.
 */
package me.lemire.integercompression.benchmark;

public class DeltaFilter extends DataFilter
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
