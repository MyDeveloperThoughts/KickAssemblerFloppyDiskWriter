package zinn.plugins;

import kickass.plugins.interf.diskwriter.DiskWriterDefinition;
import kickass.plugins.interf.diskwriter.IDiskData;
import kickass.plugins.interf.diskwriter.IDiskFileData;
import kickass.plugins.interf.diskwriter.IDiskWriter;
import kickass.plugins.interf.general.IEngine;
import kickass.plugins.interf.general.IMemoryBlock;
import kickass.plugins.interf.general.IParameterMap;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * .disk [filename="something.d64" name="empty" id="cz" driveType="1541" (1541, 1571, 1581)
 */
public final class CBMDiskWriter implements IDiskWriter
{
    private static final List<String> possibleDriveTypes = List.of("1541", "1571", "1581");
    private final DiskWriterDefinition m_definition;

    public CBMDiskWriter()
    {
        m_definition = new DiskWriterDefinition();
        m_definition.setName("zinn");

        m_definition.setAllDiskParameters(Set.of("filename", "name", "id", "driveType"));
        m_definition.setNonOptionalDiskParameters(Set.of("filename"));

        m_definition.setAllFileParameters(Set.of("name","type"));
        m_definition.setNonOptionalFileParameters(Set.of("name"));
    }

    @Override
    public DiskWriterDefinition getDefinition()
    {
        return m_definition;
    }

    @Override
    public void execute(IDiskData diskData, IEngine engine)
    {
        String fileName = diskData.getParameters().getStringValue("filename", "");
        String name     = diskData.getParameters().getStringValue("name", "UNNAMED");
        String id       = diskData.getParameters().getStringValue("id", "2A");
        String driveType = diskData.getParameters().getStringValue("driveType", "1541");

        if (!possibleDriveTypes.contains(driveType))
            engine.error(String.format("%s is an invalid drive type.  Must be one of %s", driveType, possibleDriveTypes));

        AbstractDisk disk = AbstractDisk.createDisk(fileName, name, id, driveType);
        disk.formatDisk();

        List<IDiskFileData> filesFromAssembler = diskData.getFiles();
        for(IDiskFileData file : filesFromAssembler)
        {
            IParameterMap fileParams = file.getParameters();
            List<IMemoryBlock> memoryBlocks = file.getMemoryBlocks();

            // Unpack the memory blocks into C64 Memory
            byte[] c64RAM = new byte[65535];
            for(IMemoryBlock memoryBlock : memoryBlocks)
            {
                byte[] memoryBlockMemory = memoryBlock.getBytes();
                ByteLogic.copyIntoRawBytes(c64RAM, memoryBlockMemory, memoryBlock.getStartAddress());
            }

            // Slice out the part we want to write to disk
            int lowestMemoryAddress = memoryBlocks.stream().mapToInt(IMemoryBlock::getStartAddress).min().orElse(0);
            int highestMemoryAddress = memoryBlocks.stream().mapToInt(i -> i.getStartAddress() + i.getBytes().length).max().orElse(0);
            byte[] binaryToWrite = Arrays.copyOfRange(c64RAM, lowestMemoryAddress, highestMemoryAddress);

            boolean storeStartAddress = true;
            if (storeStartAddress)
            {
                byte[] temp = new byte[binaryToWrite.length + 2];
                ByteLogic.copyIntoRawBytes(temp, binaryToWrite, 2);
                temp[0] = (byte) (lowestMemoryAddress & 256);
                temp[1] = (byte) (lowestMemoryAddress / 256);
                binaryToWrite = temp;
            }

            // todo: Store the binary file in the filesystem
        }

        try(OutputStream output = engine.openOutputStream(fileName))
        {
            output.write(disk.rawBytes);
        }
        catch(Exception e)
        {
            engine.error(e.getMessage());
        }
    }


}
