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

        while(value.length()<maxLength)
            value += (char) 160;

        return value.getBytes(StandardCharsets.ISO_8859_1);
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

        // This is a bit goofy, but this will work for now.   1541 and 1571 and 1581
        if (sector == 0 || sector == 8  || sector == 16 || sector == 24 || sector == 32)   maskingBit =        0b00000001;
        if (sector == 1 || sector == 9  || sector == 17 || sector == 25 || sector == 33)   maskingBit =        0b00000010;
        if (sector == 2 || sector == 10 || sector == 18 || sector == 26 || sector == 34)   maskingBit =        0b00000100;
        if (sector == 3 || sector == 11 || sector == 19 || sector == 27 || sector == 35)   maskingBit =        0b00001000;
        if (sector == 4 || sector == 12 || sector == 20 || sector == 28 || sector == 36)   maskingBit =        0b00010000;
        if (sector == 5 || sector == 13 || sector == 21 || sector == 29 || sector == 37)   maskingBit =        0b00100000;
        if (sector == 6 || sector == 14 || sector == 22 || sector == 30 || sector == 38)   maskingBit =        0b01000000;
        if (sector == 7 || sector == 15 || sector == 23 || sector == 31 || sector == 39)   maskingBit = (byte) 0b10000000;

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
        if (lowestMemoryAddress==0 && highestMemoryAddress==0) return null;     // There is no binary data.

        byte[] binaryToWrite = Arrays.copyOfRange(c64RAM, lowestMemoryAddress, highestMemoryAddress);

        if (storeStartAddress)
        {
            byte[] temp = new byte[binaryToWrite.length + 2];
            ByteLogic.copyIntoRawBytes(temp, binaryToWrite, 2);
            temp[0] = (byte) (lowestMemoryAddress & 255);
            temp[1] = (byte) (lowestMemoryAddress / 256);
            binaryToWrite = temp;
        }

        return new BinaryFile(storeStartAddress ? lowestMemoryAddress : null, binaryToWrite);
    }

    /**
     * Bits 0-3	File Type (DEL, SEQ, PRG etc.)
     *        4	Unused
     *        5	Used only during SAVE-@ replacement
     *        6	Locked flag (Set produces ">" locked files)
     *        7	Closed flag (Not set produces "*", or "splat" files)   (Always 1)
     * @param fileType lowercase string of the file type (del, seq, prg etc..)
     * @param isSoftwareLocked puts a > next to the file in the directory, and it cannot be messed with
     * @return byte to be used when creating the directory entry for the file
     */
    public static byte convertToFileTypeByte(String fileType, boolean isSoftwareLocked)
    {
        byte                            fileTypeByte = 0b00000000;  // DEL
        if (fileType.equals("seq"))     fileTypeByte = 0b00000001;  // SEQ
        if (fileType.equals("prg"))     fileTypeByte = 0b00000010;  // PRG
        if (fileType.equals("usr"))     fileTypeByte = 0b00000011;  // USR
        if (fileType.equals("rel"))     fileTypeByte = 0b00000100;  // USR

        if (isSoftwareLocked)           fileTypeByte = (byte) (fileTypeByte | 0b01000000);  // Set bit 6
        fileTypeByte                                 = (byte) (fileTypeByte | 0b10000000);  // Set bit 7

        return fileTypeByte;
    }

}
