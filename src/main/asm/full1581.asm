.segment CODE []
BasicUpstart2(go)
.memblock "Main Program"
go:
     lda #$02
     sta $d020
     rts

// .disk [filename="test.d64", name="PLUGIN DISK", id="12", showInfo=true]

// .disk Consolez [name="plugin disk"]
// .disk [filename="test.d64", name="", id="", showInfo=true]
// {
//         [name="----------------", type="rel" ]
// }
.disk zinn [filename="test.d81", name="STUFF", id="CZ", driveType="1581"]
{
       [name="SIDPLAYER128", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="SID.OBJ.128", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidobj.128" ],
       [name="SVIOL-AMINOR.MUS", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/viol-aminor.mus" ],

       [name="1", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="2", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="3", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="4", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="5", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="6", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="7", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="8", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="9", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="10", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="11", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="12", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="13", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="14", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="15", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="16", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="17", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="18", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="19", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="20 ", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="21", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="22", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="23", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="24", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="25", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="26", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="27", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="28", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="29", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="30 ", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="31", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="32", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="33", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="34", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="35", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="36", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="37", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="38", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="39", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="40 ", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="10", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="11", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="12", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="13", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="14", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="15", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="16", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="17", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="18", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="19", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="20 ", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="21", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="22", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="23", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="24", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="25", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="26", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="27", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="28", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="29", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="30 ", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="31", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="32", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="33", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="34", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="35", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="36", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="37", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="38", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="39", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ],
       [name="40 ", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/sidplayer.128" ]
}
