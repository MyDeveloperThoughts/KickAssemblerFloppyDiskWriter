package zinn.plugins;

import java.util.ArrayList;
import java.util.List;

public final class DiskLogic
{
    public record TrackInfo(int trackNumber, int sectorCount, int offset, String offsetInHex)
    {
        int getNextOffset()
        {
            return offset + (256 * sectorCount);
        }
    }

    static Disk createUnformattedDisk(String name, String id, String fileName, String driveType)
    {
        if (driveType.equals("1571")) return createUnformatted1571Disk(name, id, fileName);
        if (driveType.equals("1581")) return createUnformatted1581Disk(name, id, fileName);

        return createUnformatted1541Disk(name, id, fileName);
    }

    private static Disk createUnformatted1541Disk(String name, String id, String fileName)
    {
        List<TrackInfo> tracks = new ArrayList<>(35);
        tracks.addAll(createTrackInfo(1, 17, 21, 0));
        tracks.addAll(createTrackInfo(18, 7, 19, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(25, 6, 18, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(31, 5, 17, tracks.getLast().getNextOffset()));
        return new Disk(fileName, name, id, "1541", List.copyOf(tracks));
    }

    private static Disk createUnformatted1571Disk(String name, String id, String fileName)
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

    private static Disk createUnformatted1581Disk(String name, String id, String fileName)
    {
        return new Disk(fileName, name, id, "1581",
                List.copyOf( createTrackInfo(1, 80, 40, 0) ));
    }

    private static List<TrackInfo> createTrackInfo(int startingTrackingNumber, int trackCount, int sectorsPerTrack, int startingOffset)
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
