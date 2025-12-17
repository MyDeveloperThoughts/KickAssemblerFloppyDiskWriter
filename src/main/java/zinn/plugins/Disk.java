package zinn.plugins;

import kickass.plugins.interf.general.IMemoryBlock;

import java.util.ArrayList;
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
    int[]  trackCreationOrder;

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
    public abstract boolean isTrackSectorAvailable(int track, int sector);

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


    // Returns null if there is nothing left on the disk
    public record TrackSector(int track, int sector) {}
    public TrackSector findUnallocatedTrackSector()
    {
        int sector = 0;

        TrackSector availableTrackSector = null;
        for(int track : trackCreationOrder)
        {
            int attemptsLeft = getCountOfSectorsInTrack(track) + 5;
            while (attemptsLeft > 0)
            {
                if (isTrackSectorAvailable(track, sector))
                    return new TrackSector(track, sector);

                sector = getNextSectorUsingInterleave(track, sector);
                attemptsLeft--;
            }
        }

        return availableTrackSector;
    }

    public void writeFileToDisk(List<IMemoryBlock> memoryBlocks, boolean storeStartAddress, String storeFilename, String fileType, boolean isSoftwareLocked)
    {
        ByteLogic.BinaryFile binaryFile = ByteLogic.convertIMemoryBlocksToBinaryFile(memoryBlocks, storeStartAddress);
        int sectorsNeeded = binaryFile == null ? 0 : binaryFile.rawData().length / 254;  // 192 blocks
        int bytesInLastSector = binaryFile == null ? 0 : (binaryFile.rawData().length - (254 * (binaryFile.rawData().length / 254)));
        if (bytesInLastSector > 0)
            sectorsNeeded++;

        // Decide where we are going to place the data, and mark the sectors used
        if (binaryFile!=null)
        {
            List<Disk.TrackSector> trackSectors = new ArrayList<>(sectorsNeeded);
            for (int n = 0; n < sectorsNeeded; n++)
            {
                Disk.TrackSector storeAt = findUnallocatedTrackSector();
                markTrackSector(storeAt.track(), storeAt.sector(), true);
                trackSectors.add(storeAt);
            }

            // Place the sectors on the disk with pointers to the next sector
            int binaryFileOffset = 0;
            for (int n = 0; n < sectorsNeeded; n++)
            {
                Disk.TrackSector storeAt = trackSectors.get(n);

                int diskOffset = getOffsetForTrackSector(storeAt.track(), storeAt.sector());
                if (n + 1 == sectorsNeeded)       // This is the last sector
                {
                    rawBytes[diskOffset++] = 0;
                    rawBytes[diskOffset++] = (byte) bytesInLastSector;
                    for (int x = 0; x < bytesInLastSector; x++)
                        rawBytes[diskOffset++] = binaryFile.rawData()[binaryFileOffset++];
                }
                else        // Point to the next track / sector
                {
                    Disk.TrackSector nextSector = trackSectors.get(n + 1);
                    rawBytes[diskOffset++] = (byte) nextSector.track();
                    rawBytes[diskOffset++] = (byte) nextSector.sector();
                    for (int x = 0; x < 254; x++)
                        rawBytes[diskOffset++] = binaryFile.rawData()[binaryFileOffset++];
                }
            }
        }

        int entryIndexInThisSector = 0;   // We can only fit 8 directory entries in each sector
        int entryTrack = directoryTrack;
        int entrySector= directoryStartSector;

        // Create a directory entry
        // To get started, we are just going to hard code it on directoryTrack , and sector
        int directoryEntryOffset = getOffsetForTrackSector(entryTrack, entrySector);
        directoryEntryOffset+=2;  // Skip over the next directory track / sector
        rawBytes[directoryEntryOffset++] = ByteLogic.convertToFileTypeByte(fileType, isSoftwareLocked);
        rawBytes[directoryEntryOffset++] = (byte) 17;  // Hard coded for Track 17 for now
        rawBytes[directoryEntryOffset++] = (byte) 0;   // Hard coded for Sector 0 for now
        directoryEntryOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString(storeFilename, 16), directoryEntryOffset);     // File Name
        rawBytes[directoryEntryOffset++] = (byte) 0;   // Rel File Track
        rawBytes[directoryEntryOffset++] = (byte) 0;   // Rel File Sector
        rawBytes[directoryEntryOffset++] = (byte) 0;   // Rel File Length
        directoryEntryOffset+=6;                            // 6 Unused bytes
        rawBytes[directoryEntryOffset++] = (byte) (sectorsNeeded % 255);
        rawBytes[directoryEntryOffset] =   (byte) (sectorsNeeded / 256);
    }


}
