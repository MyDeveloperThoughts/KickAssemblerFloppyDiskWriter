package zinn.plugins;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public final class Disk
{
    String fileName;
    String name;
    String id;
    String driveType;
    List<DiskLogic.TrackInfo> trackInfos;
    byte[] rawBytes;

    public Disk(String fileName, String name, String id, String driveType, List<DiskLogic.TrackInfo> trackInfos)
    {
        this.fileName = fileName;
        this.name = name;
        this.id = id;
        this.driveType = driveType;
        this.trackInfos = trackInfos;

        int totalRawBytes = trackInfos.stream().mapToInt(info -> 256 * info.sectorCount()).reduce(0, Integer::sum);
        rawBytes = new byte[totalRawBytes];
    }

    public void formatDisk1541()
    {
        // https://vice-emu.sourceforge.io/vice_17.html#SEC410
        // 1541 - Prepare a BAM (Block Allocation Map) on Track 18 Sector 0
        // Lookup the offset into the byte array for Track 18 Sector 0
        // Bytes 0 - 3 is the BAM Header
        int diskOffset = getOffsetForTrackSector(18,0);         // The BAM (Block Availability Map)
        rawBytes[diskOffset] =      (byte) 18;                  // First Directory entry is on Track 18
        rawBytes[diskOffset+1] =    (byte) 1;                   //                             Sector 1
        rawBytes[diskOffset+2] =    (byte) 65;                  // ASCII A (4040 Format)
        rawBytes[diskOffset+3] =    0;                          // Always 0
        diskOffset+=4;

        // This is the 1541 BAM
        // Bytes 4 to 143 is the bitmap of blocks available for tracks 1 - 35
        // 4 bytes per track. For each track, these bytes look like this:
        // Byte 1: $15 / 21      Count of free sectors on the track
        // Byte 2: $FF    11111111     Sector  7  6  5  4  3  2  1  0    (1=Available, 0=Used)
        // Byte 2: $FF    11111111     Sector 15 14 13 12 11 10  9  8    (1=Available, 0=Used )
        // Byte 3: $1F    00011111     Sector 23 22 21 20 19 18 17 16    (1=Available, 0=Used ) (Never more than 21 sectors on a track)
        for(int track=1; track<=35; track++)
        {
            BAMEntry n = createNewBAMEntry1541(getCountOfSectorsInTrack(track));

            rawBytes[diskOffset]     = n.countOFSectorsInTrack;
            rawBytes[diskOffset + 1] = n.byte1;
            rawBytes[diskOffset + 2] = n.byte2;
            rawBytes[diskOffset + 3] = n.byte3;

            diskOffset+=4;
        }

        // Let's mark Track 18 Sector 0 used
        markTrackSector1541(18,0,true);     // The BAM
        markTrackSector1541(18,1,true);     // The Disk Directory Sector

        // Write out the disk name and id
        diskOffset = copyIntoRawBytes(createShiftSpacePaddedString(name, 16), diskOffset);     // Disk Name
        diskOffset = copyIntoRawBytes(createShiftSpacePaddedString("", 2),    diskOffset);     // 2 bytes $A0
        diskOffset = copyIntoRawBytes(createShiftSpacePaddedString(id, 2),    diskOffset);     // 2 byte disk ID
        diskOffset = copyIntoRawBytes(createShiftSpacePaddedString("", 1),    diskOffset);     // Single byte $A0
        diskOffset = copyIntoRawBytes(createShiftSpacePaddedString("2A", 2),  diskOffset);     // $2A (Dos Type)
        diskOffset = copyIntoRawBytes(createShiftSpacePaddedString("", 4),    diskOffset);     // 4 bytes $A0
        diskOffset = copyIntoRawBytes(createBytesOfChar((byte) 0,  85),       diskOffset);     // 55 bull bytes to fill out the sector

        // diskOffset is now at the start of Track 18 Sector 1 - The directory track
        rawBytes[diskOffset] = (byte) 0;                // Next Track is 0 (There is no more data)
        rawBytes[diskOffset + 1] = (byte) 255;          // Next Sector is 255 (Means entire sector is allocated)
    }

    public int copyIntoRawBytes(byte[] bytesToMerge, int offset)
    {
        for(int n=0; n<bytesToMerge.length; n++)
            rawBytes[offset+n] = bytesToMerge[n];
        return offset + bytesToMerge.length;
    }

    public byte[] createShiftSpacePaddedString(String value, int maxLength)
    {
        if (value.length()>maxLength)
            value = value.substring(0,maxLength);
        return String.format("%-" + maxLength + "s",value)
                .replace(' ', (char) 160)                       // Pad with shift space ($A0 160)
                .getBytes(StandardCharsets.ISO_8859_1);
    }

    public byte[] createBytesOfChar(byte charToFill, int length)
    {
        byte[] bytes = new byte[length];
        Arrays.fill(bytes, charToFill);
        return bytes;
    }


    public void markTrackSector1541(int track, int sector, boolean isUsed)
    {
        int testOffset = getOffsetForTrackSector(18,0) + 4;

        int sectorOffset = 2;                   // sector 16-23 is in offset 2
        if (sector <=15) sectorOffset = 1;      // sector 8-15 is in offset 1
        if (sector <=7)  sectorOffset = 0;      // sector 0-7 is in offset 0
        int sectorCountIndex = testOffset + ((track - 1 ) * 4) + sectorOffset;
        int bamIndex = sectorCountIndex + 1;

        byte existingByte = rawBytes[bamIndex];
        byte maskingBit = 0b00000000;

        // This is a bit goofy, but this will work for now.
        if (sector == 0 || sector == 8  || sector == 16)   maskingBit =        0b00000001;
        if (sector == 1 || sector == 9  || sector == 17)   maskingBit =        0b00000010;
        if (sector == 2 || sector == 10 || sector == 18)   maskingBit =        0b00000100;
        if (sector == 3 || sector == 11 || sector == 19)   maskingBit =        0b00001000;
        if (sector == 4 || sector == 12 || sector == 20)   maskingBit =        0b00010000;
        if (sector == 5 || sector == 13 || sector == 21)   maskingBit =        0b00100000;
        if (sector == 6 || sector == 14 || sector == 22)   maskingBit =        0b01000000;
        if (sector == 7 || sector == 15 || sector == 23)   maskingBit = (byte) 0b10000000;

        if (isUsed) // Force that bit to 0
        {
            maskingBit = (byte) (maskingBit ^ (byte) 255);
            rawBytes[bamIndex] = (byte) (existingByte & maskingBit);
        }
        if (!isUsed)  // Force the bit to 1
            rawBytes[bamIndex] = (byte) (existingByte | maskingBit);

        // Need to count how many sectors in the track are available (How many bits are set)
        int countOfAvail = 0;
        for(int n=0; n<3; n++)
        {
            byte sectorByte = rawBytes[testOffset + ((track - 1 ) * 4) + 1 + n];
            if ((sectorByte & 0b00000001) != 0)  countOfAvail++;
            if ((sectorByte & 0b00000010) != 0)  countOfAvail++;
            if ((sectorByte & 0b00000100) != 0)  countOfAvail++;
            if ((sectorByte & 0b00001000) != 0)  countOfAvail++;
            if ((sectorByte & 0b00010000) != 0)  countOfAvail++;
            if ((sectorByte & 0b00100000) != 0)  countOfAvail++;
            if ((sectorByte & 0b01000000) != 0)  countOfAvail++;
            if ((sectorByte & 0b10000000) != 0)  countOfAvail++;
        }
        rawBytes[testOffset + ((track - 1 ) * 4)] = (byte) countOfAvail;


    }

    record BAMEntry(byte countOFSectorsInTrack, byte byte1, byte byte2, byte byte3) {}
    private BAMEntry createNewBAMEntry1541(int countOFSectorsInTrack)
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

    public DiskLogic.TrackInfo getTrackInfo(int track)
    {
        return trackInfos.stream().filter(info -> info.trackNumber()==track).findFirst().orElse(null);
    }

    public int getOffsetForTrackSector(int track, int sector)
    {
        DiskLogic.TrackInfo trackInfo = getTrackInfo(track);
        return trackInfo==null ? 0 : trackInfo.offset() + (256 * sector);
    }

    public int getCountOfSectorsInTrack(int track)
    {
        DiskLogic.TrackInfo trackInfo = getTrackInfo(track);
        return trackInfo==null ? 0 : trackInfo.sectorCount();
    }



}
