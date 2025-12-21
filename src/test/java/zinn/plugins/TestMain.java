package zinn.plugins;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is just test code to look at the contents of a disk at a low level so I can compare it to ones created outside of this library.
 */
public class TestMain
{
    static void main(String[] args) throws Exception
    {
//        byte[] entireDisk = Files.readAllBytes(Path.of("c:\\project\\KickAssemblerFloppyDiskWriter\\output\\1764demodisk.d64"));
//        byte[] entireDisk = Files.readAllBytes(Path.of("c:\\project\\KickAssemblerFloppyDiskWriter\\output\\full2-1571.d71"));
//        byte[] entireDisk = Files.readAllBytes(Path.of("c:\\project\\KickAssemblerFloppyDiskWriter\\output\\full-1541.d64"));
//        byte[] entireDisk = Files.readAllBytes(Path.of("c:\\project\\KickAssemblerFloppyDiskWriter\\output\\full2-1541.d64"));
//        byte[] entireDisk = Files.readAllBytes(Path.of("c:\\project\\KickAssemblerFloppyDiskWriter\\output\\full2-1581.d81"));
        byte[] entireDisk = Files.readAllBytes(Path.of("c:\\project\\KickAssemblerFloppyDiskWriter\\output\\test.d64"));
//        byte[] entireDisk = Files.readAllBytes(Path.of("c:\\project\\KickAssemblerFloppyDiskWriter\\output\\broadSides.d64"));

        Disk disk = Disk.createFormattedDisk("test.d64", "stuff", "cz", "1541");
        disk.rawBytes = entireDisk;
        byte[] rawBytes = disk.rawBytes;

        List<DirectoryEntry> entries = new ArrayList<>();

        // 1571 and 1541 track=18 sectors 1 to 18
        // 1581 track=40 sectors 3 to 39
        int track = disk.directoryTrack;
        for (int sector = disk.directoryStartSector; sector<=disk.directoryEndSector; sector++)
        {
            for (int entry=0; entry<8; entry++)
            {
                int directoryOffset = disk.getOffsetForTrackSector(track, sector) + (entry * 32);

                int nextDirectoryTrack = rawBytes[directoryOffset++];
                int nextDirectorySector = rawBytes[directoryOffset++];
                int fileType = rawBytes[directoryOffset++];
                int fileTrack = rawBytes[directoryOffset++];
                int fileSector = rawBytes[directoryOffset++];

                byte[] fileNameBytes = Arrays.copyOfRange(rawBytes, directoryOffset, directoryOffset+ 16);
                directoryOffset+=16;
                String fileName = convertC64ToString(fileNameBytes);

                int relFileTrack = rawBytes[directoryOffset++];
                int relFileSector = rawBytes[directoryOffset++];
                int relRecordLength = rawBytes[directoryOffset++];

                directoryOffset+=6;         // Unused
                int fileSizeLo = rawBytes[directoryOffset++];
                int fileSizeHi = rawBytes[directoryOffset];
                if (fileSizeLo < 0) fileSizeLo = 256 + fileSizeLo;
                if (fileSizeHi < 0) fileSizeHi = 256 + fileSizeHi;
                int blocksUsed = fileSizeLo + (fileSizeHi * 256);

                DirectoryEntry n = new DirectoryEntry(track, sector, entry,
                        nextDirectoryTrack, nextDirectorySector, fileType, convertFileTypeToString(fileType), fileTrack, fileSector, fileName,
                        relFileTrack, relFileSector, relRecordLength, blocksUsed);
                entries.add(n);
            }
        }

//        printInDiskOrder(entries);
        printInDirectoryOrder(entries, disk.maxDirectoryEntries);
        followFileTrackSectorLinks(17, 0, disk);
    }

    record DirectoryEntry(int directoryTrack, int directorySector, int sectorEntryNumber,
                          int nextDirectoryTrack, int nextDirectorySector, int FileType, String fileTypeString,
                          int fileTrack, int fileSector, String fileName,
                          int relFileTrack, int relRecordLength, int relFileLength,
                          int blocksUsed) {}

