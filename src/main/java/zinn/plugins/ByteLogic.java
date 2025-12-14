package zinn.plugins;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
}
