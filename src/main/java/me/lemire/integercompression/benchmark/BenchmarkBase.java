/**
 * This code is released under the
 * Apache License Version 2.0 http://www.apache.org/licenses/.
 */
package me.lemire.integercompression.benchmark;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.IntegerCODEC;
import me.lemire.integercompression.PerformanceLogger;

public abstract class BenchmarkBase
{
    public static final int DEFAULT_WARMUP = 2;
    public static final int DEFAULT_REPEAT = 5;

    public abstract DataGenerator[] getDataGenerators();

    public abstract IntegerCODEC[] getCODECs();

    private final String name;

    protected BenchmarkBase(String name) {
        this.name = name;
    }

    public void run() throws IOException {
        File csvFile = new File(String.format(
                    "benchmark-%2$s-%1$tY%1$tm%1$tdT%1$tH%1$tM%1$tS.csv",
                    System.currentTimeMillis(), this.name));
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(csvFile);
            System.out.println("# Results will be written into a CSV file: "
                    + csvFile.getName());
            System.out.println();
            runAll(writer);
            System.out.println();
            System.out.println("# Results were written into a CSV file: "
                    + csvFile.getName());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void runAll(PrintWriter w) {
        w.format("\"Dataset\",\"CODEC\",\"Bits per int\"," +
                "\"Compress speed (MiS)\",\"Decompress speed (MiS)\"\n");
        DataGenerator[] generators = getDataGenerators();
        IntegerCODEC[] codecs = getCODECs();
        for (DataGenerator g : generators) {
            System.out.println("Processing: " + g.getName());
            for (IntegerCODEC c : codecs) {
                benchmark(w, g, c, DEFAULT_WARMUP);
            }
            g.reset();
        }
    }

    private void benchmark(
            PrintWriter w,
            DataGenerator g,
            IntegerCODEC c,
            int warmup)
    {
        System.gc();
        for (int i = 0; i < warmup; ++i) {
            benchmark2(null, g, c);
        }
        benchmark2(w, g, c);
    }

    private void benchmark2(
            PrintWriter w,
            DataGenerator g,
            IntegerCODEC c)
    {
        int[][] data = g.getData();
        int repeat = g.getRepeat();

        PerformanceLogger logger = new PerformanceLogger();

        int maxLen = getMaxLen(data);
        int[] compressBuffer = new int[4 * maxLen + 1024];
        int[] decompressBuffer = new int[maxLen];

        for (int i = 0; i < repeat; ++i) {
            for (int[] array : data) {
                int compSize = compress(logger, c, array, compressBuffer);
                int decompSize = decompress(logger, c, compressBuffer,
                        compSize, decompressBuffer);
                checkArray(array, decompressBuffer, decompSize, c);
            }
        }

        if (w != null) {
            w.format("\"%1$s\",\"%2$s\",%3$.2f,%4$.0f,%5$.0f\n",
                    g.getName(), c.toString(), logger.getBitPerInt(),
                    logger.getCompressSpeed(), logger.getDecompressSpeed());
        }
    }

    public static int getMaxLen(int[][] data) {
        int maxLen = 0;
        for (int[] array : data) {
            if (array.length > maxLen) {
                maxLen = array.length;
            }
        }
        return maxLen;
    }

    public static int compress(
            PerformanceLogger logger,
            IntegerCODEC codec,
            int[] src,
            int[] dst)
    {
        IntWrapper inpos = new IntWrapper();
        IntWrapper outpos = new IntWrapper();
        logger.compressionTimer.start();
        codec.compress(src, inpos, src.length, dst, outpos);
        logger.compressionTimer.end();
        int outSize = outpos.get();
        logger.addOriginalSize(src.length);
        logger.addCompressedSize(outSize);
        return outSize;
    }

    public static int decompress(
            PerformanceLogger logger,
            IntegerCODEC codec,
            int[] src,
            int srcLen,
            int[] dst)
    {
        IntWrapper inpos = new IntWrapper();
        IntWrapper outpos = new IntWrapper();
        logger.decompressionTimer.start();
        codec.uncompress(src, inpos, srcLen, dst, outpos);
        logger.decompressionTimer.end();
        return outpos.get();
    }

    public static void checkArray(
            int[] expected,
            int[] actualArray,
            int actualLen,
            IntegerCODEC codec)
    {
        if (actualLen != expected.length) {
            throw new RuntimeException("Length mismatch:" +
                    " expected=" + expected.length + " actual=" + actualLen +
                    " codec=" + codec.toString());
        }
        for (int i = 0; i < expected.length; ++i) {
            if (actualArray[i] != expected[i]) {
                throw new RuntimeException("Value mismatch: " +
                        " where=" + i + " expected=" + expected[i] +
                        " actual=" + actualArray[i] +
                        " codec=" + codec.toString());
            }
        }
    }
}