    static String convertC64ToString(byte[] c64Bytes)
    {
        if (c64Bytes[0]==0) return "                ";
        return new String(c64Bytes, StandardCharsets.ISO_8859_1).replace( (char) 160, ' ');
    }

    static void printInDiskOrder(List<DirectoryEntry> entries)
    {
        // Dump the records in disk order (This is how they are captured)
        int printTrack = 0;
        int printSector = 0;
        for(DirectoryEntry e : entries)
        {
            if (e.directorySector!=printSector || e.directoryTrack != printTrack)
            {
                printTrack = e.directoryTrack;
                printSector = e.directorySector;
                System.out.printf("------ Track %d  Sector %d\n", printTrack ,printSector);
            }

            if (e.nextDirectoryTrack > 0)
                System.out.printf("\t----> Track Directory Track: %d\tSector %d\n", e.nextDirectoryTrack, e.nextDirectorySector);
            System.out.printf("\tSector Entry %d \t[%s]\t%3d blocks\tFile is at Track %d Sector %2d\n", e.sectorEntryNumber, e.fileName, e.blocksUsed, e.fileTrack, e.fileSector);
        }
    }

    static void printInDirectoryOrder(List<DirectoryEntry> entries, int maxDirectoryEntries)
    {
        // Dump the records in Directory Order
        DirectoryEntry currentEntry = entries.getFirst();
        int sectorIndex = 0;
        int nextDirectoryTrack = currentEntry.nextDirectoryTrack;
        int nextDirectorySector = currentEntry.nextDirectorySector;

        while(true)
        {
            // Display the entries in this sector
            for(int n=sectorIndex; n<sectorIndex + 8; n++)
            {
                System.out.printf("\tSector Entry %d \t[%s][%4s]\t%3d blocks\tFile is at Track %d Sector %2d\n", currentEntry.sectorEntryNumber, currentEntry.fileName, currentEntry.fileTypeString,
                        currentEntry.blocksUsed, currentEntry.fileTrack, currentEntry.fileSector);
                if (n==0)
                {
                    nextDirectoryTrack = currentEntry.nextDirectoryTrack;
                    nextDirectorySector = currentEntry.nextDirectorySector;
                }

                if (n<7)
                {
                    int indexOfThis = entries.indexOf(currentEntry);
                    currentEntry = entries.get(indexOfThis + 1);
                }
            }

            if (nextDirectoryTrack==0)
                break;

            // Find the next sector to print if there is one
            for (DirectoryEntry entry : entries)
            {
                currentEntry = entry;
                if (currentEntry.directoryTrack == nextDirectoryTrack && currentEntry.directorySector == nextDirectorySector)
                    break;
            }
        }
     }


     static void followFileTrackSectorLinks(int startAtTrack, int startAtSector, Disk disk)
     {
         byte[] rawBytes = disk.rawBytes;
         int offset = disk.getOffsetForTrackSector(startAtTrack, startAtSector);

         System.out.println("Track " + startAtTrack + " Sector " + startAtSector);


         int nextTrack = rawBytes[offset];
         int nextSector = rawBytes[offset + 1];
         while(nextTrack!=0)
         {
             nextTrack = rawBytes[offset];
             nextSector = rawBytes[offset + 1];
             offset = disk.getOffsetForTrackSector(nextTrack, nextSector);
             System.out.println("Track " + nextTrack + " Sector " + nextSector);
         }
     }

    public static String convertFileTypeToString(int fileType)
    {
        String fileTypeString = "";
        if ( ((byte) fileType & 0b00001111) == 0) fileTypeString = "DEL";
        if ( ((byte) fileType & 0b00001111) == 1) fileTypeString = "SEQ";
        if ( ((byte) fileType & 0b00001111) == 2) fileTypeString = "PRG";
        if ( ((byte) fileType & 0b00001111) == 3) fileTypeString = "USR";
        if ( ((byte) fileType & 0b00001111) == 4) fileTypeString = "REL";
        if ( ((byte) fileType & 0b01000000) != 0) fileTypeString += "<";        // Software Locked

        return fileTypeString;
    }


}
