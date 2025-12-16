package zinn.plugins;

import java.util.List;
import java.util.Map;

public abstract class Disk
{
    String fileName;
    String name;
    String id;
    String driveType;
    List<DiskImageLogic.TrackInfo> trackInfos;
    Map<Integer, DiskImageLogic.TrackInfo> trackInfosMap;
    byte[] rawBytes;
    int    maxDirectoryEntries;
    int    directoryTrack;
    int    directoryStartSector;
    int    directoryEndSector;
    int    fileSectorInterleave;
    int    directorySectorInterleave;

    public static Disk createFormattedDisk(String fileName, String name, String id, String driveType)
    {
        Disk disk = null;
        if (driveType.equalsIgnoreCase("1571")) disk = new Disk1571(fileName, name, id);
        if (driveType.equalsIgnoreCase("1581")) disk = new Disk1581(fileName, name, id);
        if (disk==null)                         disk = new Disk1541(fileName, name, id);

        disk.formatDisk();
        return disk;
    }

    public static Disk createFormattedDisk(String fileName, String name, String id)
    {
        String driveType = "1541";
        if (fileName.toLowerCase().endsWith(".d71")) driveType = "1571";
        if (fileName.toLowerCase().endsWith(".d81")) driveType = "1581";

        return createFormattedDisk(fileName, name, id, driveType);
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
        return trackInfosMap.get(track);
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

    public int getNextSectorUsingInterleave(int track, int sector)
    {
        int numberOfSectorsInTrack = getCountOfSectorsInTrack(track);
        return (sector + 10) % numberOfSectorsInTrack;
    }

}
