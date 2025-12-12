package zinn.plugins;

import kickass.plugins.interf.diskwriter.DiskWriterDefinition;
import kickass.plugins.interf.diskwriter.IDiskData;
import kickass.plugins.interf.diskwriter.IDiskWriter;
import kickass.plugins.interf.general.IEngine;

import java.io.OutputStream;
import java.util.List;
import java.util.Set;

/**
 * .disk [filename="something.d64" name="empty" id="cz" driveType="1541" (1541, 1571, 1581)
 */
public class CBMDiskWriter implements IDiskWriter
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

        Disk disk = DiskLogic.createUnformattedDisk(name, id, fileName, driveType);
        disk.formatDisk1541();

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
