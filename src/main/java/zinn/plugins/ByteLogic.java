package zinn.plugins;

import kickass.plugins.interf.general.IMemoryBlock;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public final class ByteLogic
{
    static int copyIntoRawBytes(byte[] intoRawBytes, byte[] bytesToMerge, int offset)
    {
        for(int n=0; n<bytesToMerge.length; n++)
            intoRawBytes[offset+n] = bytesToMerge[n];
        return offset + bytesToMerge.length;
    }

    static byte[] createShiftSpacePaddedString(String value, int maxLength)
    {
        if (value.length()>maxLength)
            value = value.substring(0,maxLength);
        return String.format("%-" + maxLength + "s",value)
                .replace(' ', (char) 160)                       // Pad with shift space ($A0 160)
                .getBytes(StandardCharsets.ISO_8859_1);
    }

    static byte[] createBytesOfChar(byte charToFill, int length)
    {
        byte[] bytes = new byte[length];
        Arrays.fill(bytes, charToFill);
        return bytes;
    }

    public static byte getSectorBAMMaskingBit(int sector)
    {
        byte maskingBit = 0;

        // This is a bit goofy, but this will work for now.
        if (sector == 0 || sector == 8  || sector == 16)   maskingBit =        0b00000001;
        if (sector == 1 || sector == 9  || sector == 17)   maskingBit =        0b00000010;
        if (sector == 2 || sector == 10 || sector == 18)   maskingBit =        0b00000100;
        if (sector == 3 || sector == 11 || sector == 19)   maskingBit =        0b00001000;
        if (sector == 4 || sector == 12 || sector == 20)   maskingBit =        0b00010000;
        if (sector == 5 || sector == 13 || sector == 21)   maskingBit =        0b00100000;
        if (sector == 6 || sector == 14 || sector == 22)   maskingBit =        0b01000000;
        if (sector == 7 || sector == 15 || sector == 23)   maskingBit = (byte) 0b10000000;

        return maskingBit;
    }

    public record BinaryFile(Integer startingAddress, byte[] rawData) {}
    public static BinaryFile convertIMemoryBlocksToBinaryFile(List<IMemoryBlock> memoryBlocks, boolean storeStartAddress)
    {
        // Unpack the memory blocks into C64 Memory
        byte[] c64RAM = new byte[65535];
        for(IMemoryBlock memoryBlock : memoryBlocks)
        {
            byte[] memoryBlockMemory = memoryBlock.getBytes();
            ByteLogic.copyIntoRawBytes(c64RAM, memoryBlockMemory, memoryBlock.getStartAddress());
        }

        // Slice out the part we want to write to disk
        int lowestMemoryAddress = memoryBlocks.stream().mapToInt(IMemoryBlock::getStartAddress).min().orElse(0);
        int highestMemoryAddress = memoryBlocks.stream().mapToInt(i -> i.getStartAddress() + i.getBytes().length).max().orElse(0);
        byte[] binaryToWrite = Arrays.copyOfRange(c64RAM, lowestMemoryAddress, highestMemoryAddress);

        if (storeStartAddress)
        {
            byte[] temp = new byte[binaryToWrite.length + 2];
            ByteLogic.copyIntoRawBytes(temp, binaryToWrite, 2);
            temp[0] = (byte) (lowestMemoryAddress & 256);
            temp[1] = (byte) (lowestMemoryAddress / 256);
            binaryToWrite = temp;
        }

        return new BinaryFile(storeStartAddress ? lowestMemoryAddress : null, binaryToWrite);
    }
}
