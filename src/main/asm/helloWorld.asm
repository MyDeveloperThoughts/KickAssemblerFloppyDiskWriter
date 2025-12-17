.segmentdef CODE
.segment CODE
BasicUpstart2(go)
     * = * "Main Program"
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
.disk zinn [filename="test.d64", name="STUFF", id="CZ", driveType="1541"]
{
       [name="--------1-------", type="rel" ],
       [name="BROADSIDES      ", type="prg", prgFiles="C:\project\KickAssemblerFloppyDiskWriter\output/broadsides.prg" ],
       [name="--------2-------", type="rel" ],
       [name="--------3-------", type="rel" ],
       [name="--------4-------", type="rel" ],
       [name="--------5-------", type="rel" ],
       [name="--------6-------", type="rel" ],
       [name="--------7-------", type="rel" ],
       [name="--------8-------", type="rel" ],
       [name="--------9-------", type="rel" ]
}
