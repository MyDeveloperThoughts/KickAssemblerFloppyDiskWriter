# KickAssemblerFloppyDiskWriter
A Disk Writer plugin for Kick Assembler that supports 1541, 1571 and 1581 disk formats.   d64, d71 and 81.

# Setting up the project on Windows 11
## Prerequisited
1. Intellij Idea 2025.3
2. Java SDK 25
3. Kick Assembler 2.5 (Included here)


# How to run in Intellij IDEA 2025.3
1. Start a new project, clone from this repo to c:\project
2. Go to Edit Configurations
3. Click + Application
4. Set the name to Go
5. kickass.KickAssembler
6. C:\project\KickAssemblerFloppyDiskWriter\src\main\asm\helloWorld.asm -odir C:\project\KickAssemblerFloppyDiskWriter\output
7. C:\project\KickAssemblerFloppyDiskWriter

Screenshot of the application configuration 
![Idea Configuration](idea-configuration.png)


# How it works
Running the project will store the created disk files in the output directory.



