package zinn.plugins;

import java.util.List;

import static zinn.plugins.DiskImageLogic.createTrackInfo;

public final class Disk1581 extends AbstractDisk
{
    public Disk1581(String fileName, String name, String id)
    {
        this.fileName = fileName;
        this.name = name;
        this.id = id;
        this.driveType = driveType;
        this.maxDirectoryEntries = 296;
        this.driveType = "1581";
        this.trackInfos = List.copyOf( createTrackInfo(1, 80, 40, 0) );

        int totalRawBytes = trackInfos.stream().mapToInt(info -> 256 * info.sectorCount()).reduce(0, Integer::sum);
        rawBytes = new byte[totalRawBytes];
    }

    @Override
    public void formatDisk()
    {
        // https://vice-emu.sourceforge.io/vice_17.html#SEC419
        // 1541 - Prepare a BAM (Block Availability Map) on Track 40 Sector 0
        // Bytes 0 - 3 is the BAM Header
        int diskOffset = getOffsetForTrackSector(40,0);         // The BAM (Block Availability Map) 399360 / $61800
        rawBytes[diskOffset++] =    (byte) 40;                  // First Directory entry is on Track 40
        rawBytes[diskOffset++] =    (byte) 3;                   //                             Sector 3
        rawBytes[diskOffset++] =    (byte) 68;                  // ASCII D = 1581
        rawBytes[diskOffset++] =    0;                          // Always 0

        // Write out the disk name and id
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString(name, 16), diskOffset);     // Disk Name
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString("", 2),    diskOffset);     // 2 bytes $A0
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString(id, 2),    diskOffset);     // 2 byte disk ID
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString("", 1),    diskOffset);     // Single byte $A0
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString("3D", 2),  diskOffset);     // 3D (Dos Type)
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString("", 2),    diskOffset);     // 4 bytes $A0
        diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createBytesOfChar((byte) 0,  227),      diskOffset);     // 227 bytes of 0 to fill out the sector

        for(int bamSector=1; bamSector<=2; bamSector++)   // 2 Tracks of this
        {
            // 4 bytes per track. For each track, these bytes look like this:
            // Byte 1: $15 / 21      Count of free sectors on the track
            // Byte 2: $FF    11111111     Sector  7  6  5  4  3  2  1  0    (1=Available, 0=Used)
            // Byte 3: $FF    11111111     Sector 15 14 13 12 11 10  9  8    (1=Available, 0=Used )
            // Byte 4: $1F    11111111     Sector 23 22 21 20 19 18 17 16    (1=Available, 0=Used )
            // Byte 5: $1F    11111111     Sector 31 30 29 28 27 26 25 24    (1=Available, 0=Used )
            // Byte 6: $1F    11111111     Sector 39 38 37 36 35 34 33 32    (1=Available, 0=Used )
            diskOffset = getOffsetForTrackSector(40, bamSector);
            if (bamSector==1)
            {
                rawBytes[diskOffset++] = (byte) 40;                  // Next track for BAM Part 2 (Track 40)
                rawBytes[diskOffset++] = (byte) 2;                   //      sector                Sector 2
            }
            if (bamSector==2)
            {
                rawBytes[diskOffset++] = (byte) 0;                  // No next track
                rawBytes[diskOffset++] = (byte) 255;                // No next sector
            }
            rawBytes[diskOffset++] = (byte) 68;                  //  'D' (Version #)                     01000100
            rawBytes[diskOffset++] = (byte) 187;                 //  'D' (Version #) in ones complement  10111011
            diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createShiftSpacePaddedString(id, 2), diskOffset);     // 2 byte disk ID
            rawBytes[diskOffset++] = (byte) 192;                 //  11000000  I/O Byte   bit 7=Verify  bit 6=Check header CRC
            rawBytes[diskOffset++] = (byte) 0;                   //  Auto boot loader (Off)
            diskOffset = ByteLogic.copyIntoRawBytes(rawBytes, ByteLogic.createBytesOfChar((byte) 0, 8), diskOffset);     // 8 zeros

            for (int track = 1; track <= 40; track++)
            {
                rawBytes[diskOffset++] = 40;                  // 40 Sectors available
                rawBytes[diskOffset++] = (byte) 255;
                rawBytes[diskOffset++] = (byte) 255;
                rawBytes[diskOffset++] = (byte) 255;
                rawBytes[diskOffset++] = (byte) 255;
                rawBytes[diskOffset++] = (byte) 255;
            }
        }

        diskOffset = getOffsetForTrackSector(40, 3);        // The disk directory sector
        rawBytes[diskOffset++] = 0;             // No next track
        rawBytes[diskOffset++] = (byte) 255;    // No next sector

        // Let's mark Track 40 Sector 0 used
        markTrackSector(40,0,true);     // Disk Header
        markTrackSector(40,1,true);     // BAM Side 1
        markTrackSector(40,2,true);     // BAM SIde 2
        markTrackSector(40,3,true);     // Director
    }

    @Override
    public void markTrackSector(int track, int sector, boolean isUsed)
    {
        int testOffset = track <=40 ? getOffsetForTrackSector(40,1) : getOffsetForTrackSector(40,2);
        testOffset+=16;     // Skip past the header stuff

        int sectorOffset = 5;      // sector 32-40 is in offset 1
        if (sector <=31) sectorOffset = 4;      // sector 24-31 is in offset 4
        if (sector <=23) sectorOffset = 3;      // sector 16-23 is in offset 3
        if (sector <=15) sectorOffset = 2;      // sector 8-15 is in offset 2
        if (sector <=7)  sectorOffset = 1;      // sector 0-7 is in offset 0

        int sectorCountIndex = testOffset + ((track - 1 ) * 6);
        int bamIndex = sectorCountIndex + sectorOffset;

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
        for(int n=0; n<6; n++)
        {
            byte sectorByte = rawBytes[bamIndex + n];
            if ((sectorByte & 0b00000001) != 0)  countOfAvail++;
            if ((sectorByte & 0b00000010) != 0)  countOfAvail++;
            if ((sectorByte & 0b00000100) != 0)  countOfAvail++;
            if ((sectorByte & 0b00001000) != 0)  countOfAvail++;
            if ((sectorByte & 0b00010000) != 0)  countOfAvail++;
            if ((sectorByte & 0b00100000) != 0)  countOfAvail++;
            if ((sectorByte & 0b01000000) != 0)  countOfAvail++;
            if ((sectorByte & 0b10000000) != 0)  countOfAvail++;
        }
        rawBytes[testOffset + ((track - 1 ) * 6)] = (byte) countOfAvail;
    }
}
