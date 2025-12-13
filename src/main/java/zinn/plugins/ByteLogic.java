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
}
