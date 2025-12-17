package zinn.plugins;

import kickass.plugins.interf.general.IEngine;
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

    public int getNextSectorUsingInterleave(int track, int sector, int interleave)
    {
        int numberOfSectorsInTrack = getCountOfSectorsInTrack(track);
        return (sector + interleave) % numberOfSectorsInTrack;
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

                sector = getNextSectorUsingInterleave(track, sector, fileSectorInterleave);
                attemptsLeft--;
            }

            // We still didn't find anything?  Let's now scan sequentially in cased to interleave algorithm skipped one
            int sectorsInTrack = getCountOfSectorsInTrack(track);
            for(int n=0; n<=sectorsInTrack; n++)
            {
                if (isTrackSectorAvailable(track, n))
                    return new TrackSector(track, n);
            }
        }

        return availableTrackSector;
    }

    public void writeFileToDisk(IEngine engine, List<IMemoryBlock> memoryBlocks, boolean storeStartAddress, String storeFilename, String fileType, boolean isSoftwareLocked)
    {
        ByteLogic.BinaryFile binaryFile = ByteLogic.convertIMemoryBlocksToBinaryFile(memoryBlocks, storeStartAddress);
        int sectorsNeeded = binaryFile == null ? 0 : binaryFile.rawData().length / 254;  // 192 blocks
        int bytesInLastSector = binaryFile == null ? 0 : (binaryFile.rawData().length - (254 * (binaryFile.rawData().length / 254)));
        if (bytesInLastSector > 0)
            sectorsNeeded++;

        // Decide where we are going to place the data on the disk and mark the sectors used
        int binaryFileTrack = 0;
        int binaryFileSector = 0;
        if (binaryFile!=null)
        {
            List<Disk.TrackSector> trackSectors = new ArrayList<>(sectorsNeeded);
            for (int n = 0; n < sectorsNeeded; n++)
            {
                Disk.TrackSector storeAt = findUnallocatedTrackSector();
                if (storeAt == null)
                    engine.error("Out of disk space while writing " + storeFilename + " to " + name);
                if (storeAt!=null)
                {
                    markTrackSector(storeAt.track(), storeAt.sector(), true);
                    trackSectors.add(storeAt);

                    if (n==0)
                    {
                        binaryFileTrack = storeAt.track;
                        binaryFileSector = storeAt.sector;
                    }
                }

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

        int entryTrack = directoryTrack;
        int entrySector= directoryStartSector;

        // Create a directory entry
        // Find an empty entry in this sector.
        Integer useEntryIndex = null;
        while(useEntryIndex==null)
        {
            useEntryIndex = findFreeDirectoryEntryInThisTrackSector(entryTrack, entrySector);
            if (useEntryIndex == null)
            {
                // All 8 entries in this track sector are used.
                // Do need to create another track sector?
                int directoryEntryOffset = getOffsetForTrackSector(entryTrack, entrySector);
                byte nextTrack = rawBytes[directoryEntryOffset];
                if (nextTrack == 0)        // Is the next directory track link set to 0
                {
                    int nextSector = getNextSectorUsingInterleave(entryTrack, entrySector, directorySectorInterleave);

                    if (isDirectorySectorUsed(entryTrack, nextSector))
                    {
                        nextSector = -1;
                        for(int n=directoryStartSector; n<=directoryEndSector; n++)
                        {
                            int test = getNextSectorUsingInterleave(entryTrack, n, directorySectorInterleave);
                            if (!isDirectorySectorUsed(entryTrack, test))
                            {
                                nextSector = test;
                                break;
                            }
                        }

                        // We still didn't find anything?  Let's now scan sequentially in cased to interleave algorithm skipped one
                        if (nextSector == -1)
                            for(int n=directoryStartSector; n<=directoryEndSector; n++)
                            {
                                if (!isDirectorySectorUsed(entryTrack, n))
                                {
                                    nextSector = n;
                                    break;
                                }
                            }

                        if (nextSector == -1)
                            engine.error("No more directory sectors are available");
                    }

                    rawBytes[directoryEntryOffset] =  (byte) entryTrack;            // Set link to the next new directory track
                    rawBytes[directoryEntryOffset + 1] = (byte) nextSector;

                    int newDirectoryOffset = getOffsetForTrackSector(entryTrack, nextSector);
                    rawBytes[newDirectoryOffset] =  (byte) 0;                     // Set link on new directory track to 0
                    rawBytes[newDirectoryOffset + 1] = (byte) 0b11111111;         // and $FF to indicate last directory sector

                    entrySector = nextSector;
                }
                else
                    entrySector = rawBytes[directoryEntryOffset + 1];         // Follow the link to the next directory sector
            }
        }

        engine.printNow("Writing " + xx + " [" + storeFilename + "] " + sectorsNeeded + " sectors to entry " + useEntryIndex + " T:S " + entryTrack + ": "  + entrySector + "\t\tFile is at T:S " + binaryFileTrack + ":" +  binaryFileSector);
        xx++;

        int directoryEntryOffset = getOffsetForTrackSector(entryTrack, entrySector);
        directoryEntryOffset += (useEntryIndex * 32);
        directoryEntryOffset+=2;  // Skip over the next directory track / sector
        rawBytes[directoryEntryOffset++] = ByteLogic.convertToFileTypeByte(fileType, isSoftwareLocked);
        rawBytes[directoryEntryOffset++] = (byte) binaryFileTrack;  // Hard coded for Track 17 for now
        rawBytes[directoryEntryOffset++] = (byte) binaryFileSector;   // Hard coded for Sector 0 for now
        directoryEntryOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString(storeFilename, 16), directoryEntryOffset);     // File Name
        rawBytes[directoryEntryOffset++] = (byte) 0;   // Rel File Track
        rawBytes[directoryEntryOffset++] = (byte) 0;   // Rel File Sector
        rawBytes[directoryEntryOffset++] = (byte) 0;   // Rel File Length
        directoryEntryOffset+=6;                            // 6 Unused bytes
        rawBytes[directoryEntryOffset++] = (byte) (sectorsNeeded % 255);
        rawBytes[directoryEntryOffset] =   (byte) (sectorsNeeded / 256);

    }

    static int xx = 0;
    private Integer findFreeDirectoryEntryInThisTrackSector(int track, int sector)
    {
        int directoryEntryOffset = getOffsetForTrackSector(track, sector);
        for (int entryIndex = 0; entryIndex < 8; entryIndex++)
        {
            int testForNameOffset = directoryEntryOffset + 6 + (32 * entryIndex);
            if (rawBytes[testForNameOffset] == 0)       // File name is still 0 - this is available
                return entryIndex;
        }

        return null;
    }

    // A directory sector is used if there is a filename in entry 0
    private boolean isDirectorySectorUsed(int track, int sector)
    {
        int directoryEntryOffset = getOffsetForTrackSector(track, sector);
        return rawBytes[directoryEntryOffset + 6] != 0;
    }

}
