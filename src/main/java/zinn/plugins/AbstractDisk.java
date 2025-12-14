package zinn.plugins;

import java.util.List;

public abstract class AbstractDisk
{
    String fileName;
    String name;
    String id;
    String driveType;
    List<DiskImageLogic.TrackInfo> trackInfos;
    byte[] rawBytes;
    int    maxDirectoryEntries;

    public static AbstractDisk createDisk(String fileName, String name, String id, String driveType)
    {
        if (driveType.equalsIgnoreCase("1571")) return new Disk1571(fileName, name, id);
        if (driveType.equalsIgnoreCase("1581")) return new Disk1581(fileName, name, id);
        return new Disk1541(fileName, name, id);
    }

    public abstract void formatDisk();
    public abstract void markTrackSector(int track, int sector, boolean iUse);

    record BAMEntry(byte countOFSectorsInTrack, byte byte1, byte byte2, byte byte3) {}
    BAMEntry createNewBAMEntry(int countOFSectorsInTrack)
    {
        int byte1  = 255;
        int byte2  = 255;
        int byte3 = 0;
        if (countOFSectorsInTrack==21) byte3 = 31;
        if (countOFSectorsInTrack==19) byte3 = 7;
        if (countOFSectorsInTrack==18) byte3 = 3;
        if (countOFSectorsInTrack==17) byte3 = 1;

        return new BAMEntry((byte) countOFSectorsInTrack, (byte) byte1, (byte) byte2, (byte) byte3);
    }

    public DiskImageLogic.TrackInfo getTrackInfo(int track)
    {
        return trackInfos.stream().filter(info -> info.trackNumber()==track).findFirst().orElse(null);
    }

    public int getOffsetForTrackSector(int track, int sector)
    {
        DiskImageLogic.TrackInfo trackInfo = getTrackInfo(track);
        return trackInfo==null ? 0 : trackInfo.offset() + (256 * sector);
    }

    public int getCountOfSectorsInTrack(int track)
    {
        DiskImageLogic.TrackInfo trackInfo = getTrackInfo(track);
        return trackInfo==null ? 0 : trackInfo.sectorCount();
    }

}
