package zinn.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static zinn.plugins.DiskImageLogic.createTrackInfo;


// See specs on the 1541 .d64 layout here:
// https://vice-emu.sourceforge.io/vice_17.html#SEC410
public final class Disk1541 extends Disk
{
    public Disk1541(String fileName, String name, String id)
    {
        this.fileName = fileName;
        this.name = name;
        this.id = id;
        this.maxDirectoryEntries = 144;
        this.driveType = "1541";

        List<DiskImageLogic.TrackInfo> tracks = new ArrayList<>(35);
        tracks.addAll(createTrackInfo(1, 17, 21, 0));
        tracks.addAll(createTrackInfo(18, 7, 19, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(25, 6, 18, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(31, 5, 17, tracks.getLast().getNextOffset()));
        this.trackInfos = List.copyOf(tracks);

        int totalRawBytes = trackInfos.stream().mapToInt(info -> 256 * info.sectorCount()).reduce(0, Integer::sum);
        trackInfosMap = trackInfos.stream().collect(Collectors.toMap(DiskImageLogic.TrackInfo::trackNumber, info -> info));
        rawBytes = new byte[totalRawBytes];

        trackCreationOrder = new int[] {17,19,16,20,15,21,14,22,13,23,12,24,11,25,10,26,9,27,8,28,7,29,6,30,5,31,4,32,3,33,2,34,1,35};
        this.directoryTrack = 18;
        this.directoryStartSector = 1;
        this.directoryEndSector = 18;
        this.fileSectorInterleave = 10;
        this.directorySectorInterleave = 3;
    }

    @Override
    public void formatDisk()
    {
        // 1541 - Prepare a BAM (Block Availability Map) on Track 18 Sector 0
        // Bytes 0 - 3 is the BAM Header
        int diskOffset = getOffsetForTrackSector(18,0);         // The BAM (Block Availability Map)
        rawBytes[diskOffset++] =    (byte) 18;                  // First Directory entry is on Track 18
        rawBytes[diskOffset++] =    (byte) 1;                   //                             Sector 1
        rawBytes[diskOffset++] =    (byte) 65;                  // ASCII A (4040 Format)
        rawBytes[diskOffset++] =    0;                          // Always 0

        // This is the 1541 BAM
        // Bytes 4 to 143 is the bitmap of blocks available for tracks 1 - 35
        // 4 bytes per track. For each track, these bytes look like this:
        // Byte 1: $15 / 21      Count of free sectors on the track
        // Byte 2: $FF    11111111     Sector  7  6  5  4  3  2  1  0    (1=Available, 0=Used)
        // Byte 2: $FF    11111111     Sector 15 14 13 12 11 10  9  8    (1=Available, 0=Used )
        // Byte 3: $1F    00011111     Sector 23 22 21 20 19 18 17 16    (1=Available, 0=Used ) (Never more than 21 sectors on a track)
        for(int track=1; track<=35; track++)
        {
            BAMEntry n = createNewBAMEntryFor1541or1571(getCountOfSectorsInTrack(track));

            rawBytes[diskOffset++] = n.countOFSectorsInTrack();
            rawBytes[diskOffset++] = n.byte1();
            rawBytes[diskOffset++] = n.byte2();
            rawBytes[diskOffset++] = n.byte3();
        }

        markTrackSector(18,0,true);     // The BAM
        markTrackSector(18,1,true);     // The Disk Directory Sector

        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString(name, 16), diskOffset);     // Disk Name
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString("", 2),    diskOffset);     // 2 bytes $A0
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString(id, 2),    diskOffset);     // 2 byte disk ID
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString("", 1),    diskOffset);     // Single byte $A0
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString("2A", 2),  diskOffset);     // $2A (Dos Type)
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString("", 4),    diskOffset);     // 4 bytes $A0
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createBytesOfChar((byte) 0,  85),       diskOffset);     // 85 bytes of 0 to fill out the sector

        // diskOffset is now at the start of Track 18 Sector 1 - The directory track
        rawBytes[diskOffset++] = (byte) 0;          // Next Track is 0 (There is no more data)
        rawBytes[diskOffset] = (byte) 255;          // Next Sector is 255 (Means entire sector is allocated)
    }

    @Override
    public void markTrackSector(int track, int sector, boolean inUse)
    {
        markTrackSector(track, sector, inUse, this);
    }

    public static void markTrackSector(int track, int sector, boolean isUsed, Disk disk)
    {
        int testOffset = disk.getOffsetForTrackSector(18,0) + 4;

        int sectorOffset = 2;                   // sector 16-23 is in offset 2
        if (sector <=15) sectorOffset = 1;      // sector 8-15 is in offset 1
        if (sector <=7)  sectorOffset = 0;      // sector 0-7 is in offset 0
        int sectorCountIndex = testOffset + ((track - 1 ) * 4) + sectorOffset;
        int bamIndex = sectorCountIndex + 1;

        byte existingByte = disk.rawBytes[bamIndex];
        byte maskingBit = ByteLogic.getSectorBAMMaskingBit(sector);

        if (isUsed) // Force that bit to 0
        {
            maskingBit = (byte) (maskingBit ^ (byte) 255);
            disk.rawBytes[bamIndex] = (byte) (existingByte & maskingBit);
        }
        if (!isUsed)  // Force the bit to 1
            disk.rawBytes[bamIndex] = (byte) (existingByte | maskingBit);

        // Need to count how many sectors in the track are available (How many bits are set)
        int countOfAvail = 0;
        for(int n=0; n<3; n++)
        {
            byte sectorByte = disk.rawBytes[testOffset + ((track - 1 ) * 4) + 1 + n];
            countOfAvail += ByteLogic.getCountOfSectorsAvailableInBAMByte(sectorByte);

        }
        disk.rawBytes[testOffset + ((track - 1 ) * 4)] = (byte) countOfAvail;
    }

    @Override
    public boolean isTrackSectorAvailable(int track, int sector)
    {
        return isTrackSectorAvailable(this, track, sector);
    }

    public static boolean isTrackSectorAvailable(Disk disk, int track, int sector)
    {
        int testOffset = disk.getOffsetForTrackSector(18,0) + 4;

        int sectorOffset = 2;                   // sector 16-23 is in offset 2
        if (sector <=15) sectorOffset = 1;      // sector 8-15 is in offset 1
        if (sector <=7)  sectorOffset = 0;      // sector 0-7 is in offset 0
        int sectorCountIndex = testOffset + ((track - 1 ) * 4) + sectorOffset;
        int bamIndex = sectorCountIndex + 1;

        byte existingByte = disk.rawBytes[bamIndex];
        byte maskingBit = ByteLogic.getSectorBAMMaskingBit(sector);

        return (existingByte & maskingBit) != 0;
    }


}

