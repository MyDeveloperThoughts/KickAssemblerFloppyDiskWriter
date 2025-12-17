package zinn.plugins;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class Disk1581Test
{
    @Test()
    void test1581BAM()
    {
        Disk disk = Disk.createFormattedDisk("test.d81", "STUFF", "CZ", "1581");

        // All sectors on track 40 0-2 should be marked used on a freshly formatted disk
        for(int n=0; n<=3; n++)
            assertThat(disk.isTrackSectorAvailable(40, n)).isFalse();

        assertThat(disk.isTrackSectorAvailable(1, 24)).as( String.format("%d:%d should be marked available, and is not.", 1, 24) ).isTrue();

        // All tracks 1-39 and sectors 0-39 should be available
        for(int track=1;track<=39;track++)
            for(int sector=0;sector<=39;sector++)
                assertThat(disk.isTrackSectorAvailable(track, sector)).as( String.format("%d:%d should be marked available, and is not.", track, sector) ).isTrue();

        // All tracks 41-80 and sectors 0-39 should be available
        for(int track=41;track<=80;track++)
            for(int sector=0;sector<=39;sector++)
                assertThat(disk.isTrackSectorAvailable(track, sector)).as( String.format("%d:%d should be marked available, and is not.", track, sector) ).isTrue();
    }
}
