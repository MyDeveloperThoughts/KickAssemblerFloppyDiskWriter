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

    static Disk createBareDiskImage(String name, String id, String fileName, String driveType)
    {
        if (driveType.equals("1571")) return createBareDiskImage1571(name, id, fileName);
        if (driveType.equals("1581")) return createBareDiskImage1581(name, id, fileName);

        return createBareDiskImage1541(name, id, fileName);
    }

    static Disk createBareDiskImage1541(String name, String id, String fileName)
    {
        List<TrackInfo> tracks = new ArrayList<>(35);
        tracks.addAll(createTrackInfo(1, 17, 21, 0));
        tracks.addAll(createTrackInfo(18, 7, 19, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(25, 6, 18, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(31, 5, 17, tracks.getLast().getNextOffset()));
        return new Disk(fileName, name, id, "1541", List.copyOf(tracks));
    }

    static Disk createBareDiskImage1571(String name, String id, String fileName)
    {
        List<TrackInfo> tracks = new ArrayList<>(70);
        tracks.addAll(createTrackInfo(1, 17, 21, 0));
        tracks.addAll(createTrackInfo(18, 7, 19, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(25, 6, 18, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(31, 5, 17, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(36, 17, 21, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(53, 7, 19, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(60, 6, 18, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(66, 5, 17, tracks.getLast().getNextOffset()));

        return new Disk(fileName, name, id, "1571", List.copyOf(tracks));
    }

    static Disk createBareDiskImage1581(String name, String id, String fileName)
    {
        return new Disk(fileName, name, id, "1581",
                List.copyOf( createTrackInfo(1, 80, 40, 0) ));
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
