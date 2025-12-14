package zinn.plugins;

import java.util.ArrayList;
import java.util.List;

public final class DiskImageLogic
{
    public record TrackInfo(int trackNumber, int sectorCount, int offset, String offsetInHex)
    {
        int getNextOffset()
        {
            return offset + (256 * sectorCount);
        }
    }

    static List<TrackInfo> createTrackInfo(int startingTrackingNumber, int trackCount, int sectorsPerTrack, int startingOffset)
    {
        List<TrackInfo> tracks = new ArrayList<>(trackCount);

        int offset = startingOffset;
        for(int trackNumber=startingTrackingNumber;trackNumber<(trackCount + startingTrackingNumber);trackNumber++)
        {
            tracks.add(new TrackInfo(trackNumber, sectorsPerTrack, offset, offset == 0 ? "$0000" : String.format("$%4H", offset)));
            offset += (sectorsPerTrack * 256);
        }

        return tracks;
    }



}
