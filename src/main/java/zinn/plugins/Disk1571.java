package zinn.plugins;

import java.util.ArrayList;
import java.util.List;

import static zinn.plugins.DiskImageLogic.createTrackInfo;

public final class Disk1571 extends  AbstractDisk
{
    public Disk1571(String fileName, String name, String id)
    {
        this.fileName = fileName;
        this.name = name;
        this.id = id;
        this.maxDirectoryEntries = 144;
        this.driveType = "1571";

        List<DiskImageLogic.TrackInfo> tracks = new ArrayList<>(70);
        tracks.addAll(createTrackInfo(1, 17, 21, 0));
        tracks.addAll(createTrackInfo(18, 7, 19, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(25, 6, 18, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(31, 5, 17, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(36, 17, 21, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(53, 7, 19, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(60, 6, 18, tracks.getLast().getNextOffset()));
        tracks.addAll(createTrackInfo(66, 5, 17, tracks.getLast().getNextOffset()));
        this.trackInfos = List.copyOf(tracks);

        int totalRawBytes = trackInfos.stream().mapToInt(info -> 256 * info.sectorCount()).reduce(0, Integer::sum);
        rawBytes = new byte[totalRawBytes];
    }

    @Override
    public void formatDisk()
    {
        // https://vice-emu.sourceforge.io/vice_17.html#SEC416
        // 1571 - Prepare 2 BAM (Block Availability Map)
        //        First is just like the 1541 on Track 18 Sector 0

        // Prepare the 1541 like BAM at Track 18 Sector 0 (First 35 Tracks / Side 1 of the disk)
        // Bytes 0 - 3 is the BAM Header
        int diskOffset = getOffsetForTrackSector(18,0);         // The BAM (Block Availability Map) 91392 / $16500
        rawBytes[diskOffset++] =    (byte) 18;                  // First Directory entry is on Track 18
        rawBytes[diskOffset++] =    (byte) 1;                   //                             Sector 1
        rawBytes[diskOffset++] =    (byte) 65;                  // ASCII A (4040 Format)
        rawBytes[diskOffset++] =    (byte) 128;                 // Always $80 / 128 / 10000000 - Double Sided Disk flag

        // Bytes 4 to 143 is the bitmap of blocks available for tracks 1 - 35
        // 4 bytes per track. For each track, these bytes look like this:
        // Byte 1: $15 / 21      Count of free sectors on the track
        // Byte 2: $FF    11111111     Sector  7  6  5  4  3  2  1  0    (1=Available, 0=Used)
        // Byte 2: $FF    11111111     Sector 15 14 13 12 11 10  9  8    (1=Available, 0=Used )
        // Byte 3: $1F    00011111     Sector 23 22 21 20 19 18 17 16    (1=Available, 0=Used ) (Never more than 21 sectors on a track)
        for(int track=1; track<=35; track++)
        {
            BAMEntry n = createNewBAMEntry(getCountOfSectorsInTrack(track));

            rawBytes[diskOffset++] = n.countOFSectorsInTrack();
            rawBytes[diskOffset++] = n.byte1();
            rawBytes[diskOffset++] = n.byte2();
            rawBytes[diskOffset++] = n.byte3();
        }

        // Let's mark Track 18 Sector 0 used
        markTrackSector(18,0,true);     // The BAM
        markTrackSector(18,1,true);     // The Disk Directory Sector

        // Write out the disk name and id
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString(name, 16), diskOffset);     // Disk Name
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString("", 2),    diskOffset);     // 2 bytes $A0
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString(id, 2),    diskOffset);     // 2 byte disk ID
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString("", 1),    diskOffset);     // Single byte $A0
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString("2A", 2),  diskOffset);     // $2A (Dos Type)
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString("", 4),    diskOffset);     // 4 bytes $A0
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createBytesOfChar((byte) 0,  50),       diskOffset);     // 50 bytes of 0

        // Write out the count of free sectors for tracks 35-70  (The other 3 bytes for the bitmap of availability appear on track 53 sector 0)
        for(int track=36; track<=70; track++)
            rawBytes[diskOffset++] = (byte) getCountOfSectorsInTrack(track);

        // diskOffset is now at the start of Track 18 Sector 1 - The directory track
        rawBytes[diskOffset++] = (byte) 0;                // Next Track is 0 (There is no more data)
        rawBytes[diskOffset] =  (byte) 255;          // Next Sector is 255 (Means entire sector is allocated)

        // Write out the BAM bitmap entries for tracks 36-75 (3 bytes per entry)
        // These will appear on Track 53, Sector 0.
        diskOffset = getOffsetForTrackSector(53,0);     // 266240  $41000
        for(int track=36; track<=70; track++)
        {
            BAMEntry n = createNewBAMEntry(getCountOfSectorsInTrack(track));

            rawBytes[diskOffset++] = n.byte1();
            rawBytes[diskOffset++] = n.byte2();
            rawBytes[diskOffset++] = n.byte3();
        }

        // // The 1571 BAM for Side 2 - Mark all of track 53 as used
        DiskImageLogic.TrackInfo track53 = getTrackInfo(53);
        for(int n=0; n<track53.sectorCount(); n++)
            markTrackSector(53,n,true);
    }

    @Override
    public void markTrackSector(int track, int sector, boolean isUsed)
    {
        // If the track and sector are on side 1, mark the sector using the 1541 logic
        if (track<=35)
        {
            Disk1541.markTrackSector(track, sector, isUsed, this);
            return;
        }

        // Track 53 - Sector 0 is at 266240 / $41000  Target = 266291
        int bamBitmapOffset = getOffsetForTrackSector(53,0); // 266240 / $41000
        int sectorOffset = 2;                   // sector 16-23 is in offset 2
        if (sector <=15) sectorOffset = 1;      // sector 8-15 is in offset 1
        if (sector <=7)  sectorOffset = 0;      // sector 0-7 is in offset 0

        track -= 35;
        bamBitmapOffset = bamBitmapOffset + ((track - 1 ) * 3) + sectorOffset;

        byte existingByte = rawBytes[bamBitmapOffset];
        byte maskingBit = ByteLogic.getSectorBAMMaskingBit(sector);

        if (isUsed) // Force that bit to 0
        {
            maskingBit = (byte) (maskingBit ^ (byte) 255);
            rawBytes[bamBitmapOffset] = (byte) (existingByte & maskingBit);
        }
        if (!isUsed)  // Force the bit to 1
            rawBytes[bamBitmapOffset] = (byte) (existingByte | maskingBit);

        // Need to count how many sectors in the track are available (How many bits are set)
        int countOfAvail = 0;
        bamBitmapOffset = getOffsetForTrackSector(53,0);
        bamBitmapOffset = bamBitmapOffset + ((track - 1 ) * 3);
        for(int n=0; n<3; n++)
        {
            int sectorByteLocation = bamBitmapOffset + n;
            byte sectorByte = rawBytes[sectorByteLocation];
            countOfAvail += Disk1541.getCountOfSectorsAvailableInBAMByte(sectorByte);
        }

        int highTrackSectorCountIndex = getOffsetForTrackSector(18,0) + 221 + (track -1);
        rawBytes[highTrackSectorCountIndex] = (byte) countOfAvail;

    }
}
