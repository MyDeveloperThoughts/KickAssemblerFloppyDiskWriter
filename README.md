# MultiDisk Writer plugin for Kick Assembler
A Disk Writer plugin for Kick Assembler that supports the 1541, 1571 and 1581 disk formats.   d64, d71 and 81.  
Written by Chris Zinn.  
This is a work in progress and will most likely drastically change over the next few weeks.  12/18/2025

# Setting up the project on Windows 11
This is how I configured machine to work with this project.  
This project is built with Maven, meaning it should be very easy to import into any IDE of your choice.
## What I use
1. Intellij Idea 2025.3
2. Java SDK 25
3. Kick Assembler 2.5 (Included here.. but not for much longer)  
This project will **NOT** distribute Kick Assembler once this code is production ready.  
It's just very conveniant to have it in the project at the moment.

# How to run in Intellij IDEA 2025.3
1. Start a new project, clone from this repo to c:\project
2. Go to Edit Configurations
3. Click + Application
4. Set the name to Go
5. kickass.KickAssembler
6. C:\project\KickAssemblerFloppyDiskWriter\src\main\asm\helloWorld.asm -odir C:\project\KickAssemblerFloppyDiskWriter\output
7. C:\project\KickAssemblerFloppyDiskWriter
   (Of course change these to the directory you are using)

Screenshot of the application configuration 
![Idea Configuration](idea-configuration.png)


# How it works
Running the project will store the created disk files in the output directory.



