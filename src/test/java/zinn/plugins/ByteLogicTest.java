package zinn.plugins;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ByteLogicTest
{
    @Test
    public void convertToFileTypeByte()
    {
        // In Java, bytes are signed.  Add 256 to a negative byte value to a positive integer.

        // Bit 7 is always set (Bit 7 = File is properly closed)
        // Not Software Locked.  Binary would be like this:  100000xxx
        assertThat( ByteLogic.convertToFileTypeByte("del", false) + 256).isEqualTo( 0 + 128 );
        assertThat( ByteLogic.convertToFileTypeByte("seq", false) + 256).isEqualTo( 1 + 128 );
        assertThat( ByteLogic.convertToFileTypeByte("prg", false) + 256).isEqualTo( 2 + 128);
        assertThat( ByteLogic.convertToFileTypeByte("usr", false) + 256).isEqualTo( 3 + 128);
        assertThat( ByteLogic.convertToFileTypeByte("rel", false) + 256).isEqualTo( 4 + 128);

        // Bit 7 is always set (Bit 7 = File is properly closed)
        // Software Locked (bit 6 is set).  Binary would be like this:  110000xxx
        assertThat( ByteLogic.convertToFileTypeByte("del", true) + 256).isEqualTo( 0 + 192 );
        assertThat( ByteLogic.convertToFileTypeByte("seq", true) + 256).isEqualTo( 1 + 192 );
        assertThat( ByteLogic.convertToFileTypeByte("prg", true) + 256).isEqualTo( 2 + 192);
        assertThat( ByteLogic.convertToFileTypeByte("usr", true) + 256).isEqualTo( 3 + 192);
        assertThat( ByteLogic.convertToFileTypeByte("rel", true) + 256).isEqualTo( 4 + 192);
    }
}
