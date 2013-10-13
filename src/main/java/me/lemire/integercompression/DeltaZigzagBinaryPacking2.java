/*
 * This code is released under the
 * Apache License Version 2.0 http://www.apache.org/licenses/.
 */

package me.lemire.integercompression;

/**
 * BinaryPacking with Delta+Zigzag Encoding.
 *
 * @author MURAOKA Taro http://github.com/koron
 */
public final class DeltaZigzagBinaryPacking2 implements IntegerCODEC
{
    public static final int BLOCK_LENGTH = 128;

    public static class Param {
        public int length;
        public int numOfBlocks;
        public int lenOfLastBlock;

        public Param(int length) {
            this.length = length;
            length -= 1;
            this.numOfBlocks = length / BLOCK_LENGTH;
            this.lenOfLastBlock = length % BLOCK_LENGTH;
        }
    }

    @Override
    public void compress(
            int[] inBuf, IntWrapper inPos, int inLen,
            int[] outBuf, IntWrapper outPos)
    {
        if (inLen == 0) {
            return;
        }

        // Output length of original array.
        int op = outPos.get();
        outBuf[op++] = inLen;

        // Output first int, and set it as delta's initial context.
        int ip = inPos.get();
        DeltaZigzagEncoding.Encoder ctx = new DeltaZigzagEncoding.Encoder(
                outBuf[op++] = inBuf[ip++]);
        int[] work = new int[BLOCK_LENGTH];

        // Compress intermediate blocks.
        Param p = new Param(inLen);
        for (int i = p.numOfBlocks; i > 0; --i) {
            op = compressBlock(ctx, inBuf, ip, outBuf, op, work);
            ip += BLOCK_LENGTH;
        }

        // Compress last block.
        if (p.lenOfLastBlock > 0) {
            int[] last = new int[BLOCK_LENGTH];
            System.arraycopy(inBuf, ip, last, 0, p.lenOfLastBlock);
            op = compressBlock(ctx, last, 0, outBuf, op, work);
            ip += p.lenOfLastBlock;
        }

        // Update positions.
        inPos.set(ip);
        outPos.set(op);
    }

    @Override
    public void uncompress(
            int[] inBuf, IntWrapper inPos, int inLen,
            int[] outBuf, IntWrapper outPos)
    {
        if (inLen == 0) {
            return;
        }

        // Fetch length of original array.
        int ip = inPos.get();
        final int outLen = inBuf[ip++];

        // Fetch and output first int, and set it as delta's initial context.
        int op = outPos.get();
        DeltaZigzagEncoding.Decoder ctx = new DeltaZigzagEncoding.Decoder(
                outBuf[op++] = inBuf[ip++]);
        int[] work = new int[BLOCK_LENGTH];

        // Uncompress intermediate blocks.
        Param p = new Param(outLen);
        for (int i = p.numOfBlocks; i > 0; --i) {
            ip = uncompressBlock(ctx, inBuf, ip, outBuf, op, work);
            op += BLOCK_LENGTH;
        }

        // Uncompress last block.
        if (p.lenOfLastBlock > 0) {
            int[] last = new int[BLOCK_LENGTH];
            ip = uncompressBlock(ctx, inBuf, ip, last, 0, work);
            System.arraycopy(last, 0, outBuf, op, p.lenOfLastBlock);
            op += p.lenOfLastBlock;
        }

        // Update positions.
        inPos.set(ip);
        outPos.set(op);
    }

    public static int compressBlock(
            DeltaZigzagEncoding.Encoder ctx,
            int[] inBuf, int inOff,
            int[] outBuf, int outOff,
            int[] work)
    {
        ctx.encodeArray(inBuf, inOff, BLOCK_LENGTH, work);
        final int bits1 = Util.maxbits32(work,  0);
        final int bits2 = Util.maxbits32(work, 32);
        final int bits3 = Util.maxbits32(work, 64);
        final int bits4 = Util.maxbits32(work, 96);
        outBuf[outOff++] = (bits1 << 24) | (bits2 << 16) |
            (bits3 << 8) | (bits4 << 0);
        outOff += pack(work,  0, outBuf, outOff, bits1);
        outOff += pack(work, 32, outBuf, outOff, bits2);
        outOff += pack(work, 64, outBuf, outOff, bits3);
        outOff += pack(work, 96, outBuf, outOff, bits4);
        return outOff;
    }

    public static int uncompressBlock(
            DeltaZigzagEncoding.Decoder ctx,
            int[] inBuf, int inOff,
            int[] outBuf, int outOff,
            int[] work)
    {
        int n = inBuf[inOff++];
        inOff += unpack(inBuf, inOff, work,  0, (n >> 24) & 0x3F);
        inOff += unpack(inBuf, inOff, work, 32, (n >> 16) & 0x3F);
        inOff += unpack(inBuf, inOff, work, 64, (n >>  8) & 0x3F);
        inOff += unpack(inBuf, inOff, work, 96, (n >>  0) & 0x3F);
        ctx.decodeArray(work, 0, BLOCK_LENGTH, outBuf, outOff);
        return inOff;
    }

    public static int pack(int[] inBuf, int inOff,
            int[] outBuf, int outOff, int validBits)
    {
        BitPacking.fastpackwithoutmask(inBuf, inOff, outBuf, outOff,
                validBits);
        return validBits;
    }

    public static int unpack(int[] inBuf, int inOff,
            int[] outBuf, int outOff, int validBits)
    {
        BitPacking.fastunpack(inBuf, inOff, outBuf, outOff, validBits);
        return validBits;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
