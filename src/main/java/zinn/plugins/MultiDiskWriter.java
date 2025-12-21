package zinn.plugins;

import kickass.plugins.interf.diskwriter.DiskWriterDefinition;
import kickass.plugins.interf.diskwriter.IDiskData;
import kickass.plugins.interf.diskwriter.IDiskFileData;
import kickass.plugins.interf.diskwriter.IDiskWriter;
import kickass.plugins.interf.general.IEngine;
import kickass.plugins.interf.general.IMemoryBlock;
import kickass.plugins.interf.general.IParameterMap;

import java.io.OutputStream;
import java.util.List;
import java.util.Set;

/**
 * Written by Chris Zinn.  A license has not been assigned to this yet.
 * This is a work in progress.
 */
public final class MultiDiskWriter implements IDiskWriter
{
    private static final String VERSION = "0.9";
    private static final String VERSION_DATE = "12/21/2025";
    private static boolean productInformationDisplayed = false;

    private static final List<String> possibleDriveTypes = List.of("1541", "1571", "1581");
    private static final List<String> possibleFileTypes  = List.of("del", "seq", "prg", "usr", "rel");
    private final DiskWriterDefinition m_definition;

    public MultiDiskWriter()
    {
        m_definition = new DiskWriterDefinition();
        m_definition.setName("multidisk");

        m_definition.setAllDiskParameters(Set.of("filename", "name", "id", "driveType", "showInfo"));
        m_definition.setNonOptionalDiskParameters(Set.of("filename"));

        m_definition.setAllFileParameters(Set.of("name","type"));
        m_definition.setNonOptionalFileParameters(Set.of("name"));
    }

    private void displayProductInformation(IEngine engine)
    {
        if (!productInformationDisplayed)
        {
            engine.printNow(String.format("MultiDisk Writer Plugin v%s %s", VERSION, VERSION_DATE + " initialized."));
            engine.printNow("Get the latest version here: https://github.com/MyDeveloperThoughts/KickAssemblerFloppyDiskWriter/blob/main/multiDiskPlugin.zip");
            engine.printNow("Documentation Here         : https://github.com/MyDeveloperThoughts/KickAssemblerFloppyDiskWriter/blob/main/README.md");
            engine.printNow("GitHub Repo                : https://github.com/MyDeveloperThoughts/KickAssemblerFloppyDiskWriter");

            productInformationDisplayed = true;
        }
    }

    @Override
    public DiskWriterDefinition getDefinition()
    {
        return m_definition;
    }

    @Override
    public void execute(IDiskData diskData, IEngine engine)
    {
        displayProductInformation(engine);

        String diskFilename  = diskData.getParameters().getStringValue("filename", "");
        String diskName      = diskData.getParameters().getStringValue("name", "UNNAMED");
        String diskId        = diskData.getParameters().getStringValue("id", "2A");
        String driveType     = diskData.getParameters().getStringValue("driveType", "");
        boolean showInfo     = diskData.getParameters().getBoolValue("showInfo", false);

        if (driveType.isEmpty()) driveType = Disk.determineDriveTypeFromFilename(diskFilename);
        if (!possibleDriveTypes.contains(driveType))
            engine.error(String.format("%s is an invalid drive type.  Must be one of %s", driveType, possibleDriveTypes));

        Disk disk = Disk.createFormattedDisk(diskFilename, diskName, diskId, driveType);
        disk.printDebugInformation = showInfo;

        engine.printNow(String.format(".disk multidisk %s\t%s\tName:%s Id:%s",diskFilename, driveType, diskName, diskId));

        List<IDiskFileData> filesFromAssembler = diskData.getFiles();
        for(IDiskFileData file : filesFromAssembler)
        {
            IParameterMap fileParams = file.getParameters();

            String  storeFilename            = fileParams.getStringValue("name", "");
            String  fileType                = fileParams.getStringValue("type", "").toLowerCase();  // del, seq, prg, usr, rel  (If last character is < is it software locked)
            boolean doNotStoreStartAddress  = fileParams.getBoolValue("noStartAddr", false);

            List<IMemoryBlock> memoryBlocks = file.getMemoryBlocks();
            boolean storeStartAddress = !doNotStoreStartAddress;

            boolean isSoftwareLocked = fileType.endsWith("<");
            if (isSoftwareLocked)
                fileType = fileType.substring(0, fileType.length() - 1);
            if (!possibleFileTypes.contains(fileType))
                engine.error(String.format("%s is an invalid file type.  Must be one of %s", fileType, possibleFileTypes));

            disk.writeFileToDisk(engine, memoryBlocks, storeStartAddress, storeFilename, fileType, isSoftwareLocked);
        }

        try(OutputStream output = engine.openOutputStream(diskFilename))
        {
            output.write(disk.rawBytes);
        }
        catch(Exception e)
        {
            engine.error(e.getMessage());
        }
    }
}
