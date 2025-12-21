# MultiDisk Writer plugin for Kick Assembler
A Disk Writer plugin for Kick Assembler that supports the 1541, 1571 and 1581 disk formats.   d64, d71 and 81.  
Written by Chris Zinn.  

Download and unzip the multiDiskPlugin.zip file into the plugins folder of KickAssembler.  

This plugin is designed to be as compatible with the existing .disk and .file parameters as possible.  
This is an example usage of the .disk parameter that creates a 1541 disk image placing the assembled code in the CODE segment into a file called HELLO WORLD. 
```
.disk           [filename="helloworld.d64", name="STUFF", id="CZ"]
{
    [name="HELLO WORLD", type="prg", segments = "CODE"]
}
```

Just add the option **multidisk** to use this plugin to generate the disk image instead.
```
.disk multidisk [filename="helloworld.d64", name="STUFF", id="CZ"]
{
    [name="HELLO WORLD", type="prg", segments = "CODE"]
}
```

Changing the filename to end in .d71 or .d81 will create disk images of the appropriate type.  
The **driveType** option can also be used to force the image to be created using a specific drive format.
This is useful if the filename paramater does not end in .d64, .71 or .81.  
The options available are **1541, 1571 and 1581**
Just add the option **multidisk** to use this plugin to generate the disk image instead.
```
.disk multidisk [filename="myDiskImage", name="STUFF", id="CZ", driveType="1581"]
{
    [name="HELLO WORLD", type="prg", segments = "CODE"]
}
```



**Unsupported Option**  
The following options are **not** supported in this plugin:  
* dontSplitFilesOverDir    
* format
* interleave  
* storeFilesInDir  

[DEVNOTES](DEVNOTES.md)  
These are some quick notes if you wish to contribute or help test this project. 



